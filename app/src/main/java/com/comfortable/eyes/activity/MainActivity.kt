package com.comfortable.eyes.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.comfortable.eyes.*
import com.comfortable.eyes.fragment.HomeFragment
import com.comfortable.eyes.fragment.SettingsFragment
import com.comfortable.eyes.service.TimeCount

class MainActivity : AppCompatActivity() {
    private var fragmentManager: FragmentManager? = null
    private var homeFragment: HomeFragment? = null
    private var settingsFragment: SettingsFragment? = null
    private var fragmentTransaction: FragmentTransaction? = null
    private var menuName: TextView? = null
    private var btnHome: ImageButton? = null
    private var btnSettings: ImageButton? = null
    private fun restartService() {
        stopService(Intent(this, TimeCount::class.java))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(Intent(this, TimeCount::class.java))
        else
            startService(Intent(this, TimeCount::class.java))
    }

    private fun init() {
        fragmentManager = supportFragmentManager
        homeFragment = HomeFragment()
        settingsFragment = SettingsFragment()
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.main_frameLayout, homeFragment!!).commitAllowingStateLoss()
        menuName = findViewById(R.id.main_menu_name)
        menuName?.visibility = View.INVISIBLE
        btnHome = findViewById(R.id.main_btn_home)
        btnSettings = findViewById(R.id.main_btn_settings)
        btnSettings?.alpha = 0.5f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        restartService()
        init()
    }

    fun menuClick(v: View) {
        fragmentTransaction = fragmentManager!!.beginTransaction()
        when (v.id) {
            R.id.main_btn_home -> {
                menuName!!.visibility = View.INVISIBLE
                fragmentTransaction!!.replace(R.id.main_frameLayout, homeFragment!!).commitAllowingStateLoss()
                btnHome!!.alpha = 1.0f
                btnSettings!!.alpha = 0.5f
            }
            R.id.main_btn_settings -> {
                menuName!!.visibility = View.VISIBLE
                menuName!!.text = "설정"
                fragmentTransaction!!.replace(R.id.main_frameLayout, settingsFragment!!).commitAllowingStateLoss()
                btnHome!!.alpha = 0.5f
                btnSettings!!.alpha = 1.0f
            }
        }
    }
}