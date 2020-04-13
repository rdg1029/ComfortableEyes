package com.comfortable.eyes;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class ProtectModeDialog extends Handler {
    private Context mContext;

    public ProtectModeDialog(Context context) {
        this.mContext = context;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        Toast.makeText(this.mContext, "1분이 지났습니다.", Toast.LENGTH_SHORT).show();
    }
}
