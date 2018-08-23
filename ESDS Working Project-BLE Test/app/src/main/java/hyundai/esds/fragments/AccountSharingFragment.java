/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: AccountSharingFragment.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */
package hyundai.esds.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import hyundai.esds.R;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static hyundai.esds.LoginScreen.tobeSendIdNumber;
import static hyundai.esds.LoginScreen.tobeSendPassword;
import static hyundai.esds.LoginScreen.tobeSendPhoneNumber;
import static hyundai.esds.SmsBroadcastReceiver.recPhoneNumber;
import static hyundai.esds.SplashScreen.isOpenedViaShare;
import static hyundai.esds.SplashScreen.recSplitMessages;

/**
 * User can share his account details by filling the deatils of other persons phone number,duration,permissions required and vehicle location
 */
public class AccountSharingFragment extends Fragment implements View.OnClickListener {


    //Declaring all member fields
    ImageButton backImBtAccountSharing,pickContactImbt;
    public View fragview;
    CheckBox checkLock,checkUnlock,checkEngineStart,checkHornLight,checkEngineStop,checkLight;
    boolean isCheckLock,isCheckUnlock,isCheckEngineStart,isCheckHornLight,isCheckEngineStop,isCheckLight;
    EditText sendToEditPhone;
    TextView editDurationFrom,editDurationTo;
    Button approveButton;
    SmsManager smsManager;
    ImageView sendtoContactImage;
    TextView shareStatusTextView,recPhoneNumberTextView,controlsRecTextView;
    RelativeLayout sendToLayout,receiveFromLayout;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener dateFrom,dateTo;
    String myFormat;
    SimpleDateFormat sdf;
    private String contactID;     // contacts unique ID

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragview=inflater.inflate(R.layout.fragment_account_sharing, container, false);
        //initializing memeber fields
        myCalendar=Calendar.getInstance();
        myFormat = "MM/dd/yyyy"; //In which you need put here
        sdf = new SimpleDateFormat(myFormat, Locale.US);

        initializeAllItems();
        smsManager= SmsManager.getDefault();
        return fragview;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();

        receiveFromLayout.setVisibility(View.INVISIBLE);
        sendToLayout.setVisibility(View.INVISIBLE);
        //If opened app from notifcation, the condition returns true
        if(isOpenedViaShare){
            shareStatusTextView.setText("Received from");
            receiveFromLayout.setVisibility(View.VISIBLE);
            String controls_message="";
            for(int i=6;i<recSplitMessages.length;i++){
               controls_message=controls_message+recSplitMessages[i];
               if(i!=recSplitMessages.length-1)
               {
                   controls_message=controls_message+",";
               }
            }
            controlsRecTextView.setText(controls_message);
            recPhoneNumberTextView.setText(recPhoneNumber);

        }else{
            shareStatusTextView.setText("Send to");
            sendToLayout.setVisibility(View.VISIBLE);
        }
        //initialize all check box boolean variables
        isCheckHornLight=checkHornLight.isChecked();
        isCheckEngineStart=checkEngineStart.isChecked();
        isCheckLock=checkLock.isChecked();
        isCheckUnlock=checkUnlock.isChecked();
        isCheckLight=checkLight.isChecked();
        isCheckEngineStop=checkEngineStop.isChecked();

