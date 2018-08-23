/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: ClimateControlConfiguration.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */
package hyundai.esds.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import hyundai.esds.PagerAdapter;
import hyundai.esds.R;

//Fragment which holds another two fragments Temperature fragment and idle time fragment
public class ClimateControlConfiguration extends Fragment {

    //Declaring member fields
    TabLayout climateControlTabLayout;
    public ViewPager climateControlViewPager;
    PagerAdapter adapter=null;
    ImageButton backImBtClimateControlFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_climate_control_configuration, container, false);

        //Intializing member fields
        climateControlTabLayout= v.findViewById(R.id.climate_control_tab_layout);
        climateControlViewPager= v.findViewById(R.id.climate_control_view_pager);
        climateControlTabLayout.addTab(climateControlTabLayout.newTab().setText("Temperature"));
        climateControlTabLayout.addTab(climateControlTabLayout.newTab().setText("Idle Time"));
        adapter=new PagerAdapter(getChildFragmentManager(),climateControlTabLayout.getTabCount(),"Climate Control");
        climateControlViewPager.setAdapter(adapter);
        climateControlViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(climateControlTabLayout));
        backImBtClimateControlFragment= v.findViewById(R.id.imagebutton_back_climate_control_configuration);
        backImBtClimateControlFragment.setOnClickListener(new View.OnClickListener() {
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
        climateControlTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                climateControlViewPager.setCurrentItem(tab.getPosition());
               /* if(tab.getPosition()==0){
                    TemperatureFragment f = (TemperatureFragment) adapter.getClimateFragmentHash(climateControlViewPager.getCurrentItem());
                    if (f != null) {
                        f.initView();
                    }
                } else {
                    IdleTimeFragment f = (IdleTimeFragment) adapter.getClimateFragmentHash(climateControlViewPager.getCurrentItem());
                    if (f != null) {
                        f.initView();
                    }
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        climateControlViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(climateControlViewPager.getCurrentItem() == 0){
                    TemperatureFragment f = (TemperatureFragment) adapter.getClimateFragmentHash(climateControlViewPager.getCurrentItem());
                    if (f != null) {
                        f.initView();
                    }
                } else {
                    IdleTimeFragment f = (IdleTimeFragment) adapter.getClimateFragmentHash(climateControlViewPager.getCurrentItem());
                    if (f != null) {
                        f.initView();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public ViewPager getViewPager(){
        return climateControlViewPager;
    }

    public PagerAdapter getPageAdapter() {
        return adapter;
    }
}
