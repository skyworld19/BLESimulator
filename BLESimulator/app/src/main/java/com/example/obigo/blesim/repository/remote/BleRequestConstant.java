package com.example.obigo.blesim.repository.remote;

public class BleRequestConstant {

    public static final byte SERVICE_REQUEST1 = 0x00;

    //Door
    public static final byte DOOR = 0x00;
    public static final byte DOOR_UNLOCK = 0x00;
    public static final byte DOOR_LOCK = 0x01;
    public static final byte DOOR_DATA_LENGTH = 0x02;

    //Engine
    public static final byte ENGINE = 0x01;
    public static final byte ENGINE_START = 0x01;
    public static final byte ENGINE_STOP = 0x02;
    public static final byte ENGINE_DATA_LENGTH = 0x09;

    //Horn&Light
    public static final byte HORN_LIGHT = 0x02;
    public static final byte HORN_AND_LIGHT = 0x00;
    public static final byte LIGHT_ONLY = 0x01;
    public static final byte HORN_AND_LIGHT_DATA_LENGTH = 0x02;
    public static final byte HORN_LIGHT_ON = 0x03;
    public static final byte HORN_LIGHT_OFF = 0x04;

    //Defrost
    public static final byte DEFROST = 0x05;
    public static final byte DEFROST_ON = 0x00;
    public static final byte DEFROST_OFF = 0x01;
    public static final byte DEFROST_DATA_LENGTH = 0x02;

    //Temperature
    public static final byte TEMPERATURE = 0x06;
    public static final byte TEMPERATURE_DATA_LENGTH = 0x02;

    //IdleTime
    public static final byte IDLETIME = 0x07;
    public static final byte IDLETIME_DATA_LENGTH = 0x02;

    //Auto Setting
    public static final byte AUTO_SETTING = 0x08;
    public static final byte AUTO_ENGINE_START_ON = 0x00;
    public static final byte AUTO_ENGINE_START_OFF = 0x01;
    public static final byte AUTO_DOOR_UNLOCK_ON = 0x02;
    public static final byte AUTO_DOOR_UNLOCK_OFF = 0x03;
    public static final byte AUTO_DOOR_LOCK_ON = 0x04;
    public static final byte AUTO_DOOR_LOCK_OFF = 0x05;
    public static final byte AUTO_WELCOME_LIGHT_ON = 0x06;
    public static final byte AUTO_WELCOME_LIGHT_OFF = 0x07;
    public static final byte AUTO_SETTING_DATA_LENGTH = 0x02;

    //All Setting Data
    public static final byte ALL_SETTING = 0x09;
    public static final byte ALL_SETTING_DATA_LENGTH = 0x18;

    //All Status Data
    public static final byte ALL_STATUS = 0x10;
    public static final byte ALL_STATUS_DATA_LENGTH = 0x18;

}
