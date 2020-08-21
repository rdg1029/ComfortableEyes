package com.comfortable.eyes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private SharedTimeState timeState;
    private Time time;
    private TextView date, onTime;

    private void getTimeState() {
        timeState = new SharedTimeState(getActivity());
        time = new Time();
        time = timeState.getTime();
    }

    private void setDisplayTimeState() {
        getTimeState();
        Date currentTime = Calendar.getInstance().getTime();

        //timeState.setCurrentDate(new SimpleDateFormat("dd", Locale.getDefault()).format(currentTime));
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        date = view.findViewById(R.id.home_date);
        onTime = view.findViewById(R.id.home_screenOnTime);
        updateTime.sendEmptyMessageDelayed(0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(updateTime != null)
            updateTime.removeMessages(0);
    }
}
