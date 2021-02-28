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

        updateTime.sendEmptyMessageDelayed(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        updateTime.removeMessages(0)
    }
}