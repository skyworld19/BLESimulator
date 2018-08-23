/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: TemperatureFragment.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */
package hyundai.esds.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import hyundai.esds.BleRequestConstant;
import hyundai.esds.CircularSeekBar;
import hyundai.esds.R;

import static hyundai.esds.ControlPagerActivity.mDefrostEnabled;
import static hyundai.esds.ControlPagerActivity.mTemperatureValue;

/**
 * Sets tempeature of the vehicle and also controls the defog
 */
public class TemperatureFragment extends android.support.v4.app.Fragment {

    private static final int SEND = 0;
    private static final int RECEIVE = 1;

    //Declaring member fields
    CircularSeekBar mSeekBar;
    TextView mSeekBarProgressTextView;
    float mSeekBarProgress;
    CheckBox mFrontCheckbox;
    int dataStatus = SEND;
    int temperature;
    Button setBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_temperature, container, false);

        //Initializing member fields
        mSeekBar=(CircularSeekBar) v.findViewById(R.id.seek_bar);
        mSeekBarProgressTextView=v.findViewById(R.id.text_view_seekbar_progress);
        mFrontCheckbox=v.findViewById(R.id.front_checkbox);
        setBtn = v.findViewById(R.id.Temperature_button);
     /*   if(mDefrostEnabled){
            mFrontCheckbox.setChecked(true);
        }
        else{
            mFrontCheckbox.setChecked(false);
        }*/
        initView();
        //register changes for front defog selection
        mFrontCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mDefrostEnabled=true;
                    getActivity().getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).edit().putBoolean("Defrost",mDefrostEnabled).apply();
                }
                else{
                    mDefrostEnabled=false;
                    getActivity().getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).edit().putBoolean("Defrost",mDefrostEnabled).apply();
                }

                if(dataStatus == SEND) {
                    Intent intent = new Intent("defrost");
                    intent.putExtra("isChecked", b);
                    getActivity().sendBroadcast(intent);
                } else {
                    dataStatus = SEND;
                }
            }
        });

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("climate");
                intent.putExtra("climateType", "temperature");
                intent.putExtra("value", temperature);
                getActivity().sendBroadcast(intent);
            }
        });

        //Shared preferences to store the parameters of defog selection
        //And for the first time, these fields are default set to intial position


        mSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                mSeekBarProgress=progress+170;
                if((mSeekBarProgress%5)==0){
                    temperature = progress;
                    mTemperatureValue=mSeekBarProgress/10;
                    mSeekBarProgressTextView.setText(String.valueOf(mTemperatureValue)+ " " + "\u00b0" + "C");
                    getActivity().getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).edit().putFloat("Temperature",mTemperatureValue).apply();
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
        return v;

    }

    public void receive(byte [] bytes) {
        dataStatus = RECEIVE;
        if(bytes[3] == BleRequestConstant.DEFROST_ON) {
            mFrontCheckbox.setChecked(true);
        } else {
            mFrontCheckbox.setChecked(false);
        }
    }

    public void setTemperature(int value) {
        mSeekBar.setProgress(value);
    }

    public void initView() {
        mDefrostEnabled = getActivity().getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).getBoolean("Defrost",mDefrostEnabled);
        mFrontCheckbox.setChecked(mDefrostEnabled);

        if(getActivity().getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).getFloat("Temperature", 0)==0.0)
        {
            mTemperatureValue=(float)17.0;
            temperature = 17;
        }else {
            mTemperatureValue = getActivity().getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).getFloat("Temperature", 0);
            temperature = (int)(mTemperatureValue*10)-170;
        }

        mSeekBarProgressTextView.setText(String.valueOf(mTemperatureValue)+ " " + "\u00b0" + "C");
        mSeekBar.setProgress((int) ((mTemperatureValue*10)-170));
    }

    /*  @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.w("이거","보임");
            //initView();
        }
        else {
            Log.w("이거","사라짐");
        }
    }

    @Override
    public void setMenuVisibility(final boolean visible)
    {
        super.setMenuVisibility(visible);
        if (visible && isResumed())
        {
            Log.w("이거","보임2");
        } else {
            Log.w("이거","사라짐2");
        }
    }*/
}