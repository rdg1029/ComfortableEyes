package com.comfortable.eyes;

import android.content.Context;
import android.content.SharedPreferences;

public class ProtectModePref {
    private Context mContext;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;
    private Boolean enablePM;
    private Time time;

    public ProtectModePref(Context context) {
        this.mContext = context;
        time = new Time();
        pref = this.mContext.getSharedPreferences("ProtectModePref", 0);
        edit = pref.edit();
    }

    public void enableProtectMode(boolean b) {
        enablePM = b;
        edit.putBoolean("enablePM", enablePM);
        edit.commit();
    }

    public void setProtectModeCount(Time startTime, int m) {
        time = startTime;
        time.minutes += m;
        while(time.minutes >= 60) {
            time.minutes -= 60;
            time.hour++;
        }
        edit.putInt("executeTimeHour", time.hour);
        edit.putInt("executeTimeMinutes", time.minutes);
        edit.putInt("executeTimeSeconds", time.seconds);
        edit.commit();
    }

    public Boolean isProtectModeEnable() {
        return enablePM;
    }

    public Time getExecuteTime() {
        time.hour = pref.getInt("executeTimeHour", 0);
        time.minutes = pref.getInt("executeTimeMinutes", 0);
        time.seconds = pref.getInt("executeTimeSeconds", 0);
        return time;
    }
}
