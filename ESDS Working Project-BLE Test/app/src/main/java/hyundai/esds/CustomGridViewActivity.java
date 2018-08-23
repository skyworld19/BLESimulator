/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: CustomGridView.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 31-10-2017
 *  ***************************************************************************************
 */

package hyundai.esds;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import static hyundai.esds.SplashScreen.isOpenedViaShare;
import static hyundai.esds.SplashScreen.recMessage;
import static hyundai.esds.SplashScreen.recSplitMessages;
import static hyundai.esds.SplashScreen.recStringArrayContains;


/**
 * Created by R415109 on 11/10/2016.**/
 public class CustomGridViewActivity extends BaseAdapter {


      private Context mContext;
      private final String[] gridViewString;
       private final boolean clickStatus;
  /*public static int[] mThumbIdsSelected={R.drawable.ic_music_selected,R.drawable.ic_bt_music_selected,
            R.drawable.ic_usb_selected,R.drawable.ic_aux_selected,
            R.drawable.ic_ipod_selected,R.drawable.ic_cd_selected};*/



      public CustomGridViewActivity(Context context, String[] gridViewString,boolean clickStatus) {
          mContext = context;
          this.gridViewString = gridViewString;
          this.clickStatus=clickStatus;
      }


      @Override
      public int getCount() {

              return gridViewString.length;

      }


      @Override
      public Object getItem(int i) {
          return null;
      }


      @Override
      public long getItemId(int i) {
          return 0;
      }


      @Override
      public View getView(int i, View convertView, ViewGroup parent) {
          View gridViewAndroid;
          LayoutInflater inflater = (LayoutInflater) mContext
                  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


          if (convertView == null) {


              gridViewAndroid = new View(mContext);
              gridViewAndroid = inflater.inflate(R.layout.grid_row, null);
              gridViewAndroid.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, (int) (80* Resources.getSystem().getDisplayMetrics().density)));
              TextView textViewAndroid = (TextView) gridViewAndroid.findViewById(R.id.gridV_view_text_title);
              textViewAndroid.setText(gridViewString[i]);
              /*if((gridViewString[i].equals("Lock")||(gridViewString[i].equals("Unlock"))||(gridViewString[i].equals("Climate \nControl"))||
                      (gridViewString[i].equals("Horn \nLight"))||(gridViewString[i].equals("Light"))||(gridViewString[i].equals("Vehicle \nStatus")))
                      ||(gridViewString[i].equals("Engine \nStart"))||(gridViewString[i].equals("Engine \nStop")))
              {
                  if(clickStatus)
                  textViewAndroid.setAlpha(1f);
                  else
                      textViewAndroid.setAlpha(0.5f);
              }else
              {
                  textViewAndroid.setAlpha(0.5f);
              }
              */
              if(gridViewString[i].equals("BLE \nConnected \nENGINE START")){
                  textViewAndroid.setAlpha(0.5f);
              }else{
                  if(clickStatus) {
                      if(!isOpenedViaShare) {
                          textViewAndroid.setAlpha(1f);
                      }
                      else {
                          textViewAndroid.setAlpha(0.5f);
                         switch(gridViewString[i]){
                             case "Lock":
                                 if(recStringArrayContains("Lock"))textViewAndroid.setAlpha(1f);
                                 break;
                             case "Unlock":
                                 if(recStringArrayContains("Unlock"))textViewAndroid.setAlpha(1f);
                                 break;
                             case "Climate \nControl":
                                 textViewAndroid.setAlpha(1f);
                                 break;
                             case "Horn \nLight":
                                 if(recStringArrayContains("HornLight"))textViewAndroid.setAlpha(1f);
                                 break;
                             case "Light":
                                 if(recStringArrayContains("Light"))textViewAndroid.setAlpha(1f);
                                 break;
                             case "Vehicle \nStatus":
                                textViewAndroid.setAlpha(1f);
                                 break;
                             case "Engine \nStart":
                                 if(recStringArrayContains("EngineStart"))textViewAndroid.setAlpha(1f);
                                 break;
                             case "Engine \nStop":
                                 if(recStringArrayContains("EngineStop"))textViewAndroid.setAlpha(1f);
                                 break;
                             default:
                                 break;
                         }
                      }
                  }else {
                      textViewAndroid.setAlpha(0.5f);
                  }
              }

          } else {

              gridViewAndroid = convertView;
          }
          return gridViewAndroid;
      }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    /**
     * Checks whether a listview item at a particular position is to be enabled
     * @param position Indicates the Index of the ListView Item
     * @return boolean whether the item is enabled or not
     */
    @Override
    public boolean isEnabled(int position) {
        if(clickStatus) {
            if(!isOpenedViaShare) {
                switch (gridViewString[position]) {
                    case "Lock":
                        return true;
                    case "Unlock":
                        return true;
                    case "Horn \nLight":
                        return true;
                    case "Light":
                        return true;
                    case "Vehicle \nStatus":
                        return true;
                    case "Engine \nStart":
                        return true;
                    case "Engine \nStop":
                        return true;
                    case "Climate \nControl":
                        return true;
                    default:
                        return false;
                }
            }else{
                switch (gridViewString[position]) {
                    case "Lock":
                        return recStringArrayContains("Lock");
                    case "Unlock":
                        return recStringArrayContains("Unlock");
                    case "Horn \nLight":
                        return recStringArrayContains("HornLight");
                    case "Light":
                        return recStringArrayContains("Light");
                    case "Vehicle \nStatus":
                        return true;
                    case "Engine \nStart":
                        return recStringArrayContains("EngineStart");
                    case "Engine \nStop":
                        return recStringArrayContains("EngineStop");
                    case "Climate \nControl":
                        return true;
                    default:
                        return false;
                }
            }
        }else{
            return false;
        }
    }

}
