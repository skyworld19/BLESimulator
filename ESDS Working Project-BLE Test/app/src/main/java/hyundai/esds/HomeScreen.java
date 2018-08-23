/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: HomeScreen.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */

package hyundai.esds;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import static hyundai.esds.SplashScreen.sContext;

public class HomeScreen extends AppCompatActivity {

    private static Toast toast=null;
    ListView homeScreenListView;
    CustomAdapter homeScreenListAdapter;
    ImageButton imageButtonBluetoothIcon;
    String[] mtextString=null;
    Intent mIntentControlPagerActivityScreen;
   // public BluetoothAdapter mBluetoothAdapter;
   // public static final int REQUEST_ENABLE_BT=1;

    public static HomeScreen instance=null;

    public final static String EXTRA_DEVICE_ADDRESS = "EXTRA_DEVICE_ADDRESS";
    public final static String EXTRA_DEVICE_NAME = "EXTRA_DEVICE_NAME";
    private boolean doubleBackToExitPressedOnce=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        mtextString=new String[]{"Vehicle Control","Automation Setting","Location Sharing","Account Sharing","Reservation Vehicle","My Information"};
        homeScreenListView=(ListView)findViewById(R.id.listView_homeScreen);
        imageButtonBluetoothIcon=(ImageButton) findViewById(R.id.imageButton_homeScreen_bluetoothIcon);
        mIntentControlPagerActivityScreen=new Intent(getApplicationContext(),ControlPagerActivity.class);

        instance = this;
    }/**/

    @Override
    protected void onResume()
    {
        super.onResume();

        homeScreenListAdapter=new CustomAdapter(this,mtextString,"HomeScreen");
        homeScreenListView.setAdapter(homeScreenListAdapter);
        homeScreenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                mIntentControlPagerActivityScreen.putExtra("CLICKEDLISTPOSITION",position);
                startActivity(mIntentControlPagerActivityScreen);
            }
        });
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

        //register the click event when the user presses back button twice at a time
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    /**
     * Method to display toast message with LENGTH_SHORT duration
     *
     * @param messagetobedisplayed message that is to be displayed in the toast
     */
    public static void toasttobeDisplayed(String messagetobedisplayed) {
        try {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(sContext, messagetobedisplayed, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            //do nothing
        }
    }
}
