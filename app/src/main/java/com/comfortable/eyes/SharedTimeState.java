package com.comfortable.eyes;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedTimeState {
    private Context mContext;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;
    private int hour, minutes, seconds;
    private String currentDate;

    public SharedTimeState(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences("SharedState", 0);
        edit = pref.edit();
        currentDate = pref.getString("currentDate", "0");
        hour = pref.getInt("hour", 0);
        minutes = pref.getInt("minutes", 0);
        seconds = pref.getInt("seconds", 0);
    }

    public void setCurrentDate(String d) {
        edit.putString("currentDate", d);
        edit.commit();
    }

    public void setHour(int h) {
        edit.putInt("hour", h);
        edit.commit();
    }

    public void setMinutes(int m) {
        edit.putInt("minutes", m);
        edit.commit();
    }

    public void setSeconds(int s) {
        edit.putInt("seconds", s);
        edit.commit();
    }

    public String getCurrentDate() {
        return this.currentDate;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinutes() {
        return this.minutes;
    }

    public int getSeconds() {
        return this.seconds;
    }
}
