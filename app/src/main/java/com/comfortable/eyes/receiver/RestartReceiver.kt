package com.comfortable.eyes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class RestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, TimeCount::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(i) else context.startService(i)
    }
}