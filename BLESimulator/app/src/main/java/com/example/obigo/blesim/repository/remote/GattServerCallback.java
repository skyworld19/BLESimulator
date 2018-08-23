package com.example.obigo.blesim.repository.remote;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.example.obigo.blesim.viewmodel.BleCallback;

import java.util.Arrays;
import java.util.HashSet;

public class GattServerCallback extends BluetoothGattServerCallback {
    private static final String TAG = GattServerCallback.class.getSimpleName();

    private HashSet<BluetoothDevice> mBluetoothDevices;
    private BleService mBleService;
    private String mDeviceName;
    private BleCallback mBleCallback;

    public GattServerCallback (BleService bleService, BleCallback callback) {
        mBluetoothDevices = new HashSet<>();
        this.mBleService = bleService;
        this.mBleCallback = callback;
    }

    @Override
    public void onConnectionStateChange(BluetoothDevice device, final int status, int newState) {
        super.onConnectionStateChange(device, status, newState);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                mBluetoothDevices.add(device);
                mBleService.setBluetoothDevices(mBluetoothDevices);
                if(device.getName()!=null){
                    mDeviceName = device.getName();
                } else {
                    mDeviceName = device.getAddress();
                }
                Log.v(TAG, "Connected to device: " + mDeviceName + " / " + device.getAddress());
                mBleService.setConnectionState(0, mDeviceName);
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                mBluetoothDevices.remove(device);
                mBleService.setBluetoothDevices(mBluetoothDevices);
                mBleService.setConnectionState(1, null);
                Log.v(TAG, "Disconnected from device");
            }
        } else {
            mBluetoothDevices.remove(device);
            mBleService.setBluetoothDevices(mBluetoothDevices);
            mBleService.setConnectionState(0, null);

            Log.e(TAG, "Error when connecting: " + status);
        }
    }

    @Override
    public void onServiceAdded(int status, BluetoothGattService service) {
        super.onServiceAdded(status, service);
        mBleService.setmServiceConnected(true);
        Log.w(TAG,"onServiceAdded");
    }

    @Override
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                            BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
        Log.d(TAG, "Device tried to read characteristic: " + characteristic.getUuid());
        Log.d(TAG, "Value: " + Arrays.toString(characteristic.getValue()));
        if (offset != 0) {
            mBleService.getBluetoothGattServer().sendResponse(device, requestId, BluetoothGatt.GATT_INVALID_OFFSET, offset,
                    /* value (optional) */ null);
            return;
        }
        Log.d(TAG, "device : "+device.getAddress() +" requestId : "+requestId);
        mBleService.getBluetoothGattServer().sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS,
                offset, characteristic.getValue());
    }

    @Override
    public void onNotificationSent(BluetoothDevice device, int status) {
        super.onNotificationSent(device, status);
        Log.v(TAG, "Notification sent. Status: " + status);
    }

    @Override
    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded,
                                             int offset, byte[] value) {
        super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite,
                responseNeeded, offset, value);
        Log.v(TAG, "Characteristic Write request: " + Arrays.toString(value));

        if (responseNeeded) {
            mBleCallback.receiveData(value);
            mBleService.getBluetoothGattServer().sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS,                   /* No need
                     +00to respond with an offset */ 0,
                    /* No need to respond with a value */ null);
        }
    }
}

