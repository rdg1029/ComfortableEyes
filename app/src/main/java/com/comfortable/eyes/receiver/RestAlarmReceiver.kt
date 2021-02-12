package com.comfortable.eyes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.comfortable.eyes.service.RestAlarm
import com.comfortable.eyes.state.RestModeState

class RestAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val i = Intent(context, RestAlarm::class.java)

        RestModeState.restAlarmClickAllowed = true
//        RestModeState.commitState()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(i)
        else
            context.startService(i)
    }
}