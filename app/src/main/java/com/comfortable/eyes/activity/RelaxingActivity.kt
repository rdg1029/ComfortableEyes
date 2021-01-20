package com.comfortable.eyes.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.comfortable.eyes.*
import com.comfortable.eyes.service.RelaxingModeCount
import com.comfortable.eyes.service.TimeCount
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd

class RelaxingActivity : Activity() {
    private var pmState: ProtectModeState? = null
    private var rmState: RelaxingModeState? = null
    private var rmTimer: TextView? = null
    private var wording: TextView? = null
    private var count = 0
    private var finishButton: Button? = null
    private var interstitialAd: InterstitialAd? = null
    private fun doFullScreen() {
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN
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
            rmState!!.isInterrupted = false
            rmState!!.countValue = 0
            count = 0
            rmState!!.isActivityPaused = false
            rmState!!.commitState()
            finish()
            adDialog.dismiss()
        }
        adDialog.setNegativeButton {
            rmState!!.isInterrupted = false
            rmState!!.isActivityPaused = false
            rmState!!.commitState()
            adDialog.dismiss()
        }
        adDialog.show()
    }

    @SuppressLint("HandlerLeak")
    private val countRelaxingMode: Handler? = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            count = rmState!!.countValue
            if (count == 0) {
                if (interstitialAd!!.isLoaded) {
                    interstitialAd!!.show()
                }
                wording!!.visibility = View.INVISIBLE
                finishButton!!.visibility = View.VISIBLE
            }
            rmTimer!!.text = String.format("%s:%s", if (count / 60 < 10) "0" + count / 60 else Integer.toString(count / 60), if (count % 60 < 10) "0" + count % 60 else Integer.toString(count % 60))
            this.sendEmptyMessageDelayed(0, 1000)
        }
    }

    private fun initInterstitialAd() {
        interstitialAd = InterstitialAd(this)
        interstitialAd!!.adUnitId = "ca-app-pub-3940256099942544/1033173712" //전면 광고 TEST ID
        interstitialAd!!.loadAd(AdRequest.Builder().build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(this.javaClass.name, "onCreate 실행")
        setContentView(R.layout.activity_relaxing)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(3847)
        pmState = ProtectModeState(this)
        rmState = RelaxingModeState(this)
        rmTimer = findViewById(R.id.relaxing_count)
        wording = findViewById(R.id.relaxing_wording)
        finishButton = findViewById(R.id.relaxing_finish)
        finishButton?.visibility = View.GONE
        if (!pmState!!.isNotiCountPaused) {
            finish()
            return
        }
        initInterstitialAd()
        doFullScreen()
        stopService(Intent(this, TimeCount::class.java))
        if (!rmState!!.isActivityPaused!!) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(Intent(this, RelaxingModeCount::class.java)) else startService(Intent(this, RelaxingModeCount::class.java))
        }
        if (rmState!!.isInterrupted!!) {
            Log.i(this.javaClass.name, "강제 중지 다이얼로그")
            interruptedDialog()
        } else {
            rmState!!.isActivityPaused = false
            rmState!!.commitState()
        }
        count = rmState!!.countValue
        rmTimer?.text = String.format("%s:%s", if (count / 60 < 10) "0" + count / 60 else Integer.toString(count / 60), if (count % 60 < 10) "0" + count % 60 else Integer.toString(count % 60))
    }

    override fun onBackPressed() {}
    fun finishRelaxing(v: View?) {
        count = rmState!!.countValue
        if (count == 0) {
            finish()
        } else {
            Toast.makeText(applicationContext, "아직 종료할 수 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(this.javaClass.name, "onResume 실행")
        count = rmState!!.countValue
        if (count > 0 && rmState!!.isActivityPaused!!) {
            rmState!!.isActivityPaused = false
            rmState!!.commitState()
        }
        if (countRelaxingMode != null) {
            countRelaxingMode.removeMessages(0)
            countRelaxingMode.sendEmptyMessage(0)
        }
        doFullScreen()
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    override fun onPause() {
        super.onPause()
        Log.i(this.javaClass.name, "onPause 실행")
        countRelaxingMode?.removeMessages(0)
        val checkOnUsing = CheckOnUsing(this)
        if (count > 0 && checkOnUsing.isScreenOn) {
            rmState!!.isActivityPaused = true
            rmState!!.commitState()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(this.javaClass.name, "onDestroy 실행")
        if (!pmState!!.isNotiCountPaused) return
        count = rmState!!.countValue
        if (count > 0 && rmState!!.isActivityPaused!!) return
        Log.i(this.javaClass.name, "onDestroy 실행(완전 종료)")
        stopService(Intent(this, RelaxingModeCount::class.java))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(Intent(this, TimeCount::class.java)) else startService(Intent(this, TimeCount::class.java))
        pmState!!.setNotiCountPause(false)
        pmState!!.setNotUsingCountPause(false)
        rmState!!.isActivityPaused = false
        pmState!!.setNotiCount(pmState!!.notiTime)
        pmState!!.setNotUsingCount(pmState!!.notiTime / 5)
        pmState!!.commitState()
        rmState!!.commitState()
        Toast.makeText(applicationContext, "휴식 모드 종료됨", Toast.LENGTH_SHORT).show()
    }
}