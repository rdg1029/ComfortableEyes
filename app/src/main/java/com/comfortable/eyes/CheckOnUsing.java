package com.comfortable.eyes;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.RequiresApi;

public class CheckOnUsing {
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public boolean isScreenOn() {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isInteractive();
    }

    public boolean isDeviceLock() {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.isKeyguardLocked();
    }
}
