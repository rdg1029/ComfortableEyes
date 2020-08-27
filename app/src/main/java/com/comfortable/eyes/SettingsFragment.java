package com.comfortable.eyes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    private View view;
    private ProtectModeState pmState;
    private RelaxingModeState rmState;
    private int seekBarVal = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = v;
        pmState = new ProtectModeState(getActivity());
        rmState = new RelaxingModeState(getActivity());

        setProtectModeSwitch();
        setProtectModePreferencesLayout();
        setProtectModeSeekbar();
        setProtectModeSeekbarTextView();
        setProtectModeSeekbarSaveButton();
    }

    private void commitState() {
        pmState.commitState();
        rmState.commitState();
    }

    private void setProtectModeSwitch() {
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
                    commitState();
                    setProtectModePreferencesLayout();
                }
                else {
                    pmState.enableProtectMode(false);
                    pmState.commitState();
                    setProtectModePreferencesLayout();
                }

            }
        });
    }

    private void setProtectModePreferencesLayout() {
        ConstraintLayout layout = view.findViewById(R.id.settings_layout_noti_time);

        if(pmState.isProtectModeEnable())
            layout.setVisibility(View.VISIBLE);
        else
            layout.setVisibility(View.GONE);
    }

    private void setProtectModeSeekbarTextView() {
        TextView textView = view.findViewById(R.id.settings_tv_seekbar_val);
        switch (seekBarVal) {
            case 0:
                textView.setText("15분 마다 휴식");
                break;

            case 1:
                textView.setText("30분 마다 휴식");
                break;

            case 2:
                textView.setText("45분 마다 휴식");
                break;

            case 3:
                textView.setText("1시간 마다 휴식");
                break;
        }
    }

    private void setProtectModeSeekbar() {
        SeekBar protectModeSeekbar = view.findViewById(R.id.settings_seekbar_protect_mode);
        protectModeSeekbar.setProgress(getSeekbarState());
        seekBarVal = protectModeSeekbar.getProgress();
        protectModeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarVal = i;
                setProtectModeSeekbarTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setProtectModeSeekbarSaveButton() {
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
                commitState();
                break;

            case 1:
                pmState.setNotiCount(30);
                pmState.setNotUsingCount(pmState.getNotiTime()/5);
                rmState.setCount(pmState.getNotiTime()/5);
                commitState();
                break;

            case 2:
                pmState.setNotiCount(45);
                pmState.setNotUsingCount(pmState.getNotiTime()/5);
                rmState.setCount(pmState.getNotiTime()/5);
                commitState();
                break;

            case 3:
                pmState.setNotiCount(60);
                pmState.setNotUsingCount(pmState.getNotiTime()/5);
                rmState.setCount(pmState.getNotiTime()/5);
                commitState();
                break;
        }
    }
}
