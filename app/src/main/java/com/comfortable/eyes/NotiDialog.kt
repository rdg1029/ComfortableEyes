package com.comfortable.eyes

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.comfortable.eyes.receiver.NotiActionReceiver

class NotiDialog(private val mContext: Context, private val dialogTextMSG: String, private val actionConfirm: String, private val actionCancel: String) {
    private var notiBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private fun setNotification() {
        val confirmIntent = Intent(mContext, NotiActionReceiver::class.java)
        val cancelIntent = Intent(mContext, NotiActionReceiver::class.java)
        confirmIntent.action = actionConfirm
        cancelIntent.action = actionCancel
        val confirmPIntent = PendingIntent.getBroadcast(mContext, 0, confirmIntent, 0)
        val cancelPIntent = PendingIntent.getBroadcast(mContext, 0, cancelIntent, 0)
        val remoteViews = RemoteViews(mContext.packageName, R.layout.noti_protect_mode)
        remoteViews.setTextViewText(R.id.pm_dialog_msg, dialogTextMSG)
        remoteViews.setOnClickPendingIntent(R.id.pm_dialog_btn_confirm, confirmPIntent)
        remoteViews.setOnClickPendingIntent(R.id.pm_dialog_btn_cancel, cancelPIntent)
        notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("NotiProtect", "NotiProtect", NotificationManager.IMPORTANCE_HIGH)
            //notificationChannel.setVibrationPattern(new long[]{0});
            notificationChannel.enableVibration(true)
            if (notificationManager == null) return
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
        notiBuilder = NotificationCompat.Builder(mContext, "NotiProtect")
                .setSmallIcon(R.drawable.ic_baseline_visibility_24)
                .setContent(remoteViews)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(false)
    }

    fun displayNotification() {
        if (notiBuilder == null && notificationManager == null) return
        notificationManager!!.notify(3847, notiBuilder!!.build())
    }

    init {
        setNotification()
    }
}