package com.comfortable.eyes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private SharedTimeState timeState;
    private int hour, minutes, seconds;
    TextView onTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeState = new SharedTimeState(getApplicationContext());
        hour = timeState.getHour();
        minutes = timeState.getMinutes();
        seconds = timeState.getSeconds();

        onTime = findViewById(R.id.main_screenOnTime);
        onTime.setText(String.format("%d시간 %d분 %d초", hour, minutes, seconds));
    }

    public void start(View v) {
        seconds++;
        setTimeValue();
        onTime.setText(String.format("%d시간 %d분 %d초", hour, minutes, seconds));
    }

    private void setTimeValue() {
        if(seconds == 60) {
            seconds = 0;
            minutes++;
            if(minutes == 60) {
                minutes = 0;
                hour++;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        timeState.setHour(hour);
        timeState.setMinutes(minutes);
        timeState.setSeconds(seconds);
    }
}
