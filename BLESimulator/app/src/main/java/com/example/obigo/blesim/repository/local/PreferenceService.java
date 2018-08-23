package com.example.obigo.blesim.repository.local;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.example.obigo.blesim.R;
import com.example.obigo.blesim.model.SwitchData;
import com.example.obigo.blesim.repository.remote.BleRequestConstant;

import java.lang.ref.WeakReference;

public class PreferenceService {
    private static final String TAG = PreferenceService.class.getSimpleName();

    private MutableLiveData<SwitchData> mDoorStatus;
    private MutableLiveData<SwitchData> mEngineStatus;
    private MutableLiveData<SwitchData> mDefrostStatus;
    private MutableLiveData<Integer> mTemperatureStatus;
    private MutableLiveData<Integer> mIdleTimeStatus;

    private MutableLiveData<SwitchData> mAutoEngineStartStatus;
    private MutableLiveData<SwitchData> mAutoDoorUnlockStatus;
    private MutableLiveData<SwitchData> mAutoDoorLockStatus;
    private MutableLiveData<SwitchData> mAutoWelcomeLightStatus;

    private AppPreference mPreference;
    private WeakReference<Context> mContextReference;
    private Context mContext;

    private static final int SEND = 0;
    private static final int RECEIVE = 1;

    public PreferenceService(Application application) {
        mContextReference = new WeakReference<Context>(application);
        mContext = mContextReference.get();
        mPreference = new AppPreference(mContext, "Settings");
    }

    public void setStatusChange(int id, boolean isChecked) {
        switch (id) {
            case R.id.switch_door:
                mPreference.setDoorStatus(isChecked);
                break;
            case R.id.switch_engine:
                mPreference.setEngineStatus(isChecked);
                break;
            case R.id.switch_defrost:
                mPreference.setDefrostStatus(isChecked);
                break;
            case R.id.switch_auto_engine:
                mPreference.setAutoEngineStartStatus(isChecked);
                break;
            case R.id.switch_auto_unlock:
                mPreference.setAutoDoorUnlockStatus(isChecked);
                break;
            case R.id.switch_auto_lock:
                mPreference.setAutoDoorLockStatus(isChecked);
                break;
            case R.id.switch_auto_light:
                mPreference.setAutoWelcomeLightStatus(isChecked);
                break;
        }
    }

    public void setValueChange(int id, int value) {
        switch (id) {
            case R.id.btn_temperature:
                mPreference.setTemperature(value);
                break;
            case R.id.btn_idle_time:
                mPreference.setIdleTime(value);
                break;
        }
    }

    public MutableLiveData<SwitchData> getEngineStatus() {
        Log.w(TAG, "getEngineStatus");
        if (mEngineStatus == null) {
            mEngineStatus = new MutableLiveData<>();
        }
        mEngineStatus.postValue(new SwitchData(mPreference.getEngineStatus(), RECEIVE));
        return mEngineStatus;
    }

    public MutableLiveData<SwitchData> getDoorStatus() {
        Log.w(TAG, "getDoorStatus");
        if (mDoorStatus == null) {
            mDoorStatus = new MutableLiveData<>();
        }
        mDoorStatus.postValue(new SwitchData(mPreference.getDoorStatus(), RECEIVE));
        return mDoorStatus;
    }

    public MutableLiveData<SwitchData> getDefrostStatus() {
        Log.w(TAG, "getDefrostStatus");
        if (mDefrostStatus == null) {
            mDefrostStatus = new MutableLiveData<>();
        }
        mDefrostStatus.postValue(new SwitchData(mPreference.getDefrostStatus(), RECEIVE));
        return mDefrostStatus;
    }

    public MutableLiveData<Integer> getTemperatureStatus() {
        Log.w(TAG, "getTemperatureStatus");
        if (mTemperatureStatus == null) {
            mTemperatureStatus = new MutableLiveData<>();
        }
        mTemperatureStatus.postValue(mPreference.getTemperature());
        return mTemperatureStatus;
    }

    public MutableLiveData<Integer> getIdleTimeStatus() {
        Log.w(TAG, "getIdleTimeStatus");
        if (mIdleTimeStatus == null) {
            mIdleTimeStatus = new MutableLiveData<>();
        }
        mIdleTimeStatus.postValue(mPreference.getIdleTime());
        return mIdleTimeStatus;
    }

    public MutableLiveData<SwitchData> getAutoEngineStartStatus() {
        Log.w(TAG, "getAutoEngineStartStatus");
        if (mAutoEngineStartStatus == null) {
            mAutoEngineStartStatus = new MutableLiveData<>();
        }
        mAutoEngineStartStatus.postValue(new SwitchData(mPreference.getAutoEngineStartStatus(), RECEIVE));
        return mAutoEngineStartStatus;
    }

    public MutableLiveData<SwitchData> getAutoDoorUnlockStatus() {
        Log.w(TAG, "getAutoDoorUnlockStatus");
        if (mAutoDoorUnlockStatus == null) {
            mAutoDoorUnlockStatus = new MutableLiveData<>();
        }
        mAutoDoorUnlockStatus.postValue(new SwitchData(mPreference.getAutoDoorUnlockStatus(), RECEIVE));
        return mAutoDoorUnlockStatus;
    }

    public MutableLiveData<SwitchData> getAutoDoorLockStatus() {
        Log.w(TAG, "getAutoDoorLockStatus");
        if (mAutoDoorLockStatus == null) {
            mAutoDoorLockStatus = new MutableLiveData<>();
        }
        mAutoDoorLockStatus.postValue(new SwitchData(mPreference.getAutoDoorLockStatus(), RECEIVE));
        return mAutoDoorLockStatus;
    }

    public MutableLiveData<SwitchData> getAutoWelcomeLightStatus() {
        Log.w(TAG, "getAutoWelcomeLightStatus");
        if (mAutoWelcomeLightStatus == null) {
            mAutoWelcomeLightStatus = new MutableLiveData<>();
        }
        mAutoWelcomeLightStatus.postValue(new SwitchData(mPreference.getAutoWelcomeLightStatus(), RECEIVE));
        return mAutoWelcomeLightStatus;
    }

    public boolean getAutoEngineStartSetting(){
        return mPreference.getAutoEngineStartStatus();
    }
}
