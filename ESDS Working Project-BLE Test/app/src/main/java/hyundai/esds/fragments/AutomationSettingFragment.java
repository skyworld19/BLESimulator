/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: AutomationSettingFragment.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */
package hyundai.esds.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import hyundai.esds.BleRequestConstant;
import hyundai.esds.R;
import hyundai.esds.RBLService;

import static hyundai.esds.ControlPagerActivity.connectionStatusString;
import static hyundai.esds.ControlPagerActivity.makeGattUpdateIntentFilter;
import static hyundai.esds.CustomDialogClass.RESULTFROMBDCST;

/**
 * Sends automatic messages to the vehicle if the corresponding fields are enabled
 */
public class AutomationSettingFragment extends Fragment {

    //Intializing member fields
    ImageButton backImBtAutomationSettingFragment;
    Switch mAutoEngineStart, mAutoDoorUnlock, mWelcomeLight, mAutoDoorLock;
    public static boolean autoEngineStartEnabled, autoDoorUnlockEnabled, welcomeLightEnabled, autodoorLockEnabled;
    public ImageView mImgViewConnectionStatusAutomation;
    public Context mContext;
    private static final int SEND = 0;
    private static final int RECEIVE = 1;
    private int dataStatus = 0;
    SharedPreferences automationSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("automationfragment", "oncreateview");
        View v = inflater.inflate(R.layout.fragment_automation_setting, container, false);
        backImBtAutomationSettingFragment = (ImageButton) v.findViewById(R.id.imagebutton_back_automation_setting_fragment);
        mAutoEngineStart = v.findViewById(R.id.ble_conn_eng_auto_switch);
        mAutoDoorUnlock = v.findViewById(R.id.near_dist_door_unlock_auto_switch);
        mWelcomeLight = v.findViewById(R.id.welcome_light_auto_switch);
        mAutoDoorLock = v.findViewById(R.id.near_dist_door_lock_auto_switch);

        mImgViewConnectionStatusAutomation = v.findViewById(R.id.connection_status_automation);

