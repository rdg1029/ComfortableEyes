package com.comfortable.eyes;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotiActionReceiver extends BroadcastReceiver {

    private void initState(Context context) {
        RelaxingModeState rmState = new RelaxingModeState(context);
        ProtectModeState pmState = new ProtectModeState(context);
        pmState.setNotiCount(pmState.getNotiTime());
        pmState.setNotUsingCount(pmState.getNotiTime()/5);
        rmState.setCount(pmState.getNotiTime()/5);
        rmState.setActivityPaused(false);

        pmState.commitState();
        rmState.commitState();
    }

    private void actionConfirm(Context context) {
        Toast.makeText(context, "확인 버튼 클릭됨", Toast.LENGTH_SHORT).show();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3847);
        Intent i = new Intent(context, RelaxingActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private void actionCancel(Context context) {
        Toast.makeText(context, "취소 버튼 클릭됨", Toast.LENGTH_SHORT).show();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3847);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case "com.comfortable.eyes.PM_CONFIRM":
                initState(context);
                actionConfirm(context);
                break;

            case "com.comfortable.eyes.PM_CANCEL":
                initState(context);
                ProtectModeState pmState = new ProtectModeState(context);
                pmState.setNotiCountPause(false);
                pmState.setNotUsingCountPause(false);
                pmState.commitState();
                actionCancel(context);
                break;

            case "com.comfortable.eyes.RM_CONFIRM":
                actionConfirm(context);
                break;

            case "com.comfortable.eyes.RM_CANCEL":
                //actionCancel(context);
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(3847);
                RelaxingModeState rmState = new RelaxingModeState(context);
                rmState.setInterrupted(true);
                rmState.commitState();
                Intent i = new Intent(context, RelaxingActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                break;
        }
    }
}
