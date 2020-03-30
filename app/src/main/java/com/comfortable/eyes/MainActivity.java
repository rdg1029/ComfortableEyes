package com.comfortable.eyes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SharedTimeState timeState;
    private TextView date, onTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        date = findViewById(R.id.main_date);
        onTime = findViewById(R.id.main_screenOnTime);

        setDisplayTimeState();
    }

    private void setDisplayTimeState() {
        timeState = new SharedTimeState(getApplicationContext());
        Date currentTime = Calendar.getInstance().getTime();
        timeState.setCurrentDate(new SimpleDateFormat("dd", Locale.getDefault()).format(currentTime));
        String displayCurrentDate = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(currentTime);
        date.setText(displayCurrentDate);
        onTime.setText(String.format("%d시간 %d분 %d초", timeState.getHour(), timeState.getMinutes(), timeState.getSeconds()));
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
        timeState.setHour(0);
        timeState.setMinutes(0);
        timeState.setSeconds(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
