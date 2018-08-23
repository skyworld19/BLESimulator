/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: VehicleStatusFragment.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */
package hyundai.esds.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

import hyundai.esds.BleRequestConstant;
import hyundai.esds.R;

import static hyundai.esds.ControlPagerActivity.mTempMap;
import static hyundai.esds.ControlPagerActivity.mTemperatureValue;
import static hyundai.esds.CustomDialogClass.RESULTFROMBDCST;
import static hyundai.esds.RBLService.frontLeft;
import static hyundai.esds.RBLService.frontRight;
import static hyundai.esds.RBLService.rearLeft;
import static hyundai.esds.RBLService.rearRight;
import static hyundai.esds.RBLService.sClimateCtrl;
import static hyundai.esds.RBLService.sDefrost;
import static hyundai.esds.RBLService.sDoorOn;
import static hyundai.esds.RBLService.sEngineOn;
import static hyundai.esds.RBLService.sTempValue;
import static hyundai.esds.RBLService.trunk;

/**
 * Display the status of the vehicle
 * Give the status of Door open/close, engine start,climate control,door lock/unlock,trunk.
 */
public class VehicleStatusFragment extends android.support.v4.app.Fragment {

    //Declaring the member fields
    TextView mEngineOnStatus, mDoorOnStatus, mDefrostStatus, mClimateCtrlStatus, temperatureValueTextView, frontRightStatus, frontLeftStatus, rearRightStatus, rearLeftStatus, trunkStatus, timeStatus;
    ImageButton backImBtStatusFragment;
    private static VehicleStatusFragment statusInstance;
    public static boolean statusFragmentActive = false;
    Button refreshVehicleStatusButton;
    Calendar mCalendar;
    SimpleDateFormat mSimpleDataFormat;

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status, container, false);
        //Initializing the meber fields
        backImBtStatusFragment = v.findViewById(R.id.imagebutton_back_status_fragment);
        mEngineOnStatus = v.findViewById(R.id.engine_start_status);
        mDoorOnStatus = v.findViewById(R.id.door_lock_status);
        mDefrostStatus = v.findViewById(R.id.textview_defrost_status);
        mClimateCtrlStatus = v.findViewById(R.id.cimlate_control_status);
        temperatureValueTextView = v.findViewById(R.id.temperature_value);
        timeStatus = v.findViewById(R.id.rec_time);
        refreshVehicleStatusButton = v.findViewById(R.id.status_refresh);

        frontLeftStatus = v.findViewById(R.id.textview_front_left_status);
        frontRightStatus = v.findViewById(R.id.textview_front_right_status);
        rearLeftStatus = v.findViewById(R.id.textview_rear_left_status);
        rearRightStatus = v.findViewById(R.id.textview_rear_right_status);
        trunkStatus = v.findViewById(R.id.textview_trunk_status);

        return v;

    }

    public static VehicleStatusFragment statusFragmentInstance() {
        return statusInstance;
    }

    @Override
    public void onStart() {
        super.onStart();
        statusInstance = this;
        statusFragmentActive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        statusFragmentActive = false;
    }

    @Override
    public void onResume() {
        super.onResume();

        //updateVehicleStatus();

        backImBtStatusFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        //Refreshes the entire screen when the "Refresh" button is clicked
        refreshVehicleStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("finish_activity");
                intent.putExtra(RESULTFROMBDCST, "true");
                getActivity().sendBroadcast(intent);
                VehicleControlFragment.clickedGridPosition = 3;
            }
        });

        Intent intent = new Intent("finish_activity");
        intent.putExtra(RESULTFROMBDCST, "true");
        getActivity().sendBroadcast(intent);
        VehicleControlFragment.clickedGridPosition = 3;
    }

    //updates all fields in the screens when user enters to the vehicle status screen or when the refresh button is pressed
    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    public void updateVehicleStatus() {

        //Show Engine status On/Off
        Log.d("Vehicle", "Entered");
        mCalendar = Calendar.getInstance();
        // mSimpleDataFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mSimpleDataFormat = new SimpleDateFormat("HH:mm:ss");
        timeStatus.setText("Received Time: " + mSimpleDataFormat.format(mCalendar.getTime()) + " ");
        if (sEngineOn == 1) {
            mEngineOnStatus.setText("ON");
            mEngineOnStatus.setTextColor(getResources().getColor(R.color.Blue));
        } else if (sEngineOn == 0) {
            mEngineOnStatus.setText("OFF");
            mEngineOnStatus.setTextColor(getResources().getColor(R.color.Red));
        } else {
            mEngineOnStatus.setText(" ");
        }

        //Show Door Locak Status On/Off
        if (sDoorOn == 1) {
            mDoorOnStatus.setText("Unlock");
            mDoorOnStatus.setTextColor(getResources().getColor(R.color.Red));
        } else if (sDoorOn == 0) {
            mDoorOnStatus.setText("Lock");
            mDoorOnStatus.setTextColor(getResources().getColor(R.color.Blue));
        } else {
            mDoorOnStatus.setText(" ");
        }

        //Show Defrost Status On/Off
        if (sDefrost == 1) {
            mDefrostStatus.setText("ON");
            mDefrostStatus.setTextColor(getResources().getColor(R.color.Blue));
        } else if (sDefrost == 0) {
            mDefrostStatus.setText("OFF");
            mDefrostStatus.setTextColor(getResources().getColor(R.color.Red));
        } else {
            mDefrostStatus.setText(" ");
        }


        //Show airComtrol Statsu On/Off
        if (sClimateCtrl == 1) {
            mClimateCtrlStatus.setText("ON");
            mClimateCtrlStatus.setTextColor(getResources().getColor(R.color.Blue));
        } else if (sClimateCtrl == 0) {
            mClimateCtrlStatus.setText("OFF");
            mClimateCtrlStatus.setTextColor(getResources().getColor(R.color.Red));
        } else {
            mClimateCtrlStatus.setText(" ");
        }

        //Show front left door status
        if (frontRight == 0) {
            frontRightStatus.setText("Close");
            frontRightStatus.setTextColor(getResources().getColor(R.color.Red));
        } else if (frontRight == 1) {
            frontRightStatus.setText("Open");
            frontRightStatus.setTextColor(getResources().getColor(R.color.Blue));
        } else {
            frontRightStatus.setText(" ");
        }

        //Show front right door status
        if (frontLeft == 0) {
            frontLeftStatus.setText("Close");
            frontLeftStatus.setTextColor(getResources().getColor(R.color.Red));
        } else if (frontLeft == 1) {
            frontLeftStatus.setText("Open");
            frontLeftStatus.setTextColor(getResources().getColor(R.color.Blue));
        } else {
            frontLeftStatus.setText(" ");
        }


        //Show front left door status
        if (rearRight == 0) {
            rearRightStatus.setText("Close");
            rearRightStatus.setTextColor(getResources().getColor(R.color.Red));
        } else if (rearRight == 1) {
            rearRightStatus.setText("Open");
            rearRightStatus.setTextColor(getResources().getColor(R.color.Blue));
        } else {
            rearRightStatus.setText(" ");
        }

        //Show front left door status
        if (rearLeft == 0) {
            rearLeftStatus.setText("Close");
            rearLeftStatus.setTextColor(getResources().getColor(R.color.Red));
        } else if (rearLeft == 1) {
            rearLeftStatus.setText("Open");
            rearLeftStatus.setTextColor(getResources().getColor(R.color.Blue));
        } else {
            rearLeftStatus.setText(" ");
        }

        //Show front left door status
        if (trunk == 0) {
            trunkStatus.setText("Close");
            trunkStatus.setTextColor(getResources().getColor(R.color.Red));
        } else if (trunk == 1) {
            trunkStatus.setText("Open");
            trunkStatus.setTextColor(getResources().getColor(R.color.Blue));
        } else {
            trunkStatus.setText(" ");
        }

        if (mTempMap != null) {
            if ((sTempValue >= 0x06) && (sTempValue <= 0x1A)) {
                temperatureValueTextView.setText(String.valueOf(getKeyFromValue(mTempMap, sTempValue)) + " " + "\u00b0" + "C");
            } else {
                temperatureValueTextView.setText(String.valueOf(sTempValue));
            }
        }
    }

    //Checks the recived temperature value with the stored hash map and return the key which is nothing but a temperature of the vehicle
    public static Object getKeyFromValue(Map hm, byte value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    public void receive(byte[] bytes) {
        //Show Engine status On/Off
        Log.d("Vehicle", "Entered");
        mCalendar = Calendar.getInstance();
        // mSimpleDataFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mSimpleDataFormat = new SimpleDateFormat("HH:mm:ss");
        timeStatus.setText("Received Time: " + mSimpleDataFormat.format(mCalendar.getTime()) + " ");

        //Show Door Locak Status On/Off
        if (bytes[3] == BleRequestConstant.DOOR_UNLOCK) {
            mDoorOnStatus.setText("Unlock");
            mDoorOnStatus.setTextColor(getResources().getColor(R.color.Red));
            frontRightStatus.setText("Open");
            frontRightStatus.setTextColor(getResources().getColor(R.color.Red));
            frontLeftStatus.setText("Open");
            frontLeftStatus.setTextColor(getResources().getColor(R.color.Red));
            rearRightStatus.setText("Open");
            rearRightStatus.setTextColor(getResources().getColor(R.color.Red));
            rearLeftStatus.setText("Open");
            rearLeftStatus.setTextColor(getResources().getColor(R.color.Red));
        } else {
            mDoorOnStatus.setText("Lock");
            mDoorOnStatus.setTextColor(getResources().getColor(R.color.Blue));
            frontRightStatus.setText("Close");
            frontRightStatus.setTextColor(getResources().getColor(R.color.Blue));
            frontLeftStatus.setText("Close");
            frontLeftStatus.setTextColor(getResources().getColor(R.color.Blue));
            rearRightStatus.setText("Close");
            rearRightStatus.setTextColor(getResources().getColor(R.color.Blue));
            rearLeftStatus.setText("Close");
            rearLeftStatus.setTextColor(getResources().getColor(R.color.Blue));
        }

        if (bytes[4] == BleRequestConstant.ENGINE_START) {
            mEngineOnStatus.setText("ON");
            mEngineOnStatus.setTextColor(getResources().getColor(R.color.Blue));
        } else {
            mEngineOnStatus.setText("OFF");
            mEngineOnStatus.setTextColor(getResources().getColor(R.color.Red));
        }

        //Show Defrost Status On/Off
        if (bytes[5] == BleRequestConstant.DEFROST_ON) {
            mDefrostStatus.setText("ON");
            mDefrostStatus.setTextColor(getResources().getColor(R.color.Blue));
        } else {
            mDefrostStatus.setText("OFF");
            mDefrostStatus.setTextColor(getResources().getColor(R.color.Red));
        }

        mClimateCtrlStatus.setText("ON");
        mClimateCtrlStatus.setTextColor(getResources().getColor(R.color.Blue));

        trunkStatus.setText(" ");

        byte [] rBytes = Arrays.copyOfRange(bytes, 6, bytes.length);
        String temp = new String(rBytes);
        Toast.makeText(getActivity(),temp,Toast.LENGTH_SHORT).show();
        float f = (float)(Integer.valueOf(temp)+170)/10;
        temperatureValueTextView.setText(String.valueOf(f)+ " " + "\u00b0" + "C");

    }

}