/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: CustomDialogClass.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 31-10-2017
 *  ***************************************************************************************
 */
package hyundai.esds;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class CustomDialogClass {

    public static LayoutInflater sInflater;
    public static View sAlertDialogView;
    public static AlertDialog sAlertDialog;

    public static EditText sPasswordField;
    public static ImageView sPasswordViewOne,sPasswordViewTwo,sPasswordViewThree,sPasswordViewFour;
    public static TextView sKeyNumberOne,sKeyNumberTwo,sKeyNumberThree,sKeyNumberFour,
            sKeyNumberFive,sKeyNumberSix,sKeyNumberSeven,sKeyNumberEight,sKeyNumberNine,
            tViewZero,sKeyClear;
    public static ImageButton sKeyBackSpace;

    public static Context sContext;
    public static String RESULTFROMBDCST="resultfrombraodcast";


    /*public CustomDialogClass(Context context)
    {
        this.sContext=context;
    }*/

    public static void showDialog(final Activity activity, Context context)
    {
        sContext=context;
        sInflater = activity.getLayoutInflater();
        sAlertDialog = new AlertDialog.Builder(activity).create();
        sAlertDialog.setCancelable(true);
        if (sAlertDialog != null) {
            sAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        sAlertDialogView = sInflater.inflate(R.layout.activity_verify_pin_screen, null);
        sAlertDialog.setView(sAlertDialogView);

        //initializing all elements
        sPasswordField = (EditText) sAlertDialogView.findViewById(R.id.password_field);
        sPasswordViewOne=(ImageView)sAlertDialogView.findViewById (R.id.view1);
        sPasswordViewTwo =(ImageView)sAlertDialogView.findViewById (R.id.view2);
        sPasswordViewThree=(ImageView)sAlertDialogView.findViewById (R.id.view3);
        sPasswordViewFour=(ImageView)sAlertDialogView.findViewById (R.id.view4);
        sPasswordViewOne.setBackgroundColor(Color.LTGRAY);
        sPasswordViewTwo.setBackgroundColor(Color.LTGRAY);
        sPasswordViewThree.setBackgroundColor(Color.LTGRAY);
        sPasswordViewFour.setBackgroundColor(Color.LTGRAY);

        tViewZero=(TextView)sAlertDialogView.findViewById(R.id.t9_key_0);
        sKeyNumberOne=(TextView)sAlertDialogView.findViewById(R.id.t9_key_1);
        sKeyNumberTwo=(TextView)sAlertDialogView.findViewById(R.id.t9_key_2);
        sKeyNumberThree=(TextView)sAlertDialogView.findViewById(R.id.t9_key_3);
        sKeyNumberFour=(TextView)sAlertDialogView.findViewById(R.id.t9_key_4);
        sKeyNumberFive=(TextView)sAlertDialogView.findViewById(R.id.t9_key_5);
        sKeyNumberSix=(TextView)sAlertDialogView.findViewById(R.id.t9_key_6);
        sKeyNumberSeven=(TextView)sAlertDialogView.findViewById(R.id.t9_key_7);
        sKeyNumberEight=(TextView)sAlertDialogView.findViewById(R.id.t9_key_8);
        sKeyNumberNine=(TextView)sAlertDialogView.findViewById(R.id.t9_key_9);
        sKeyClear=(TextView)sAlertDialogView.findViewById(R.id.t9_key_clear);
        sKeyBackSpace=(ImageButton)sAlertDialogView.findViewById(R.id.t9_key_backspace);

        tViewZero.setOnClickListener(onclicklistener);
        sKeyNumberOne.setOnClickListener(onclicklistener);
        sKeyNumberTwo.setOnClickListener(onclicklistener);
        sKeyNumberThree.setOnClickListener(onclicklistener);
        sKeyNumberFour.setOnClickListener(onclicklistener);
        sKeyNumberFive.setOnClickListener(onclicklistener);
        sKeyNumberSix.setOnClickListener(onclicklistener);
        sKeyNumberSeven.setOnClickListener(onclicklistener);
        sKeyNumberEight.setOnClickListener(onclicklistener);
        sKeyNumberNine.setOnClickListener(onclicklistener);
        sKeyClear.setOnClickListener(onclicklistener);
        sKeyBackSpace.setOnClickListener(onclicklistener);

        sPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(sPasswordField.getText().toString().length()==4)
                {
                    Intent intent = new Intent("finish_activity");
                    if(sPasswordField.getText().toString().equals("1234"))
                    {
                        intent.putExtra(RESULTFROMBDCST, "true");
                        activity.sendBroadcast(intent);
                        sAlertDialog.dismiss();
                    }else if(!sPasswordField.getText().toString().equals("1234"))
                    {
                        sPasswordField.setText("");
                        intent.putExtra(RESULTFROMBDCST, "false");
                        Toast.makeText(sContext,"Wrong PIN",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        if(sAlertDialog!=null && sAlertDialog.isShowing()) {
            sAlertDialog.dismiss();
        }
        sAlertDialog.show();
    }

    private static View.OnClickListener onclicklistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // handle number button click

            if (v.getTag() != null && "number_button".equals(v.getTag()))
            {
                if(sPasswordField.getText().toString().length()>=4)
                {
                    Toast.makeText(sContext,"Length Exceeded",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    sPasswordField.append(((TextView) v).getText());
                    //Toast.makeText(getContext(), String.valueOf(sPasswordField.getText().toString().length()), Toast.LENGTH_SHORT).show();
                    updateViewsandValues(sPasswordField.getText().toString());
                    return;
                }
            }
            switch (v.getId())
            {
                case R.id.t9_key_clear: { // handle clear button
                    sPasswordField.setText(null);
                }
                break;
                case R.id.t9_key_backspace: { // handle backspace button
                    // delete one character
                    Editable editable = sPasswordField.getText();
                    int charCount = editable.length();
                    if (charCount > 0) {
                        editable.delete(charCount - 1, charCount);
                    }
                }
                break;
                case R.id.t9_key_0:
                    break;
            /*case R.id.imageButton_backVerifyPinScreen:
                onBackPressed();
                break;*/
            }

            // Toast.makeText(getContext(),String.valueOf(sPasswordField.getText().toString().length()),Toast.LENGTH_SHORT).show();
            updateViewsandValues(sPasswordField.getText().toString());

        }
    };


    /**
     * Used to update views based on the selected password
     * @param password used to store the value typed and extract its length
     */
    private static void updateViewsandValues(String password)
    {
        sPasswordViewOne.setBackgroundColor(Color.LTGRAY);
        sPasswordViewTwo.setBackgroundColor(Color.LTGRAY);
        sPasswordViewThree.setBackgroundColor(Color.LTGRAY);
        sPasswordViewFour.setBackgroundColor(Color.LTGRAY);
        // setXyz(password);
        switch(password.length())
        {
            case 0:
                break;
            case 1:
                sPasswordViewOne.setBackgroundColor(Color.BLACK);
                break;
            case 2:
                sPasswordViewOne.setBackgroundColor(Color.BLACK);
                sPasswordViewTwo.setBackgroundColor(Color.BLACK);
                break;
            case 3:
                sPasswordViewOne.setBackgroundColor(Color.BLACK);
                sPasswordViewTwo.setBackgroundColor(Color.BLACK);
                sPasswordViewThree.setBackgroundColor(Color.BLACK);
                break;
            case 4:
                sPasswordViewOne.setBackgroundColor(Color.BLACK);
                sPasswordViewTwo.setBackgroundColor(Color.BLACK);
                sPasswordViewThree.setBackgroundColor(Color.BLACK);
                sPasswordViewFour.setBackgroundColor(Color.BLACK);
                break;
            default:
                break;
        }

    }

}
