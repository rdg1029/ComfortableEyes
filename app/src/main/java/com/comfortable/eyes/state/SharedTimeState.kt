package com.comfortable.eyes.state

import com.comfortable.eyes.AppContext.Companion.context
import android.os.SystemClock

object SharedTimeState {
    private val pref = context.getSharedPreferences("SharedTimeState", 0)
    private val edit = pref.edit()

    var startTime: Long = 0
    var usedTime: Long = pref.getLong("time_used", 0)
    var dayOfYear: Int = pref.getInt("day_of_year", 0)

    fun init() {
        usedTime = 0
        startTime = SystemClock.elapsedRealtime()
    }

    fun commitState() {
        edit.putLong("time_used", usedTime)
        edit.putInt("day_of_year", dayOfYear)
        edit.commit()
    }
}