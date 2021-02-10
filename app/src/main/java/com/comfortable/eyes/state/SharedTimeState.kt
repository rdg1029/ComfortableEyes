package com.comfortable.eyes.state

import android.content.Context
import android.os.SystemClock

class SharedTimeState(mContext: Context) {
    private val pref = mContext.getSharedPreferences("SharedTimeState", 0)
    private val edit = pref.edit()

    companion object {
        var startTime: Long = 0
        var usedTime: Long = 0
        var dayOfYear: Int = 0
    }

//    fun getSavedStartTime() {
//        startTime = pref.getLong("time_start", 0)
//    }

    fun getSavedUsedTime() {
        usedTime = pref.getLong("time_used", 0)
    }

    fun getSavedDayOfYear() {
        dayOfYear = pref.getInt("day_of_year", 0)
    }

    fun init() {
        usedTime = 0
        startTime = SystemClock.elapsedRealtime()
//        edit.putLong("time_used", 0)
//        edit.putLong("time_start", SystemClock.elapsedRealtime())
    }

    fun commitState() {
        edit.putLong("time_start", startTime)
        edit.putLong("time_used", usedTime)
        edit.putInt("day_of_year", dayOfYear)
        edit.commit()
    }
}