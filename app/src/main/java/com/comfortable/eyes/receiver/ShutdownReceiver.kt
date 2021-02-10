package com.comfortable.eyes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.comfortable.eyes.RestAlarmManager
import com.comfortable.eyes.state.SharedTimeState
import java.util.*

class ShutdownReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val sharedTimeState = SharedTimeState(context)
        val restAlarmManager = RestAlarmManager(context)

        SharedTimeState.usedTime = SharedTimeState.usedTime + (SystemClock.elapsedRealtime() - SharedTimeState.startTime)
        SharedTimeState.dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        sharedTimeState.commitState()

        restAlarmManager.cancel()
    }
}