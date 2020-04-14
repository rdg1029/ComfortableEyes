package com.comfortable.eyes;

import android.content.Context;
import android.content.SharedPreferences;

public class ProtectModePref {
    private Context mContext;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;

    public ProtectModePref(Context context) {
        this.mContext = context;
        pref = this.mContext.getSharedPreferences("ProtectModePref", 0);
        edit = pref.edit();
    }

    public void enableProtectMode(boolean b) {
        edit.putBoolean("enablePM", b);
        edit.commit();
    }

    private void commitCountValue(int m) {
        edit.putInt("pmCount", m);
        edit.commit();
    }

    public void setCount(int m) {
        commitCountValue(m*60);
    }

    public void setProtectModeCountValue(int m) {
        commitCountValue(m);
    }

    public void setCountPause(Boolean b) {
        edit.putBoolean("PAUSE_pmCount", b);
        edit.commit();
    }

    public Boolean isProtectModeEnable() {
        return pref.getBoolean("enablePM", false);
    }

    public int getCountValue() {
        return pref.getInt("pmCount",1);
    }

    public Boolean isCountPaused() {
        return pref.getBoolean("PAUSE_pmCount", false);
    }
}
