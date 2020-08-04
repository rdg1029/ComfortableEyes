package com.comfortable.eyes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class RelaxingModeCount extends Service {

    private Thread timer;
    private RelaxingModeState rmState;
    private NotiDialog rmDialog;
    private int count;

    private NotificationManager notificationManager;

    private PowerManager.WakeLock wakeLock;

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    private void loopTask() {
        CheckOnUsing checkOnUsing = new CheckOnUsing(this);
        if(!rmState.isActivityPaused()) {
            count--;
            rmState.setCountValue(count);
            if(rmState.getCountValue() == 0) {
                wakeLock.release();
            }
        }
        else if(rmState.isActivityPaused() && checkOnUsing.isScreenOn()) {
            rmDialog.displayNotification(); //휴식모드 진행 중 다른 화면으로 나가면 헤드업 표시
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock");
        wakeLock.acquire();
        timer = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
            @Override
            public void run() {
                while(count > 0) {
                    updateNotification();
                    loopTask();
                    try {
                        timer.sleep(1000);
                    } catch (InterruptedException e) {}
                }
            }
        });
        timer.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rmState = new RelaxingModeState(this);
        rmDialog = new NotiDialog(this, "휴식을 계속 진행하시겠습니까?", "RM_CONFIRM", "RM_CANCEL");
        count = rmState.getCountValue();
        setNotificationChannel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        timer.interrupt();
        wakeLock.release();
    }
}
