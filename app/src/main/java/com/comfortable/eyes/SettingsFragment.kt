package com.comfortable.eyes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {
    private var pmState: ProtectModeState? = null
    private var rmState: RelaxingModeState? = null
    private var seekBarVal = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)
        pmState = activity?.let { ProtectModeState(it) }
        rmState = RelaxingModeState(activity!!)
        setProtectModeSwitch()
        setProtectModePreferencesLayout()
        setProtectModeSeekbar()
        setProtectModeSeekbarTextView()
        setProtectModeSeekbarSaveButton()
    }

    override fun onResume() {
        super.onResume()
        val protectModeSeekbar = view!!.findViewById<SeekBar>(R.id.settings_seekbar_protect_mode)
        protectModeSeekbar.progress = seekbarState
    }

    private fun commitState() {
        pmState!!.commitState()
        rmState!!.commitState()
    }

    private fun setProtectModeSwitch() {
        val protectModeSwitch = view!!.findViewById<Switch>(R.id.settings_switch_protect_mode)
        protectModeSwitch.isChecked = pmState!!.isProtectModeEnable
        protectModeSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                pmState!!.enableProtectMode(true)
                pmState!!.setNotiCount(pmState!!.notiTime)
                pmState!!.setNotUsingCount(pmState!!.notiTime / 5)
                rmState!!.setCount(pmState!!.notiTime / 5)
                commitState()
                setProtectModePreferencesLayout()
            } else {
                pmState!!.enableProtectMode(false)
                pmState!!.commitState()
                setProtectModePreferencesLayout()
            }
        }
    }

    private fun setProtectModePreferencesLayout() {
        val layout: ConstraintLayout = view!!.findViewById(R.id.settings_layout_noti_time)
        if (pmState!!.isProtectModeEnable) layout.visibility = View.VISIBLE else layout.visibility = View.GONE
    }

    private fun setProtectModeSeekbarTextView() {
        val textView = view!!.findViewById<TextView>(R.id.settings_tv_seekbar_val)
        when (seekBarVal) {
            0 -> textView.text = "15분 마다 휴식"
            1 -> textView.text = "30분 마다 휴식"
            2 -> textView.text = "45분 마다 휴식"
            3 -> textView.text = "1시간 마다 휴식"
        }
    }

    private fun setProtectModeSeekbar() {
        val protectModeSeekbar = view!!.findViewById<SeekBar>(R.id.settings_seekbar_protect_mode)
        protectModeSeekbar.progress = seekbarState
        seekBarVal = protectModeSeekbar.progress
        protectModeSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                seekBarVal = i
                setProtectModeSeekbarTextView()
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

    private fun setProtectModeSeekbarSaveButton() {
        val btn = view!!.findViewById<Button>(R.id.settings_btn_seekbar_save)
        btn.setOnClickListener { setApplyDialog() }
    }

    private val seekbarState: Int
        private get() {
            return when (pmState!!.notiTime) {
                15 -> 0
                30 -> 1
                45 -> 2
                60 -> 3
                else -> 0
            }
        }

    private fun applyNotiTime() {
        when (seekBarVal) {
            0 -> {
                pmState!!.setNotiCount(15)
                pmState!!.setNotUsingCount(pmState!!.notiTime / 5)
                rmState!!.setCount(pmState!!.notiTime / 5)
                commitState()
            }
            1 -> {
                pmState!!.setNotiCount(30)
                pmState!!.setNotUsingCount(pmState!!.notiTime / 5)
                rmState!!.setCount(pmState!!.notiTime / 5)
                commitState()
            }
            2 -> {
                pmState!!.setNotiCount(45)
                pmState!!.setNotUsingCount(pmState!!.notiTime / 5)
                rmState!!.setCount(pmState!!.notiTime / 5)
                commitState()
            }
            3 -> {
                pmState!!.setNotiCount(60)
                pmState!!.setNotUsingCount(pmState!!.notiTime / 5)
                rmState!!.setCount(pmState!!.notiTime / 5)
                commitState()
            }
        }
    }
}