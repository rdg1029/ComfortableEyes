package com.comfortable.eyes;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class RelaxingActivity extends Activity {

    ProtectModePref pmPref;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relaxing);

        pmPref = new ProtectModePref(this);
        doFullScreen();
    }

    public void finishRelaxing(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pmPref.setNotiCountPause(false);
        Toast.makeText(getApplicationContext(), "휴식 모드 종료됨", Toast.LENGTH_SHORT).show();
    }
}
