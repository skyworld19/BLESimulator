/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: IdleFragment.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */
package hyundai.esds.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import hyundai.esds.CircularSeekBar;
import hyundai.esds.R;

import static hyundai.esds.ControlPagerActivity.mDefrostEnabled;
import static hyundai.esds.ControlPagerActivity.mTemperatureValue;

/**
 * Sets the idle time, to set the engine running time
 */
public class IdleTimeFragment extends Fragment {

    //Declaring meber fields
    TextView mSeekbarProgressTextview;
    CircularSeekBar seekBar;
    Button mButton;
    int mSeekBarProgress;
    public static int mIdleTimeValue=2;
    int mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_idle_time, container, false);
        //Initialzing member fields
        seekBar= v.findViewById(R.id.seek_bar);
        mButton=v.findViewById(R.id.idleTime_button);
        mSeekbarProgressTextview=v.findViewById(R.id.text_view_seekbar_progress);
        initView();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("climate");
                intent.putExtra("climateType", "idletime");
                intent.putExtra("value", mProgress);
                getActivity().sendBroadcast(intent);
            }
        });

        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                mSeekBarProgress=progress+2;
                mProgress = progress;
                mIdleTimeValue=mSeekBarProgress;
                mSeekbarProgressTextview.setText(String.valueOf(mSeekBarProgress)+" min");
                getActivity().getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).edit().putInt("Idle Time",mIdleTimeValue).apply();

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

    public void setIdleTime(int value) {
        seekBar.setProgress(value);
    }

    public void initView() {
        mIdleTimeValue=getActivity().getSharedPreferences("Automation Settings",Context.MODE_PRIVATE).getInt("Idle Time",0);
        mProgress = mIdleTimeValue-2;
        mSeekbarProgressTextview.setText(String.valueOf(mIdleTimeValue)+" min");
        seekBar.setProgress(mIdleTimeValue-2);
    }
}