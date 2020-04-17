package com.comfortable.eyes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RelaxingActivity extends Activity {

    private ProtectModeState pmPref;
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

    @SuppressLint("HandlerLeak")
    private Handler countRelaxingMode = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            count = rmState.getCountValue();
            rmTimer.setText(String.format("%s:%s", count/60 < 10 ? "0"+ count / 60 : Integer.toString(count/60), count%60 < 10 ? "0"+ count % 60 : Integer.toString(count%60)));
            if(count != 0) {
                count--;
                rmState.setCountValue(count);
                countRelaxingMode.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relaxing);

        pmPref = new ProtectModeState(this);
        rmState = new RelaxingModeState(this);
        rmTimer = findViewById(R.id.relaxing_count);
        doFullScreen();
        countRelaxingMode.sendEmptyMessageDelayed(0, 0);
    }

    public void finishRelaxing(View v) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doFullScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countRelaxingMode != null) {
            countRelaxingMode.removeMessages(0);
        }
        pmPref.setNotiCountPause(false);
        Toast.makeText(getApplicationContext(), "휴식 모드 종료됨", Toast.LENGTH_SHORT).show();
    }
}
