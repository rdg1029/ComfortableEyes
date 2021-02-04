package com.comfortable.eyes.fragment

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.comfortable.eyes.AdDialog
import com.comfortable.eyes.R

class EmergencyFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_emergency, container, false)
    }

    private fun showDialog() {
        val adDialog = activity?.let { AdDialog(it) }
        adDialog?.setTitle("앱을 강제로 종료합니다!")
        adDialog?.setPositiveButton {
            val notificationManager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1029)
            notificationManager.cancel(3847)
            notificationManager.cancel(4756)
            Process.killProcess(Process.myPid())
        }
        adDialog?.setNegativeButton { adDialog?.dismiss() }
        adDialog?.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnEmergency = view.findViewById<Button>(R.id.emergency_btn)
        btnEmergency.setOnClickListener { showDialog() }
    }
}