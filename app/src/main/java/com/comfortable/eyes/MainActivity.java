package com.comfortable.eyes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private SettingsFragment settingsFragment;
    private FragmentTransaction fragmentTransaction;
    private TextView menuName;

    private void checkService() {
        ServiceRunningState serviceRunningState = new ServiceRunningState(this);
        if(!serviceRunningState.isServiceRunning("TimeCount")) { //TimeCount가 실행 중이 아니면 서비스 실행
            Log.i(this.getClass().getName(), "TimeCount 서비스 실행 여부 : " + serviceRunningState.isServiceRunning("TimeCount"));
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(new Intent(this, TimeCount.class));
            else
                startService(new Intent(this, TimeCount.class));
        }
    }

    private void init() {
        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        settingsFragment = new SettingsFragment();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frameLayout, homeFragment).commitAllowingStateLoss();

        menuName = findViewById(R.id.main_menu_name);
        menuName.setText("오늘의 휴대폰 사용 시간");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkService();
        init();

    }

    public void menuClick(View v) {
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (v.getId()) {
            case R.id.main_btn_home:
                menuName.setText("오늘의 휴대폰 사용 시간");
                fragmentTransaction.replace(R.id.main_frameLayout, homeFragment).commitAllowingStateLoss();
                break;

            case R.id.main_btn_settings:
                menuName.setText("설정");
                fragmentTransaction.replace(R.id.main_frameLayout, settingsFragment).commitAllowingStateLoss();
                break;
        }
    }
/*
    public void start(View v) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(new Intent(this, TimeCount.class));
        else
            startService(new Intent(this, TimeCount.class));
    }

    public void stop(View v) {
        stopService(new Intent(this, TimeCount.class));
    }

    public void reset(View v) {
        timeState.resetTime(time);
    }
*/
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
