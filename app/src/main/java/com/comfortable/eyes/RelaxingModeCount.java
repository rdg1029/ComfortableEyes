package com.comfortable.eyes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class RelaxingModeCount extends Service {

    private Thread timer;
    private RelaxingModeState rmState;
    private int count;

    private NotificationManager notificationManager;

    private void setNotificationChannel() {
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("RelaxingModeCount", "RelaxingMode", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setVibrationPattern(new long[]{0});
            notificationChannel.enableVibration(true);
            if(notificationManager == null) return;
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void updateNotification() {
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, "RelaxingModeCount")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("휴식 시간")
                .setContentText(String.format(String.format("%s:%s", count/60 < 10 ? "0"+ count / 60 : Integer.toString(count/60), count%60 < 10 ? "0"+ count % 60 : Integer.toString(count%60))))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(false);
        if(notiBuilder == null && notificationManager == null) return;
        startForeground(4756, notiBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Thread(new Runnable() {
            @Override
            public void run() {
                while(count > 0) {
                    updateNotification();
                    try {
                        timer.sleep(1000);
                    } catch (InterruptedException e) {}
                }
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rmState = new RelaxingModeState(this);
        count = rmState.getCountValue();
        setNotificationChannel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
