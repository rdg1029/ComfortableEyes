package com.comfortable.eyes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.comfortable.eyes.RestAlarmManager
import com.comfortable.eyes.activity.RestActivity
import com.comfortable.eyes.service.RestAlarm
import com.comfortable.eyes.state.RestModeState

class NotiActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmService = Intent(context, RestAlarm::class.java)
        val restActivity = Intent(context, RestActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (!RestModeState.restAlarmClickAllowed) return
        when (intent.action) {
            "com.comfortable.eyes.PM_CONFIRM" -> {
                context.stopService(alarmService)

                RestModeState.isRestMode = true
                RestModeState.isRestPaused = false
                RestModeState.isInterrupted = false

                context.startActivity(restActivity)

                Toast.makeText(context, "확인 버튼 클릭됨", Toast.LENGTH_SHORT).show()
            }
            "com.comfortable.eyes.PM_CANCEL" -> {
                context.stopService(alarmService)
                RestAlarmManager.cancel()
                RestAlarmManager.apply(RestAlarmManager.timeAlarmCycle, RestAlarmManager.timeRest, true)
                Toast.makeText(context, "취소 버튼 클릭됨", Toast.LENGTH_SHORT).show()
            }
            "com.comfortable.eyes.RM_CONFIRM" -> {
                context.startActivity(restActivity)
            }
            "com.comfortable.eyes.RM_CANCEL" -> {
                RestModeState.isInterrupted = true

                context.startActivity(restActivity)
            }
        }
        RestModeState.restAlarmClickAllowed = false
//        RestModeState.commitState()
    }
}