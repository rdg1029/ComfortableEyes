package com.comfortable.eyes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

public class NotiDialog {
    private Context mContext;
    private String dialogTextMSG, actionConfirm, actionCancel;

    private NotificationCompat.Builder notiBuilder;
    private NotificationManager notificationManager;

    public NotiDialog(Context context, String msg, String confirmAction, String cancelAction) {
        this.mContext = context;
        this.dialogTextMSG = msg;
        this.actionConfirm = confirmAction;
        this.actionCancel = cancelAction;
        setNotification();
    }

    private void setNotification() {
        Intent confirmIntent = new Intent(mContext, NotiActionReceiver.class);
        Intent cancelIntent = new Intent(mContext, NotiActionReceiver.class);
        confirmIntent.setAction(actionConfirm);
        cancelIntent.setAction(actionCancel);
        PendingIntent confirmPIntent = PendingIntent.getBroadcast(mContext, 0, confirmIntent, 0);
        PendingIntent cancelPIntent = PendingIntent.getBroadcast(mContext, 0, cancelIntent, 0);

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.noti_protect_mode);
        remoteViews.setTextViewText(R.id.pm_dialog_msg, dialogTextMSG);
        remoteViews.setOnClickPendingIntent(R.id.pm_dialog_btn_confirm, confirmPIntent);
        remoteViews.setOnClickPendingIntent(R.id.pm_dialog_btn_cancel, cancelPIntent);

        notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("NotiProtect", "NotiProtect", NotificationManager.IMPORTANCE_HIGH);
            //notificationChannel.setVibrationPattern(new long[]{0});
            notificationChannel.enableVibration(true);
            if(notificationManager == null) return;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notiBuilder = new NotificationCompat.Builder(mContext, "NotiProtect")
                .setSmallIcon(R.drawable.ic_baseline_visibility_24)
                .setContent(remoteViews)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(false);
    }

    public void displayNotification() {
        if(notiBuilder == null && notificationManager == null) return;
        notificationManager.notify(3847, notiBuilder.build());
    }

}
