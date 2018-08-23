package hyundai.esds;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by R415109 on 9/14/2017.
 */

public class MyApplication extends Application {
    SharedPreferences prefs;
    public  MyApplication() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
    }


    public void setSharedPreferenceValue(String message)
    {
        String dateTimeKey = "com.example.app.datetime";

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Name","Harneet");
        editor.apply();


    }

    public static String getSharedPreferenceValue()
    {

        return "send your value";
    }
}
