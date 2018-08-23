package com.example.obigo.blesim.repository.remote;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import com.example.obigo.blesim.App;
import com.example.obigo.blesim.R;
import com.example.obigo.blesim.viewmodel.BleCallback;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.UUID;

public class BleService {
    private static final String TAG = BleService.class.getSimpleName();

    private Context mContext;
    private BluetoothGattServer mGattServer;
    private BluetoothGattService mBluetoothGattService;
    private HashSet<BluetoothDevice> mBluetoothDevices;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private AdvertiseData mAdvData;
    private AdvertiseData mAdvScanResponse;
    private AdvertiseSettings mAdvSettings;
    private BluetoothLeAdvertiser mAdvertiser;
    private BluetoothGattServerCallback mGattServerCallback;
    private WeakReference<Context> mContextReference;
    private BleRequestData mBleRequestData;
    private boolean mServiceConnected;

    //public static final java.util.UUID SERVICE_UUID = java.util.UUID.fromString("00001110-0000-1000-8000-00805f9b34fb");
    public static final java.util.UUID SERVICE_UUID = java.util.UUID.fromString("00005500-d102-11e1-9b23-000240198212");

    //public static final java.util.UUID CHAR_UUID = java.util.UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    public static final java.util.UUID CHAR_UUID = java.util.UUID.fromString("00005502-d102-11e1-9b23-000240198212");
    public static final java.util.UUID BLE_SHIELD_RX = java.util.UUID.fromString("00005501-d102-11e1-9b23-000240198212");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION_UUID = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");
    // GATT
    private BluetoothGattService mService;
    private BluetoothGattCharacteristic mCharacteristicTx;
    private BluetoothGattCharacteristic mCharacteristicRx;
    private BleCallback mBleCallback;

    public BleService(Application application, BleCallback callback) {
        mContextReference = new WeakReference<Context>(application);
        mContext = mContextReference.get();
        mBleCallback = callback;
        mBleRequestData = new BleRequestData();
        mServiceConnected = false;
    }

    public void bleInitSetting() {
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothAdapter.setName("BLE Simulator");
        mCharacteristicTx = new BluetoothGattCharacteristic(CHAR_UUID, BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        mService = new BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        mService.addCharacteristic(mCharacteristicTx);
        mCharacteristicRx = new BluetoothGattCharacteristic(BLE_SHIELD_RX, BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
        mService.addCharacteristic(mCharacteristicRx);

        mBluetoothGattService = mService;
        mAdvSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)
                .build();
        mAdvData = new AdvertiseData.Builder()
                .setIncludeTxPowerLevel(true)
                .addServiceUuid(new ParcelUuid(SERVICE_UUID))
                .build();
        mAdvScanResponse = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .build();

        startAdvertising();
    }

    public void startAdvertising() {
        mGattServerCallback = new GattServerCallback(this, mBleCallback);
        mGattServer = mBluetoothManager.openGattServer(mContext, mGattServerCallback);
        // Add a service for a total of three services (Generic Attribute and Generic Access
        // are present by default).
        mGattServer.addService(mBluetoothGattService);
        if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            mAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
            mAdvertiser.startAdvertising(mAdvSettings, mAdvData, mAdvScanResponse, mAdvCallback);
        } else {
            mBleCallback.receiveBleConnectStatus(getString(R.string.status_noLeAdv));
        }
    }

    public void disconnectFromDevices() {
        Log.d(TAG, "Disconnecting devices...");
        for (BluetoothDevice device : mBluetoothManager.getConnectedDevices(
                BluetoothGattServer.GATT)) {
            Log.d(TAG, "Devices: " + device.getAddress() + " " + device.getName());
            mGattServer.cancelConnection(device);
        }
    }

    public void setConnectionState(int mode, String deviceName) {
        final String message;
        if(mode==0) {
            // connected
            message = getString(R.string.status_devicesConnected);
            Log.w(TAG," mConnectStatus.postValue, mode=0");
            mBleCallback.receiveBleConnectStatus(message);
            mBleCallback.receiveBleDeviceName(deviceName);
        } else {
            // disconnected
            Log.w(TAG," mConnectStatus.postValue, mode=1");
            message = getString(R.string.status_devicesDisconnected);
            mBleCallback.receiveBleConnectStatus(message);
            mBleCallback.receiveBleDeviceName("");
        }
    }

    public void sendNotificationToDevices(BluetoothGattCharacteristic characteristic) {
        Log.e(TAG, "sendNotificationToDevices");
        boolean indicate = (characteristic.getProperties()
                & BluetoothGattCharacteristic.PROPERTY_INDICATE)
                == BluetoothGattCharacteristic.PROPERTY_INDICATE;
        for (BluetoothDevice device : mBluetoothDevices) {
            // true for indication (acknowledge) and false for notification (unacknowledge).
            mGattServer.notifyCharacteristicChanged(device, characteristic, indicate);
        }
    }

    private final AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.e(TAG, "Not broadcasting: " + errorCode);
            int statusText;
            switch (errorCode) {
                case ADVERTISE_FAILED_ALREADY_STARTED:
                    statusText = R.string.status_advertising;
                    Log.w(TAG, "App was already advertising");
                    break;
                case ADVERTISE_FAILED_DATA_TOO_LARGE:
                    statusText = R.string.status_advDataTooLarge;
                    break;
                case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                    statusText = R.string.status_advFeatureUnsupported;
                    break;
                case ADVERTISE_FAILED_INTERNAL_ERROR:
                    statusText = R.string.status_advInternalError;
                    break;
                case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                    statusText = R.string.status_advTooManyAdvertisers;
                    break;
                default:
                    statusText = R.string.status_notAdvertising;
                    Log.wtf(TAG, "Unhandled error: " + errorCode);
            }
            mBleCallback.receiveBleDeviceName(getString(statusText));
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.v(TAG, "Broadcasting");
            mBleCallback.receiveBleDeviceName(getString(R.string.status_advertising));
        }
    };


    public String getString(int id) {
        return App.getContext().getString(id);
    }

    public BluetoothGattServer getBluetoothGattServer(){
        return mGattServer;
    }

    public void setBluetoothDevices(HashSet<BluetoothDevice> devices) {
        mBluetoothDevices = devices;
    }

    public void notifyToDevice(int id, boolean isChecked){
        mCharacteristicRx.setValue(mBleRequestData.makeRequestBytes(id, isChecked));
        sendNotificationToDevices(mCharacteristicRx);
    }

    public void notifyToDevice(int id, int value) {
        mCharacteristicRx.setValue(mBleRequestData.makeRequestBytes(id, value));
        sendNotificationToDevices(mCharacteristicRx);
    }

    public void notifyToDeviceAllSettingData(){
        mCharacteristicRx.setValue(mBleRequestData.sendAllSettingData());
        sendNotificationToDevices(mCharacteristicRx);
    }

    public void notifyToDeviceAllStatusData(){
        mCharacteristicRx.setValue(mBleRequestData.sendAllStatusData());
        sendNotificationToDevices(mCharacteristicRx);
    }

    public boolean getBluetoothState(){
        if(BluetoothGatt.GATT_SUCCESS==0 && mServiceConnected){
            return true;
        } else {
            return false;
        }
    }

    public void setmServiceConnected(boolean b){
        mServiceConnected = b;
    }
}
