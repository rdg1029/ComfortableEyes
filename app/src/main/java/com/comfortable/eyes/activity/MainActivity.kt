package com.comfortable.eyes

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity() {
    private var fragmentManager: FragmentManager? = null
    private var homeFragment: HomeFragment? = null
    private var settingsFragment: SettingsFragment? = null
    private var emergencyFragment: EmergencyFragment? = null
    private var fragmentTransaction: FragmentTransaction? = null
    private var menuName: TextView? = null
    private var btnHome: ImageButton? = null
    private var btnSettings: ImageButton? = null
    private var btnEmergency: ImageButton? = null
    private fun restartService() {
        val pmState = ProtectModeState(this)
        if (pmState.isNotiCountPaused) return
        stopService(Intent(this, TimeCount::class.java))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(Intent(this, TimeCount::class.java)) else startService(Intent(this, TimeCount::class.java))
    }

    private fun init() {
        fragmentManager = supportFragmentManager
        homeFragment = HomeFragment()
        settingsFragment = SettingsFragment()
        emergencyFragment = EmergencyFragment()
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.main_frameLayout, homeFragment!!).commitAllowingStateLoss()
        menuName = findViewById(R.id.main_menu_name)
        menuName?.visibility = View.INVISIBLE
        btnHome = findViewById(R.id.main_btn_home)
        btnSettings = findViewById(R.id.main_btn_settings)
        btnEmergency = findViewById(R.id.main_btn_emergency)
        btnSettings?.alpha = 0.5f
        btnEmergency?.alpha = 0.5f
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
                btnEmergency!!.alpha = 0.5f
            }
            R.id.main_btn_settings -> {
                menuName!!.visibility = View.VISIBLE
                menuName!!.text = "설정"
                fragmentTransaction!!.replace(R.id.main_frameLayout, settingsFragment!!).commitAllowingStateLoss()
                btnHome!!.alpha = 0.5f
                btnSettings!!.alpha = 1.0f
                btnEmergency!!.alpha = 0.5f
            }
            R.id.main_btn_emergency -> {
                menuName!!.visibility = View.VISIBLE
                menuName!!.text = "문제 발생"
                fragmentTransaction!!.replace(R.id.main_frameLayout, emergencyFragment!!).commitAllowingStateLoss()
                btnHome!!.alpha = 0.5f
                btnSettings!!.alpha = 0.5f
                btnEmergency!!.alpha = 1.0f
            }
        }
    }

    /*
    public void start(View v) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(new Intent(this, TimeCount.class));
        else
            startService(new Intent(this, TimeCount.class));
    }

    public void stop(View v) {
        stopService(new Intent(this, TimeCount.class));
    }

    public void reset(View v) {
        timeState.resetTime(time);
    }
*/
    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}