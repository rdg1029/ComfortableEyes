package com.comfortable.eyes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

    private void interruptedDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.ActivityDialogStyle)
                .setMessage("휴식을 종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        rmState.setInterrupted(false);
                        rmState.setCountValue(0);
                        count = 0;
                        rmState.setActivityPaused(false);
                        finish();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        rmState.setInterrupted(false);
                        rmState.setActivityPaused(false);
                    }
                });
        dialog.create();
        dialog.show();
    }

    @SuppressLint("HandlerLeak")
    private Handler countRelaxingMode = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            count = rmState.getCountValue();
            rmTimer.setText(String.format("%s:%s", count/60 < 10 ? "0"+ count / 60 : Integer.toString(count/60), count%60 < 10 ? "0"+ count % 60 : Integer.toString(count%60)));
            countRelaxingMode.sendEmptyMessageDelayed(0, 1000);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(this.getClass().getName(), "onCreate 실행");
        setContentView(R.layout.activity_relaxing);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3847);

        pmState = new ProtectModeState(this);
        rmState = new RelaxingModeState(this);
        rmTimer = findViewById(R.id.relaxing_count);

        doFullScreen();

        stopService(new Intent(this, TimeCount.class));
        if(!rmState.isActivityPaused()) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(new Intent(this, RelaxingModeCount.class));
            else
                startService(new Intent(this, RelaxingModeCount.class));
        }

        if(rmState.isInterrupted()) {
            Log.i(this.getClass().getName(), "강제 중지 다이얼로그");
            interruptedDialog();
        }
        else {
            rmState.setActivityPaused(false);
        }

        count = rmState.getCountValue();
        rmTimer.setText(String.format("%s:%s", count/60 < 10 ? "0"+ count / 60 : Integer.toString(count/60), count%60 < 10 ? "0"+ count % 60 : Integer.toString(count%60)));
        //countRelaxingMode.sendEmptyMessageDelayed(0, 0);
    }

    @Override
    public void onBackPressed() { }

    public void finishRelaxing(View v) {
        count = rmState.getCountValue();
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
        count = rmState.getCountValue();
        if(count > 0 && rmState.isActivityPaused()) {
            rmState.setActivityPaused(false);
        }
        if(countRelaxingMode != null) {
            countRelaxingMode.removeMessages(0);
            countRelaxingMode.sendEmptyMessage(0);
        }
        doFullScreen();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(this.getClass().getName(), "onPause 실행");
        if(countRelaxingMode != null)
            countRelaxingMode.removeMessages(0);
        CheckOnUsing checkOnUsing = new CheckOnUsing(this);
        if(checkOnUsing.isScreenOn()) {
            rmState.setActivityPaused(true);
            finish();
        }
    }
    /*
        @Override
        protected void onStop() {
            super.onStop();
            Log.i(this.getClass().getName(), "onStop 실행");
        }
    */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(this.getClass().getName(), "onDestroy 실행");
        count = rmState.getCountValue();
        if(count > 0 && rmState.isActivityPaused()) return;
        Log.i(this.getClass().getName(), "onDestroy 실행(완전 종료)");
        //countRelaxingMode.removeMessages(0);
        /*
        if(countRelaxingMode != null) {
            countRelaxingMode.removeMessages(0);
        }
        */
        stopService(new Intent(this, RelaxingModeCount.class));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(new Intent(this, TimeCount.class));
        else
            startService(new Intent(this, TimeCount.class));
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3847);
        pmState.setNotiCountPause(false);
        pmState.setNotUsingCountPause(false);
        rmState.setActivityPaused(false);
        pmState.setNotiCount(pmState.getNotiTime());
        pmState.setNotUsingCount(pmState.getNotiTime()/5);
        Toast.makeText(getApplicationContext(), "휴식 모드 종료됨", Toast.LENGTH_SHORT).show();
    }
}