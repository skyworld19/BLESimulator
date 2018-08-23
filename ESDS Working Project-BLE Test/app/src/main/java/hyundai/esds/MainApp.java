/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: MainApp.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */
package hyundai.esds;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//used for monitoring report crashes
@ReportsCrashes(
        formUri = "https://medo.cloudant.com/acra-example/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "tubtakedstinumenterences",
//        formUriBasicAuthLogin = "medo",
//        formUriBasicAuthPassword = "acraexample",
        formUriBasicAuthPassword = "igqMFFMatvtMXVCKgy7u6a5W",
        formKey = "", // This is required for backward compatibility but not used
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.toast_crash
)

/**
 * Activity that implements ActivityLifeCycleCallBack events for monitoring whether the app is in foreground or background
 */
public class MainApp extends Application implements Application.ActivityLifecycleCallbacks {

    //Declaring member fields
    String toastString=null;
    public static boolean networkStatus=false,timeZoneStatus=false,datePermitStatus=false;
    int instCounter=0;
    @Override
    public void onCreate() {
        super.onCreate();
        // Triggers the initialization of ACRA
        //ACRA.init(this);
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d("STATUS","onResumed");
        toastString="ESDS App Warning!!!";
        if(BuildConfig.FLAVOR.equals("supplier")){
            isConnected();
            isEnabled();
            checkifDatePermits();
            //checks whether network, time zone and date settings are allowed to use by ESDS app, return false if any of the condition is failed
            if(networkStatus&&timeZoneStatus&&datePermitStatus){
              //continue
            }else{
                Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
                instCounter=0;
                activity.finish();
            }
        }
    }

    //Method to check whether the supplier
    public void isConnected() {
        ConnectivityManager cm =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
            networkStatus=activeNetwork != null && activeNetwork.isConnected();
        }else{
            networkStatus=false;
        }

        if(networkStatus){
            //do nothing
        }else{
            instCounter++;
            toastString=toastString+"\n"+instCounter+") Make sure mobile data is enabled";
        }
    }

    //Method to check whether time zone in the settings is enabled or not
    public void isEnabled() {
        //integer value which will intialized with 0 or 1 depending on the permiisions given under date and time zone settings
        int test= Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0);
        int test1= Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0);
        if((test1+test)==0){
            timeZoneStatus=false;
            instCounter++;
            toastString=toastString+"\n"+instCounter+") Make sure mobile date & time are synced with network timezone";
        }else if(test1+test==2){
            timeZoneStatus=true;
        }else{
            timeZoneStatus=false;
            instCounter++;
            toastString=toastString+"\n"+instCounter+") Make sure mobile date & time are synced with network timezone";
        }
    }


    //Method to check whether supplier is using app in the permitted period
    private void checkifDatePermits() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String mydate=formatter.format(today);
        String dateStart = "12/18/2017 12:30:00";
        //HH converts hour in 24 hours format (0-23), day calculation
        ///SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        Date d1 = null;
        Date d2 = null;
        try {
            //d1 = format.parse(dateStart);
            d1=formatter.parse(dateStart);
            d2 = formatter.parse(mydate);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();
            //long diff=d1.getTime()-d2.getTime();
            long diffDays = Math.abs(diff / (24 * 60 * 60 * 1000));

            /*long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;*/

            System.out.println(diffDays + " days, ");
            datePermitStatus=diffDays >=0 && diffDays <= 60;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(datePermitStatus){

        }else{

            if(timeZoneStatus&&networkStatus){
                instCounter++;
                toastString=toastString+"\n"+instCounter+") Your trail period expired";
            }

        }
    }


    @Override
    public void onActivityPaused(Activity activity) {
        Log.d("STATUS","onPaused");
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
