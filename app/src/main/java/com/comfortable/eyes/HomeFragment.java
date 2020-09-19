package com.comfortable.eyes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private TextView year, month, day, hour, minutes, seconds, pmStateTextView;
    private ImageView pmStateImg;

    private void getTimeState() {
        timeState = new SharedTimeState(getActivity());
        time = new Time();
        time = timeState.getTime();
    }

    private void setDisplayTimeState() {
        getTimeState();
        Date currentTime = Calendar.getInstance().getTime();

        //timeState.setCurrentDate(new SimpleDateFormat("dd", Locale.getDefault()).format(currentTime));
        //String displayCurrentDate = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(currentTime);

        year.setText(new SimpleDateFormat("yyyy", Locale.getDefault()).format(currentTime));
        month.setText(new SimpleDateFormat("MM", Locale.getDefault()).format(currentTime));
        day.setText(new SimpleDateFormat("dd", Locale.getDefault()).format(currentTime));

        hour.setText(String.format("%d", time.hour));
        minutes.setText(String.format("%d", time.minutes));
        seconds.setText(String.format("%d", time.seconds));
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
        year = view.findViewById(R.id.home_date_year);
        month = view.findViewById(R.id.home_date_month);
        day = view.findViewById(R.id.home_date_day);

        hour = view.findViewById(R.id.home_tv_hour);
        minutes = view.findViewById(R.id.home_tv_minutes);
        seconds = view.findViewById(R.id.home_tv_seconds);

        pmStateTextView = view.findViewById(R.id.home_tv_pm_state);
        pmStateImg = view.findViewById(R.id.home_img_pm_state);
        updateTime.sendEmptyMessageDelayed(0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        ProtectModeState pmState = new ProtectModeState(getActivity());
        if(pmState.isProtectModeEnable()) {
            pmStateTextView.setText("보호 기능 사용 중");
            pmStateTextView.setAlpha(1.0f);
            pmStateImg.setAlpha(1.0f);
        }
        else {
            pmStateTextView.setText("보호 기능 중지됨");
            pmStateTextView.setAlpha(0.5f);
            pmStateImg.setAlpha(0.4f);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(updateTime != null)
            updateTime.removeMessages(0);
    }
}
