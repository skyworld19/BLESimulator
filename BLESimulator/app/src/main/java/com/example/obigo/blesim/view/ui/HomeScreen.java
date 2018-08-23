package com.example.obigo.blesim.view.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.databinding.DataBindingUtil;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.obigo.blesim.R;
import com.example.obigo.blesim.databinding.ActivityHomeScreenBinding;
import com.example.obigo.blesim.databinding.ContentHomeScreenBinding;
import com.example.obigo.blesim.model.SwitchData;
import com.example.obigo.blesim.repository.remote.BleUtils;
import com.example.obigo.blesim.viewmodel.BleViewModel;


public class HomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = HomeScreen.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int SEND = 0;
    private static final int RECEIVE = 1;

    // view
    private FrameLayout frameLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private View mView;
    private Menu mMenu;
    private TextView mConnectionStatusTxt;
    private TextView mMasterDeviceNameTxt;
    private SoundPool mSoundPool;
    private int mSoundId;
    private int dataStatus = SEND;
    private int mTemperature;
    private int mIdleTime;
    private boolean mIsBleConnected = false;
    public boolean cbEngine = false;
    // viewmodel
    private BleViewModel mBleViewModel;
    private Observer<String> mBleDeviceNameObserver;
    private Observer<String> mConnectStatusObserver;
    private Observer<SwitchData> mDoorStatusObserver;
    private Observer<SwitchData> mEngineStatusObserver;
    private Observer<SwitchData> mHornLightObserver;
    private Observer<SwitchData> mLightOnlyObserver;
    private Observer<SwitchData> mDefrostObserver;
    private Observer<Integer> mTemperatureObserver;
    private Observer<Integer> mIdleTimeObserver;

    private ActivityHomeScreenBinding mActivityHomeScreenBinding;
    private ContentHomeScreenBinding mContentHomeScreenBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        mActivityHomeScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);
        mContentHomeScreenBinding = mActivityHomeScreenBinding.appbar.content;
        mContentHomeScreenBinding.setActivity(this);
        initView();
        checkEnableBle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        dataStatus = SEND;
        mBleViewModel = ViewModelProviders.of(this).get(BleViewModel.class);
        initObserver();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mView = mNavigationView.getHeaderView(0);
        mConnectionStatusTxt = (TextView) mView.findViewById(R.id.txt_ble_status);
        mMasterDeviceNameTxt = (TextView) mView.findViewById(R.id.txt_ble_device_name);
    }

    public void initObserver() {
        mBleDeviceNameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String s) {
                Log.w(TAG, "mBleDeviceNameObserver : onChanged() : " + s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMasterDeviceNameTxt.setText(s);
                    }
                });
            }
        };

        mConnectStatusObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String s) {
                Log.w(TAG, "mConnectStatusObserver : onChanged()");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mConnectionStatusTxt.setText(s);
                        if (s.equals(getString(R.string.status_devicesConnected))) {
                            mContentHomeScreenBinding.checkboxBluetooth.setChecked(true);
                            mIsBleConnected = true;
                        } else {
                            mContentHomeScreenBinding.checkboxBluetooth.setChecked(false);
                            mIsBleConnected = false;
                            //toggleBleButton(false);
                        }
                    }
                });
            }
        };

        mDoorStatusObserver = new Observer<SwitchData>() {
            @Override
            public void onChanged(@Nullable SwitchData switchData) {
                Log.w(TAG, "DATASTATUS 값 : " + switchData.isChecked());
                dataStatus = switchData.getDataStatus();
                mContentHomeScreenBinding.switchDoor.setChecked(switchData.isChecked());
                carDoorImageChanged(switchData.isChecked());
            }
        };

        mEngineStatusObserver = new Observer<SwitchData>() {
            @Override
            public void onChanged(@Nullable SwitchData switchData) {
                Log.w(TAG, " mEngineStatusObserver onChanged");
                dataStatus = switchData.getDataStatus();
                mContentHomeScreenBinding.switchEngine.setChecked(switchData.isChecked());
                mContentHomeScreenBinding.checkboxEngine.setChecked(switchData.isChecked());
                resetDataStatus();
            }
        };

        mLightOnlyObserver = new Observer<SwitchData>() {
            @Override
            public void onChanged(@Nullable SwitchData switchData) {
                if (switchData.isChecked()) {
                    Log.w(TAG, " mLightOnlyObserver : onChanged");
                    dataStatus = switchData.getDataStatus();
                    mContentHomeScreenBinding.switchLight.setChecked(switchData.isChecked());
                    mContentHomeScreenBinding.checkboxLight.setChecked(switchData.isChecked());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mContentHomeScreenBinding.switchLight.setChecked(false);
                            mContentHomeScreenBinding.switchHornLight.setEnabled(true);
                            mContentHomeScreenBinding.checkboxLight.setChecked(false);

                        }
                    }, 3000);
                    mContentHomeScreenBinding.switchHornLight.setEnabled(false);
                }
                resetDataStatus();
            }
        };

        mHornLightObserver = new Observer<SwitchData>() {
            @Override
            public void onChanged(@Nullable SwitchData switchData) {
                Log.w(TAG, "mHornLightObserver : onChanged");
                if (switchData.isChecked()) {
                    dataStatus = switchData.getDataStatus();
                    mContentHomeScreenBinding.switchHornLight.setChecked(switchData.isChecked());
                    mContentHomeScreenBinding.checkboxHorn.setChecked(switchData.isChecked());
                    playHorn();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mContentHomeScreenBinding.switchHornLight.setChecked(false);
                            mContentHomeScreenBinding.switchLight.setEnabled(true);
                            mContentHomeScreenBinding.checkboxHorn.setChecked(false);
                            stopHorn();
                        }
                    }, 3000);
                    mContentHomeScreenBinding.switchLight.setEnabled(false);
                }
                resetDataStatus();
            }
        };

        mDefrostObserver = new Observer<SwitchData>() {
            @Override
            public void onChanged(@Nullable SwitchData switchData) {
                Log.w(TAG, " mDefrostObserver onChanged");
                dataStatus = switchData.getDataStatus();
                mContentHomeScreenBinding.switchDefrost.setChecked(switchData.isChecked());
                mContentHomeScreenBinding.checkboxDefrost.setChecked(switchData.isChecked());
                resetDataStatus();
            }
        };

        mTemperatureObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer progress) {
                Log.w(TAG, " mTemperatureObserver onChanged");
                mContentHomeScreenBinding.seekbarTemperature.setProgress(progress);
                mContentHomeScreenBinding.textCarTemperature.setText(getTemperatureFormat((float)(progress + 170)/10));
               /* if (mContentHomeScreenBinding.textTemperature.getText().equals(""))*/
                    mContentHomeScreenBinding.textTemperature.setText(getTemperatureFormat((float)(progress + 170)/10));
                resetDataStatus();
            }
        };

        mIdleTimeObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer progress) {
                Log.w(TAG, " mIdleTimeObserver onChanged");
                mContentHomeScreenBinding.seekbarIdletime.setProgress(progress);
                mContentHomeScreenBinding.textCarIdleTime.setText(getIdleTimeFormat(progress + 2));
               /* if (mContentHomeScreenBinding.textTemperature.getText().equals(""))*/
                    mContentHomeScreenBinding.textIdleTime.setText(getIdleTimeFormat(progress + 2));
                resetDataStatus();
            }
        };

        mBleViewModel.getBleDeviceName().observe(this, mBleDeviceNameObserver);
        mBleViewModel.getConnectStatus().observe(this, mConnectStatusObserver);
        mBleViewModel.getDoorStatus().observe(this, mDoorStatusObserver);
        mBleViewModel.getEngineStatus().observe(this, mEngineStatusObserver);
        mBleViewModel.getHornLightStatus().observe(this, mHornLightObserver);
        mBleViewModel.getLightOnlyStatus().observe(this, mLightOnlyObserver);
        mBleViewModel.getDefrostStatus().observe(this, mDefrostObserver);
        mBleViewModel.getTemperatureStatus().observe(this, mTemperatureObserver);
        mBleViewModel.getIdleTimeStatus().observe(this, mIdleTimeObserver);
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Log.i(TAG, "onCheckedChanged : " + isChecked);
        Log.w(TAG, "checkedchanged status값 : " + dataStatus);
        if (dataStatus == SEND) {
            // send
            mBleViewModel.setChange(compoundButton.getId(), isChecked);
        } /*else {
            // receive
            dataStatus = SEND;
        }*/
    }

    public void checkEnableBle() {

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        BluetoothAdapter bluetoothAdapter;

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showToastText(getString(R.string.bluetoothNotSupported));
            finish();
        }

        bluetoothAdapter = BleUtils.getBluetoothAdapter(this);
        if (bluetoothAdapter == null) {
            showToastText(getString(R.string.bluetoothNotSupported));
            Log.e(TAG, "Bluetooth not supported");
            finish();
        } else if (!bluetoothAdapter.isEnabled()) {
            // Make sure bluetooth is enabled.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i(TAG, "onNavigationItemSelected");
        int id = item.getItemId();

       /* if (id == R.id.nav_menu1) {
            frameLayout.setBackgroundColor(Color.parseColor("#A52A2A"));
        } else if (id == R.id.nav_menu2) {
            frameLayout.setBackgroundColor(Color.parseColor("#5F9EA0"));
        } else if (id == R.id.nav_menu3) {
            frameLayout.setBackgroundColor(Color.parseColor("#556B2F"));
        }
*/
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        mMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_home_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if (id == R.id.action_setting) {
            FragmentManager fm = getSupportFragmentManager();
            SettingDialogFragment dialogFragment = new SettingDialogFragment();
            dialogFragment.show(fm, "fragment_dialog_test");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void showToastText(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void playHorn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .build();
        } else {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        mSoundId = mSoundPool.load(this, R.raw.hornsound, 1);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                mSoundPool.play(mSoundId, 1.0f, 1.0f, 0, -1, 1.0f);
            }
        });
    }

    public void stopHorn() {
        mSoundPool.stop(mSoundId);
    }

    public void onTemperatureProgressChanged(SeekBar seekBar, int progress, boolean b) {
        float temperature = progress + 170;
        if ((temperature % 5) == 0) {
            temperature = temperature / 10;
            mTemperature = progress;
            mContentHomeScreenBinding.textTemperature.setText(getTemperatureFormat(temperature));
        }
    }

    public void onIdleTimeProgressChanged(SeekBar seekBar, int progress, boolean b) {
        int idleTime = progress + 2;
        mIdleTime = progress;
        mContentHomeScreenBinding.textIdleTime.setText(getIdleTimeFormat(idleTime));
    }

    public String getTemperatureFormat(float temperature) {
        return temperature + "\u00b0" + "C";
    }

    public String getIdleTimeFormat(int idleTime) {
        return idleTime + "min";
    }

    public void onTemperatureClick(View v) {
        mBleViewModel.sendProgressChange(v.getId(), mTemperature);
    }

    public void onIdleTimeClick(View v) {
        mBleViewModel.sendProgressChange(v.getId(), mIdleTime);
    }

    public void resetDataStatus() {
        if (dataStatus == RECEIVE) {
            dataStatus = SEND;
            Log.w(TAG, "reset-datastatus값 : " + dataStatus);
        }
    }

    public void carDoorImageChanged(boolean isLock) {
        if (isLock) {
            mContentHomeScreenBinding.imageViewUnlockLeftBack.setVisibility(View.INVISIBLE);
            mContentHomeScreenBinding.imageViewUnlockRightBack.setVisibility(View.INVISIBLE);
            mContentHomeScreenBinding.imageViewUnlockLeftFront.setVisibility(View.INVISIBLE);
            mContentHomeScreenBinding.imageViewUnlockRightFront.setVisibility(View.INVISIBLE);
            mContentHomeScreenBinding.imageViewLockLeftBack.setVisibility(View.VISIBLE);
            mContentHomeScreenBinding.imageViewLockRightBack.setVisibility(View.VISIBLE);
            mContentHomeScreenBinding.imageViewLockLeftFront.setVisibility(View.VISIBLE);
            mContentHomeScreenBinding.imageViewLockRightFront.setVisibility(View.VISIBLE);

        } else {
            mContentHomeScreenBinding.imageViewUnlockLeftBack.setVisibility(View.VISIBLE);
            mContentHomeScreenBinding.imageViewUnlockRightBack.setVisibility(View.VISIBLE);
            mContentHomeScreenBinding.imageViewUnlockLeftFront.setVisibility(View.VISIBLE);
            mContentHomeScreenBinding.imageViewUnlockRightFront.setVisibility(View.VISIBLE);
            mContentHomeScreenBinding.imageViewLockLeftBack.setVisibility(View.INVISIBLE);
            mContentHomeScreenBinding.imageViewLockRightBack.setVisibility(View.INVISIBLE);
            mContentHomeScreenBinding.imageViewLockLeftFront.setVisibility(View.INVISIBLE);
            mContentHomeScreenBinding.imageViewLockRightFront.setVisibility(View.INVISIBLE);
        }
    }

}
