package com.comfortable.eyes.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.os.SystemClock
import com.comfortable.eyes.TimeNotification
import com.comfortable.eyes.receiver.DateChangedReceiver
import com.comfortable.eyes.receiver.ScreenStateReceiver
import com.comfortable.eyes.receiver.ShutdownReceiver
import com.comfortable.eyes.state.SharedTimeState

class TimeCount : Service() {
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
        SharedTimeState.getSavedUsedTime()
        SharedTimeState.startTime = SystemClock.elapsedRealtime()

        startForeground(1029, TimeNotification.buildNotification(TimeNotification.usedTimeToMin))

        setReceiver()

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        unregisterReceiver(screenStateReceiver)
        unregisterReceiver(shutdownReceiver)
        unregisterReceiver(dateChangedReceiver)

        SharedTimeState.usedTime = SharedTimeState.usedTime + (SystemClock.elapsedRealtime() - SharedTimeState.startTime)
        SharedTimeState.commitState()
    }
}