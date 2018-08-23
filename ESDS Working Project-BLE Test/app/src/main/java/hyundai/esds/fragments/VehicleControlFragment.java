/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: VehicleControlFragment.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */
package hyundai.esds.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import hyundai.esds.CustomDialogClass;
import hyundai.esds.CustomGridViewActivity;
import hyundai.esds.R;
import hyundai.esds.RBLService;

import static hyundai.esds.ControlPagerActivity.connectionStatusString;
import static hyundai.esds.ControlPagerActivity.makeGattUpdateIntentFilter;
import static hyundai.esds.ControlPagerActivity.serviceConnected;
import static hyundai.esds.CustomDialogClass.RESULTFROMBDCST;
import static hyundai.esds.CustomDialogClass.sAlertDialog;

public class VehicleControlFragment extends Fragment {
    //Declaring meber fields
    public static String[] textString;
    public CustomGridViewActivity vehicleControlGridAdapter;
    public GridView vehicleControlGridView;
    public static int clickedGridPosition;
    ImageButton backImBtVehicleControl;
    public ImageView mImgViewConnectionStatus;
    public Vibrator vibrate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_vehicle_control, container, false);

        //Initialzing member fields
        vibrate=(Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        textString=new String[]{"Engine \nStart","Horn \nLight","Engine \nStop","Vehicle \nStatus","BLE \nConnected \nENGINE START","Climate \nControl","Lock","Light","Unlock"};
        vehicleControlGridView= view.findViewById(R.id.gridView_vehicleControl);
        backImBtVehicleControl=(ImageButton) view.findViewById(R.id.imagebutton_back_vehicle_control_fragment);
        backImBtVehicleControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mImgViewConnectionStatus=view.findViewById(R.id.connection_status);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //register recivwer for updating the service connection changes
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        updateGridItems();
        vehicleControlGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {

                //Vibrate when the button is clicked
                vibrate.vibrate(150);
                if(textString[position].equals("Climate \nControl"))
                {
                    ClimateControlConfiguration fragment2 = new ClimateControlConfiguration();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.vehicle_control_fragment, fragment2);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();


                }
                else if(textString[position].equals("Vehicle \nStatus")){
                    Intent intent = new Intent("finish_activity");
                    intent.putExtra(RESULTFROMBDCST, "true");
                    getActivity().sendBroadcast(intent);
                    clickedGridPosition = position;
                    VehicleStatusFragment fragment2 = new VehicleStatusFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction =        fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.vehicle_control_fragment, fragment2);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
                else if(textString[position].equals("Engine \nStart")) {
                    if(sAlertDialog!=null)
                    {
                        if(sAlertDialog.isShowing()) {
                            sAlertDialog.dismiss();
                        }
                    }
                    CustomDialogClass.showDialog(getActivity(), getActivity());
                    // Toast.makeText(getContext(),String.valueOf(position),Toast.LENGTH_LONG).show();
                    clickedGridPosition = position;
                }else{
                    Intent intent = new Intent("finish_activity");
                    intent.putExtra(RESULTFROMBDCST, "true");
                    getActivity().sendBroadcast(intent);
                    clickedGridPosition = position;
                }

                twoSecondDelay();
            }
        });

        if(connectionStatusString!=null)
        {
            updateConnectionStatus();
        }
    }

    //Disable default all buttons for 3 seconds when any of the buttons in the vehicle control screen is clicked
    private void twoSecondDelay() {
        updateGrid("disable");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                updateGrid("enable");
            }
        }, 3000);
    }

    private void updateGrid(String status) {
        if(status.equals("enable"))
            vehicleControlGridAdapter = new CustomGridViewActivity(getActivity(), textString, true);
        else
            vehicleControlGridAdapter = new CustomGridViewActivity(getActivity(), textString, false);

        vehicleControlGridView.setAdapter(vehicleControlGridAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    //Initialing mGattUpdateReceiver for registering the changes of RBL service connection
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
                if((sAlertDialog!=null) && (sAlertDialog.isShowing()))
                {
                    sAlertDialog.cancel();
                }
                updateGridItems();
                updateConnectionStatus();
            } else if(RBLService.ACTION_GATT_CONNECTED.equals(action)){
                System.out.println("Gatt Connected Called");
                updateConnectionStatus();
            }
            else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                updateGridItems();
                updateConnectionStatus();
            } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
            }
        }
    };

    //Updates the connection status of RBL Service by changing the colors of indicator
    private void updateConnectionStatus() {
        if(connectionStatusString.equals("red"))
        {
          mImgViewConnectionStatus.setImageResource(R.drawable.red);
        }else if(connectionStatusString.equals("orange"))
        {
            mImgViewConnectionStatus.setImageResource(R.drawable.orange);
        }else if(connectionStatusString.equals("blue"))
        {
            mImgViewConnectionStatus.setImageResource(R.drawable.blue);
        }else
        {
            mImgViewConnectionStatus.setImageResource(R.drawable.orange);
        }
    }

    //Updating the button action to enable/disable depending on the connection status of RBL Service
    private void updateGridItems() {
        if(serviceConnected) {
            vehicleControlGridAdapter = new CustomGridViewActivity(getActivity(), textString, true);
        }else
        {
            vehicleControlGridAdapter = new CustomGridViewActivity(getActivity(), textString, false);
        }
        vehicleControlGridView.setAdapter(vehicleControlGridAdapter);
    }
}