/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: Hyundai iblue Mobile App Development
 *  ** Target: Car Audio Control Gen 4.0 and D Audio platforms supported
 *  ** File Name: CustomViewPager.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 21-08-2017
 *  ***************************************************************************************
 */

package hyundai.esds;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Class to make use of Custom View Pager for Swiping between the fragments in the same activity
 */
public class CustomViewPager extends android.support.v4.view.ViewPager {
    public boolean enabled;

    /**
     * Initializes the constructor based on the parameters
     * @param context The Current Application Context
     * @param attrs
     */
    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return enabled && super.onTouchEvent(event);
    }

    /**
     * Recognize the TouchEvent enabled or disabled
     * @param event MotionEvent for the detection of the Swiping Action
     * @return boolean whether it is enabled or not
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return enabled && super.onInterceptTouchEvent(event);
    }
}