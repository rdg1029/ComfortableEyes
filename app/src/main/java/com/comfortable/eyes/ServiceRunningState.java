package com.comfortable.eyes;

import android.content.Context;
import android.content.SharedPreferences;

public class ServiceRunningState {
    private Context mContext;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;

    public ServiceRunningState(Context context) {
        this.mContext = context;
        this.pref = context.getSharedPreferences("ServiceRunningState", 0);
        edit = pref.edit();
    }

    public void setRunningState(String name, Boolean b) {
        edit.putBoolean(name, b);
        edit.commit();
    }

    public boolean isServiceRunning(String name) {
        return pref.getBoolean(name, false);
    }
}
