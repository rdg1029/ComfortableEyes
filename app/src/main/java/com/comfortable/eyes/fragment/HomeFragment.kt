package com.comfortable.eyes.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.comfortable.eyes.R
import com.comfortable.eyes.state.SharedTimeState
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private var year: TextView? = null
    private var month: TextView? = null
    private var day: TextView? = null
    private var hour: TextView? = null
    private var minutes: TextView? = null
    private var seconds: TextView? = null

    private fun setDisplayTimeState() {
        val currentTime = Calendar.getInstance().time
        val sec = (( SharedTimeState.usedTime + (SystemClock.elapsedRealtime() - SharedTimeState.startTime) ) / 1000).toInt()

        //timeState.setCurrentDate(new SimpleDateFormat("dd", Locale.getDefault()).format(currentTime));
        //String displayCurrentDate = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(currentTime);
        year!!.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(currentTime)
        month!!.text = SimpleDateFormat("MM", Locale.getDefault()).format(currentTime)
        day!!.text = SimpleDateFormat("dd", Locale.getDefault()).format(currentTime)
        seconds!!.text = "${(sec%60).toShort()}"
        minutes!!.text = "${((sec/60)%60).toShort()}"
        hour!!.text = "${(sec/3600).toShort()}"
    }

    @SuppressLint("HandlerLeak")
    private val updateTime: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            setDisplayTimeState()
            this.sendEmptyMessageDelayed(0, 1000)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        year = view.findViewById(R.id.home_date_year)
        month = view.findViewById(R.id.home_date_month)
        day = view.findViewById(R.id.home_date_day)
        hour = view.findViewById(R.id.home_tv_hour)
        minutes = view.findViewById(R.id.home_tv_minutes)
        seconds = view.findViewById(R.id.home_tv_seconds)

        //pmStateTextView = view.findViewById(R.id.home_tv_pm_state);
        //pmStateImg = view.findViewById(R.id.home_img_pm_state);
        updateTime.sendEmptyMessageDelayed(0, 0)
    }

    override fun onResume() {
        super.onResume()
        /*
        ProtectModeState pmState = new ProtectModeState(getActivity());
        if(pmState.isProtectModeEnable()) {
            pmStateTextView.setText("보호 기능 사용 중");
            pmStateTextView.setAlpha(1.0f);
            pmStateImg.setAlpha(1.0f);
        }
        else {
            pmStateTextView.setText("보호 기능 중지됨");
            pmStateTextView.setAlpha(0.5f);
            pmStateImg.setAlpha(0.4f);
        }
        */
    }

    override fun onDestroy() {
        super.onDestroy()
        updateTime.removeMessages(0)
    }
}