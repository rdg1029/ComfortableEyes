package com.comfortable.eyes.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class TimeCountStarter: Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TimeCountStarter", "service started")
        val i = Intent("com.comfortable.eyes.TEST")
        sendBroadcast(i)
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TimeCountStarter", "service stopped")
    }
}