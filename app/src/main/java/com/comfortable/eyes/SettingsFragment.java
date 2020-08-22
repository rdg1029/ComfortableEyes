package com.comfortable.eyes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    private ProtectModeState pmState;
    private RelaxingModeState rmState;
    private int seekBarVal;

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

        setProtectModeSwitch(view);
        setProtectModeSeekbar(view);
        setProtectModeSeekbarSaveButton(view);
    }

    private void setProtectModeSwitch(View view) {
        Switch protectModeSwitch = view.findViewById(R.id.settings_switch_protect_mode);
        protectModeSwitch.setChecked(pmState.isProtectModeEnable());
        protectModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    pmState.enableProtectMode(true);
                    pmState.setNotiCount(pmState.getNotiTime());
                    pmState.setNotUsingCount(pmState.getNotiTime()/5);
                    rmState.setCount(pmState.getNotiTime()/5);
                }
                else
                    pmState.enableProtectMode(false);
            }
        });
    }

    private void setProtectModeSeekbar(View view) {
        SeekBar protectModeSeekbar = view.findViewById(R.id.settings_seekbar_protect_mode);
        protectModeSeekbar.setProgress(getSeekbarState());
        protectModeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarVal = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setProtectModeSeekbarSaveButton(View view) {
        Button btn = view.findViewById(R.id.settings_btn_seekbar_save);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyNotiTime();
                Toast.makeText(getActivity(), "저장 되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getSeekbarState() {
        int val;
        switch (pmState.getNotiTime()) {
            case 15:
                val = 0;
                break;

            case 30:
                val = 1;
                break;

            case 45:
                val = 2;
                break;

            case 60:
                val = 3;
                break;

            default:
                val = 0;
        }
        return val;
    }

    private void applyNotiTime() {
        switch (seekBarVal) {
            case 0:
                pmState.setNotiCount(15);
                pmState.setNotUsingCount(pmState.getNotiTime()/5);
                rmState.setCount(pmState.getNotiTime()/5);
                break;

            case 1:
                pmState.setNotiCount(30);
                pmState.setNotUsingCount(pmState.getNotiTime()/5);
                rmState.setCount(pmState.getNotiTime()/5);
                break;

            case 2:
                pmState.setNotiCount(45);
                pmState.setNotUsingCount(pmState.getNotiTime()/5);
                rmState.setCount(pmState.getNotiTime()/5);
                break;

            case 3:
                pmState.setNotiCount(60);
                pmState.setNotUsingCount(pmState.getNotiTime()/5);
                rmState.setCount(pmState.getNotiTime()/5);
                break;
        }
    }
}
