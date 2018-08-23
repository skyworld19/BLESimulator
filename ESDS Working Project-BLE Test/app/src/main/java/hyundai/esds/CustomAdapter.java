/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: CustomAdapter.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 31-10-2017
 *  ***************************************************************************************
 */

package hyundai.esds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Adapter Used to Populate the Custom ListViews based on the received values
 */

public class CustomAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mTitle;
    private ArrayList<String> mList;
    private String mFromString;


    /**
     * Initializes Constructor based on the param values
     * @param context Takes the current Context of the Application
     * @param text1 ListView Item Text
     * @param mFromString Sending Class
     */
    public CustomAdapter(Context context, String[] text1, String mFromString) {
        this.mContext = context;
        this.mTitle = text1;
        this.mFromString = mFromString;
    }

    /**
     * Method to calculate the size of the list
     * @return List Size
     */
    public int getCount() {
        // TODO Auto-generated method stub
        if (mFromString.equals("HomeScreen"))
        {
            return mTitle.length;
        }else if(mFromString.equals("VehicleControl"))
        {
            return mTitle.length;
        }
        else {
            return mList.size();
        }
    }

    /**
     * Method used to get the Item from the List. Currently not used
     * @param arg0 Position in the List
     * @return list item object found at the position specified
     */
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method used to getItemId from the list at a specified position
     * @param position Specifies the Index of the item in the list
     * @return Integer variable specifying the itemId at position
     */
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /**
     * Used to set the view based on the parent view and the List Type
     * @param position Integer value that holds the index of the item in the ListView
     * @param convertView Not Used
     * @param parent used to set the custom layout for the row in the list
     * @return Returns the View Associated with the Current Populated ListView
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mFromString.equals("HomeScreen"))
        {
            LayoutInflater inflater_media = LayoutInflater.from(mContext);
            View list_rowView = inflater_media.inflate(R.layout.list_row, parent, false);
            TextView listRowText = (TextView) list_rowView.findViewById(R.id.list_row_title);
            listRowText.setText(mTitle[position]);
            listRowText.setAlpha(1f);
            return list_rowView;
        }
        else if(mFromString.equals("VehicleControl"))
        {
            LayoutInflater inflater_media = LayoutInflater.from(mContext);
            View grid_rowView = inflater_media.inflate(R.layout.grid_row, parent, false);
            TextView gridViewText = (TextView) grid_rowView.findViewById(R.id.gridV_view_text_title);
            gridViewText.setText(mTitle[position]);
            gridViewText.setAlpha(1f);
            return grid_rowView;
        }
        else {
            return null;
        }


    }

    /**
     * Checks whether all items are enabled in the list
     * @return boolean whether all items are enabled based on the source availability
     */
    @Override
    public boolean areAllItemsEnabled() {
        if (mFromString.equals("HomeScreen")) {
            return false;
        }else if (mFromString.equals("VehicleControl")) {
            return true;
        }  else {
            return true;
        }
    }

    /**
     * Checks whether a listview item at a particular position is to be enabled
     * @param position Indicates the Index of the ListView Item
     * @return boolean whether the item is enabled or not
     */
    @Override
    public boolean isEnabled(int position) {
        if (mFromString.equals("HomeScreen")) {
            return true;
        } else if (mFromString.equals("DeviceActivity")) {
            return true;
        } else {
            return true;
        }
    }

}