package com.comfortable.eyes;

import android.content.Context;
import android.content.SharedPreferences;

public class ProtectModeState {
    private Context mContext;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;

    public ProtectModeState(Context context) {
        this.mContext = context;
        pref = this.mContext.getSharedPreferences("ProtectModeState", 0);
        edit = pref.edit();
    }

    public void commitState() {
        edit.commit();
    }

    public void enableProtectMode(boolean b) {
        edit.putBoolean("enableNoti", b);
    }

    public void setNotiCount(int m) {
        edit.putInt("notiTime", m);
        edit.putInt("notiCount", m*1);
    }

    public void setNotUsingCount(int m) {
        edit.putInt("notUsingCount", m*1);
    }

    public void setNotiCountValue(int m) {
        edit.putInt("notiCount", m);
    }

    public void setNotUsingCountValue(int m) {
        edit.putInt("notUsingCount", m);
    }

    public void setNotiCountPause(Boolean b) {
        edit.putBoolean("PAUSE_notiCount", b);
    }

    public void setNotUsingCountPause(Boolean b) {
        edit.putBoolean("PAUSE_notUsingCount", b);
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

    public int getNotiTime() {
        return pref.getInt("notiTime", 15);
    }

    public Boolean isNotiCountPaused() {
        return pref.getBoolean("PAUSE_notiCount", false);
    }

    public Boolean isNotUsingCountPaused() {
        return pref.getBoolean("PAUSE_notUsingCount", false);
    }
}
