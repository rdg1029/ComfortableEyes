package com.comfortable.eyes;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

public class AdDialog {
    private Context mContext;
    private Dialog dialog;

    public AdDialog(Context context) {
        this.mContext = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ad_dialog);
        setAdTemplate();
    }

    public void setTitle(String text) {
        TextView title = dialog.findViewById(R.id.ad_dialog_title);
        title.setText(text);
    }

    private void setAdTemplate() {
        final TemplateView template = dialog.findViewById(R.id.ad_dialog_template);
        template.setVisibility(View.INVISIBLE);
        AdLoader.Builder adBuilder = new AdLoader.Builder(mContext, "ca-app-pub-3940256099942544/2247696110"); //네이티브 광고 TEST ID
        adBuilder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                template.setVisibility(View.VISIBLE);
                template.setNativeAd(unifiedNativeAd);
            }
        });
        AdLoader adLoader = adBuilder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public void setPositiveButton(View.OnClickListener listener) {
        Button positiveButton = dialog.findViewById(R.id.ad_dialog_positive);
        positiveButton.setOnClickListener(listener);
    }

    public void setNegativeButton(View.OnClickListener listener) {
        Button negativeButton = dialog.findViewById(R.id.ad_dialog_negative);
        negativeButton.setOnClickListener(listener);
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void show() {
        dialog.show();
    }
}
