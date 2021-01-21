package com.comfortable.eyes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.comfortable.eyes.service.TimeCount
import com.comfortable.eyes.state.SharedTimeState
import java.util.*

class RestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, TimeCount::class.java)
        val sharedTimeState = SharedTimeState(context)
        if (sharedTimeState.dayOfYear != Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
            sharedTimeState.dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            sharedTimeState.init()
            sharedTimeState.commitState()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) 
            context.startForegroundService(i) 
        else 
            context.startService(i)
    }
}