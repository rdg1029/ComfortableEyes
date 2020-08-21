package com.comfortable.eyes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    private ProtectModeState pmState;
    private RelaxingModeState rmState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pmState = new ProtectModeState(getActivity());
        rmState = new RelaxingModeState(getActivity());

        Switch protectMode = view.findViewById(R.id.settings_switch_protect_mode);
        protectMode.setChecked(pmState.isProtectModeEnable());
        protectMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    pmState.enableProtectMode(true);
                    pmState.setNotiCount(15);
                    pmState.setNotUsingCount(15/5);
                    rmState.setCount(15/5);
                }
                else
                    pmState.enableProtectMode(false);
            }
        });
    }
}
