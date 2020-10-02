package com.comfortable.eyes;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EmergencyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_emergency, container, false);
    }

    private void showDialog() {
        final AdDialog adDialog = new AdDialog(getActivity());
        adDialog.setTitle("앱을 강제로 종료합니다!");
        adDialog.setPositiveButton(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                ProtectModeState pmState = new ProtectModeState(getActivity());

                notificationManager.cancel(1029);
                notificationManager.cancel(3847);
                notificationManager.cancel(4756);

                pmState.enableProtectMode(false);
                pmState.setNotiCount(pmState.getNotiTime());
                pmState.setNotUsingCount(pmState.getNotiTime()/5);
                pmState.commitState();

                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        adDialog.setNegativeButton(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adDialog.dismiss();
            }
        });
        adDialog.show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnEmergency = view.findViewById(R.id.emergency_btn);
        btnEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }
}
