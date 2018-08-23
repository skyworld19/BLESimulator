/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: Hyundai iblue Mobile App Development
 *  ** Target: Car Audio Control Gen 4.0 and D Audio platforms supported
 *  ** File Name: PagerAdapter.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Manideep Kumar Sadhu
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */

package hyundai.esds;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;

import hyundai.esds.fragments.AccountSharingFragment;
import hyundai.esds.fragments.AutomationSettingFragment;
import hyundai.esds.fragments.IdleTimeFragment;
import hyundai.esds.fragments.LocationSharingFragment;
import hyundai.esds.fragments.MyInformationFragment;
import hyundai.esds.fragments.ReservationVehicleFragment;
import hyundai.esds.fragments.TemperatureFragment;
import hyundai.esds.fragments.VehicleControlFragment;

/**
 * Sets the PagerAdapter based on the Calling Activity and number of Tabs
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    public int mNumOfTabs;
    public String fromClass;
    public HashMap<Integer, Fragment> mHashMap;
    public HashMap<Integer, Fragment> mClimateHashMap;
    // public String callingpagername;

    /**
     * Initializes the Constructor based on the params assigned
     * @param fm create transactions for adding, removing or replacing fragments.
     * @param NumOfTabs Indicates the Number of Tabs required
     * @param fromClass Calling Class
     */
    public PagerAdapter(FragmentManager fm, int NumOfTabs, String fromClass) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.fromClass = fromClass;
        mHashMap = new HashMap<>();
        mClimateHashMap = new HashMap<>();
        // this.callingpagername=callingpagername;
    }

    /**
     * Returns the item at the specified position
     * @param position Index of the Current Page
     * @return Item at Index
     */
    @Override
    public Fragment getItem(int position) {
        Fragment f;
        if(fromClass.equals("ControlPagerActivity"))
        {
            switch (position)
            {
                case 0:
                    f = new VehicleControlFragment();
                    mHashMap.put(position,f);
                    return f;
                case 1:
                    f = new AutomationSettingFragment();
                    mHashMap.put(position,f);
                    return f;
                case 2:
                    f = new LocationSharingFragment();
                    mHashMap.put(position,f);
                    return f;
                case 3:
                    f = new AccountSharingFragment();
                    mHashMap.put(position,f);
                    return f;
                case 4:
                    f = new ReservationVehicleFragment();
                    mHashMap.put(position,f);
                    return f;
                case 5:
                    f = new MyInformationFragment();
                    mHashMap.put(position,f);
                    return f;
                default:
                    return null;
            }
        }
        else if(fromClass.equals("Climate Control"))
        {
            switch(position)
            {
                case 0:
                    f = new TemperatureFragment();
                    mClimateHashMap.put(position, f);
                    return f;
                case 1:
                    f = new IdleTimeFragment();
                    mClimateHashMap.put(position, f);
                    return f;
                default:
                    return null;
            }
        }else
         {
            return null;
        }
    }

    /**
     * Returns the Count of Number of Pages
     * @return Count of Total Pages in the Adapter
     */
    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public Fragment getFragmentHash(int pos) {
        return mHashMap.get(pos);
    }

    public Fragment getClimateFragmentHash(int pos) { return  mClimateHashMap.get(pos); }
}