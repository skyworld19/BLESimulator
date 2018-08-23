package com.example.obigo.blesim.repository.local;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference implements PreferenceHelper {

    private final SharedPreferences mPrefs;

    private static final String PREF_KEY_ENGINE_STATUS = "ENGINE_STATUS";
    private static final String PREF_KEY_DOOR_STATUS = "DOOR_STATUS";
    private static final String PREF_KEY_DEFROST_STATUS = "DEFROST_STATUS";
    private static final String PREF_KEY_TEMPERATURE = "TEMPERATURE";
    private static final String PREF_KEY_IDLETIME = "IDLETIME";
    private static final String PREF_KEY_AUTO_ENGINE_START_STATUS = "AUTO_ENGINE_START_STATUS";
    private static final String PREF_KEY_AUTO_DOOR_UNLOCK_STATUS = "AUTO_DOOR_UNLOCK_STATUS";
    private static final String PREF_KEY_AUTO_DOOR_LOCK_STATUS = "AUTO_DOOR_LOCK_STATUS";
    private static final String PREF_KEY_AUTO_WELCOMELIGHT_STATUS = "AUTO_WELCOMELIGHT_STATUS";

    public AppPreference(Context context, String prefFileName) {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }

    @Override
    public boolean getEngineStatus() {
        return mPrefs.getBoolean(PREF_KEY_ENGINE_STATUS, false);
    }

    @Override
    public void setEngineStatus(boolean b) {
        mPrefs.edit().putBoolean(PREF_KEY_ENGINE_STATUS, b).apply();
    }

    @Override
    public boolean getDoorStatus() {
        return mPrefs.getBoolean(PREF_KEY_DOOR_STATUS, false);
    }

    @Override
    public void setDoorStatus(boolean b) {
        mPrefs.edit().putBoolean(PREF_KEY_DOOR_STATUS, b).apply();
    }

    @Override
    public boolean getDefrostStatus() {
        return mPrefs.getBoolean(PREF_KEY_DEFROST_STATUS, false);
    }

    @Override
    public void setDefrostStatus(boolean b) {
        mPrefs.edit().putBoolean(PREF_KEY_DEFROST_STATUS, b).apply();
    }

    @Override
    public int getTemperature() {
        return mPrefs.getInt(PREF_KEY_TEMPERATURE, 17);
    }

    @Override
    public void setTemperature(int i) {
        mPrefs.edit().putInt(PREF_KEY_TEMPERATURE, i).apply();
    }

    @Override
    public int getIdleTime() {
        return mPrefs.getInt(PREF_KEY_IDLETIME, 3);
    }

    @Override
    public void setIdleTime(int i) {
        mPrefs.edit().putInt(PREF_KEY_IDLETIME, i).apply();
    }

    @Override
    public boolean getAutoEngineStartStatus() {
        return mPrefs.getBoolean(PREF_KEY_AUTO_ENGINE_START_STATUS, false);
    }

    @Override
    public void setAutoEngineStartStatus(boolean b) {
        mPrefs.edit().putBoolean(PREF_KEY_AUTO_ENGINE_START_STATUS, b).apply();
    }

    @Override
    public boolean getAutoDoorUnlockStatus() {
        return mPrefs.getBoolean(PREF_KEY_AUTO_DOOR_UNLOCK_STATUS, false);
    }

    @Override
    public void setAutoDoorUnlockStatus(boolean b) {
        mPrefs.edit().putBoolean(PREF_KEY_AUTO_DOOR_UNLOCK_STATUS, b).apply();
    }

    @Override
    public boolean getAutoDoorLockStatus() {
        return mPrefs.getBoolean(PREF_KEY_AUTO_DOOR_LOCK_STATUS, false);
    }

    @Override
    public void setAutoDoorLockStatus(boolean b) {
        mPrefs.edit().putBoolean(PREF_KEY_AUTO_DOOR_LOCK_STATUS, b).apply();
    }

    @Override
    public boolean getAutoWelcomeLightStatus() {
        return mPrefs.getBoolean(PREF_KEY_AUTO_WELCOMELIGHT_STATUS, false);
    }

    @Override
    public void setAutoWelcomeLightStatus(boolean b) {
        mPrefs.edit().putBoolean(PREF_KEY_AUTO_WELCOMELIGHT_STATUS, b).apply();
    }
}
