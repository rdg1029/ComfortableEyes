package com.comfortable.eyes;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedTimeState {
    private Context mContext;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;
    private Time time;
    private String currentDate;

    public SharedTimeState(Context context) {
        this.mContext = context;
        time = new Time();
        pref = mContext.getSharedPreferences("SharedState", 0);
        edit = pref.edit();
        currentDate = pref.getString("currentDate", "0");
        time.hour = pref.getInt("hour", 0);
        time.minutes = pref.getInt("minutes", 0);
        time.seconds = pref.getInt("seconds", 0);
    }

    public void setTime(Time t) {
        edit.putInt("hour", t.hour);
        edit.putInt("minutes", t.minutes);
        edit.putInt("seconds", t.seconds);
        edit.commit();
    }

    public void setCurrentDate(String d) {
        edit.putString("currentDate", d);
        edit.commit();
    }

    public void resetTime(Time t) {
        t.hour = 0;
        t.minutes = 0;
        t.seconds = 0;
        setTime(t);
    }

    public Time getTime() {
        return this.time;
    }

    public String getCurrentDate() {
        return this.currentDate;
    }
}
