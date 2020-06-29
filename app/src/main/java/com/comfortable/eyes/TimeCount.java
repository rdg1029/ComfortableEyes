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
    private ProtectModeState pmState;
    private RelaxingModeState rmState;
    private Time time = new Time();
    private Thread timer;
    private ProtectModeDialog pmDialog;
    private boolean isCount;

    private void getState() {
        timeState = new SharedTimeState(getApplicationContext());
        pmState = new ProtectModeState(getApplicationContext());
        rmState = new RelaxingModeState(getApplicationContext());
        time = timeState.getTime();
    }

    private void taskOnUsing() {
        if(pmState.getNotiCountValue() <= 0) {
            pmState.setNotiCountPause(true);
            //pmState.setNotiCount(15);
            //pmState.setNotUsingCount(15/5); // -> move to NotiActionReceiver.class
            //rmState.setCount(15/5);
            pmDialog.sendEmptyMessage(0); //다이얼로그 표시 -> 헤드업 노티피케이션 표시
        }
        else if(pmState.getNotUsingCountValue() <= 0) {
            pmState.setNotiCountPause(false);
        }
        if(pmState.isProtectModeEnable() && !pmState.isNotiCountPaused()) {
            int notiCount = pmState.getNotiCountValue();
            notiCount--;
            pmState.setNotiCountValue(notiCount);
        }
    }

    private void taskNotUsing() {
        if(pmState.getNotUsingCountValue() > 0) {
            int notiCount = pmState.getNotiCountValue();
            notiCount++;
            pmState.setNotiCountValue(notiCount);
        }
        else if(pmState.getNotUsingCountValue() <= 0){
            pmState.setNotiCountPause(true);
            pmState.setNotiCount(15);
        }
        if(pmState.isProtectModeEnable() && !pmState.isNotiCountPaused()) {
            int notUsingCount = pmState.getNotUsingCountValue();
            notUsingCount--;
            pmState.setNotUsingCountValue(notUsingCount);
        }
    }

    private void setTimeValue() {
        time.seconds++;
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
        getState();
        Date currentTime = Calendar.getInstance().getTime();
        String currentDate = new SimpleDateFormat("dd", Locale.getDefault()).format(currentTime);
        setTimeValue();
        if(!currentDate.equals(timeState.getCurrentDate())) {
            timeState.setCurrentDate(currentDate);
            timeState.resetTime(time);
        }
    }

    private void setNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("TimeCount", "UsingTime", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setVibrationPattern(new long[]{0});
            notificationChannel.enableVibration(true);
            if(notificationManager == null) return;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, "TimeCount")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("오늘의 휴대폰 사용 시간")
                .setContentText(String.format("%d시간 %d분 %d초", time.hour, time.minutes, time.seconds))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(false);
        if(notiBuilder == null && notificationManager == null) return;
        startForeground(1029, notiBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    private void loopTask() {
        CheckOnUsing checkOnUsing = new CheckOnUsing(TimeCount.this);
        if(checkOnUsing.isScreenOn() || !checkOnUsing.isDeviceLock()) {
            getState();
            taskOnUsing(); //화면 사용 중 처리하는 작업
            timeCount();
            setNotification();
        }
        else {
            taskNotUsing(); //화면 사용X 일 때 처리하는 작업
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isCount = true;
        setNotification();
        timer = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
            @Override
            public void run() {
                while(isCount) {
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
        getState();
        pmState.setNotiCount(15);
        pmState.setNotUsingCount(15/5);
        rmState.setCount(15/5);
        pmDialog = new ProtectModeDialog(
                this,
                "눈에 휴식이 필요한 시간입니다!",
                "확인",
                "취소"
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCount = false;
        timer.interrupt();
        stopForeground(true);
    }
}
