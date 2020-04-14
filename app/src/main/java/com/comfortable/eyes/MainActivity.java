package com.comfortable.eyes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    private ProtectModePref pmPref;
    private Time time;
    private TextView date, onTime;

    private void getTimeState() {
        timeState = new SharedTimeState(getApplicationContext());
        time = new Time();
        time = timeState.getTime();
    }

    private void setDisplayTimeState() {
        getTimeState();
        Date currentTime = Calendar.getInstance().getTime();

        timeState.setCurrentDate(new SimpleDateFormat("dd", Locale.getDefault()).format(currentTime));
        String displayCurrentDate = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(currentTime);

        date.setText(displayCurrentDate);
        onTime.setText(String.format("%d시간 %d분 %d초", time.hour, time.minutes, time.seconds));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pmPref = new ProtectModePref(MainActivity.this);
        date = findViewById(R.id.main_date);
        onTime = findViewById(R.id.main_screenOnTime);

        setDisplayTimeState();

        Switch protectMode = findViewById(R.id.main_switch_protect_mode);
        protectMode.setChecked(pmPref.isProtectModeEnable());
        protectMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                pmPref = new ProtectModePref(MainActivity.this);
                if(isChecked) {
                    getTimeState();
                    pmPref.enableProtectMode(true);
                    pmPref.setCount(15);
                }
                else {
                    pmPref.enableProtectMode(false);
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

    public void refresh(View v) {
        setDisplayTimeState();
    }

    public void reset(View v) {
        timeState.resetTime(time);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
