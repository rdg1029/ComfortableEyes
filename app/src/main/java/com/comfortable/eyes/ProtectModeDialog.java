package com.comfortable.eyes;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class ProtectModeDialog extends Handler {
    private Context mContext;
    private String dialogTextMSG, btnTextConfirm, btnTextCancel;
    private WindowManager windowManager;
    private View view;

    public ProtectModeDialog(Context context, String msg, String btnConfirm, String btnCancel) {
        this.mContext = context;
        this.dialogTextMSG = msg;
        this.btnTextConfirm = btnConfirm;
        this.btnTextCancel = btnCancel;
    }

    private int overlayType() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else {
            return WindowManager.LayoutParams.TYPE_PHONE;
        }
    }

    private void setWindowManager(FrameLayout layout) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayType(),
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.CENTER;

        windowManager = (WindowManager)this.mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(layout, params);
    }

    private void removeWindowManager() {
        if(view == null) return;
        windowManager.removeView(view);
        view = null;
    }

    private void setButtonOnClick(Button confirm, Button cancel) {
        final ProtectModeState pmPref = new ProtectModeState(this.mContext);
        Button.OnClickListener btnClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.pm_dialog_btn_confirm :
                        Toast.makeText(mContext.getApplicationContext(), "확인 버튼 클릭됨", Toast.LENGTH_SHORT).show();
                        removeWindowManager();
                        ProtectModeDialog.this.mContext.startActivity(new Intent(ProtectModeDialog.this.mContext, RelaxingActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    case R.id.pm_dialog_btn_cancel :
                        Toast.makeText(mContext.getApplicationContext(), "취소 버튼 클릭됨", Toast.LENGTH_SHORT).show();
                        removeWindowManager();
                        pmPref.setNotiCountPause(false);
                        break;
                }
            }
        };
        confirm.setOnClickListener(btnClickListener);
        cancel.setOnClickListener(btnClickListener);
    }

    private void setProtectModeDialog() {
        LayoutInflater inflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_protect_mode, null);
        FrameLayout dialogLayout = view.findViewById(R.id.pm_dialog_layout);
        TextView dialogMSG = view.findViewById(R.id.pm_dialog_msg);
        Button btnConfirm = view.findViewById(R.id.pm_dialog_btn_confirm);
        Button btnCancel = view.findViewById(R.id.pm_dialog_btn_cancel);

        dialogMSG.setText(this.dialogTextMSG);
        btnConfirm.setText(this.btnTextConfirm);
        btnCancel.setText(this.btnTextCancel);

        setButtonOnClick(btnConfirm, btnCancel);
        setWindowManager(dialogLayout);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        Toast.makeText(this.mContext, "15분이 지났습니다.", Toast.LENGTH_SHORT).show();
        setProtectModeDialog();
    }
}
