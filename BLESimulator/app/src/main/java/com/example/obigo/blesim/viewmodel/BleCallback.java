package com.example.obigo.blesim.viewmodel;

public interface BleCallback {

    void receiveBleConnectStatus(String s);

    void receiveBleDeviceName(String s);

    void receiveData(byte [] bytes);

}
