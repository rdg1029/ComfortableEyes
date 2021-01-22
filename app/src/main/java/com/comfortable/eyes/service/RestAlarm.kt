package com.comfortable.eyes.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import com.comfortable.eyes.NotiDialog

class RestAlarm : Service() {
    lateinit var alarmDialog: NotiDialog

    inner class LoopHandler: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            alarmDialog.displayNotification()
            sendEmptyMessageDelayed(0, 1000)
        }
    }
    private val handler = LoopHandler()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(3847, alarmDialog.buildNotification())
        handler.sendEmptyMessageDelayed(0, 1000)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        alarmDialog = NotiDialog(this, "눈에 휴식이 필요한 시간입니다!", "com.comfortable.eyes.PM_CONFIRM", "com.comfortable.eyes.PM_CANCEL")
    }

    override fun onDestroy() {
        handler.removeMessages(0)
        alarmDialog.removeNotification()
        stopForeground(true)
        Log.d("RestAlarm", "Service Stopped")
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}