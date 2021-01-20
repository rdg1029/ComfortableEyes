package com.comfortable.eyes

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class ProtectModeState(private val mContext: Context) {
    private val pref: SharedPreferences
    private val edit: Editor
    fun commitState() {
        edit.commit()
    }

    fun enableProtectMode(b: Boolean) {
        edit.putBoolean("enableNoti", b)
    }

    fun setNotiCount(m: Int) {
        edit.putInt("notiTime", m)
        edit.putInt("notiCount", m * 60)
    }

    fun setNotUsingCount(m: Int) {
        edit.putInt("notUsingCount", m * 60)
    }

    fun setNotiCountPause(b: Boolean?) {
        edit.putBoolean("PAUSE_notiCount", b!!)
    }

    fun setNotUsingCountPause(b: Boolean?) {
        edit.putBoolean("PAUSE_notUsingCount", b!!)
    }

    val isProtectModeEnable: Boolean
        get() = pref.getBoolean("enableNoti", false)
    var notiCountValue: Int
        get() = pref.getInt("notiCount", 1)
        set(m) {
            edit.putInt("notiCount", m)
        }
    var notUsingCountValue: Int
        get() = pref.getInt("notUsingCount", 1)
        set(m) {
            edit.putInt("notUsingCount", m)
        }
    val notiTime: Int
        get() = pref.getInt("notiTime", 15)
    val isNotiCountPaused: Boolean
        get() = pref.getBoolean("PAUSE_notiCount", false)
    val isNotUsingCountPaused: Boolean
        get() = pref.getBoolean("PAUSE_notUsingCount", false)

    init {
        pref = mContext.getSharedPreferences("ProtectModeState", 0)
        edit = pref.edit()
    }
}