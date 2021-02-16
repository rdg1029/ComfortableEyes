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
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            SharedTimeState.getSavedDayOfYear()
            if (SharedTimeState.dayOfYear != Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                SharedTimeState.dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                SharedTimeState.init()
                SharedTimeState.commitState()
            }

            if (RestAlarmManager.isAlarmEnabled && !km.isKeyguardLocked) {
                val timeStart = SystemClock.elapsedRealtime()
                val timeSleep = timeStart - RestAlarmManager.timeAlarmCanceled
                val timeAlarmActived = RestAlarmManager.timeAlarmCanceled - RestAlarmManager.timeAlarmApplied
                val timeRest = RestAlarmManager.timeRest

                if (timeSleep < timeRest && timeSleep < timeAlarmActived) {
                    Log.d("ScreenStateReceiver", "NOT Enough Rest")
                    RestAlarmManager.apply(RestAlarmManager.timeAlarmCycle - (timeAlarmActived - timeSleep).toInt(), (timeRest - timeSleep).toInt(), false)
                }
                else {
                    Log.d("ScreenStateReceiver", "Enough Rest")
                    RestAlarmManager.apply(RestAlarmManager.timeAlarmCycle, RestAlarmManager.timeRest, true)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) 
            context.startForegroundService(i) 
        else 
            context.startService(i)
    }
}