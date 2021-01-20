package com.comfortable.eyes.state

import android.content.Context
import android.os.SystemClock

class SharedTimeState(mContext: Context) {
    private val pref = mContext.getSharedPreferences("SharedTimeState", 0)
    private val edit = pref.edit()

    var startTime: Long
        get() = pref.getLong("time_start", 0)
        set(value) {
            edit.putLong("time_start", value)
        }

    var usedTime: Long
        get() = pref.getLong("time_used", 0)
        set(value) {
            edit.putLong("time_used", value)
        }

    var dayOfYear: Int
        get() = pref.getInt("time_day", 0)
        set(value) {
            edit.putInt("time_day", value)
        }

    fun init() {
        edit.putLong("time_used", 0)
        edit.putLong("time_start", SystemClock.elapsedRealtime())
    }

    fun commitState() {
        edit.commit()
    }
}