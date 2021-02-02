package com.comfortable.eyes.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.comfortable.eyes.NotiDialog
import com.comfortable.eyes.R
import com.comfortable.eyes.state.RestModeState

class RestModeCount : Service() {
    private lateinit var restTimer: Thread
    private lateinit var restModeState: RestModeState
    private lateinit var rmDialog: NotiDialog
    private lateinit var notificationManager: NotificationManager

    private var endTime: Long = 0

    private var restTime: String = ""
        get() {
            val sec = ((endTime - SystemClock.elapsedRealtime())/1000).toInt()
            val m = (sec/60)%60
            val s = sec%60

            return if (sec > 0)
                "${if(m < 10) {"0$m"} else{"$m"}}:${if(s < 10) {"0$s"} else{"$s"}}"
            else
                "00:00"
        }

    private fun isCountFinished(): Boolean {
        return if (restModeState.isRestPaused)
            false
        else
            (endTime - SystemClock.elapsedRealtime() <= 0)
    }

    private fun setNotificationChannel() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("RestModeCount", "RestMode", NotificationManager.IMPORTANCE_LOW)
            notificationChannel.vibrationPattern = longArrayOf(0)
            notificationChannel.enableVibration(true)
            if (notificationManager == null) return
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun buildNotification(): Notification? {
        val notiBuilder = NotificationCompat.Builder(this, "RestModeCount")
                .setSmallIcon(R.drawable.ic_baseline_visibility_24)
                .setContentTitle("휴식 시간")
                .setContentText(this.restTime)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(false)
        return notiBuilder.build()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(4756, buildNotification())

        restTimer = Thread {
            while (!isCountFinished()) {
                if (restModeState.isRestPaused) {
                    rmDialog.displayNotification()
                }
                else {
                    endTime = restModeState.endTime
                    notificationManager.notify(4756, buildNotification())
                    notificationManager.cancel(3847)
                }

                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {}
            }
        }
        restTimer.start()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        restModeState = RestModeState(this)
        endTime = restModeState.endTime
        rmDialog = NotiDialog(this, "휴식을 계속 진행하시겠습니까?", "com.comfortable.eyes.RM_CONFIRM", "com.comfortable.eyes.RM_CANCEL")
        setNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        restTimer.interrupt()
        stopForeground(true)
        notificationManager.cancel(3847)
    }
}