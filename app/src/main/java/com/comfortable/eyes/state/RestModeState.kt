package com.comfortable.eyes.state

import android.content.Context

class RestModeState(mContext: Context) {
    private val pref = mContext.getSharedPreferences("RestModeState", 0)
    private val edit = pref.edit()

    var restCount: Int
        get() = pref.getInt("rest_count", 0)
        set(value) {
            edit.putInt("rest_count", value)
        }

    var startTime: Long
        get() = pref.getLong("time_start", 0)
        set(value) {
            edit.putLong("time_start", value)
        }

    var pausedTime: Long
        get() = pref.getLong("time_paused", 0)
        set(value) {
            edit.putLong("time_paused", value)
        }

    var endTime: Long
        get() = pref.getLong("time_end", 0)
        set(value) {
            edit.putLong("time_end", value)
        }

    fun commitState() {
        edit.commit()
    }
}