package com.example.obigo.blesim.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.support.annotation.NonNull;

import android.util.Log;

import com.example.obigo.blesim.App;
import com.example.obigo.blesim.R;
import com.example.obigo.blesim.model.SwitchData;
import com.example.obigo.blesim.repository.local.AppPreference;
import com.example.obigo.blesim.repository.local.PreferenceService;
import com.example.obigo.blesim.repository.remote.BleRequestConstant;
import com.example.obigo.blesim.repository.remote.BleService;;import java.util.Arrays;

public class BleViewModel extends AndroidViewModel {
    private static final String TAG = BleViewModel.class.getSimpleName();

    private static final int SEND = 0;
    private static final int RECEIVE = 1;

    private MutableLiveData<String> mBleDeviceName;
    private MutableLiveData<String> mConnectStatus;
    private MutableLiveData<SwitchData> mDoorStatus;
    private MutableLiveData<SwitchData> mEngineStatus;
    private MutableLiveData<SwitchData> mHornLightStatus;
    private MutableLiveData<SwitchData> mLightOnlyStatus;
    private MutableLiveData<SwitchData> mDefrostStatus;
    private MutableLiveData<Integer> mTemperatureStatus;
    private MutableLiveData<Integer> mIdleTimeStatus;

    private MutableLiveData<SwitchData> mAutoEngineStartStatus;
    private MutableLiveData<SwitchData> mAutoDoorUnlockStatus;
    private MutableLiveData<SwitchData> mAutoDoorLockStatus;
    private MutableLiveData<SwitchData> mAutoWelcomeLightStatus;

    private BleService mBleService;
    private BleCallback mBleCallback;
    private PreferenceService mPreferenceService;

    // GATT
    private BluetoothGattService mService;
    private BluetoothGattCharacteristic mCharacteristic;

    private Observer<String> mConnectStatusObserver;

    public BleViewModel(@NonNull Application application) {
        super(application);
        initCallback();
        mPreferenceService = new PreferenceService(application);
        mBleService = new BleService(application, mBleCallback);
        mBleService.bleInitSetting();
    }

    @NonNull
    @Override
    public <T extends Application> T getApplication() {
        return super.getApplication();
    }

    public LiveData<String> getConnectStatus() {
        Log.w(TAG, "getConnectStatus()");
        if (mConnectStatus == null) {
            mConnectStatus = new MutableLiveData<>();
        }
        return mConnectStatus;
    }

    public void setConnectStatus(String s) {
        mConnectStatus.postValue(s);
    }

    public LiveData<String> getBleDeviceName() {
        Log.w(TAG, "getBleDeviceName()");
        if (mBleDeviceName == null) {
            mBleDeviceName = new MutableLiveData<>();
        }
        return mBleDeviceName;
    }

    public void setBleDeviceName(String s) {
        mBleDeviceName.postValue(s);
    }

    public LiveData<SwitchData> getEngineStatus() {
        Log.w(TAG, "getEngineStatus");
        mEngineStatus = mPreferenceService.getEngineStatus();
        return mEngineStatus;
    }

    public void setEngineStatus(SwitchData switchData) {
        mEngineStatus.postValue(switchData);
    }

    public LiveData<SwitchData> getDoorStatus() {
        Log.w(TAG, "getDoorStatus");
        mDoorStatus = mPreferenceService.getDoorStatus();
        return mDoorStatus;
    }

    public void setDoorStatus(SwitchData switchData) {
        mDoorStatus.postValue(switchData);
    }

    public LiveData<SwitchData> getHornLightStatus() {
        Log.w(TAG, "getHornLightStatus");
        if (mHornLightStatus == null) {
            mHornLightStatus = new MutableLiveData<>();
        }
        return mHornLightStatus;
    }

    public void setHornLightStatus(SwitchData switchData) {
        mHornLightStatus.postValue(switchData);
    }

    public LiveData<SwitchData> getLightOnlyStatus() {
        Log.w(TAG, "getLightOnlyStatus");
        if (mLightOnlyStatus == null) {
            mLightOnlyStatus = new MutableLiveData<>();
        }
        return mLightOnlyStatus;
    }

    public void setLightOnlyStatus(SwitchData switchData) {
        mLightOnlyStatus.postValue(switchData);
    }

    public LiveData<SwitchData> getDefrostStatus() {
        Log.w(TAG, "getDefrostStatus");
        mDefrostStatus = mPreferenceService.getDefrostStatus();
        return mDefrostStatus;
    }

    public void setDefrostStatus(SwitchData switchData) {
        mDefrostStatus.postValue(switchData);
    }

