package com.example.obigo.blesim.repository.local;

public interface PreferenceHelper {

    boolean getEngineStatus();

    void setEngineStatus(boolean b);

    boolean getDoorStatus();

    void setDoorStatus(boolean b);

    boolean  getDefrostStatus();

    void setDefrostStatus(boolean b);

    int getTemperature();

    void setTemperature(int i);

    int getIdleTime();

    void setIdleTime(int i);

    boolean getAutoEngineStartStatus();

    void setAutoEngineStartStatus(boolean b);

    boolean getAutoDoorUnlockStatus();

    void setAutoDoorUnlockStatus(boolean b);

    boolean getAutoDoorLockStatus();

    void setAutoDoorLockStatus(boolean b);

    boolean getAutoWelcomeLightStatus();

    void setAutoWelcomeLightStatus(boolean b);

}
