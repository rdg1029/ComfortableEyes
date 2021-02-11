package com.comfortable.eyes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.comfortable.eyes.TimeNotification
import com.comfortable.eyes.state.SharedTimeState
import java.util.*

class DateChangedReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val timeNoti = context?.let { TimeNotification(it) }!!

        SharedTimeState.dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        SharedTimeState.init()
        SharedTimeState.commitState()
        timeNoti.updateNotification()
    }
}