    public LiveData<Integer> getTemperatureStatus() {
        Log.w(TAG, "getTemperatureStatus");
        mTemperatureStatus = mPreferenceService.getTemperatureStatus();
        return mTemperatureStatus;
    }

    public void setTemperatureStatus(Integer i) {
        mTemperatureStatus.postValue(i);
    }

    public LiveData<Integer> getIdleTimeStatus() {
        Log.w(TAG, "getIdleTimeStatus");
        mIdleTimeStatus = mPreferenceService.getIdleTimeStatus();
        return mIdleTimeStatus;
    }

    public void setIdleTimeStatus(Integer i) {
        mIdleTimeStatus.postValue(i);
    }

    public LiveData<SwitchData> getAutoEngineStartStatus() {
        Log.w(TAG, "getAutoEngineStartStatus");
        mAutoEngineStartStatus = mPreferenceService.getAutoEngineStartStatus();
        return mAutoEngineStartStatus;
    }

    public void setAutoEngineStartStatus(SwitchData switchData) {
        mAutoEngineStartStatus.postValue(switchData);
    }

    public LiveData<SwitchData> getAutoDoorUnlockStatus() {
        Log.w(TAG, "getAutoDoorUnlockStatus");
        mAutoDoorUnlockStatus = mPreferenceService.getAutoDoorUnlockStatus();
        return mAutoDoorUnlockStatus;
    }

    public void setAutoDoorUnlockStatus(SwitchData switchData) {
        mAutoDoorUnlockStatus.postValue(switchData);
    }

    public LiveData<SwitchData> getAutoDoorLockStatus() {
        Log.w(TAG, "getAutoDoorLockStatus");
        mAutoDoorLockStatus = mPreferenceService.getAutoDoorLockStatus();
        return mAutoDoorLockStatus;
    }

    public void setAutoDoorLockStatus(SwitchData switchData) {
        mAutoDoorLockStatus.postValue(switchData);
    }

    public LiveData<SwitchData> getAutoWelcomeLightStatus() {
        Log.w(TAG, "getAutoWelcomeLightStatus");
        mAutoWelcomeLightStatus = mPreferenceService.getAutoWelcomeLightStatus();
        return mAutoWelcomeLightStatus;
    }

    public void setAutoWelcomeLightStatus(SwitchData switchData) {
        mAutoWelcomeLightStatus.postValue(switchData);
    }

    public void setChange(int id, boolean isChecked) {
        if (mBleService.getBluetoothState()) {
            // case 1 : ble connected
            mBleService.notifyToDevice(id, isChecked);
        }

        // case 2 : ble not connected => use local storage (shared preference)
        switch (id) {
            case R.id.switch_door:
                mPreferenceService.setStatusChange(id, isChecked);
                setDoorStatus(new SwitchData(isChecked, SEND));
                break;
            case R.id.switch_engine:
                mPreferenceService.setStatusChange(id, isChecked);
                setEngineStatus(new SwitchData(isChecked, SEND));
                break;
            case R.id.switch_defrost:
                mPreferenceService.setStatusChange(id, isChecked);
                setDefrostStatus(new SwitchData(isChecked, SEND));
                break;
            case R.id.switch_horn_light:
                setHornLightStatus(new SwitchData(isChecked, SEND));
                break;
            case R.id.switch_light:
                setLightOnlyStatus(new SwitchData(isChecked, SEND));
                break;
            default :
                mPreferenceService.setStatusChange(id, isChecked);
                break;
        }
    }

    public void sendProgressChange(int id, int value) {
        if (mBleService.getBluetoothState()) {
            // case 1 : ble connected
            mBleService.notifyToDevice(id, value);
        }
        // case 2 : ble not connected => use local storage (shared preference)
        mPreferenceService.setValueChange(id, value);
        if(id==R.id.btn_temperature)
            setTemperatureStatus(value);
        else
            setIdleTimeStatus(value);
    }

