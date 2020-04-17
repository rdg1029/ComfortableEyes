package com.comfortable.eyes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SharedTimeState timeState;
    private ProtectModeState pmState;
    private RelaxingModeState rmState;
    private Time time;
    private TextView date, onTime;

    private void getState() {
        timeState = new SharedTimeState(getApplicationContext());
        time = new Time();
        time = timeState.getTime();
    }

    private void setDisplayTimeState() {
        getState();
        Date currentTime = Calendar.getInstance().getTime();

        timeState.setCurrentDate(new SimpleDateFormat("dd", Locale.getDefault()).format(currentTime));
        String displayCurrentDate = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(currentTime);

        date.setText(displayCurrentDate);
        onTime.setText(String.format("%d시간 %d분 %d초", time.hour, time.minutes, time.seconds));
    }

    @SuppressLint("HandlerLeak")
    private Handler updateTime = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            setDisplayTimeState();
            updateTime.sendEmptyMessageDelayed(0, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pmState = new ProtectModeState(MainActivity.this);
        date = findViewById(R.id.main_date);
        onTime = findViewById(R.id.main_screenOnTime);

        updateTime.sendEmptyMessageDelayed(0, 0);

        Switch protectMode = findViewById(R.id.main_switch_protect_mode);
        protectMode.setChecked(pmState.isProtectModeEnable());
        protectMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                pmState = new ProtectModeState(MainActivity.this);
                rmState = new RelaxingModeState(MainActivity.this);
                if(isChecked) {
                    getState();
                    pmState.enableProtectMode(true);
                    pmState.setNotiCount(15);
                    pmState.setNotUsingCount(15/5);
                    rmState.setCount(15/5);
                }
                else {
                    pmState.enableProtectMode(false);
                }
            }
        });
    }

    public void start(View v) {
        startService(new Intent(this, TimeCount.class));
    }

    public void stop(View v) {
        stopService(new Intent(this, TimeCount.class));
    }

    public void reset(View v) {
        timeState.resetTime(time);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
