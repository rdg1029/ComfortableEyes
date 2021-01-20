package com.comfortable.eyes.receiver

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.comfortable.eyes.TimeNotification
import com.comfortable.eyes.state.SharedTimeState

class ScreenStateReceiver: BroadcastReceiver() {
    var isScreenLocked: Boolean = false

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return

        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val sharedTimeState = SharedTimeState(context)

        when(intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                isScreenLocked = km.isKeyguardLocked
            }

            Intent.ACTION_SCREEN_OFF -> {
                if (isScreenLocked) return

                // usedTime = usedTime + (screenOffTime - startTime)
                sharedTimeState.usedTime = sharedTimeState.usedTime + (SystemClock.elapsedRealtime() - sharedTimeState.startTime)
                sharedTimeState.commitState()
            }

            Intent.ACTION_USER_PRESENT -> {
                isScreenLocked = km.isKeyguardLocked

                sharedTimeState.startTime = SystemClock.elapsedRealtime()
                sharedTimeState.commitState()
            }

            Intent.ACTION_TIME_TICK -> {
                if (km.isKeyguardLocked) return

                val timeNoti = TimeNotification(context)
                timeNoti.updateNotification()
            }
        }
    }
}