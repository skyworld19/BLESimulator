/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: SplashScreen.java
 *  ** @Author: Sai Sriram Madhiraju
 *  ** @Co-Author: Sivaram Boina
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */

package hyundai.esds;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.SEND_SMS;
import static hyundai.esds.MainApp.datePermitStatus;
import static hyundai.esds.MainApp.networkStatus;
import static hyundai.esds.MainApp.timeZoneStatus;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 2500;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 0;
    Intent loginScreenIntent;
    public static Context sContext;
    public RelativeLayout splashRelativeLayout,permissionLocationAccessLayout,permissionSmsAccessLayout,permissionContactsAccessLayout;
    Button permissionAccessOkButton;
    TextView permissionAccessTextview;
    Animation slide_up;

    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    private static final int READ_CONTACTS_REQUEST=2;
    ArrayAdapter arrayAdapter;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    public static boolean isOpenedViaShare=false;
    public static String recMessage=null;
    public static String[] recSplitMessages=null;


    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        splashRelativeLayout=(RelativeLayout)findViewById(R.id.splash_relative_layout);
        loginScreenIntent=new Intent(this, LoginScreen.class);
        loginScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        permissionAccessOkButton=(Button)findViewById(R.id.permission_access_button);
        permissionAccessTextview=(TextView)findViewById(R.id.permission_access_textview);

        permissionLocationAccessLayout=(RelativeLayout)findViewById(R.id.permission_access_layout);

        permissionSmsAccessLayout=(RelativeLayout)findViewById(R.id.permission_access_layout);

        permissionContactsAccessLayout=(RelativeLayout)findViewById(R.id.permission_access_layout);

        showLocationPermitLayout(false);
        showSmsPermitLayout(false);
        showContactsPermitLayout(false);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,smsMessagesList);
    }


    public static boolean recStringArrayContains(String checkMes){
        for(int i=0;i<recSplitMessages.length;i++){
            if(recSplitMessages[i].equals(checkMes)) return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume()
    {
        super.onResume();
        recMessage=getIntent().getStringExtra("ESDS SERVICE MESSAGE");
        if(recMessage!=null){
            isOpenedViaShare=true;
            recSplitMessages=recMessage.split(" ");
            Log.d("FINAL","Value:"+recMessage+" Splits Messages:"+ Arrays.toString(recSplitMessages));
        }else{
          isOpenedViaShare=false;
          recSplitMessages=null;
        }


        sContext=getApplicationContext();
        populateAutoComplete();

        permissionAccessOkButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(permissionAccessTextview.getText().equals("Need permission to access location to find nearby BLE devices")){
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                }else if(permissionAccessTextview.getText().equals("Need permission to access sms to share account details")){
                    requestPermissions(new String[]{Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS},READ_SMS_PERMISSIONS_REQUEST);
                }else if(permissionAccessTextview.getText().equals("Need permission to access contacts to share account details")){
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},READ_CONTACTS_REQUEST);
                }
            }
        });
    }

    private static SplashScreen inst;
    public static SplashScreen instance() {
        return inst;
    }

    public static boolean active=false;
    @Override
    public void onStart(){
        super.onStart();
        inst=this;
        active=true;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        active=false;
    }

    public  void showNotification(String smsBody){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification");


        Intent notificationIntent = new Intent(this, LoginScreen.class);
        notificationIntent.putExtra("USERID",smsBody.split(" ")[3]);
        notificationIntent.putExtra("PASSWORD",smsBody.split(" ")[5]);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    //Method that refershes the inbox of the user mobile
    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do {
            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
    }


    boolean isLocation=false,isSms=false,isContacts=false;
    private void populateAutoComplete() {
        isLocation = mayRequestLocation();
        isSms = mayRequestSmsPermission();
        isContacts=mayRequestContacts();

        if(isSms&&isLocation&&isContacts){
            if(BuildConfig.FLAVOR.equals("supplier")){
                if(networkStatus&&timeZoneStatus&&datePermitStatus){
                    moveToNextScreen();
                }
            }else if(BuildConfig.FLAVOR.equals("hmc")){
                moveToNextScreen();
            }else{
                //add if any further flavors to be included
            }
        }
    }

    /**
     * Check if location permissions are allowed or not
     * @return true if the user allowed
     */
    private boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)){
            showLocationPermitLayout(true);
        }else{
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        }
        return false;
    }

    /**
     * Check if contacts permissions are allowed or not
     * @return true if the user allowed
     */
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            return true;
        }

        if (shouldShowRequestPermissionRationale(READ_CONTACTS)){
            showContactsPermitLayout(true);
        }else{
            requestPermissions(new String[]{READ_CONTACTS}, READ_CONTACTS_REQUEST);
        }
        return false;
    }

    /**
     * Check if sms permissions are allowed or not
     * @return true if the user allowed
     */
    private boolean mayRequestSmsPermission(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(READ_SMS) == PackageManager.PERMISSION_GRANTED)&&(checkSelfPermission(SEND_SMS)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(READ_SMS))||(shouldShowRequestPermissionRationale(SEND_SMS))){
            showSmsPermitLayout(true);
        }else{
            requestPermissions(new String[]{Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS},READ_SMS_PERMISSIONS_REQUEST);
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    private void showSmsPermitLayout(boolean showornot) {
        permissionAccessTextview.setText("Need permission to access sms to share account details");
        if(showornot)
        {
            permissionSmsAccessLayout.startAnimation(slide_up);
            permissionSmsAccessLayout.setVisibility(View.VISIBLE);
        }else{
            permissionSmsAccessLayout.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showLocationPermitLayout(boolean showornot) {
        permissionAccessTextview.setText("Need permission to access location to find nearby BLE devices");
        if(showornot)
        {
            permissionLocationAccessLayout.startAnimation(slide_up);
            permissionLocationAccessLayout.setVisibility(View.VISIBLE);
        }else{
            permissionLocationAccessLayout.setVisibility(View.INVISIBLE);
        }
    }


    @SuppressLint("SetTextI18n")
    private void showContactsPermitLayout(boolean showornot) {
        permissionAccessTextview.setText("Need permission to access contacts to share account details");
        if(showornot)
        {
            permissionLocationAccessLayout.startAnimation(slide_up);
            permissionLocationAccessLayout.setVisibility(View.VISIBLE);
        }else{
            permissionLocationAccessLayout.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
                populateAutoComplete();
                showLocationPermitLayout(false);
               // moveToNextScreen();
            }
        }else if(requestCode== READ_SMS_PERMISSIONS_REQUEST){
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                refreshSmsInbox();
                populateAutoComplete();
                showSmsPermitLayout(false);
            }
        }else if(requestCode==READ_CONTACTS_REQUEST){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
                populateAutoComplete();
                showContactsPermitLayout(false);
            }
        }
    }


    @Override
    public void onPause()
    {
        super.onPause();
        if(permissionLocationAccessLayout.getVisibility()==View.VISIBLE){
            showLocationPermitLayout(false);
        }else if(permissionSmsAccessLayout.getVisibility()==View.VISIBLE){
            showSmsPermitLayout(false);
        }else if(permissionContactsAccessLayout.getVisibility()==View.VISIBLE){
            showContactsPermitLayout(false);
        }else{
            //add if any further permissions requested in future
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public void moveToNextScreen()
    {
        //Handler to wait for a certain time in the screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(loginScreenIntent);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
