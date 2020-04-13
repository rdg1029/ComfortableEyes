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
        commitCountValue(m*10);
    }

    public void setProtectModeCountValue(int m) {
        commitCountValue(m);
    }

    public Boolean isProtectModeEnable() {
        return pref.getBoolean("enablePM", false);
    }

    public int getCountValue() {
        return pref.getInt("pmCount",1);
    }
}