    public void initCallback() {
        mBleCallback = new BleCallback() {
            @Override
            public void receiveBleConnectStatus(String s) {
                setConnectStatus(s);
                if(s.equals(App.getContext().getString(R.string.status_devicesConnected))){
                    if(mPreferenceService.getAutoEngineStartSetting()) {
                        mPreferenceService.setStatusChange(R.id.switch_engine, true);
                        setEngineStatus(new SwitchData(true, RECEIVE));
                    }
                }
            }

            @Override
            public void receiveBleDeviceName(String s) {
                setBleDeviceName(s);
            }

            @Override
            public void receiveData(byte[] bytes) {
                byte[] rBytes;
                String temp;
                switch (bytes[2]) {
                    case BleRequestConstant.DOOR:
                        if (bytes[3] == BleRequestConstant.DOOR_LOCK) {
                            mPreferenceService.setStatusChange(R.id.switch_door, true);
                            setDoorStatus(new SwitchData(true, RECEIVE));
                        } else {
                            mPreferenceService.setStatusChange(R.id.switch_door, false);
                            setDoorStatus(new SwitchData(false, RECEIVE));
                        }
                        break;
                    case BleRequestConstant.ENGINE:
                        if (bytes[3] == BleRequestConstant.ENGINE_START) {
                            mPreferenceService.setStatusChange(R.id.switch_engine, true);
                            setEngineStatus(new SwitchData(true, RECEIVE));
                        } else {
                            mPreferenceService.setStatusChange(R.id.switch_engine, false);
                            setEngineStatus(new SwitchData(false, RECEIVE));
                        }
                        break;
                    case BleRequestConstant.HORN_LIGHT:
                        if (bytes[3] == BleRequestConstant.HORN_AND_LIGHT)
                            setHornLightStatus(new SwitchData(true, RECEIVE));
                        else
                            setLightOnlyStatus(new SwitchData(true, RECEIVE));
                        break;
                    case BleRequestConstant.DEFROST:
                        if (bytes[3] == BleRequestConstant.DEFROST_ON) {
                            mPreferenceService.setStatusChange(R.id.switch_defrost, true);
                            setDefrostStatus(new SwitchData(true, RECEIVE));
                        } else {
                            mPreferenceService.setStatusChange(R.id.switch_defrost, false);
                            setDefrostStatus(new SwitchData(false, RECEIVE));
                        }
                        break;
                    case BleRequestConstant.TEMPERATURE:
                        rBytes = Arrays.copyOfRange(bytes, 3, bytes.length);
                        temp = new String(rBytes);
                        mPreferenceService.setValueChange(R.id.btn_temperature, Integer.valueOf(temp));
                        setTemperatureStatus(Integer.valueOf(temp));
                        break;
                    case BleRequestConstant.IDLETIME:
                        rBytes = Arrays.copyOfRange(bytes, 3, bytes.length);
                        temp = new String(rBytes);
                        mPreferenceService.setValueChange(R.id.btn_idle_time, Integer.valueOf(temp));
                        setIdleTimeStatus(Integer.valueOf(temp));
                        break;
                    case BleRequestConstant.AUTO_SETTING:
                        if (bytes[3] == BleRequestConstant.AUTO_ENGINE_START_ON) {
                            mPreferenceService.setStatusChange(R.id.switch_auto_engine, true);
                            setAutoEngineStartStatus(new SwitchData(true, RECEIVE));
                        } else if (bytes[3] == BleRequestConstant.AUTO_ENGINE_START_OFF) {
                            mPreferenceService.setStatusChange(R.id.switch_auto_engine, false);
                            setAutoEngineStartStatus(new SwitchData(false, RECEIVE));
                        } else if (bytes[3] == BleRequestConstant.AUTO_DOOR_UNLOCK_ON) {
                            mPreferenceService.setStatusChange(R.id.switch_auto_unlock, true);
                            setAutoDoorUnlockStatus(new SwitchData(true, RECEIVE));
                        } else if (bytes[3] == BleRequestConstant.AUTO_DOOR_UNLOCK_OFF) {
                            mPreferenceService.setStatusChange(R.id.switch_auto_unlock, false);
                            setAutoDoorUnlockStatus(new SwitchData(false, RECEIVE));
                        } else if (bytes[3] == BleRequestConstant.AUTO_DOOR_LOCK_ON) {
                            mPreferenceService.setStatusChange(R.id.switch_auto_lock, true);
                            setAutoDoorLockStatus(new SwitchData(true, RECEIVE));
                        } else if (bytes[3] == BleRequestConstant.AUTO_DOOR_LOCK_OFF) {
                            mPreferenceService.setStatusChange(R.id.switch_auto_lock, false);
                            setAutoDoorLockStatus(new SwitchData(false, RECEIVE));
                        } else if (bytes[3] == BleRequestConstant.AUTO_WELCOME_LIGHT_ON) {
                            mPreferenceService.setStatusChange(R.id.switch_auto_light, true);
                            setAutoWelcomeLightStatus(new SwitchData(true, RECEIVE));
                        } else {
                            mPreferenceService.setStatusChange(R.id.switch_auto_light, false);
                            setAutoWelcomeLightStatus(new SwitchData(false, RECEIVE));
                        }
                        break;
                    case BleRequestConstant.ALL_SETTING :
                        mBleService.notifyToDeviceAllSettingData();
                        break;
                    case BleRequestConstant.ALL_STATUS:
                        mBleService.notifyToDeviceAllStatusData();
                        break;
                }
            }
        };
    }
}
