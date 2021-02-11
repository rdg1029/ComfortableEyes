package com.comfortable.eyes

import android.annotation.SuppressLint
import android.app.Application

class AppContext: Application() {
    init {
        instance = this
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: AppContext? = null
        @SuppressLint("StaticFieldLeak")
        val context = instance!!.applicationContext
    }
}