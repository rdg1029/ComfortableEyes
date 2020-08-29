package com.comfortable.eyes;

import android.content.Context;
import android.content.SharedPreferences;

public class RelaxingModeState {
    private Context mContext;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;

    public RelaxingModeState(Context context) {
        this.mContext = context;
        this.pref = this.mContext.getSharedPreferences("RelaxingModeState", 0);
        edit = pref.edit();
    }

    public void commitState() {
        edit.commit();
    }

    public void setCount(int m) {
        edit.putInt("count", m);
        edit.putInt("countValue", m*10);
    }

    public void setCountValue(int m) {
        edit.putInt("countValue", m);
    }

    public void setActivityPaused(Boolean b) {
        edit.putBoolean("activityPaused", b);
    }

    public void setInterrupted(Boolean b) {
        edit.putBoolean("interrupted", b);
    }

    public int getCountValue() {
        return pref.getInt("countValue", 0);
    }

    public boolean isActivityPaused() {
        return pref.getBoolean("activityPaused", false);
    }

    public Boolean isInterrupted() {
        return pref.getBoolean("interrupted", false);
    }
}
