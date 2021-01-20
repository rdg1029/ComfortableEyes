package com.comfortable.eyes.receiver

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenStateReceiver: BroadcastReceiver() {
    lateinit var mContext: Context
    var isScreenLocked: Boolean = false

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return

        mContext = context

        val km = mContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        when(intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                isScreenLocked = km.isKeyguardLocked
            }

            Intent.ACTION_SCREEN_OFF -> {
                if (isScreenLocked) return
            }

            Intent.ACTION_USER_PRESENT -> {
                isScreenLocked = km.isKeyguardLocked
            }

            Intent.ACTION_TIME_TICK -> {
                if (km.isKeyguardLocked) return
            }
        }
    }
}