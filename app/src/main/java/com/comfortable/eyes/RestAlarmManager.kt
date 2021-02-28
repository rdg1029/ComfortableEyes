package com.comfortable.eyes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import com.comfortable.eyes.receiver.RestAlarmReceiver
import com.comfortable.eyes.AppContext.Companion.context

object RestAlarmManager {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val restAlarm = Intent(context, RestAlarmReceiver::class.java).let {
        intent -> PendingIntent.getBroadcast(context, 2938, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private val pref = context.getSharedPreferences("RestAlarmManager", 0)
    private val edit = pref.edit()

    var isAlarmEnabled: Boolean = pref.getBoolean("rest_alarm_state", false)

    var timeAlarmCycle: Int = pref.getInt("time_alarm_cycle", 15*60000)

    var timeRest: Int = pref.getInt("time_rest", 3*60000)

    var timeAlarmApplied: Long = pref.getLong("alarm_applied", 0)

    var timeAlarmCanceled: Long = pref.getLong("alarm_canceled", 0)

    fun apply(timeAlarmCycle: Int, timeRest: Int, isAppointedTime: Boolean) {
        val currentTime = SystemClock.elapsedRealtime()

        alarmManager.set(
                AlarmManager.ELAPSED_REALTIME,
                currentTime + timeAlarmCycle,
                restAlarm
        )

        if (isAppointedTime) {
            this.timeAlarmCycle = timeAlarmCycle
            this.timeRest = timeRest
            Log.d("RestAlarmManager", "Cycle : $timeAlarmCycle")
            Log.d("RestAlarmManager", "Rest : $timeRest")
        }
        this.timeAlarmApplied = currentTime
        this.isAlarmEnabled = true
    }

    fun cancel() {
        alarmManager.cancel(restAlarm)
        timeAlarmCanceled = SystemClock.elapsedRealtime()
    }

    fun commitState() {
        edit.putBoolean("rest_alarm_state", isAlarmEnabled)
        edit.putInt("time_alarm_cycle", timeAlarmCycle)
        edit.putInt("time_rest", timeRest)
        edit.putLong("alarm_applied", timeAlarmApplied)
        edit.putLong("alarm_canceled", timeAlarmCanceled)
        edit.commit()
    }
}