        //Shared preferences to store the parameters of engine start, near distance unlock,welcome light and light
        //And for the first time, these fields are default set to not enabled position
        automationSettings = getActivity().getSharedPreferences("Automation Settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = automationSettings.edit();
        switchSetting();

        //register changes for engine start check box of the screen
        mAutoEngineStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Intent intent = new Intent("automation");
                intent.putExtra("autoType", "EngineStart");
                if (isChecked) {
                    compoundButton.setText("Automatic");
                    autoEngineStartEnabled = true;
                    editor.putBoolean("Auto Engine Start", true);
                    editor.apply();
                    editor.commit();
                } else {
                    compoundButton.setText("Manual");
                    autoEngineStartEnabled = false;
                    editor.putBoolean("Auto Engine Start", false);
                    editor.apply();
                    editor.commit();
                }

                checkDataStatus(intent);
            }
        });

        //register changes for door unlock check box of the screen
        mAutoDoorUnlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Intent intent = new Intent("automation");
                intent.putExtra("autoType", "DoorUnlock");
                if (isChecked) {
                    compoundButton.setText("Automatic");
                    autoDoorUnlockEnabled = true;
                    editor.putBoolean("Auto Door Unlock", true);
                    editor.apply();
                    editor.commit();
                } else {
                    compoundButton.setText("Manual");
                    autoDoorUnlockEnabled = false;
                    editor.putBoolean("Auto Door Unlock", false);
                    editor.apply();
                    editor.commit();
                }

                checkDataStatus(intent);
            }
        });

        //register changes for door lock check box of the screen
        mAutoDoorLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Intent intent = new Intent("automation");
                intent.putExtra("autoType", "DoorLock");
                if (isChecked) {
                    compoundButton.setText("Automatic");
                    autodoorLockEnabled = true;
                    editor.putBoolean("Auto Door Lock", true);
                    editor.apply();
                    editor.commit();
                } else {
                    compoundButton.setText("Manual");
                    autodoorLockEnabled = false;
                    editor.putBoolean("Auto Door Lock", false);
                    editor.apply();
                    editor.commit();
                }

                checkDataStatus(intent);
            }
        });

        //register changes for welcome light check box of the screen
        mWelcomeLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Intent intent = new Intent("automation");
                intent.putExtra("autoType", "WelcomeLight");
                if (isChecked) {
                    compoundButton.setText("ON");
                    welcomeLightEnabled = true;
                    editor.putBoolean("Welcome Light", true);
                    editor.apply();
                    editor.commit();
                } else {
                    compoundButton.setText("OFF");
                    welcomeLightEnabled = false;
                    editor.putBoolean("Welcome Light", false);
                    editor.apply();
                    editor.commit();
                }

                checkDataStatus(intent);
            }
        });


        backImBtAutomationSettingFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        return v;

    }

    public void checkDataStatus(Intent intent) {
        if (dataStatus == SEND) {
            getActivity().sendBroadcast(intent);
        } else {
            dataStatus = SEND;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("automationfragment", "onResume()");
        getActivity().registerReceiver(mGattUpdateReceiverAutomation, makeGattUpdateIntentFilter());
        if (connectionStatusString != null) {
            updateConnectionStatusAutomation();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mGattUpdateReceiverAutomation);
    }


    /**
     * Intialising broadcast reciever for registering the events of Gatt Service connection and disconnection
     */
    private final BroadcastReceiver mGattUpdateReceiverAutomation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
                updateConnectionStatusAutomation();
            } else if (RBLService.ACTION_GATT_CONNECTED.equals(action)) {
                updateConnectionStatusAutomation();
            } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                updateConnectionStatusAutomation();
                Intent i = new Intent("getData");
                //intent.putExtra("type", "setting");
                getActivity().sendBroadcast(i);
            } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
            }
        }
    };

    //Updates the connection status of RBL Service by changing the colors of indicator
    private void updateConnectionStatusAutomation() {
        if (connectionStatusString.equals("red")) {
            mImgViewConnectionStatusAutomation.setImageResource(R.drawable.red);
        } else if (connectionStatusString.equals("orange")) {
            mImgViewConnectionStatusAutomation.setImageResource(R.drawable.orange);
        } else if (connectionStatusString.equals("blue")) {
            mImgViewConnectionStatusAutomation.setImageResource(R.drawable.blue);
        } else {
            mImgViewConnectionStatusAutomation.setImageResource(R.drawable.orange);
        }
    }

    public void switchSetting() {
        autoDoorUnlockEnabled = automationSettings.getBoolean("Auto Door Unlock", false);
        autoEngineStartEnabled = automationSettings.getBoolean("Auto Engine Start", false);
        welcomeLightEnabled = automationSettings.getBoolean("Welcome Light", false);
        autodoorLockEnabled = automationSettings.getBoolean("Auto Door Lock", false);

        if (autodoorLockEnabled) {
            mAutoDoorLock.setChecked(true);
            mAutoDoorLock.setText("Automatic");
        } else {
            mAutoDoorLock.setChecked(false);
            mAutoDoorLock.setText("Manual");
        }

        if (autoDoorUnlockEnabled) {
            mAutoDoorUnlock.setChecked(true);
            mAutoDoorUnlock.setText("Automatic");
        } else {
            mAutoDoorUnlock.setChecked(false);
            mAutoDoorUnlock.setText("Manual");
        }

        if (autoEngineStartEnabled) {
            mAutoEngineStart.setChecked(true);
            mAutoEngineStart.setText("Automatic");
        } else {
            mAutoEngineStart.setChecked(false);
            mAutoEngineStart.setText("Manual");
        }

        if (welcomeLightEnabled) {
            mWelcomeLight.setChecked(true);
            mWelcomeLight.setText("ON");
        } else {
            mWelcomeLight.setChecked(false);
            mWelcomeLight.setText("OFF");
        }

    }

    public void receive(byte[] bytes) {
        dataStatus = RECEIVE;
        switch (bytes[3]) {
            case BleRequestConstant.AUTO_ENGINE_START_ON:
                mAutoEngineStart.setChecked(true);
                break;
            case BleRequestConstant.AUTO_ENGINE_START_OFF:
                mAutoEngineStart.setChecked(false);
                break;
            case BleRequestConstant.AUTO_DOOR_UNLOCK_ON:
                mAutoDoorUnlock.setChecked(true);
                break;
            case BleRequestConstant.AUTO_DOOR_UNLOCK_OFF:
                mAutoDoorUnlock.setChecked(false);
                break;
            case BleRequestConstant.AUTO_DOOR_LOCK_ON:
                mAutoDoorLock.setChecked(true);
                break;
            case BleRequestConstant.AUTO_DOOR_LOCK_OFF:
                mAutoDoorLock.setChecked(false);
                break;
            case BleRequestConstant.AUTO_WELCOME_LIGHT_ON:
                mWelcomeLight.setChecked(true);
                break;
            default:
                mWelcomeLight.setChecked(false);
                break;
        }
    }

    public void receiveAllData(byte[] bytes) {
        dataStatus = RECEIVE;
        if (bytes[3] == BleRequestConstant.AUTO_ENGINE_START_ON) {
            mAutoEngineStart.setChecked(true);
        } else {
            mAutoEngineStart.setChecked(false);
        }
        if (bytes[4] == BleRequestConstant.AUTO_DOOR_LOCK_ON) {
            mAutoDoorLock.setChecked(true);
        } else {
            mAutoDoorLock.setChecked(false);
        }
        if (bytes[5] == BleRequestConstant.AUTO_DOOR_UNLOCK_ON) {
            mAutoDoorUnlock.setChecked(true);
        } else {
            mAutoDoorUnlock.setChecked(false);
        }
        if (bytes[6] == BleRequestConstant.AUTO_WELCOME_LIGHT_ON) {
            mWelcomeLight.setChecked(true);
        } else {
            mWelcomeLight.setChecked(false);
        }
    }
}

