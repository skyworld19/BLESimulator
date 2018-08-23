/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: ControlPagerActivity.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */

package hyundai.esds;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import hyundai.esds.fragments.AutomationSettingFragment;
import hyundai.esds.fragments.ClimateControlConfiguration;
import hyundai.esds.fragments.IdleTimeFragment;
import hyundai.esds.fragments.TemperatureFragment;
import hyundai.esds.fragments.VehicleStatusFragment;

import static hyundai.esds.HomeScreen.toasttobeDisplayed;
import static hyundai.esds.RBLService.automationSettingsEnabled;
import static hyundai.esds.RBLService.mDistMsgCounter;
import static hyundai.esds.ResponseAlertClass.sResponseLayoutAlertDialog;
import static hyundai.esds.fragments.AutomationSettingFragment.autoDoorUnlockEnabled;
import static hyundai.esds.fragments.AutomationSettingFragment.autodoorLockEnabled;
import static hyundai.esds.fragments.AutomationSettingFragment.welcomeLightEnabled;
import static hyundai.esds.fragments.IdleTimeFragment.mIdleTimeValue;
import static hyundai.esds.fragments.VehicleControlFragment.clickedGridPosition;
import static hyundai.esds.fragments.VehicleControlFragment.textString;

/**
 * Activity that holds all the fragments
 * Connection establishment will be done once after users enter into this screen
 */
