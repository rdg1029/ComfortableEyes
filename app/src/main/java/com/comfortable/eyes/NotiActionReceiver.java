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
        pmState.setNotiCount(15);
        pmState.setNotUsingCount(15/5);
        rmState.setCount(15/5);
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
        RelaxingModeState rmState = new RelaxingModeState(context);
        ProtectModeState pmState = new ProtectModeState(context);
        pmState.setNotiCount(15);
        pmState.setNotUsingCount(15/5);
        rmState.setCount(15/5);
        pmState.setNotiCountPause(false);
        pmState.setNotUsingCountPause(false);
        rmState.setActivityPaused(false);
        Toast.makeText(context, "취소 버튼 클릭됨", Toast.LENGTH_SHORT).show();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3847);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("PM_CONFIRM")) {
            initState(context);
            actionConfirm(context);
        }
        else if(intent.getAction().equals("PM_CANCEL")) {
            actionCancel(context);
        }
        else if(intent.getAction().equals("RM_CONFIRM")) {
            actionConfirm(context);
        }
        else if(intent.getAction().equals("RM_CANCEL")) {
            actionCancel(context);
            RelaxingActivity rmActivity = (RelaxingActivity)RelaxingActivity.rmActivity;
            rmActivity.finish();
        }
    }
}