        //Dummy duration for demo purposes
        editDurationFrom.setText("Duration to be allowed");
        editDurationTo.setText("Duration to be allowed");
        dateFrom=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editDurationFrom.setText(sdf.format(myCalendar.getTime()));

            }
        };

        dateTo=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editDurationTo.setText(sdf.format(myCalendar.getTime()));

            }
        };

        backImBtAccountSharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }


    //For getting result from Contacts screen when user picks any one of them
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;

    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.check_engine_start:
                isCheckEngineStart=((CheckBox)view).isChecked();
                break;
            case R.id.check_engine_stop:
                isCheckEngineStop=((CheckBox)view).isChecked();
                break;
            case R.id.check_lock:
                isCheckLock=((CheckBox)view).isChecked();
                break;
            case R.id.check_unlock:
                isCheckUnlock=((CheckBox)view).isChecked();
                break;
            case R.id.check_light:
                isCheckLight=((CheckBox)view).isChecked();
                break;
            case R.id.check_horn_light:
                isCheckHornLight=((CheckBox)view).isChecked();
                break;
            case R.id.image_button_contacts:
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
                break;
            case R.id.sedn_to_approve_button:
                    if(sendToEditPhone.getText().length()>=10) {

                        smsManager.sendTextMessage(sendToEditPhone.getText().toString(), null, prepareMessage(), null, null);
                        Toast.makeText(getActivity().getApplicationContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(), "Make sure you have entered correct number", Toast.LENGTH_SHORT).show();
                    }
                break;

            case R.id.edit_duration_from:
                new DatePickerDialog(getActivity(), dateFrom, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.edit_duration_to:
                new DatePickerDialog(getActivity(), dateTo, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            default:
                break;
        }

    }



    //Preparing message that is to be sent while sharing accoutn details
    private String prepareMessage() {
        String message="ESDS Service Message"+" "+tobeSendPhoneNumber+" "+tobeSendIdNumber+" "+tobeSendPassword+" ";
        if(isCheckHornLight){
            message=message+"HornLight"+" ";
        }

        if(isCheckLight){
            message=message+"Light"+" ";
        }

        if(isCheckLock){
            message=message+"Lock"+" ";
        }

        if(isCheckUnlock){
            message=message+"Unlock"+" ";
        }

        if(isCheckEngineStart){
            message=message+"EngineStart"+" ";
        }

        if(isCheckEngineStop){
            message=message+"EngineStop"+" ";
        }

        return message;
    }


    //Uri for getting the result when a specific contact is being selected from the contacts screen
    private Uri uriContact;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            retrieveContactName();
            retrieveContactNumber();
             retrieveContactPhoto();
        }
    }


    //Method to retrieve photo if available in the contacts data base of the users phone
    private void retrieveContactPhoto() {

        Bitmap photo = null;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getActivity().getContentResolver(),ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                sendtoContactImage.setImageBitmap(photo);
            }
            assert inputStream != null;
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            sendtoContactImage.setImageDrawable(getResources().getDrawable(R.drawable.no_image_available));
        }

    }


    //Method to retrieve contact number from the contacts screen
    private void retrieveContactNumber() {

        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getActivity().getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        Log.d(TAG, "Contact Phone Number: " + contactNumber);
        sendToEditPhone.setText(contactNumber);
    }

    //Method to retrieve contact name from the contacts screen
    private void retrieveContactName() {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getActivity().getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        Log.d(TAG, "Contact Name: " + contactName);

    }

    //initilaizing all check boxes
    private void initializeAllItems() {
        controlsRecTextView=fragview.findViewById(R.id.controls_text_view);
        recPhoneNumberTextView=fragview.findViewById(R.id.rec_phone_number);
        sendToLayout=fragview.findViewById(R.id.send_to_layout);
        receiveFromLayout=fragview.findViewById(R.id.received_from_layout);
        sendtoContactImage=fragview.findViewById(R.id.send_to_image_view);
        shareStatusTextView=fragview.findViewById(R.id.share_status_text_view);
        approveButton=fragview.findViewById(R.id.sedn_to_approve_button);
        backImBtAccountSharing= fragview.findViewById(R.id.imagebutton_back_account_sharing_fragment);
        pickContactImbt=fragview.findViewById(R.id.image_button_contacts);
        sendToEditPhone=fragview.findViewById(R.id.send_to_edit_phone);
        editDurationFrom=fragview.findViewById(R.id.edit_duration_from);
        editDurationTo=fragview.findViewById(R.id.edit_duration_to);


        checkEngineStart=fragview.findViewById(R.id.check_engine_start);
        checkLock=fragview.findViewById(R.id.check_lock);
        checkUnlock=fragview.findViewById(R.id.check_unlock);
        checkHornLight=fragview.findViewById(R.id.check_horn_light);
        checkEngineStop=fragview.findViewById(R.id.check_engine_stop);
        checkHornLight=fragview.findViewById(R.id.check_horn_light);
        checkLight=fragview.findViewById(R.id.check_light);

        approveButton.setOnClickListener(this);
        pickContactImbt.setOnClickListener(this);
        checkEngineStart.setOnClickListener(this);
        checkLock.setOnClickListener(this);
        checkUnlock.setOnClickListener(this);
        checkEngineStop.setOnClickListener(this);
        checkHornLight.setOnClickListener(this);
        checkLight.setOnClickListener(this);
        editDurationFrom.setOnClickListener(this);
        editDurationTo.setOnClickListener(this);
    }
}