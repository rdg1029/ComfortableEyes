package com.comfortable.eyes.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.comfortable.eyes.AdDialog
import com.comfortable.eyes.AlarmCycle
import com.comfortable.eyes.R
import com.comfortable.eyes.RestAlarmManager
import com.comfortable.eyes.state.RestModeState

class SettingsFragment : Fragment() {
    private var seekBarVal = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)
        setRestAlarmSwitch()
        setRestAlarmPreferencesLayout()
        setRestAlarmSeekbar()
        setRestAlarmSeekbarTextView()
        setRestAlarmSeekbarSaveButton()
    }

    override fun onResume() {
        super.onResume()
        val protectModeSeekbar = view!!.findViewById<SeekBar>(R.id.settings_seekbar_rest_alarm)
        protectModeSeekbar.progress = seekbarState
    }

    private fun setRestAlarmSwitch() {
        val protectModeSwitch = view!!.findViewById<Switch>(R.id.settings_switch_rest_alarm)
        protectModeSwitch.isChecked = RestAlarmManager.isAlarmEnabled
        protectModeSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                val timeAlarmCycle = RestAlarmManager.timeAlarmCycle
                val timeRest = RestAlarmManager.timeRest

                RestAlarmManager.apply(timeAlarmCycle, timeRest, true)
                RestModeState.restCount = timeRest

                setRestAlarmPreferencesLayout()
            } else {
                RestAlarmManager.isAlarmEnabled = false
                RestAlarmManager.cancel()

                setRestAlarmPreferencesLayout()
            }
            RestModeState.commitState()
            RestAlarmManager.commitState()
        }
    }

    private fun setRestAlarmPreferencesLayout() {
        val layout: ConstraintLayout = view!!.findViewById(R.id.settings_layout_noti_time)
        if (RestAlarmManager.isAlarmEnabled)
            layout.visibility = View.VISIBLE
        else
            layout.visibility = View.GONE
    }

    private fun setRestAlarmSeekbarTextView() {
        val textView = view!!.findViewById<TextView>(R.id.settings_tv_seekbar_val)
        when (seekBarVal) {
            0 -> textView.text = "15분 마다 휴식"
            1 -> textView.text = "30분 마다 휴식"
            2 -> textView.text = "45분 마다 휴식"
            3 -> textView.text = "1시간 마다 휴식"
        }
    }

    private fun setRestAlarmSeekbar() {
        val protectModeSeekbar = view!!.findViewById<SeekBar>(R.id.settings_seekbar_rest_alarm)
        protectModeSeekbar.progress = seekbarState
        seekBarVal = protectModeSeekbar.progress
        protectModeSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                seekBarVal = i
                setRestAlarmSeekbarTextView()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun setApplyDialog() {
        val adDialog = activity?.let { AdDialog(it) }
        adDialog?.setTitle("설정을 적용하시겠습니까?")
        adDialog?.setPositiveButton {
            applyNotiTime()
            adDialog.dismiss()
            Toast.makeText(activity, "저장 되었습니다", Toast.LENGTH_SHORT).show()
        }
        adDialog?.setNegativeButton { adDialog.dismiss() }
        adDialog?.show()
    }

    private fun setRestAlarmSeekbarSaveButton() {
        val btn = view!!.findViewById<Button>(R.id.settings_btn_seekbar_save)
        btn.setOnClickListener { setApplyDialog() }
    }

    private val seekbarState: Int
        get() {
            return when (RestAlarmManager.timeAlarmCycle) {
                AlarmCycle._15_MIN -> 0
                AlarmCycle._30_MIN -> 1
                AlarmCycle._45_MIN -> 2
                AlarmCycle._60_MIN -> 3
                else -> 0
            }
        }

    private fun applyNotiTime() {
        var cycle = 0
        when (seekBarVal) {
            0 -> cycle = AlarmCycle._15_MIN
            1 -> cycle = AlarmCycle._30_MIN
            2 -> cycle = AlarmCycle._45_MIN
            3 -> cycle = AlarmCycle._60_MIN
        }
        val timeRest = (cycle / 5)*10

        RestAlarmManager.apply(cycle, timeRest, true)
        RestAlarmManager.commitState()

        RestModeState.restCount = timeRest
        RestModeState.commitState()
    }
}