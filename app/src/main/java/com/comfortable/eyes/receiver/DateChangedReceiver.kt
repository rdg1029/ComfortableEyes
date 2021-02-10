package com.comfortable.eyes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.comfortable.eyes.TimeNotification
import com.comfortable.eyes.state.SharedTimeState
import java.util.*

class DateChangedReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val sharedTimeState = context?.let { SharedTimeState(it) }!!
        val timeNoti = TimeNotification(context)

        SharedTimeState.dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        sharedTimeState.init()
        sharedTimeState.commitState()
        timeNoti.updateNotification()
    }
}