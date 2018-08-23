package com.example.obigo.blesim.repository.remote;


import com.example.obigo.blesim.App;
import com.example.obigo.blesim.R;
import com.example.obigo.blesim.repository.local.AppPreference;

public class BleRequestData {
    private static final String TAG = BleRequestData.class.getSimpleName();
    private AppPreference mPrefs;

    public BleRequestData() {
        mPrefs = new AppPreference(App.getContext(), "Settings");
    }

    public byte[] makeRequestBytes(int id, boolean isChecked) {
        byte[] bytes = new byte[20];

        switch (id) {
            case R.id.switch_door:
                bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                bytes[1] = BleRequestConstant.DOOR_DATA_LENGTH;
                bytes[2] = BleRequestConstant.DOOR;
                if (isChecked) {
                    bytes[3] = BleRequestConstant.DOOR_LOCK;
                } else {
                    bytes[3] = BleRequestConstant.DOOR_UNLOCK;
                }
                break;
            case R.id.switch_engine:
                bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                bytes[1] = BleRequestConstant.ENGINE_DATA_LENGTH;
                bytes[2] = BleRequestConstant.ENGINE;
                if (isChecked) {
                    bytes[3] = BleRequestConstant.ENGINE_START;
                } else {
                    bytes[3] = BleRequestConstant.ENGINE_STOP;
                }
                break;
            case R.id.switch_horn_light:
                bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                bytes[1] = BleRequestConstant.HORN_AND_LIGHT_DATA_LENGTH;
                bytes[2] = BleRequestConstant.HORN_LIGHT;
                if (isChecked) {
                    bytes[3] = BleRequestConstant.HORN_AND_LIGHT;
                    bytes[4] = BleRequestConstant.HORN_LIGHT_ON;
                } else {
                    bytes[3] = BleRequestConstant.HORN_AND_LIGHT;
                    bytes[4] = BleRequestConstant.HORN_LIGHT_OFF;
                }
                break;
            case R.id.switch_light:
                bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                bytes[1] = BleRequestConstant.HORN_AND_LIGHT_DATA_LENGTH;
                bytes[2] = BleRequestConstant.HORN_LIGHT;
                if (isChecked) {
                    bytes[3] = BleRequestConstant.LIGHT_ONLY;
                    bytes[4] = BleRequestConstant.HORN_LIGHT_ON;
                } else {
                    bytes[3] = BleRequestConstant.LIGHT_ONLY;
                    bytes[4] = BleRequestConstant.HORN_LIGHT_OFF;
                }
                break;
            case R.id.switch_defrost:
                bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                bytes[1] = BleRequestConstant.DEFROST_DATA_LENGTH;
                bytes[2] = BleRequestConstant.DEFROST;
                if (isChecked)
                    bytes[3] = BleRequestConstant.DEFROST_ON;
                else
                    bytes[3] = BleRequestConstant.DEFROST_OFF;
                break;
            case R.id.switch_auto_engine:
                bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                bytes[1] = BleRequestConstant.AUTO_SETTING_DATA_LENGTH;
                bytes[2] = BleRequestConstant.AUTO_SETTING;
                if (isChecked) {
                    bytes[3] = BleRequestConstant.AUTO_ENGINE_START_ON;
                } else {
                    bytes[3] = BleRequestConstant.AUTO_ENGINE_START_OFF;
                }
                break;
            case R.id.switch_auto_unlock:
                bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                bytes[1] = BleRequestConstant.AUTO_SETTING_DATA_LENGTH;
                bytes[2] = BleRequestConstant.AUTO_SETTING;
                if (isChecked) {
                    bytes[3] = BleRequestConstant.AUTO_DOOR_UNLOCK_ON;
                } else {
                    bytes[3] = BleRequestConstant.AUTO_DOOR_UNLOCK_OFF;
                }
                break;
            case R.id.switch_auto_lock:
                bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                bytes[1] = BleRequestConstant.AUTO_SETTING_DATA_LENGTH;
                bytes[2] = BleRequestConstant.AUTO_SETTING;
                if (isChecked) {
                    bytes[3] = BleRequestConstant.AUTO_DOOR_LOCK_ON;
                } else {
                    bytes[3] = BleRequestConstant.AUTO_DOOR_LOCK_OFF;
                }
                break;
            case R.id.switch_auto_light:
                bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                bytes[1] = BleRequestConstant.AUTO_SETTING_DATA_LENGTH;
                bytes[2] = BleRequestConstant.AUTO_SETTING;
                if (isChecked) {
                    bytes[3] = BleRequestConstant.AUTO_WELCOME_LIGHT_ON;
                } else {
                    bytes[3] = BleRequestConstant.AUTO_WELCOME_LIGHT_OFF;
                }
                break;
        }

        return bytes;
    }