public class ControlPagerActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    //Declaring memeber fields
    ViewPager vehicleControlViewPager;
    PagerAdapter viewpagerAdapter;
    private int numOfPages = 6;
    private final static String TAG = ControlPagerActivity.class.getSimpleName();
    private RBLService mBluetoothLeService;
    private Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();
    private String mDeviceAddress;
    static int mAutoCount = 0;
    public static Map<Float, Byte> mTempMap = new HashMap<>();
    Intent gattServiceIntent;

    public BluetoothAdapter mBluetoothAdapter;
    public static final int REQUEST_ENABLE_BT = 1;
    public Dialog mDialog;
    public static final long SCAN_PERIOD = 2000;
    public static List<BluetoothDevice> sDevices = new ArrayList<BluetoothDevice>();
    HashMap<String, String> deviceHashMap;
    private Animation slide_up;

    RelativeLayout mScanRequestBleDeviceLayout;
    Button mScanRequestButton;

    private static boolean sDeviceFound = false;
    public static boolean serviceConnected = false;
    public static String connectionStatusString = null;
    public static Activity sControlPagerActivity;

    public static float mTemperatureValue = (float) 17.0;
    public static boolean mDefrostEnabled = false;

    private SensorManager sensorManager;
    private Sensor accel;
    private StepDetector simpleStepDetector;
    private int numSteps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_control_pager);

        //Initializing member fields
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        mScanRequestBleDeviceLayout = (RelativeLayout) findViewById(R.id.scan_ble_devices_layout);
        mScanRequestButton = (Button) findViewById(R.id.scan_request_ble_device_button);
        mScanRequestBleDeviceLayout.setVisibility(View.INVISIBLE);
        vehicleControlViewPager = (ViewPager) findViewById(R.id.vehicle_control_viewpager);
        viewpagerAdapter = new PagerAdapter(getSupportFragmentManager(), numOfPages, "ControlPagerActivity");
        vehicleControlViewPager.setAdapter(viewpagerAdapter);
        vehicleControlViewPager.setPageTransformer(true, new DepthPageTransformer());

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        //intializing bluetooth parameters
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            toasttobeDisplayed("Ble not supported");
            finish();
        }

        // d
        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            toasttobeDisplayed("Ble not supported");
            finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        deviceHashMap = new HashMap<String, String>();
        scanLeDevice();
        showRoundProcessDialog(ControlPagerActivity.this, R.layout.loading_process_dialog_anim);

        load_mTempMapValues();

        sControlPagerActivity = ControlPagerActivity.this;


    }

    @Override
    public void onResume() {
        super.onResume();
        mDefrostEnabled = getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).getBoolean("Defrost", false);
        vehicleControlViewPager.setCurrentItem(getIntent().getIntExtra("CLICKEDLISTPOSITION", 0));
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        registerReceiver(broadcast_reciever, new IntentFilter("finish_activity"));
        registerReceiver(broadcast_reciever, new IntentFilter("automation"));
        registerReceiver(broadcast_reciever, new IntentFilter("defrost"));
        registerReceiver(broadcast_reciever, new IntentFilter("climate"));
        registerReceiver(broadcast_reciever, new IntentFilter("getData"));
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);


        mScanRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mScanRequestBleDeviceLayout.getVisibility() == View.VISIBLE) {
                    showLayout(false);
                }
                if (mBluetoothAdapter != null && mLeScanCallback != null) {
                    scanLeDevice();
                    showRoundProcessDialog(ControlPagerActivity.this, R.layout.loading_process_dialog_anim);
                }
            }
        });
        vehicleControlViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (vehicleControlViewPager.getCurrentItem() == 1) {
                    AutomationSettingFragment f = (AutomationSettingFragment) viewpagerAdapter.getFragmentHash(vehicleControlViewPager.getCurrentItem());
                    if (f != null) {
                        f.switchSetting();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //attempts for connecting the BLE unit(Here: LGIT)
        if (mBluetoothLeService != null)
            mBluetoothLeService.connect(mDeviceAddress);
    }

    @Override
    protected void onStop() {
        super.onStop();


//        unregisterReceiver(mGattUpdateReceiver);
//        unregisterReceiver(broadcast_reciever);
        sDeviceFound = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sResponseLayoutAlertDialog != null && sResponseLayoutAlertDialog.isShowing()) {
            sResponseLayoutAlertDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
            mBluetoothLeService.close();
        }
        serviceConnected = false;
        connectionStatusString = null;
    }

    //mLeScanCallBack method for searching the BLE device(here LGIT)
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (device != null) {
                            if (sDevices.indexOf(device) == -1)
                                sDevices.add(device);
                            deviceHashMap.put(device.getName(), device.getAddress());
                            System.out.println("devices:  " + device.getName());
                            //  Log.d(getClass().getSimpleName(),"Scan Record: "+Arrays.toString(scanRecord));
                            if (device.getName().equals("BLE Simulator")) {
                                sDeviceFound = true;
                                System.out.println("Rssi Value:  " + rssi);
                                startProcessing();
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                if (mDialog != null && mDialog.isShowing()) {
                                    mDialog.dismiss();
                                }

                            } else {
                                sDeviceFound = false;
                            }
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void startProcessing() {
        mDeviceAddress = deviceHashMap.get("BLE Simulator");
        Log.i(TAG, "mDeviceAddress : " + mDeviceAddress);
        gattServiceIntent = new Intent(this, RBLService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }


    /**
     * method for intiating search for BLE device(Here: LGIT)
     * After, 2 Second and 500 milliseconds , it will automatically stop searching for the BLE device(Reason: Scan method consumes lot of mobile battery)
     * After that, user can again initiate by clicking try again button
     */
    private void scanLeDevice() {
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                if (mDialog != null)
                    mDialog.dismiss();
                if (sDeviceFound) {
                    //show layout
                    showLayout(false);

                } else {
                    //hide layout
                    showLayout(true);
                }
            }
        }, 2500);
    }

    //Method to display bottom request layout
    private void showLayout(boolean check) {
        if (check && !serviceConnected) {
            mScanRequestBleDeviceLayout.startAnimation(slide_up);
            mScanRequestBleDeviceLayout.setVisibility(View.VISIBLE);
        } else {
            mScanRequestBleDeviceLayout.setVisibility(View.INVISIBLE);
        }
    }

    //Intilaizing Service Connection for Connecting to BLE unit
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            Log.w(TAG, "onServiceConnected");
            mBluetoothLeService = ((RBLService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            // Toast.makeText(getApplicationContext(),"Service Disconnected",Toast.LENGTH_SHORT).show();
            toasttobeDisplayed("Service Disconnected");


        }
    };


    //Initialing mGattUpdateReceiver for registering the changes of RBL service connection
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //Toast.makeText(getApplicationContext(),"GATT Service Disconnected",Toast.LENGTH_SHORT).show();
                //toasttobeDisplayed("GATT Service Disconnected");
                serviceConnected = false;
                connectionStatusString = "red";
                mBluetoothLeService.close();
                mBluetoothLeService.connect(mDeviceAddress);
            } else if (RBLService.ACTION_GATT_CONNECTED.equals(action)) {
                //toasttobeDisplayed("GATT Service Connected");
                serviceConnected = false;
                connectionStatusString = "orange";
            } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.w(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
                getGattService(mBluetoothLeService.getSupportedGattService());

                //hide the scan request layout if it is visible even after service is discovered
                if (mScanRequestBleDeviceLayout != null && mScanRequestBleDeviceLayout.getVisibility() == View.VISIBLE) {
                    mScanRequestBleDeviceLayout.setVisibility(View.INVISIBLE);
                }
                serviceConnected = true;
                connectionStatusString = "blue";
            } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
                byte[] bytes = intent.getByteArrayExtra(RBLService.EXTRA_DATA);
                Log.w(TAG, "bytes[2] : " + bytes[2]);
                bleReceiveData(bytes);
            }
        }
    };

    public void bleReceiveData(byte[] bytes) {
        SharedPreferences automationSettings = getSharedPreferences("Automation Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = automationSettings.edit();
        android.support.v4.app.Fragment fragment;
        byte[] rBytes;
        String temp;

        switch (bytes[2]) {
            case BleRequestConstant.DEFROST:
                fragment = getSupportFragmentManager().findFragmentById(R.id.vehicle_control_fragment);
                if (fragment != null && fragment instanceof ClimateControlConfiguration) {
                    ViewPager vp = ((ClimateControlConfiguration) fragment).getViewPager();
                    PagerAdapter adapter = ((ClimateControlConfiguration) fragment).getPageAdapter();
                    android.support.v4.app.Fragment f = adapter.getClimateFragmentHash(vp.getCurrentItem());
                    if (f instanceof TemperatureFragment && f != null) {
                        ((TemperatureFragment) f).receive(bytes);
                    } else {
                        if (bytes[3] == BleRequestConstant.DEFROST_ON)
                            editor.putBoolean("Defrost", true).apply();
                        else
                            editor.putBoolean("Defrost", false).apply();
                    }
                } else {
                    if (bytes[3] == BleRequestConstant.DEFROST_ON)
                        editor.putBoolean("Defrost", true).apply();
                    else
                        editor.putBoolean("Defrost", false).apply();
                }

                break;
            case BleRequestConstant.TEMPERATURE:
                rBytes = Arrays.copyOfRange(bytes, 3, bytes.length);
                temp = new String(rBytes);
                fragment = getSupportFragmentManager().findFragmentById(R.id.vehicle_control_fragment);
                if (fragment != null && fragment instanceof ClimateControlConfiguration) {
                    ViewPager vp = ((ClimateControlConfiguration) fragment).getViewPager();
                    PagerAdapter adapter = ((ClimateControlConfiguration) fragment).getPageAdapter();
                    android.support.v4.app.Fragment f = adapter.getClimateFragmentHash(vp.getCurrentItem());
                    if (f instanceof TemperatureFragment && f != null) {
                        ((TemperatureFragment) f).setTemperature(Integer.valueOf(temp));
                    } else {
                        editor.putFloat("Temperature", getTemperature(Integer.valueOf(temp))).apply();
                    }
                } else {
                    editor.putFloat("Temperature", getTemperature(Integer.valueOf(temp))).apply();
                }
                break;
            case BleRequestConstant.IDLETIME:
                rBytes = Arrays.copyOfRange(bytes, 3, bytes.length);
                temp = new String(rBytes);
                fragment = getSupportFragmentManager().findFragmentById(R.id.vehicle_control_fragment);
                if (fragment != null && fragment instanceof ClimateControlConfiguration) {
                    ViewPager vp = ((ClimateControlConfiguration) fragment).getViewPager();
                    PagerAdapter adapter = ((ClimateControlConfiguration) fragment).getPageAdapter();
                    android.support.v4.app.Fragment f = adapter.getClimateFragmentHash(vp.getCurrentItem());
                    if (f instanceof IdleTimeFragment && f != null) {
                        ((IdleTimeFragment) f).setIdleTime(Integer.valueOf(temp));
                    } else {
                        editor.putInt("Idle Time", Integer.valueOf(temp) + 2).apply();
                    }
                } else {
                    editor.putInt("Idle Time", Integer.valueOf(temp) + 2).apply();
                }
            case BleRequestConstant.AUTO_SETTING:
                if (vehicleControlViewPager.getCurrentItem() == 1) {
                    AutomationSettingFragment f = (AutomationSettingFragment) viewpagerAdapter.getFragmentHash(vehicleControlViewPager.getCurrentItem());
                    if (f != null) {
                        f.receive(bytes);
                    }
                } else {
                    if (bytes[3] == BleRequestConstant.AUTO_ENGINE_START_ON)
                        editor.putBoolean("Auto Engine Start", true);
                    else if (bytes[3] == BleRequestConstant.AUTO_ENGINE_START_OFF)
                        editor.putBoolean("Auto Engine Start", false);
                    else if (bytes[3] == BleRequestConstant.AUTO_DOOR_UNLOCK_ON)
                        editor.putBoolean("Auto Door Unlock", true);
                    else if (bytes[3] == BleRequestConstant.AUTO_DOOR_UNLOCK_OFF)
                        editor.putBoolean("Auto Door Unlock", false);
                    else if (bytes[3] == BleRequestConstant.AUTO_DOOR_LOCK_ON)
                        editor.putBoolean("Auto Door Lock", true);
                    else if (bytes[3] == BleRequestConstant.AUTO_DOOR_LOCK_OFF)
                        editor.putBoolean("Auto Door Lock", false);
                    else if (bytes[3] == BleRequestConstant.AUTO_WELCOME_LIGHT_ON)
                        editor.putBoolean("Welcome Light", true);
                    else
                        editor.putBoolean("Welcome Light", false);

                    editor.apply();
                    editor.commit();
                }
                break;
            case BleRequestConstant.ALL_SETTING:
                if (vehicleControlViewPager.getCurrentItem() == 1) {
                    AutomationSettingFragment f = (AutomationSettingFragment) viewpagerAdapter.getFragmentHash(vehicleControlViewPager.getCurrentItem());
                    if (f != null) {
                        f.receiveAllData(bytes);
                    }
                } else {
                    if (bytes[3] == BleRequestConstant.AUTO_ENGINE_START_ON) {
                        editor.putBoolean("Auto Engine Start", true);
                    } else {
                        editor.putBoolean("Auto Engine Start", false);
                    }
                    if (bytes[4] == BleRequestConstant.AUTO_DOOR_LOCK_ON) {
                        editor.putBoolean("Auto Door Lock", true);
                    } else {
                        editor.putBoolean("Auto Door Lock", false);
                    }
                    if (bytes[5] == BleRequestConstant.AUTO_DOOR_UNLOCK_ON) {
                        editor.putBoolean("Auto Door Unlock", true);
                    } else {
                        editor.putBoolean("Auto Door Unlock", false);
                    }
                    if (bytes[6] == BleRequestConstant.AUTO_WELCOME_LIGHT_ON) {
                        editor.putBoolean("Welcome Light", true);
                    } else {
                        editor.putBoolean("Welcome Light", false);
                    }
                    editor.apply();
                    editor.commit();
                }
                break;
            case BleRequestConstant.ALL_STATUS:
                fragment = getSupportFragmentManager().findFragmentById(R.id.vehicle_control_fragment);
                if (fragment != null && fragment instanceof VehicleStatusFragment) {
                    ((VehicleStatusFragment) fragment).receive(bytes);
                }
                break;
        }
    }

    private long mUnlockCounter = 0, mWelcomeLightCounter = 0;
    private boolean mDistLockMsgSent = false;

    //Initializing broadcast receiver for registering the click of all control buttons form variious screen with action("finish_activity","automation")
    BroadcastReceiver broadcast_reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {

            try {
                String action = intent.getAction();
                Log.w(TAG, "broadcast_reciever : " + action + " : " + textString[clickedGridPosition]);
                final byte[] mSendMessage = new byte[20];
                for (int i = 0; i < mSendMessage.length; i++) {
                    mSendMessage[i] = 0;
                }

             /*   BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
                Log.w(TAG, "UUID_BLE_SHIELD_TX : "+characteristic);
                map.put(characteristic.getUuid(), characteristic);

                BluetoothGattCharacteristic characteristicRx = gattService.getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
                Log.w(TAG, "UUID_BLE_SHIELD_RX : "+characteristicRx);
                mBluetoothLeService.setCharacteristicNotification(characteristicRx,true);
                mBluetoothLeService.readCharacteristic(characteristicRx);*/

                final BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);

               /* if(characteristic == null){
                    Log.w(TAG, "Custom BLE Service not found");
                    return;
                }*/

                if (action.equals("finish_activity")) {
                    // DO WHATEVER YOU WANT.
                    String trueorfalse = intent.getExtras().getString(CustomDialogClass.RESULTFROMBDCST);
                    if (trueorfalse.equals("true")) {
                        if (textString[clickedGridPosition].equals("Lock")) {
                            mSendMessage[0] = BleRequestConstant.SERVICE_REQUEST1;
                            mSendMessage[1] = BleRequestConstant.DOOR_DATA_LENGTH;
                            mSendMessage[2] = BleRequestConstant.DOOR;
                            mSendMessage[3] = BleRequestConstant.DOOR_LOCK;
                        } else if (textString[clickedGridPosition].equals("Unlock")) {
                            mSendMessage[0] = BleRequestConstant.SERVICE_REQUEST1;
                            mSendMessage[1] = BleRequestConstant.DOOR_DATA_LENGTH;
                            mSendMessage[2] = BleRequestConstant.DOOR;
                            mSendMessage[3] = BleRequestConstant.DOOR_UNLOCK;
                        } else if (textString[clickedGridPosition].equals("Horn \nLight")) {
                            mSendMessage[0] = BleRequestConstant.SERVICE_REQUEST1;
                            mSendMessage[1] = BleRequestConstant.HORN_AND_LIGHT_DATA_LENGTH;
                            mSendMessage[2] = BleRequestConstant.HORN_LIGHT;
                            mSendMessage[3] = BleRequestConstant.HORN_AND_LIGHT;
                        } else if (textString[clickedGridPosition].equals("Light")) {
                            mSendMessage[0] = BleRequestConstant.SERVICE_REQUEST1;
                            mSendMessage[1] = BleRequestConstant.HORN_AND_LIGHT_DATA_LENGTH;
                            mSendMessage[2] = BleRequestConstant.HORN_LIGHT;
                            mSendMessage[3] = BleRequestConstant.LIGHT_ONLY;
                        } else if (textString[clickedGridPosition].equals("Vehicle \nStatus")) {
                            mSendMessage[0] = BleRequestConstant.SERVICE_REQUEST1;
                            mSendMessage[1] = BleRequestConstant.ALL_STATUS_DATA_LENGTH;
                            mSendMessage[2] = BleRequestConstant.ALL_STATUS;
                        } else if (textString[clickedGridPosition].equals("Engine \nStart")) {
                            mSendMessage[0] = BleRequestConstant.SERVICE_REQUEST1;
                            mSendMessage[1] = BleRequestConstant.ENGINE_DATA_LENGTH;
                            mSendMessage[2] = BleRequestConstant.ENGINE;
                            mSendMessage[3] = BleRequestConstant.ENGINE_START;
                         /*   mSendMessage[2] = 0x07;
                            mSendMessage[3] = 0x02;
                            mSendMessage[4] = 0x01;

                            mSendMessage[5] = (byte) (mDefrostEnabled?0x01:0x00);
                            mSendMessage[6] = mTempMap.get(mTemperatureValue);
                            mSendMessage[7] = 0x00;
                            mSendMessage[8] = (byte) mIdleTimeValue;
                            if (mIdleTimeValue == 10) {
                                mSendMessage[8] = 0x0A;
                            }
                            mSendMessage[9] = 0x00;*/
                        } else if (textString[clickedGridPosition].equals("Engine \nStop")) {
                            mSendMessage[0] = BleRequestConstant.SERVICE_REQUEST1;
                            mSendMessage[1] = BleRequestConstant.ENGINE_DATA_LENGTH;
                            mSendMessage[2] = BleRequestConstant.ENGINE;
                            mSendMessage[3] = BleRequestConstant.ENGINE_STOP;
                        } else {
                            //do nothing
                        }

                        //reverseByte(mSendMessage);
                        if (serviceConnected) {
                            characteristic.setValue(mSendMessage);
                            mBluetoothLeService.writeCharacteristic(characteristic);
                            toasttobeDisplayed(textString[clickedGridPosition].replace("\n", " ") + " request sent");
                        } else {
                            System.out.println("Not connected ");
                        }

                    } else {
                        //do nothing
                    }

                }

                if (action.equals("automation")) {
                    String type = intent.getExtras().getString("autoType");
                    boolean isChecked = false;
                    mSendMessage[0] = BleRequestConstant.SERVICE_REQUEST1;
                    mSendMessage[1] = BleRequestConstant.AUTO_SETTING_DATA_LENGTH;
                    mSendMessage[2] = BleRequestConstant.AUTO_SETTING;
                    if (type.equals("EngineStart")) {
                        isChecked = getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).getBoolean("Auto Engine Start", false);
                        if (isChecked) {
                            mSendMessage[3] = BleRequestConstant.AUTO_ENGINE_START_ON;
                        } else {
                            mSendMessage[3] = BleRequestConstant.AUTO_ENGINE_START_OFF;
                        }
                    } else if (type.equals("DoorUnlock")) {
                        isChecked = getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).getBoolean("Auto Door Unlock", false);
                        if (isChecked) {
                            mSendMessage[3] = BleRequestConstant.AUTO_DOOR_UNLOCK_ON;
                        } else {
                            mSendMessage[3] = BleRequestConstant.AUTO_DOOR_UNLOCK_OFF;
                        }
                    } else if (type.equals("DoorLock")) {
                        isChecked = getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).getBoolean("Auto Door Lock", false);
                        if (isChecked) {
                            mSendMessage[3] = BleRequestConstant.AUTO_DOOR_LOCK_ON;
                        } else {
                            mSendMessage[3] = BleRequestConstant.AUTO_DOOR_LOCK_OFF;
                        }
                    } else {
                        isChecked = getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).getBoolean("Welcome Light", false);
                        if (isChecked) {
                            mSendMessage[3] = BleRequestConstant.AUTO_WELCOME_LIGHT_ON;
                        } else {
                            mSendMessage[3] = BleRequestConstant.AUTO_WELCOME_LIGHT_OFF;
                        }
                    }

                    if (serviceConnected) {
                        characteristic.setValue(mSendMessage);
                        mBluetoothLeService.writeCharacteristic(characteristic);
                        toasttobeDisplayed(type + " " + isChecked + " request sent");
                    } else {
                        System.out.println("Not connected ");
                    }

                   /* Handler handler = new Handler();
//                if(mAutoCount<1) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            BluetoothGattCharacteristic characteristic = map.get(RBLService.UUID_BLE_SHIELD_TX);
                            byte[] autoSendEngineMsg = {0x00, 0x00, 0x07, 0x02, 0x02, 0x01, 0x0A, 0x00, 0x0A, 0x00};
                            byte[] autoDoorUnlockMsg = {0x00, 0x00, 0x01, 0x00};
                            byte[] autoDoorLockMsg = {0x00, 0x00, 0x01, 0x01};
                            byte[] welcomeLightMsg = {0x00, 0x00, 0x01, 0x06};
                            if (automationSettingsEnabled) {
                                mAutoCount++;
                                mDistLockMsgSent = false;
                                if (mAutoCount == 1) {


*//*                                if (autoEngineStartEnabled) {
                                    characteristic.setValue(autoSendEngineMsg);
                                    mBluetoothLeService.writeCharacteristic(characteristic);
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }*//*

                                    if ((mDistMsgCounter - mUnlockCounter > 3) && (sResponseLayoutAlertDialog != null) &&
                                            (!sResponseLayoutAlertDialog.isShowing()) && numSteps > 2) {
                                        if (autoDoorUnlockEnabled) {
                                            characteristic.setValue(autoDoorUnlockMsg);
                                            mBluetoothLeService.writeCharacteristic(characteristic);


                                            mUnlockCounter = mDistMsgCounter;
                                            clickedGridPosition = 8;
                                            try {
                                                Thread.sleep(2000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if (!welcomeLightEnabled) {
                                                numSteps = 0;
                                            }
                                        }
                                    }
                                    if ((mDistMsgCounter - mWelcomeLightCounter > 3 && numSteps > 2)) {
                                        if (sResponseLayoutAlertDialog == null) {
                                            if (welcomeLightEnabled) {
                                                characteristic.setValue(welcomeLightMsg);
                                                mBluetoothLeService.writeCharacteristic(characteristic);
                                                numSteps = 0;
                                                mWelcomeLightCounter = mDistMsgCounter;
                                                clickedGridPosition = 7;
                                            }
                                        } else if (!sResponseLayoutAlertDialog.isShowing()) {
                                            if (welcomeLightEnabled) {
                                                characteristic.setValue(welcomeLightMsg);
                                                mBluetoothLeService.writeCharacteristic(characteristic);
                                                numSteps = 0;
                                                mWelcomeLightCounter = mDistMsgCounter;
                                                clickedGridPosition = 7;
                                            }
                                        }
                                    }
                                }
                            } else {
                                mAutoCount = 0;

                                if ((!mDistLockMsgSent) && (sResponseLayoutAlertDialog != null) &&
                                        (!sResponseLayoutAlertDialog.isShowing()) && numSteps > 2) {
                                    if (autodoorLockEnabled) {
                                        characteristic.setValue(autoDoorLockMsg);
                                        mBluetoothLeService.writeCharacteristic(characteristic);
                                        numSteps = 0;
                                        clickedGridPosition = 6;
                                    }
                                    mDistLockMsgSent = true;
                                }
                            }

                        }

                    }, 1500);*/

                }
                if (action.equals("defrost")) {
                    boolean isChecked = intent.getExtras().getBoolean("isChecked");
                    mSendMessage[0] = BleRequestConstant.SERVICE_REQUEST1;
                    mSendMessage[1] = BleRequestConstant.DEFROST_DATA_LENGTH;
                    mSendMessage[2] = BleRequestConstant.DEFROST;
                    if (isChecked)
                        mSendMessage[3] = BleRequestConstant.DEFROST_ON;
                    else
                        mSendMessage[3] = BleRequestConstant.DEFROST_OFF;

                    if (serviceConnected) {
                        characteristic.setValue(mSendMessage);
                        mBluetoothLeService.writeCharacteristic(characteristic);
                        toasttobeDisplayed("defrost" + isChecked + " request sent");
                    } else {
                        System.out.println("Not connected ");
                    }
                }

                if (action.equals("climate")) {
                    int value = intent.getExtras().getInt("value");
                    String type = intent.getExtras().getString("climateType");
                    byte[] bytes = new byte[3];
                    byte[] valueBytes = String.valueOf(value).getBytes();
                    byte[] sendBytes = new byte[bytes.length + valueBytes.length];
                    if (type.equals("temperature")) {
                        // temperature
                        bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                        bytes[1] = BleRequestConstant.TEMPERATURE_DATA_LENGTH;
                        bytes[2] = BleRequestConstant.TEMPERATURE;
                        System.arraycopy(bytes, 0, sendBytes, 0, bytes.length);
                        System.arraycopy(valueBytes, 0, sendBytes, bytes.length, valueBytes.length);
                    } else {
                        // idle time
                        bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                        bytes[1] = BleRequestConstant.IDLETIME_DATA_LENGTH;
                        bytes[2] = BleRequestConstant.IDLETIME;
                        System.arraycopy(bytes, 0, sendBytes, 0, bytes.length);
                        System.arraycopy(valueBytes, 0, sendBytes, bytes.length, valueBytes.length);
                    }

                    if (serviceConnected) {
                        characteristic.setValue(sendBytes);
                        mBluetoothLeService.writeCharacteristic(characteristic);
                        toasttobeDisplayed("climate" + type + " request sent");
                    } else {
                        System.out.println("Not connected ");
                    }
                }

                if (action.equals("getData")) {
                    byte[] bytes = new byte[20];
                    bytes[0] = BleRequestConstant.SERVICE_REQUEST1;
                    bytes[1] = BleRequestConstant.ALL_SETTING_DATA_LENGTH;
                    bytes[2] = BleRequestConstant.ALL_SETTING;

                    if (serviceConnected) {
                        characteristic.setValue(bytes);
                        mBluetoothLeService.writeCharacteristic(characteristic);
                    } else {
                        System.out.println("Not connected ");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This
     * should be invoked only after {@code BluetoothGatt#discoverServices()}
     * completes successfully.
     */
    private void getGattService(BluetoothGattService gattService) {
        if (gattService == null)
            return;
        Log.w(TAG, "getGattService");
        BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
        Log.w(TAG, "UUID_BLE_SHIELD_TX : " + characteristic);
        map.put(characteristic.getUuid(), characteristic);

        BluetoothGattCharacteristic characteristicRx = gattService.getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
        Log.w(TAG, "UUID_BLE_SHIELD_RX : " + characteristicRx);
        mBluetoothLeService.setCharacteristicNotification(characteristicRx, true);
        mBluetoothLeService.readCharacteristic(characteristicRx);
    }


    //Adding actions to the mGattUpdateReceiver
    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);

        intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }


    //Showing round progress bar to wait for the nearest ble devices to be detected
    public void showRoundProcessDialog(Context mContext, int layout) {
        DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_HOME
                        || keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                }
                return false;
            }
        };

        mDialog = new AlertDialog.Builder(mContext).create();
        mDialog.setOnKeyListener(keyListener);
        mDialog.show();
        // 娉ㄦ��姝ゅ��瑕���惧��show涔���� ������浼���ュ��甯�
        mDialog.setContentView(layout);
    }

    //loading mTempMapValues Hashmap
    public void load_mTempMapValues() {
        mTempMap.put((float) 17.0, (byte) 0x06);
        mTempMap.put((float) 17.5, (byte) 0x07);
        mTempMap.put((float) 18.0, (byte) 0x08);
        mTempMap.put((float) 18.5, (byte) 0x09);
        mTempMap.put((float) 19.0, (byte) 0x0A);
        mTempMap.put((float) 19.5, (byte) 0x0B);
        mTempMap.put((float) 20.0, (byte) 0x0C);
        mTempMap.put((float) 20.5, (byte) 0x0D);
        mTempMap.put((float) 21.0, (byte) 0x0E);
        mTempMap.put((float) 21.5, (byte) 0x0F);
        mTempMap.put((float) 22.0, (byte) 0x10);
        mTempMap.put((float) 22.5, (byte) 0x11);
        mTempMap.put((float) 23.0, (byte) 0x12);
        mTempMap.put((float) 23.5, (byte) 0x13);
        mTempMap.put((float) 24.0, (byte) 0x14);
        mTempMap.put((float) 24.5, (byte) 0x15);
        mTempMap.put((float) 25.0, (byte) 0x16);
        mTempMap.put((float) 25.5, (byte) 0x17);
        mTempMap.put((float) 26.0, (byte) 0x18);
        mTempMap.put((float) 26.5, (byte) 0x19);
        mTempMap.put((float) 27.0, (byte) 0x1A);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    @Override
    public void step(long timeNs) {
        numSteps++;
    }

    public Float getTemperature(int value) {
        float f = value + 170;
        f = f / 10;
        return f;
    }

}
