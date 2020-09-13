package com.comfortable.eyes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private SettingsFragment settingsFragment;
    private EmergencyFragment emergencyFragment;
    private FragmentTransaction fragmentTransaction;
    private TextView menuName;
    private ImageButton btnHome, btnSettings, btnEmergency;

    private void restartService() {
        ProtectModeState pmState = new ProtectModeState(this);
        if(pmState.isNotiCountPaused()) return;
        stopService(new Intent(this, TimeCount.class));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(new Intent(this, TimeCount.class));
        else
            startService(new Intent(this, TimeCount.class));
    }

    private void animationBackgroundState(boolean isProtectModeEnable) {
        ConstraintLayout mainLayout = findViewById(R.id.main_layout);
        if(isProtectModeEnable) {
            mainLayout.setBackground(getResources().getDrawable(R.drawable.bg_main_protect_on));
            AnimationDrawable mainAnim = (AnimationDrawable)mainLayout.getBackground();
            mainAnim.setEnterFadeDuration(0);
            mainAnim.setExitFadeDuration(700);
            mainAnim.start();
        }
        else {
            mainLayout.setBackground(getResources().getDrawable(R.drawable.bg_anim_protect_off));
        }
    }

    private void init() {
        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        settingsFragment = new SettingsFragment();
        emergencyFragment = new EmergencyFragment();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frameLayout, homeFragment).commitAllowingStateLoss();

        ProtectModeState pmState = new ProtectModeState(this);
        animationBackgroundState(pmState.isProtectModeEnable());

        menuName = findViewById(R.id.main_menu_name);
        menuName.setText("오늘의 휴대폰 사용 시간");

        btnHome = findViewById(R.id.main_btn_home);
        btnSettings = findViewById(R.id.main_btn_settings);
        btnEmergency = findViewById(R.id.main_btn_emergency);
        btnSettings.setAlpha(0.5f);
        btnEmergency.setAlpha(0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restartService();
        init();

    }

    public void menuClick(View v) {
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (v.getId()) {
            case R.id.main_btn_home:
                menuName.setText("오늘의 휴대폰 사용 시간");
                fragmentTransaction.replace(R.id.main_frameLayout, homeFragment).commitAllowingStateLoss();
                btnHome.setAlpha(1.0f);
                btnSettings.setAlpha(0.5f);
                btnEmergency.setAlpha(0.5f);
                break;

            case R.id.main_btn_settings:
                menuName.setText("설정");
                fragmentTransaction.replace(R.id.main_frameLayout, settingsFragment).commitAllowingStateLoss();
                btnHome.setAlpha(0.5f);
                btnSettings.setAlpha(1.0f);
                btnEmergency.setAlpha(0.5f);
                break;

            case R.id.main_btn_emergency:
                menuName.setText("문제 발생");
                fragmentTransaction.replace(R.id.main_frameLayout, emergencyFragment).commitAllowingStateLoss();
                btnHome.setAlpha(0.5f);
                btnSettings.setAlpha(0.5f);
                btnEmergency.setAlpha(1.0f);
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
