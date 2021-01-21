package com.comfortable.eyes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock

class RestAlarmManager(context: Context) {
    private val mContext = context

    private val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val restAlarm = Intent(mContext, RestAlarmManager::class.java).let {
        intent -> PendingIntent.getBroadcast(mContext, 2938, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private val pref = mContext.getSharedPreferences("RestAlarmManager", 0)
    private val edit = pref.edit()

    var isAlarmEnabled: Boolean
        get() = pref.getBoolean("rest_alarm_state", false)
        set(value) {
            edit.putBoolean("rest_alarm_state", value)
        }

    var timeAlarmApplied: Long
        get() = pref.getLong("alarm_applied", 0)
        set(value) {
            edit.putLong("alarm_applied", value)
        }

    var timeAlarmCanceled: Long
        get() = pref.getLong("alarm_canceled", 0)
        set(value) {
            edit.putLong("alarm_canceled", value)
        }

    var timeRest: Long
        get() = pref.getLong("time_rest", 0)
        set(value) {
            edit.putLong("time_rest", value)
        }

    fun commitState() {
        edit.commit()
    }

    fun apply(timeAlarmCycle: Long, timeRest: Long) {
        val currentTime = SystemClock.elapsedRealtime()

        alarmManager.set(
                AlarmManager.ELAPSED_REALTIME,
                currentTime + timeAlarmCycle,
                restAlarm
        )

        this.timeAlarmApplied = currentTime
        this.timeRest = timeRest
        this.isAlarmEnabled = true
        commitState()
    }

    fun cancel() {
        alarmManager.cancel(restAlarm)
        this.timeAlarmCanceled = SystemClock.elapsedRealtime()
        commitState()
    }
}