package com.comfortable.eyes

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError

class AdDialog(private val mContext: Context) {
    private val dialog: Dialog = Dialog(mContext)
    private lateinit var ad: TemplateView

    private var isAdLoaded: Boolean = false

    fun setTitle(text: String?) {
        val title = dialog.findViewById<TextView>(R.id.ad_dialog_title)
        title.text = text
    }

    private fun setAdTemplate() {
        ad = dialog.findViewById(R.id.ad_dialog_template)

        //네이티브 광고 TEST ID
        AdLoader.Builder(mContext, "ca-app-pub-3940256099942544/2247696110").apply {
            forUnifiedNativeAd { unifiedNativeAd ->
                ad.setNativeAd(unifiedNativeAd)
            }
            withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError?) {
                    ad.visibility = View.INVISIBLE
                    isAdLoaded = false
                }

                override fun onAdLoaded() {
                    ad.visibility = View.VISIBLE
                    isAdLoaded = true
                }
            })
            build().loadAd(AdRequest.Builder().build())
        }
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
        if (isAdLoaded)
            ad.destroyNativeAd()
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