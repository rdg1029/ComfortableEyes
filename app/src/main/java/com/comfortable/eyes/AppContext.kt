package com.comfortable.eyes

import android.app.Application
import android.content.Context

class AppContext: Application() {
    init {
        instance = this
    }
    companion object {
        private var instance: AppContext? = null
        val context: Context
            get() = instance!!.applicationContext
    }
}