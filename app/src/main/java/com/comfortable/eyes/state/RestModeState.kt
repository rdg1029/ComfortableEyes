package com.comfortable.eyes.state

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class RestModeState(private val mContext: Context) {
    private val pref: SharedPreferences
    private val edit: Editor
    fun commitState() {
        edit.commit()
    }

    fun setCount(m: Int) {
        edit.putInt("count", m)
        edit.putInt("countValue", m * 60)
    }

    var countValue: Int
        get() = pref.getInt("countValue", 0)
        set(m) {
            edit.putInt("countValue", m)
        }
    var isActivityPaused: Boolean?
        get() = pref.getBoolean("activityPaused", false)
        set(b) {
            edit.putBoolean("activityPaused", b!!)
        }
    var isInterrupted: Boolean?
        get() = pref.getBoolean("interrupted", false)
        set(b) {
            edit.putBoolean("interrupted", b!!)
        }

    init {
        pref = mContext.getSharedPreferences("RelaxingModeState", 0)
        edit = pref.edit()
    }
}