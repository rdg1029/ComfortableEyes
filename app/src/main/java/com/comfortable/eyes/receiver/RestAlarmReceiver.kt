package com.comfortable.eyes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.comfortable.eyes.service.RestAlarm

class RestAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
//        Toast.makeText(context, "TEST ALARM!", Toast.LENGTH_SHORT).show()
        val i = Intent(context, RestAlarm::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(i)
        else
            context.startService(i)
    }
}