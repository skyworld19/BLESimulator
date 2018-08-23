/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: LoginScreen.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */

package hyundai.esds;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static hyundai.esds.SplashScreen.isOpenedViaShare;
import static hyundai.esds.SplashScreen.recSplitMessages;

/**
 * This class is used to handle the Login operation of the user
 */

public class LoginScreen extends AppCompatActivity {

    Button mButtonLogIn;
    EditText mEditPhoneNumber, mEditIdNumber, mEditPassword;
    String mStringPhoneNumber, mStringIdNumber, mStringPassword;
    Intent mHomeScreenIntent;
    CheckBox mAutoLogin;
    boolean doubleBackToExitPressedOnce = false;
    private boolean mAutologinEnabledValue = false;
    public static String tobeSendPhoneNumber = null, tobeSendIdNumber = null, tobeSendPassword = null;
    TextView autoLoginTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        //Assign UI Elements to the variables
        mButtonLogIn = (Button) findViewById(R.id.button_logIn);
        mEditPhoneNumber = (EditText) findViewById(R.id.editText_phoneNumber);
        mEditIdNumber = (EditText) findViewById(R.id.editText_idNumber);
        mEditPassword = (EditText) findViewById(R.id.editText_password);
        mAutoLogin = (CheckBox) findViewById(R.id.checkBox_AutoLogIn);
        autoLoginTextView = (TextView) findViewById(R.id.auto_login_textview);

        //Open Home Screen upon successful login
        mHomeScreenIntent = new Intent(this, HomeScreen.class);
    }

    @Override
    public void onBackPressed() {

        //Handles double back press to exit the App
        if (doubleBackToExitPressedOnce) {
            //super.onBackPressed();
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEditPhoneNumber.setText("");
        mEditIdNumber.setText("");
        mEditPassword.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        mStringPhoneNumber = mEditPhoneNumber.getText().toString();
        mStringIdNumber = mEditIdNumber.getText().toString();
        mStringPassword = mEditPassword.getText().toString();

        //If opened app from notifcation, the condition returns true
        if (isOpenedViaShare) {
            mAutoLogin.setEnabled(false);
        } else {
            //to disable auto login feature when opened by supplier
            if (BuildConfig.FLAVOR.equals("hmc")) {
                mAutoLogin.setEnabled(true);
                mAutoLogin.setVisibility(View.VISIBLE);
                autoLoginTextView.setVisibility(View.VISIBLE);
                mAutologinEnabledValue = getSharedPreferences("Automation Settings", MODE_PRIVATE).getBoolean("AutoLogin", false);
            } else if (BuildConfig.FLAVOR.equals("supplier")) {
                mAutoLogin.setEnabled(false);
                mAutoLogin.setChecked(false);
                mAutoLogin.setVisibility(View.INVISIBLE);
                autoLoginTextView.setVisibility(View.INVISIBLE);
                mAutologinEnabledValue = false;
            }
        }

        //onClick handler for logon button
        mButtonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ESDS", mStringPhoneNumber + " " + mStringIdNumber + " " + mStringPassword);
                if (getResult()) {
                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                    tobeSendIdNumber = mStringIdNumber;
                    tobeSendPhoneNumber = mStringPhoneNumber;
                    tobeSendPassword = mStringPassword;
                    startActivity(mHomeScreenIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //get the values from the entered phone number in the text field
        mEditPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mStringPhoneNumber = mEditPhoneNumber.getText().toString();
            }
        });

        //get the values from the entered id in the text field
        mEditIdNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mStringIdNumber = mEditIdNumber.getText().toString();
            }
        });

        //get the values from the entered password in the text field
        mEditPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mStringPassword = mEditPassword.getText().toString();
            }
        });

        //register changes for auto login check box of the screen
        mAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mEditPhoneNumber.setText("18561126531");
                    mEditIdNumber.setText("1234");
                    mEditPassword.setText("61126531");
                    getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).edit().putBoolean("AutoLogin", true).apply();

                } else {
                    mEditPhoneNumber.setText("");
                    mEditIdNumber.setText("");
                    mEditPassword.setText("");
                    getSharedPreferences("Automation Settings", Context.MODE_PRIVATE).edit().putBoolean("AutoLogin", false).apply();
                }
            }
        });

        if (!isOpenedViaShare) {
            if (mAutologinEnabledValue) {
                mEditPhoneNumber.setText("18561126531");
                mEditIdNumber.setText("1234");
                mEditPassword.setText("61126531");
                mAutoLogin.setChecked(true);
            } else {
                mEditPhoneNumber.setText("");
                mEditIdNumber.setText("");
                mEditPassword.setText("");
                mAutoLogin.setChecked(false);
            }
        } else {
            mEditPhoneNumber.setText(recSplitMessages[3]);
            mEditIdNumber.setText(recSplitMessages[4]);
            mEditPassword.setText(recSplitMessages[5]);
        }

    }


    /**
     * Handle credentials
     *
     * @return Set of credentials depending on the user if he/she belongs to hmc/supplier
     */
    private boolean getResult() {
        if (BuildConfig.FLAVOR.equals("hmc")) {
            return (mStringPhoneNumber.equals("18561126531") && (mStringIdNumber.equals("1234")) && (mStringPassword.equals("61126531")));
        } else if (BuildConfig.FLAVOR.equals("supplier")) {
            return (mStringPhoneNumber.equals("18561126531") && (mStringIdNumber.equals("1234")) && (mStringPassword.equals("61126531")));
        } else {
            return false;
        }
     /*   if(BuildConfig.FLAVOR.equals("hmc"))
        {
            return (mStringPhoneNumber.equals("1") && (mStringIdNumber.equals("1")) && (mStringPassword.equals("1")));
        }else if(BuildConfig.FLAVOR.equals("supplier"))
        {
            return (mStringPhoneNumber.equals("1") && (mStringIdNumber.equals("1")) && (mStringPassword.equals("1")));
        }else
        {
            return false;
        }*/

    }
}
