package com.comfortable.eyes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class RelaxingActivity extends Activity {

    public static Activity rmActivity;

    private ProtectModeState pmState;
    private RelaxingModeState rmState;
    private TextView rmTimer;
    private int count;

    private void doFullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE|
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    //네비게이션 바, 상단바가 나왔을 때 화면을 터치하면 다시 전체화면으로 전환
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            doFullScreen();
        }
        return super.dispatchTouchEvent(ev);
    }

    @SuppressLint("HandlerLeak")
    private Handler countRelaxingMode = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            count = rmState.getCountValue();
            rmTimer.setText(String.format("%s:%s", count/60 < 10 ? "0"+ count / 60 : Integer.toString(count/60), count%60 < 10 ? "0"+ count % 60 : Integer.toString(count%60)));
            if(count > 0 && !rmState.isActivityPaused()) {
                count--;
                rmState.setCountValue(count);
                countRelaxingMode.sendEmptyMessageDelayed(0, 100);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relaxing);

        rmActivity = RelaxingActivity.this;

        pmState = new ProtectModeState(this);
        rmState = new RelaxingModeState(this);
        rmTimer = findViewById(R.id.relaxing_count);
        rmState.setActivityPaused(false);
        doFullScreen();
        countRelaxingMode.sendEmptyMessageDelayed(0, 0);
    }

    @Override
    public void onBackPressed() { }

    public void finishRelaxing(View v) {
        if(count == 0) {
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "아직 종료할 수 없습니다", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(this.getClass().getName(), "onResume 실행");
        if(count > 0 && rmState.isActivityPaused()) {
            rmState.setActivityPaused(false);
            countRelaxingMode.sendEmptyMessageDelayed(0, 0);
        }
        doFullScreen();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(this.getClass().getName(), "onPause 실행");
        CheckOnUsing checkOnUsing = new CheckOnUsing(this);
        if(checkOnUsing.isScreenOn()) {
            rmState.setActivityPaused(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(this.getClass().getName(), "onStop 실행");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(count > 0 && rmState.isActivityPaused()) return;
        Log.i(this.getClass().getName(), "onDestroy 실행");
        if(countRelaxingMode != null) {
            countRelaxingMode.removeMessages(0);
        }
        pmState.setNotiCountPause(false);
        pmState.setNotUsingCountPause(false);
        rmState.setActivityPaused(false);
        pmState.setNotiCount(15);
        pmState.setNotUsingCount(15/5);
        Toast.makeText(getApplicationContext(), "휴식 모드 종료됨", Toast.LENGTH_SHORT).show();
    }
}