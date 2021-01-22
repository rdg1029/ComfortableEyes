package com.comfortable.eyes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class RestAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "TEST ALARM!", Toast.LENGTH_SHORT).show()
    }
}