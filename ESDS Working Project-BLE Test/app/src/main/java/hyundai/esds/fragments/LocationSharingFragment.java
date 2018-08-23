/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: LocationSharingFragment.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 31-10-2017
 *  ***************************************************************************************
 */
package hyundai.esds.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import hyundai.esds.R;

public class LocationSharingFragment extends Fragment {

    ImageButton mbackImBtLocationSharingFragment;
    Button mbuttonSendLocationtoNewUser,mbuttonCheckLastVehicleLocation;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_location_sharing, container, false);
        mbackImBtLocationSharingFragment=(ImageButton) v.findViewById(R.id.imagebutton_back_location_sharing_fragment);
        mbuttonSendLocationtoNewUser=(Button)v.findViewById(R.id.button_send_vehicle_location);
        mbuttonCheckLastVehicleLocation=(Button)v.findViewById(R.id.button_check_vehicle_last_location);
        mbackImBtLocationSharingFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        final Intent i=new Intent(this.getContext(),LocationSharingMapViewActivity.class);
        mbuttonSendLocationtoNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(i);
/*                LocationSharingMapViewActivity fragmentLocationSharingMapview = new LocationSharingMapViewActivity();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.layout_location_sharing, fragmentLocationSharingMapview);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
            }
        });
    }
}
