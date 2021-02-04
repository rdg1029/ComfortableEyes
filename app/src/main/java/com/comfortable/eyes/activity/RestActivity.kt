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
import com.comfortable.eyes.service.RestModeCount
import com.comfortable.eyes.service.TimeCount
import com.comfortable.eyes.state.RestModeState
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd

class RestActivity : Activity() {
    private lateinit var restModeState: RestModeState
    private lateinit var restTimer: TextView
    private lateinit var wording: TextView

    private var startTime: Long = 0
    private var endTime: Long = 0

    private lateinit var finishButton: Button
    private lateinit var interstitialAd: InterstitialAd

    private val countRestTime: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if (isCountFinished()) {
                restTimer.text = "00:00"
                if (interstitialAd.isLoaded)
                    interstitialAd.show()

                wording.visibility = View.INVISIBLE
                finishButton.visibility = View.VISIBLE
            }
            else {
                setTimerText()
                this.sendEmptyMessageDelayed(0, 1000)
            }
        }
    }

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
        val sec = ((endTime - SystemClock.elapsedRealtime())/1000).toInt()
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
            endTime = SystemClock.elapsedRealtime()
            restModeState.isRestPaused = false
            restModeState.isInterrupted = false
            restModeState.commitState()

            finish()
            adDialog.dismiss()
        }
        adDialog.setNegativeButton {
            restModeState.isRestPaused = false
            restModeState.isInterrupted = false
            restModeState.commitState()
            adDialog.dismiss()
        }
        adDialog.show()
    }

    private fun initInterstitialAd() {
        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712" //전면 광고 TEST ID
        interstitialAd.loadAd(AdRequest.Builder().build())
    }

    private fun isCountFinished(): Boolean {
        return (endTime - SystemClock.elapsedRealtime() <= 0)
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
        if (restModeState.isRestPaused) {
            val continueTime = restModeState.restCount - (restModeState.pausedTime - restModeState.startTime).toInt()
            startTime = SystemClock.elapsedRealtime()
            endTime = startTime + continueTime

            restModeState.restCount = continueTime
            restModeState.startTime = startTime
            restModeState.endTime = endTime

            restModeState.isRestPaused = false
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(Intent(this, RestModeCount::class.java))
            else
                startService(Intent(this, RestModeCount::class.java))

            startTime = SystemClock.elapsedRealtime()
            endTime = startTime + restModeState.restCount + 1000

            restModeState.startTime = startTime
            restModeState.endTime = endTime
        }
    }

    private fun checkIsInterrupted() {
        if (restModeState.isInterrupted) {
            Log.i(this.javaClass.name, "강제 중지 다이얼로그")
            interruptedDialog()
        } else {
            restModeState.isRestPaused = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(this.javaClass.name, "onCreate 실행")
        setContentView(R.layout.activity_rest)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(3847)

        restModeState = RestModeState(this)

        if (!restModeState.isRestMode) {
            finish()
            return
        }

        restTimer = findViewById(R.id.rest_count)
        wording = findViewById(R.id.rest_wording)
        finishButton = findViewById(R.id.rest_finish)
        finishButton.visibility = View.GONE

        initInterstitialAd()
        doFullScreen()
        stopService(Intent(this, TimeCount::class.java))

        checkIsRestPaused()
        checkIsInterrupted()
        restModeState.commitState()

        setTimerText()
        countRestTime.sendEmptyMessageDelayed(0, 0)
    }

    override fun onResume() {
        super.onResume()
        Log.i(this.javaClass.name, "onResume 실행")

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
            restModeState.restAlarmClickAllowed = true
            restModeState.isRestPaused = true
            restModeState.pausedTime = SystemClock.elapsedRealtime()
            restModeState.commitState()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(this.javaClass.name, "onDestroy 실행")

        countRestTime.removeMessages(0)

        if (!restModeState.isRestMode) return
        if (!isCountFinished() && restModeState.isRestPaused) return

        Log.i(this.javaClass.name, "onDestroy 실행(완전 종료)")

        stopService(Intent(this, RestModeCount::class.java))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(Intent(this, TimeCount::class.java))
        else
            startService(Intent(this, TimeCount::class.java))

        val restAlarmManager = RestAlarmManager(this)
        restModeState.isRestMode = false
        restModeState.isRestPaused = false
        restModeState.isInterrupted = false
        restModeState.restCount = restAlarmManager.timeRest
        restModeState.commitState()

        restAlarmManager.apply(restAlarmManager.timeAlarmCycle, restAlarmManager.timeRest, true)

        Toast.makeText(applicationContext, "휴식 모드 종료됨", Toast.LENGTH_SHORT).show()
    }
}