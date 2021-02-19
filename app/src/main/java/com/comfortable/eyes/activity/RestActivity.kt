package com.comfortable.eyes.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.comfortable.eyes.*
import com.comfortable.eyes.service.RestCount
import com.comfortable.eyes.service.TimeCount
import com.comfortable.eyes.state.RestModeState
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError

class RestActivity : Activity() {
    private lateinit var restTimer: TextView
    private lateinit var wording: TextView

    private lateinit var finishButton: Button

    private lateinit var ad: TemplateView
    private var isAdLoaded: Boolean = false

    private val countRestTime: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if (isCountFinished()) {
                restTimer.text = "00:00"
                stopRestCount()

                if (isAdLoaded)
                    ad.visibility = View.VISIBLE
                else
                    ad.visibility = View.INVISIBLE

                wording.visibility = View.INVISIBLE
                finishButton.visibility = View.VISIBLE
            }
            else {
                setTimerText()
                this.sendEmptyMessageDelayed(0, 1000)
            }
        }
    }

    private fun stopRestCount() { stopService(Intent(this, RestCount::class.java)) }

    private fun doFullScreen() {
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private fun setTimerText() {
        val sec = ((RestModeState.endTime - SystemClock.elapsedRealtime())/1000).toInt()
        val m = (sec/60)%60
        val s = sec%60

        restTimer.text = "${if(m < 10) {"0$m"} else{"$m"}}:${if(s < 10) {"0$s"} else{"$s"}}"
    }

    //네비게이션 바, 상단바가 나왔을 때 화면을 터치하면 다시 전체화면으로 전환
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            doFullScreen()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun interruptedDialog() {
        val adDialog = AdDialog(this)
        adDialog.setTitle("휴식을 종료하시겠습니까?")
        adDialog.setPositiveButton {
            RestModeState.endTime = SystemClock.elapsedRealtime()
            RestModeState.isRestPaused = false
            RestModeState.isInterrupted = false
//            RestModeState.commitState()

            finish()
            adDialog.dismiss()
        }
        adDialog.setNegativeButton {
            RestModeState.isRestPaused = false
            RestModeState.isInterrupted = false
//            RestModeState.commitState()
            adDialog.dismiss()
        }
        adDialog.show()
    }

    private fun initAd() {
        ad = findViewById(R.id.rest_ad)
        ad.visibility = View.GONE

        //네이티브 광고 TEST ID
        AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110").apply {
            forUnifiedNativeAd { unifiedNativeAd ->
                ad.setNativeAd(unifiedNativeAd)
            }
            withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError?) {
                    isAdLoaded = false
                }

                override fun onAdLoaded() {
                    isAdLoaded = true
                }
            })
            build().loadAd(AdRequest.Builder().build())
        }
    }

    private fun isCountFinished(): Boolean {
        return (RestModeState.endTime - SystemClock.elapsedRealtime() <= 0)
    }

    override fun onBackPressed() {}

    fun finishRelaxing(v: View?) {
        if (isCountFinished()) {
            finish()
        } else {
            Toast.makeText(applicationContext, "아직 종료할 수 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkIsRestPaused() {
        if (RestModeState.isRestPaused) {
            val continueTime = RestModeState.restCount - (RestModeState.pausedTime - RestModeState.startTime).toInt()

            RestModeState.restCount = continueTime
            RestModeState.startTime = SystemClock.elapsedRealtime()
            RestModeState.endTime = RestModeState.startTime + continueTime

            RestModeState.isRestPaused = false
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(Intent(this, RestCount::class.java))
            else
                startService(Intent(this, RestCount::class.java))

            RestModeState.startTime = SystemClock.elapsedRealtime()
            RestModeState.endTime = RestModeState.startTime + RestModeState.restCount + 1000
        }
    }

    private fun checkIsInterrupted() {
        if (RestModeState.isInterrupted) {
            Log.i(this.javaClass.name, "강제 중지 다이얼로그")
            interruptedDialog()
        } else {
            RestModeState.isRestPaused = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(this.javaClass.name, "onCreate 실행")
        setContentView(R.layout.activity_rest)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(3847)

        if (!RestModeState.isRestMode) {
            finish()
            return
        }

        restTimer = findViewById(R.id.rest_count)
        wording = findViewById(R.id.rest_wording)
        finishButton = findViewById(R.id.rest_finish)
        finishButton.visibility = View.GONE

        initAd()
        doFullScreen()
        stopService(Intent(this, TimeCount::class.java))

        checkIsRestPaused()
        checkIsInterrupted()
//        RestModeState.commitState()

        setTimerText()
        countRestTime.sendEmptyMessageDelayed(0, 0)
    }

    override fun onResume() {
        super.onResume()
        Log.i(this.javaClass.name, "onResume 실행")
        RestModeState.restAlarmClickAllowed = false

        doFullScreen()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onPause() {
        super.onPause()
        Log.i(this.javaClass.name, "onPause 실행")

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!pm.isInteractive) return
        if(isCountFinished()) {
            finish()
        }
        else {
            RestModeState.restAlarmClickAllowed = true
            RestModeState.isRestPaused = true
            RestModeState.pausedTime = SystemClock.elapsedRealtime()
//            RestModeState.commitState()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(this.javaClass.name, "onDestroy 실행")

        countRestTime.removeMessages(0)

        if (!RestModeState.isRestMode) return
        if (!isCountFinished() && RestModeState.isRestPaused) return

        Log.i(this.javaClass.name, "onDestroy 실행(완전 종료)")

        if (isAdLoaded)
            ad.destroyNativeAd()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(Intent(this, TimeCount::class.java))
        else
            startService(Intent(this, TimeCount::class.java))

        RestModeState.isRestMode = false
        RestModeState.isRestPaused = false
        RestModeState.isInterrupted = false
        RestModeState.restCount = RestAlarmManager.timeRest

        RestAlarmManager.apply(RestAlarmManager.timeAlarmCycle, RestAlarmManager.timeRest, true)

        Toast.makeText(applicationContext, "휴식 모드 종료됨", Toast.LENGTH_SHORT).show()
    }
}