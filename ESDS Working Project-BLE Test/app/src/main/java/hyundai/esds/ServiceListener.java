/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: ServiceListener.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */
package hyundai.esds;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Backgorund servcie that handles recognising messages when received form ESDS app
 */
public class ServiceListener extends Service {
        @Override
        public IBinder onBind(Intent arg0) {
        return null;
        }


        @Override
        public void onDestroy() {
        super.onDestroy();
        }


        @Override
        public int onStartCommand(Intent intent, int flags, int startID) {
        return super.onStartCommand(intent,flags,startID);

        }
    }
