package com.comfortable.eyes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeCount extends Service {

    private SharedTimeState timeState;
    private ProtectModePref pmPref;
    private Time time = new Time();
    private Thread timer;
    private ProtectModeDialog pmDialog;

    private void getTimeState() {
        timeState = new SharedTimeState(getApplicationContext());
        pmPref = new ProtectModePref(getApplicationContext());
        time = timeState.getTime();
    }

    private void setTimeValue() {
        if(time.seconds == 60) {
            time.seconds = 0;
            time.minutes++;
            if(time.minutes == 60) {
                time.minutes = 0;
                time.hour++;
            }
        }
        timeState.setTime(time);
    }

    private void timeCount() {
        getTimeState();
        Date currentTime = Calendar.getInstance().getTime();
        String currentDate = new SimpleDateFormat("dd", Locale.getDefault()).format(currentTime);
        if(pmPref.isProtectModeEnable() && !pmPref.isNotiCountPaused()) {
            int notiCount = pmPref.getNotiCountValue();
            notiCount--;
            pmPref.setNotiCountValue(notiCount);
        }
        if(currentDate.equals(timeState.getCurrentDate())) {
            time.seconds++;
            setTimeValue();
        }
        else {
            timeState.setCurrentDate(currentDate);
            timeState.resetTime(time);
        }
    }

    private void setNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("TimeCount", "UsingTime", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setVibrationPattern(new long[]{0});
            notificationChannel.enableVibration(true);
            if(notificationManager == null) return;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, "TimeCount")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("오늘의 휴대폰 사용 시간")
                .setContentText(String.format("%d시간 %d분 %d초", time.hour, time.minutes, time.seconds))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false);
        if(notiBuilder == null && notificationManager == null) return;
        startForeground(1029, notiBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    private void loopTask() {
        CheckOnUsing checkOnUsing = new CheckOnUsing(TimeCount.this);
        if(checkOnUsing.isScreenOn() || !checkOnUsing.isDeviceLock()) {
            pmPref.setNotiCountPause(false);
            getTimeState();
            if(pmPref.getNotiCountValue() <= 0) {
                pmPref.setNotiCountPause(true);
                pmPref.setNotiCount(15);
                pmPref.setNotUsingCount(15/5);
                pmDialog.sendEmptyMessage(0);
            }
            timeCount();
            setNotification();
        }
        else if(pmPref.isProtectModeEnable() && !pmPref.isNotiCountPaused()) {
            int notUsingCount = pmPref.getNotUsingCountValue();
            notUsingCount--;
            pmPref.setNotUsingCountValue(notUsingCount);
            if(pmPref.getNotUsingCountValue() <= 0) {
                pmPref.setNotiCountPause(true);
                pmPref.setNotiCount(15);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setNotification();
        pmDialog = new ProtectModeDialog(
                this,
                "눈에 휴식이 필요한 시간입니다!",
                "확인",
                "취소");
        timer = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
            @Override
            public void run() {
                while(true) {
                    loopTask();
                    try {
                        timer.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        timer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getTimeState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.interrupt();
        stopForeground(true);
    }
}
