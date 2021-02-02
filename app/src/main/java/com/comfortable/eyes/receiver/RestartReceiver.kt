package com.comfortable.eyes.receiver

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.comfortable.eyes.RestAlarmManager
import com.comfortable.eyes.service.TimeCount
import com.comfortable.eyes.state.SharedTimeState
import java.util.*

class RestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, TimeCount::class.java)
        val sharedTimeState = SharedTimeState(context)
        val restAlarmManager = RestAlarmManager(context)
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (sharedTimeState.dayOfYear != Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
            sharedTimeState.dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            sharedTimeState.init()
            sharedTimeState.commitState()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) 
            context.startForegroundService(i) 
        else 
            context.startService(i)

        if (restAlarmManager.isAlarmEnabled) {
            if (km.isKeyguardLocked) return

            val timeStart = SystemClock.elapsedRealtime()
            val timeSleep = timeStart - restAlarmManager.timeAlarmCanceled
            val timeAlarmActived = restAlarmManager.timeAlarmCanceled - restAlarmManager.timeAlarmApplied
            val timeRest = restAlarmManager.timeRest

            if (timeSleep < timeRest && timeSleep < timeAlarmActived) {
                Log.d("ScreenStateReceiver", "NOT Enough Rest")
                restAlarmManager.apply(restAlarmManager.timeAlarmCycle - (timeAlarmActived - timeSleep).toInt(), (timeRest - timeSleep).toInt(), false)
            }
            else {
                Log.d("ScreenStateReceiver", "Enough Rest")
                restAlarmManager.apply(restAlarmManager.timeAlarmCycle, restAlarmManager.timeRest, true)
            }
        }
    }
}