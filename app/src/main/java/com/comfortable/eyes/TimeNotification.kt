package com.comfortable.eyes

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.graphics.Color
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.comfortable.eyes.state.SharedTimeState
import com.comfortable.eyes.AppContext.Companion.context

object TimeNotification {
    var usedTimeToMin: String = ""
        get() {
            val sec = (( SharedTimeState.usedTime + (SystemClock.elapsedRealtime() - SharedTimeState.startTime) ) / 1000).toInt()
            val s = (sec%60)
            var m = ((sec/60)%60)
            var h = (sec/3600)

            if (s >= 30) {
                m += 1
                if (m >= 60) {
                    m %= 60
                    h += 1
                }
            }

            return "약 ${h}시간 ${m}분"
        }

    fun buildNotification(contentText: String): Notification? {
        val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("TimeCount", "UsingTime", NotificationManager.IMPORTANCE_LOW)
            notificationChannel.vibrationPattern = longArrayOf(0)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        return NotificationCompat.Builder(context, "TimeCount")
                .setSmallIcon(R.drawable.ic_baseline_visibility_24)
                .setContentTitle("휴대폰 사용 시간")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(false)
                .setColor(Color.parseColor("#175F30"))
                .build()
    }

    fun updateNotification() {
        val notification = buildNotification(this.usedTimeToMin)
        val notiManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notiManager.notify(1029, notification)
    }
}