    public byte[] makeRequestBytes(int id, int value) {
        byte[] bytes = new byte[3];
        byte[] valueBytes = String.valueOf(value).getBytes();
        byte[] sendBytes = new byte[bytes.length + valueBytes.length];
        switch (id) {
            case R.id.btn_temperature:
                bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                bytes[1] = BleRequestConstant.TEMPERATURE_DATA_LENGTH;
                bytes[2] = BleRequestConstant.TEMPERATURE;
                System.arraycopy(bytes, 0, sendBytes, 0, bytes.length);
                System.arraycopy(valueBytes, 0, sendBytes, bytes.length, valueBytes.length);
                break;
            case R.id.btn_idle_time:
                bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                bytes[1] = BleRequestConstant.IDLETIME_DATA_LENGTH;
                bytes[2] = BleRequestConstant.IDLETIME;
                System.arraycopy(bytes, 0, sendBytes, 0, bytes.length);
                System.arraycopy(valueBytes, 0, sendBytes, bytes.length, valueBytes.length);
                break;
        }

        return sendBytes;
    }

    public byte[] sendAllStatusData() {
        byte[] bytes = new byte[6];
        byte[] temperatureBytes = String.valueOf(mPrefs.getTemperature()).getBytes();
        byte[] sendBytes = new byte[bytes.length + temperatureBytes.length];

        bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
        bytes[1] = BleRequestConstant.ALL_STATUS_DATA_LENGTH;
        bytes[2] = BleRequestConstant.ALL_STATUS;
        if (mPrefs.getDoorStatus()) {
            bytes[3] = BleRequestConstant.DOOR_LOCK;
        } else {
            bytes[3] = BleRequestConstant.DOOR_UNLOCK;
        }
        if (mPrefs.getEngineStatus()) {
            bytes[4] = BleRequestConstant.ENGINE_START;
        } else {
            bytes[4] = BleRequestConstant.ENGINE_STOP;
        }
        if (mPrefs.getDefrostStatus()) {
            bytes[5] = BleRequestConstant.DEFROST_ON;
        } else {
            bytes[5] = BleRequestConstant.DEFROST_OFF;
        }

        System.arraycopy(bytes, 0, sendBytes, 0, bytes.length);
        System.arraycopy(temperatureBytes, 0, sendBytes, bytes.length, temperatureBytes.length);

        return sendBytes;
    }

    public byte[] sendAllSettingData() {
        // engine start, temperature, defrost, door, setting값 4가지
        byte[] bytes = new byte[20];

        bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
        bytes[1] = BleRequestConstant.ALL_SETTING_DATA_LENGTH;
        bytes[2] = BleRequestConstant.ALL_SETTING;
        if (mPrefs.getAutoEngineStartStatus()) {
            bytes[3] = BleRequestConstant.AUTO_ENGINE_START_ON;
        } else {
            bytes[3] = BleRequestConstant.AUTO_ENGINE_START_OFF;
        }
        if (mPrefs.getAutoDoorLockStatus()) {
            bytes[4] = BleRequestConstant.AUTO_DOOR_LOCK_ON;
        } else {
            bytes[4] = BleRequestConstant.AUTO_DOOR_LOCK_OFF;
        }
        if (mPrefs.getAutoDoorUnlockStatus()) {
            bytes[5] = BleRequestConstant.AUTO_DOOR_UNLOCK_ON;
        } else {
            bytes[5] = BleRequestConstant.AUTO_DOOR_UNLOCK_OFF;
        }
        if (mPrefs.getAutoWelcomeLightStatus()) {
            bytes[6] = BleRequestConstant.AUTO_WELCOME_LIGHT_ON;
        } else {
            bytes[6] = BleRequestConstant.AUTO_WELCOME_LIGHT_OFF;
        }

        return bytes;
    }

}
