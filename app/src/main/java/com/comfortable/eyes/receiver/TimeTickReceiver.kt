package com.comfortable.eyes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.comfortable.eyes.TimeNotification

object TimeTickReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return
        TimeNotification.updateNotification()
    }
}