package com.comfortable.eyes.receiver

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import com.comfortable.eyes.RestAlarmManager
import com.comfortable.eyes.TimeNotification
import com.comfortable.eyes.state.SharedTimeState

class ScreenStateReceiver: BroadcastReceiver() {
    var isScreenLocked: Boolean = false

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return

        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        when(intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                isScreenLocked = km.isKeyguardLocked
            }

            Intent.ACTION_SCREEN_OFF -> {
                if (isScreenLocked) return

                // usedTime = usedTime + (screenOffTime - startTime)
                SharedTimeState.usedTime = SharedTimeState.usedTime + (SystemClock.elapsedRealtime() - SharedTimeState.startTime)

                RestAlarmManager.cancel()
            }

            Intent.ACTION_USER_PRESENT -> {
                isScreenLocked = km.isKeyguardLocked

                val timeStart = SystemClock.elapsedRealtime()
                SharedTimeState.startTime = timeStart

                if (!RestAlarmManager.isAlarmEnabled) return

                val timeSleep = timeStart - RestAlarmManager.timeAlarmCanceled
                val timeAlarmActived = RestAlarmManager.timeAlarmCanceled - RestAlarmManager.timeAlarmApplied
                val timeRest = RestAlarmManager.timeRest

                if (timeSleep < timeRest && timeSleep < timeAlarmActived) {
                    Log.d("ScreenStateReceiver", "NOT Enough Rest")
                    RestAlarmManager.apply(RestAlarmManager.timeAlarmCycle - (timeAlarmActived - timeSleep).toInt(), (timeRest - timeSleep).toInt(), false)
                }
                else {
                    Log.d("ScreenStateReceiver", "Enough Rest")
                    RestAlarmManager.apply(RestAlarmManager.timeAlarmCycle, RestAlarmManager.timeRest, true)
                }
            }

            Intent.ACTION_TIME_TICK -> {
                if (km.isKeyguardLocked) return

                TimeNotification.updateNotification()
            }
        }
    }
}