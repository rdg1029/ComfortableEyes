package com.comfortable.eyes

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.comfortable.eyes.state.SharedTimeState

class TimeNotification(context: Context) {
    val mContext = context

    fun getUsedTimeToMin(): String {
        val sharedTimeState = SharedTimeState(mContext)
        val sec = (( sharedTimeState.usedTime + (SystemClock.elapsedRealtime() - sharedTimeState.startTime) ) / 1000).toUInt()
        val s = (sec%60u).toUShort()
        val m = ((sec/60u)%60u).toUShort()
        val h = (sec/3600u).toUShort()

        return "약 ${h}시간 ${if(s >= 30u) {m+1u} else {m}}분"
    }

    fun buildNotification(contentText: String): Notification? {
        val notificationManager = mContext.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("TimeCount", "UsingTime", NotificationManager.IMPORTANCE_LOW)
            notificationChannel.vibrationPattern = longArrayOf(0)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        return NotificationCompat.Builder(mContext, "TimeCount")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("휴대폰 사용 시간")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(false)
                .build()
    }

    fun updateNotification() {
        val notification = buildNotification(getUsedTimeToMin())
        val notiManager = mContext.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notiManager.notify(1029, notification)
    }
}