package com.comfortable.eyes

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.comfortable.eyes.receiver.NotiActionReceiver

class NotiDialog(private val mContext: Context,
                 private val dialogTextTitle: String,
                 private val dialogTextMSG: String,
                 private val dialogTextConfirm: String,
                 private val dialogTextCancel: String,
                 private val actionConfirm: String,
                 private val actionCancel: String) {

    private lateinit var notiBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager

    private fun makeNotificationButton(action: String, btnTitle: String): NotificationCompat.Action {
        val intent = Intent(mContext, NotiActionReceiver::class.java).let { i ->
            i.action = action
            PendingIntent.getBroadcast(mContext, 0, i, 0)
        }

        return NotificationCompat.Action.Builder(0, btnTitle, intent).build()
    }

    private fun setNotification() {
        notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("RestAlarm", "RestAlarm", NotificationManager.IMPORTANCE_HIGH)
            //notificationChannel.setVibrationPattern(new long[]{0});
            notificationChannel.enableVibration(true)
            if (notificationManager == null) return
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notiBuilder = NotificationCompat.Builder(mContext, "RestAlarm")
                .setSmallIcon(R.drawable.ic_baseline_visibility_24)
                .setContentTitle(dialogTextTitle)
                .setContentText(dialogTextMSG)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(false)
                .addAction(makeNotificationButton(actionConfirm, dialogTextConfirm))
                .addAction(makeNotificationButton(actionCancel, dialogTextCancel))
                .setColor(Color.parseColor("#175F30"))
    }

    fun buildNotification(): Notification {
        return notiBuilder.build()
    }

    fun displayNotification() {
        if (notiBuilder == null && notificationManager == null) return
        notificationManager.notify(3847, notiBuilder!!.build())
    }

    fun removeNotification() {
        notificationManager.cancel(3847)
    }

    init {
        setNotification()
    }
}