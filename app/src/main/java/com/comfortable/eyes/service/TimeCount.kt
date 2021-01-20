package com.comfortable.eyes

import android.app.Service
import android.content.Intent
import android.os.IBinder

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
    

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}