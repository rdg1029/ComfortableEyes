package com.comfortable.eyes

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest

class AdDialog(private val mContext: Context) {
    private val dialog: Dialog = Dialog(mContext)
    fun setTitle(text: String?) {
        val title = dialog.findViewById<TextView>(R.id.ad_dialog_title)
        title.text = text
    }

    private fun setAdTemplate() {
        val template: TemplateView = dialog.findViewById(R.id.ad_dialog_template)
        template.visibility = View.INVISIBLE
        val adBuilder = AdLoader.Builder(mContext, "ca-app-pub-3940256099942544/2247696110") //네이티브 광고 TEST ID
        adBuilder.forUnifiedNativeAd { unifiedNativeAd ->
            template.visibility = View.VISIBLE
            template.setNativeAd(unifiedNativeAd)
        }
        val adLoader = adBuilder.build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun setPositiveButton(listener: View.OnClickListener?) {
        val positiveButton = dialog.findViewById<Button>(R.id.ad_dialog_positive)
        positiveButton.setOnClickListener(listener)
    }

    fun setNegativeButton(listener: View.OnClickListener?) {
        val negativeButton = dialog.findViewById<Button>(R.id.ad_dialog_negative)
        negativeButton.setOnClickListener(listener)
    }

    fun dismiss() {
        dialog.dismiss()
    }

    fun show() {
        dialog.show()
    }

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.ad_dialog)
        setAdTemplate()
    }
}