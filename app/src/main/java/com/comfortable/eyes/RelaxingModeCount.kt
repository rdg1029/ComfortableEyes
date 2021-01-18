package com.comfortable.eyes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class RelaxingModeCount : Service() {
    private var timer: Thread? = null
    private var rmState: RelaxingModeState? = null
    private var rmDialog: NotiDialog? = null
    private var count = 0
    private var notificationManager: NotificationManager? = null
    private lateinit var wakeLock: WakeLock
    private fun setNotificationChannel() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("RelaxingModeCount", "RelaxingMode", NotificationManager.IMPORTANCE_LOW)
            notificationChannel.vibrationPattern = longArrayOf(0)
            notificationChannel.enableVibration(true)
            if (notificationManager == null) return
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
    }

    private fun updateNotification() {
        val notiBuilder = NotificationCompat.Builder(this, "RelaxingModeCount")
                .setSmallIcon(R.drawable.ic_baseline_visibility_24)
                .setContentTitle("휴식 시간")
                .setContentText(String.format(String.format("%s:%s", if (count / 60 < 10) "0" + count / 60 else Integer.toString(count / 60), if (count % 60 < 10) "0" + count % 60 else Integer.toString(count % 60))))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(false)
        if (notiBuilder == null && notificationManager == null) return
        startForeground(4756, notiBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    private fun loopTask() {
        val checkOnUsing = CheckOnUsing(this)
        if (!rmState!!.isActivityPaused!!) {
            count--
            rmState!!.countValue = count
            rmState!!.commitState()
            if (rmState!!.countValue == 0) {
                if (wakeLock!!.isHeld) wakeLock!!.release()
            }
        } else if (rmState!!.isActivityPaused!! && checkOnUsing.isScreenOn) {
            rmDialog!!.displayNotification() //휴식모드 진행 중 다른 화면으로 나가면 헤드업 표시
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock")
        wakeLock.acquire()
        timer = Thread {
            while (count > 0) {
                loopTask()
                updateNotification()
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }
        timer!!.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        rmState = RelaxingModeState(this)
        rmDialog = NotiDialog(this, "휴식을 계속 진행하시겠습니까?", "com.comfortable.eyes.RM_CONFIRM", "com.comfortable.eyes.RM_CANCEL")
        count = rmState!!.countValue
        setNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        notificationManager!!.cancel(3847)
        timer!!.interrupt()
        if (wakeLock!!.isHeld) wakeLock!!.release()
    }
}