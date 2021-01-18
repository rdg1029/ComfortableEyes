package com.comfortable.eyes

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi

class CheckOnUsing(var mContext: Context) {
    @get:RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    val isScreenOn: Boolean
        get() {
            val pm = mContext.getSystemService(Context.POWER_SERVICE) as PowerManager
            return pm.isInteractive
        }
    val isDeviceLock: Boolean
        get() {
            val keyguardManager = mContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            return keyguardManager.isKeyguardLocked
        }
}