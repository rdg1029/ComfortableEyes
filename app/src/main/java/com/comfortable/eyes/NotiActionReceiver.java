package com.comfortable.eyes;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotiActionReceiver extends BroadcastReceiver {

    private void initState(Context context) {
        ProtectModeState pmState = new ProtectModeState(context);
        RelaxingModeState rmState = new RelaxingModeState(context);
        pmState.setNotiCount(15);
        pmState.setNotUsingCount(15/5);
        rmState.setCount(15/5);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        initState(context);

        if(intent.getAction().equals("NOTI_CONFIRM")) {
            Toast.makeText(context, "확인 버튼 클릭됨", Toast.LENGTH_SHORT).show();
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(3847);
            Intent i = new Intent(context, RelaxingActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        else if(intent.getAction().equals("NOTI_CANCEL")) {
            ProtectModeState pmPref = new ProtectModeState(context);
            pmPref.setNotiCountPause(false);
            Toast.makeText(context, "취소 버튼 클릭됨", Toast.LENGTH_SHORT).show();
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(3847);
        }
    }
}
