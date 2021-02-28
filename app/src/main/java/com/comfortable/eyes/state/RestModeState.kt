package com.comfortable.eyes.state

import com.comfortable.eyes.AppContext.Companion.context

object RestModeState {
    private val pref = context.getSharedPreferences("RestModeState", 0)
    private val edit = pref.edit()

    var restCount: Int = pref.getInt("rest_count", 3*60000)

    var startTime: Long = 0

    var pausedTime: Long = 0

    var endTime: Long = 0

    var restAlarmClickAllowed: Boolean = true

    var isRestMode: Boolean = false

    var isRestPaused: Boolean = false

    var isInterrupted: Boolean = false

    fun commitState() {
        edit.putInt("rest_count", restCount)
        edit.commit()
    }
}