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
        edit.putBoolean("enableNoti", b);
        edit.commit();
    }

    private void commitCountValue(int m, String key) {
        edit.putInt(key, m);
        edit.commit();
    }

    public void setNotiCount(int m) {
        commitCountValue(m*1, "notiCount");
    }

    public void setNotUsingCount(int m) {
        commitCountValue(m*1, "notUsingCount");
    }

    public void setNotiCountValue(int m) {
        commitCountValue(m, "notiCount");
    }

    public void setNotUsingCountValue(int m) {
        commitCountValue(m, "notUsingCount");
    }

    public void setNotiCountPause(Boolean b) {
        edit.putBoolean("PAUSE_notiCount", b);
        edit.commit();
    }

    public Boolean isProtectModeEnable() {
        return pref.getBoolean("enableNoti", false);
    }

    public int getNotiCountValue() {
        return pref.getInt("notiCount",1);
    }

    public int getNotUsingCountValue() {
        return pref.getInt("notUsingCount", 1);
    }

    public Boolean isNotiCountPaused() {
        return pref.getBoolean("PAUSE_notiCount", false);
    }
}
