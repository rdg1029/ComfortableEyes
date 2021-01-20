package com.comfortable.eyes.state

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.comfortable.eyes.Time

class SharedTimeState(private val mContext: Context) {
    private val pref: SharedPreferences
    private val edit: Editor
    private val time: Time
    private val currentDate: String?
    fun commitState() {
        edit.commit()
    }

    fun setTime(t: Time) {
        edit.putInt("hour", t.hour)
        edit.putInt("minutes", t.minutes)
        edit.putInt("seconds", t.seconds)
    }

    fun setCurrentDate(d: String?) {
        edit.putString("currentDate", d)
    }

    fun resetTime(t: Time) {
        t.hour = 0
        t.minutes = 0
        t.seconds = 0
        setTime(t)
    }

    fun getTime(): Time {
        return time
    }

    fun getCurrentDate(): String? {
        return currentDate
    }

    init {
        time = Time()
        pref = mContext.getSharedPreferences("SharedState", 0)
        edit = pref.edit()
        currentDate = pref.getString("currentDate", "0")
        time.hour = pref.getInt("hour", 0)
        time.minutes = pref.getInt("minutes", 0)
        time.seconds = pref.getInt("seconds", 0)
    }
}