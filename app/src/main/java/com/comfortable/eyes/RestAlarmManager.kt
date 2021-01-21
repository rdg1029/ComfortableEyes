package com.comfortable.eyes

import android.app.AlarmManager
import android.content.Context
import android.os.SystemClock

class RestAlarmManager(context: Context) {
    val mContext = context

    val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val pref = mContext.getSharedPreferences("RestAlarmManager", 0)
    val edit = pref.edit()

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

        this.timeAlarmApplied = currentTime
        this.timeRest = timeRest
        this.isAlarmEnabled = true
        commitState()
    }

    fun cancel() {
        this.timeAlarmCanceled = SystemClock.elapsedRealtime()
        commitState()
    }
}