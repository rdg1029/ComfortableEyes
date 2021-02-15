package com.comfortable.eyes.state

import com.comfortable.eyes.AppContext.Companion.context

object RestModeState {
    private val pref = context.getSharedPreferences("RestModeState", 0)
    private val edit = pref.edit()

    var restCount: Int = pref.getInt("rest_count", 3*60000)
//        get() = pref.getInt("rest_count", 0)
//        set(value) {
//            edit.putInt("rest_count", value)
//        }

    var startTime: Long = 0
//        get() = pref.getLong("time_start", 0)
//        set(value) {
//            edit.putLong("time_start", value)
//        }

    var pausedTime: Long = 0
//        get() = pref.getLong("time_paused", 0)
//        set(value) {
//            edit.putLong("time_paused", value)
//        }

    var endTime: Long = 0
//        get() = pref.getLong("time_end", 0)
//        set(value) {
//            edit.putLong("time_end", value)
//        }

    var restAlarmClickAllowed: Boolean = true
//        get() = pref.getBoolean("rest_alarm_click", true)
//        set(value) {
//            edit.putBoolean("rest_alarm_click", value)
//        }

    var isRestMode: Boolean = false
//        get() = pref.getBoolean("rest_mode", false)
//        set(value) {
//            edit.putBoolean("rest_mode", value)
//        }

    var isRestPaused: Boolean = false
//        get() = pref.getBoolean("rest_paused", false)
//        set(value) {
//            edit.putBoolean("rest_paused", value)
//        }

    var isInterrupted: Boolean = false
//        get() = pref.getBoolean("rest_interrupted", false)
//        set(value) {
//            edit.putBoolean("rest_interrupted", value)
//        }

    fun commitState() {
        edit.commit()
    }
}