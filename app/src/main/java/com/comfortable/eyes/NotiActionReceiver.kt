package com.comfortable.eyes

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotiActionReceiver : BroadcastReceiver() {
    private fun initState(context: Context) {
        val rmState = RelaxingModeState(context)
        val pmState = ProtectModeState(context)
        pmState.setNotiCount(pmState.notiTime)
        pmState.setNotUsingCount(pmState.notiTime / 5)
        rmState.setCount(pmState.notiTime / 5)
        rmState.isActivityPaused = false
        pmState.commitState()
        rmState.commitState()
    }

    private fun actionConfirm(context: Context) {
        //Toast.makeText(context, "확인 버튼 클릭됨", Toast.LENGTH_SHORT).show();
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(3847)
        val i = Intent(context, RelaxingActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(i)
    }

    private fun actionCancel(context: Context) {
        //Toast.makeText(context, "취소 버튼 클릭됨", Toast.LENGTH_SHORT).show();
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(3847)
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "com.comfortable.eyes.PM_CONFIRM" -> {
                initState(context)
                actionConfirm(context)
            }
            "com.comfortable.eyes.PM_CANCEL" -> {
                initState(context)
                val pmState = ProtectModeState(context)
                pmState.setNotiCountPause(false)
                pmState.setNotUsingCountPause(false)
                pmState.commitState()
                actionCancel(context)
            }
            "com.comfortable.eyes.RM_CONFIRM" -> actionConfirm(context)
            "com.comfortable.eyes.RM_CANCEL" -> {
                actionCancel(context)
                val rmState = RelaxingModeState(context)
                rmState.isInterrupted = true
                rmState.commitState()
                val i = Intent(context, RelaxingActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(i)
            }
        }
    }
}