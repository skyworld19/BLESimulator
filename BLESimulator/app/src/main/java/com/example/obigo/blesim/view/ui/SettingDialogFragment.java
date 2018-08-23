package com.example.obigo.blesim.view.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;

import com.example.obigo.blesim.R;
import com.example.obigo.blesim.databinding.FragmentSettingBinding;
import com.example.obigo.blesim.model.SwitchData;
import com.example.obigo.blesim.viewmodel.BleViewModel;

public class SettingDialogFragment extends DialogFragment {
    private static final String TAG = SettingDialogFragment.class.getSimpleName();
    private static final int SEND = 0;
    private static final int RECEIVE = 1;
    private int dataStatus = 0;

    private FragmentSettingBinding mFragmentSettingBinding;
    public BleViewModel mBleViewModel;

    public Observer<SwitchData> mAutoEngineStartObserver;
    public Observer<SwitchData> mAutoDoorUnlockObserver;
    public Observer<SwitchData> mAutoDoorLockObserver;
    public Observer<SwitchData> mAutoWelcomeLightObserver;

    public SettingDialogFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBleViewModel = ViewModelProviders.of(getActivity()).get(BleViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentSettingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        View view = mFragmentSettingBinding.getRoot();
        //View view = inflater.inflate(R.layout.fragment_setting, container);
        mFragmentSettingBinding.setSettingfragment(this);
        // 레이아웃 XML과 뷰 변수 연결
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // remove dialog background
        getDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        initObserver();
        return view;
    }

    public void initObserver() {
        mAutoEngineStartObserver = new Observer<SwitchData>() {
            @Override
            public void onChanged(@Nullable final SwitchData switchData) {
                Log.w(TAG, "mAutoEngineStartObserver : onChanged");
                dataStatus = switchData.getDataStatus();
                mFragmentSettingBinding.switchAutoEngine.setChecked(switchData.isChecked());
                if(dataStatus == RECEIVE) {
                    dataStatus = SEND;
                }
            }
        };

        mAutoDoorUnlockObserver = new Observer<SwitchData>() {
            @Override
            public void onChanged(@Nullable final SwitchData switchData) {
                Log.w(TAG, "mAutoDoorUnlockObserver : onChanged");
                dataStatus = switchData.getDataStatus();
                mFragmentSettingBinding.switchAutoUnlock.setChecked(switchData.isChecked());
                if(dataStatus == RECEIVE) {
                    dataStatus = SEND;
                }
            }
        };

        mAutoDoorLockObserver = new Observer<SwitchData>() {
            @Override
            public void onChanged(@Nullable final SwitchData switchData) {
                Log.w(TAG, "mAutoDoorLockObserver : onChanged");
                dataStatus = switchData.getDataStatus();
                mFragmentSettingBinding.switchAutoLock.setChecked(switchData.isChecked());
                if(dataStatus == RECEIVE) {
                    dataStatus = SEND;
                }
            }
        };

        mAutoWelcomeLightObserver = new Observer<SwitchData>() {
            @Override
            public void onChanged(@Nullable final SwitchData switchData) {
                Log.w(TAG, "mAutoWelcomeLightObserver : onChanged");
                dataStatus = switchData.getDataStatus();
                mFragmentSettingBinding.switchAutoLight.setChecked(switchData.isChecked());
                if(dataStatus == RECEIVE) {
                    dataStatus = SEND;
                }
            }
        };

        mBleViewModel.getAutoEngineStartStatus().observe(getActivity(), mAutoEngineStartObserver);
        mBleViewModel.getAutoDoorUnlockStatus().observe(getActivity(), mAutoDoorUnlockObserver);
        mBleViewModel.getAutoDoorLockStatus().observe(getActivity(), mAutoDoorLockObserver);
        mBleViewModel.getAutoWelcomeLightStatus().observe(getActivity(), mAutoWelcomeLightObserver);
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Log.i(TAG, "onCheckedChanged : "+isChecked+" / dataStatus : "+dataStatus);
        if(dataStatus==SEND){
            // send
            mBleViewModel.setChange(compoundButton.getId(), isChecked);
        } /*else {
            // receive
            dataStatus = SEND;
        }*/
    }

}
