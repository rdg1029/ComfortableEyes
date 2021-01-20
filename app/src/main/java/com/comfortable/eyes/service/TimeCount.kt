package com.comfortable.eyes.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.comfortable.eyes.receiver.DateChangedReceiver
import com.comfortable.eyes.receiver.ScreenStateReceiver
import com.comfortable.eyes.receiver.ShutdownReceiver

class TimeCount : Service() {
/*
    private fun buildNotification(contentText: String): Notification? {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("TimeCount", "UsingTime", NotificationManager.IMPORTANCE_LOW)
            notificationChannel.vibrationPattern = longArrayOf(0)
            notificationChannel.enableVibration(true)
            if (notificationManager == null) return null
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
        return NotificationCompat.Builder(this, "TimeCount")
                .setSmallIcon(R.drawable.ic_baseline_visibility_24)
                .setContentTitle("오늘의 휴대폰 사용 시간")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(false)
                .build()
    }
*/
    private val screenStateReceiver = ScreenStateReceiver()
    private val shutdownReceiver = ShutdownReceiver()
    private val dateChangedReceiver = DateChangedReceiver()

    private fun setReceiver() {
        val screenStateFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_TIME_TICK)
        }
        val shutdownFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SHUTDOWN)
        }
        val dateChangedFilter = IntentFilter().apply {
            addAction(Intent.ACTION_DATE_CHANGED)
        }

        registerReceiver(screenStateReceiver, screenStateFilter)
        registerReceiver(shutdownReceiver, shutdownFilter)
        registerReceiver(dateChangedReceiver, dateChangedFilter)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        setReceiver()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenStateReceiver)
        unregisterReceiver(shutdownReceiver)
        unregisterReceiver(dateChangedReceiver)
    }
}