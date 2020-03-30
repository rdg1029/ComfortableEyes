package com.comfortable.eyes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeCount extends Service {

    private SharedTimeState timeState;
    private Time time = new Time();
    private Thread timer;
    private String currentDate;

    private void setTimeValue() {
        if(time.seconds == 60) {
            time.seconds = 0;
            time.minutes++;
            if(time.minutes == 60) {
                time.minutes = 0;
                time.hour++;
            }
        }
        timeState.setHour(time.hour);
        timeState.setMinutes(time.minutes);
        timeState.setSeconds(time.seconds);
    }

    private void timeCount() {
        Date currentTime = Calendar.getInstance().getTime();
        currentDate = new SimpleDateFormat("dd", Locale.getDefault()).format(currentTime);
        if(!currentDate.equals(timeState.getCurrentDate())) {
            timeState.setCurrentDate(currentDate);
            time.hour = 0;
            time.minutes = 0;
            time.seconds = 0;
        }
        time.seconds++;
        setTimeValue();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    timeCount();
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
        timeState = new SharedTimeState(getApplicationContext());
        time.hour = timeState.getHour();
        time.minutes = timeState.getMinutes();
        time.seconds = timeState.getSeconds();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.interrupt();
    }
}
