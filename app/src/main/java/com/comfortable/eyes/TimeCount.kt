package com.comfortable.eyes

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*

class TimeCount : Service() {
    private var timeState: SharedTimeState? = null
    private var pmState: ProtectModeState? = null
    private var time = Time()
    private var notificationManager: NotificationManager? = null
    private var timer: Thread? = null
    private var pmDialog: NotiDialog? = null
    private var isCount = false
    private lateinit var wakeLock: WakeLock
    private val state: Unit
        private get() {
            timeState = SharedTimeState(applicationContext)
            pmState = ProtectModeState(applicationContext)
            time = timeState!!.getTime()
        }

    private fun taskOnUsing() {
        if (!pmState!!.isProtectModeEnable) {
            if (wakeLock!!.isHeld) wakeLock!!.release()
            return
        }
        if (pmState!!.notUsingCountValue <= 0 && pmState!!.isNotUsingCountPaused) {
            //Log.i(this.getClass().getName(), "화면 사용 중 : notUsingCount 일시 중지 상태 확인, notiCount와 notUsingCount 재개(일시 중지 취소) 및 초기화");
            //Log.i(this.getClass().getName(), "화면 사용 중 : notiTime : " + pmState.getNotiTime());
            pmState!!.setNotiCountPause(false)
            pmState!!.setNotUsingCountPause(false)
            pmState!!.setNotiCount(pmState!!.notiTime)
            pmState!!.setNotUsingCount(pmState!!.notiTime / 5)
            pmState!!.commitState()
            wakeLock!!.acquire()
        } else if (!pmState!!.isNotiCountPaused && pmState!!.notiCountValue > 0) {
            pmState!!.setNotUsingCount(pmState!!.notiTime / 5)
            pmState!!.notiCountValue = pmState!!.notiCountValue - 1
            pmState!!.commitState()
            //Log.i(this.getClass().getName(), "화면 사용 중 : notiCount 감소 " + pmState.getNotiCountValue());
            //Log.i(this.getClass().getName(), "화면 사용 중 : notiTime : " + pmState.getNotiTime());
        } else if (pmState!!.notiCountValue <= 0) {
            Log.i(this.javaClass.name, "화면 사용 중 : 헤드업 노티 표시, notiCount 일시 중지")
            pmState!!.setNotiCountPause(true)
            pmState!!.setNotUsingCountPause(true)
            pmState!!.commitState()
            //pmState.setNotiCount(15);
            //pmState.setNotUsingCount(15/5); // -> move to NotiActionReceiver.class
            //rmState.setCount(15/5);
            pmDialog!!.displayNotification() //다이얼로그 표시 -> 헤드업 노티피케이션 표시
        }
    }

    private fun taskNotUsing() {
        if (pmState!!.isNotUsingCountPaused || !pmState!!.isProtectModeEnable) return
        if (pmState!!.notUsingCountValue > 0 && pmState!!.notiCountValue < pmState!!.notiTime * 60) {
            pmState!!.notiCountValue = pmState!!.notiCountValue + 1
            pmState!!.notUsingCountValue = pmState!!.notUsingCountValue - 1
            pmState!!.commitState()
            /*
            Log.i(this.getClass().getName(), "화면 사용 X : notiCount 증가 " + pmState.getNotiCountValue());
            Log.i(this.getClass().getName(), "화면 사용 X : notiTime : " + pmState.getNotiTime());
            Log.i(this.getClass().getName(), "화면 사용 X : notUsingCount 감소 " + pmState.getNotUsingCountValue());
            Log.i(this.getClass().getName(), "화면 사용 X : notiTime : " + pmState.getNotiTime());
            */
        } else if (pmState!!.notUsingCountValue <= 0 || pmState!!.notiCountValue >= pmState!!.notiTime * 60) {
            //Log.i(this.getClass().getName(), "화면 사용 X : notUsingCount <= 0 또는 notiCountValue >= notiTime 이므로 notUsingCount 일시 중지");
            //Log.i(this.getClass().getName(), "화면 사용 X : notiTime : " + pmState.getNotiTime());
            pmState!!.notUsingCountValue = 0
            pmState!!.setNotUsingCountPause(true)
            pmState!!.commitState()
            if (wakeLock!!.isHeld) wakeLock!!.release()
        }
        /*
        if(pmState.isProtectModeEnable()) {
            int notUsingCount = pmState.getNotUsingCountValue();
            notUsingCount--;
            pmState.setNotUsingCountValue(notUsingCount);
            Log.i(this.getClass().getName(), "화면 사용 X : notUsingCount 감소 " + notUsingCount);
            Log.i(this.getClass().getName(), "화면 사용 X : notiTime : " + pmState.getNotiTime());
        }
        */
    }

    private fun setTimeValue() {
        time.seconds++
        if (time.seconds == 60) {
            time.seconds = 0
            time.minutes++
            if (time.minutes == 60) {
                time.minutes = 0
                time.hour++
            }
        }
        timeState!!.setTime(time)
    }

    private fun timeCount() {
        state
        val currentTime = Calendar.getInstance().time
        val currentDate = SimpleDateFormat("dd", Locale.getDefault()).format(currentTime)
        setTimeValue()
        if (currentDate != timeState!!.getCurrentDate()) {
            timeState!!.setCurrentDate(currentDate)
            timeState!!.resetTime(time)
        }
        timeState!!.commitState()
    }

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

    private fun updateNotification() {
        val notification = buildNotification(String.format("%d시간 %d분 %d초", time.hour, time.minutes, time.seconds))
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1029, notification)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    private fun loopTask(checkOnUsing: CheckOnUsing) {
        if (checkOnUsing.isScreenOn && !checkOnUsing.isDeviceLock) {
            //Log.i(this.getClass().getName(), "KeyguardLocked 메소드 반환 값 : " + checkOnUsing.isDeviceLock());
            //getState();
            taskOnUsing() //화면 사용 중 처리하는 작업
            timeCount()
            updateNotification()
        } else {
            taskNotUsing() //화면 사용X 일 때 처리하는 작업
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock")
        wakeLock.acquire()
        startForeground(1029, buildNotification(String.format("%d시간 %d분 %d초", time.hour, time.minutes, time.seconds)))
        val checkOnUsing = CheckOnUsing(this@TimeCount)
        timer = Thread {
            isCount = true
            while (isCount) {
                loopTask(checkOnUsing)
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        timer!!.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        state
        pmState!!.setNotiCountPause(false)
        pmState!!.setNotUsingCountPause(false)
        pmState!!.commitState()
        pmDialog = NotiDialog(this, "눈에 휴식이 필요한 시간입니다!", "com.comfortable.eyes.PM_CONFIRM", "com.comfortable.eyes.PM_CANCEL")
    }

    override fun onDestroy() {
        super.onDestroy()
        isCount = false
        timer!!.interrupt()
        stopForeground(true)
        if (wakeLock!!.isHeld) wakeLock!!.release()
    }
}