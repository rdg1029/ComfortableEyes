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

    public void setCount(int m) {
        edit.putInt("count", m);
        edit.putInt("countValue", m*60);
        edit.commit();
    }

    public void setCountValue(int m) {
        edit.putInt("countValue", m);
        edit.commit();
    }

    public int getCountValue() {
        return pref.getInt("countValue", 0);
    }
}
