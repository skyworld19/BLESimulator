/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: NotificationActionResponser.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */
package hyundai.esds;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Receiver which responds when the user clicks on the buttons form the notification
 */
public class NotificationActionResponser extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getStringExtra("BUTTONACTIONS");
        switch (action){
            case "buttonAccept":
                Toast.makeText(context,action,Toast.LENGTH_SHORT).show();
                Intent notificationIntent = new Intent(context, SplashScreen.class);
                notificationIntent.putExtra("ESDS SERVICE MESSAGE",NotificationCreator.receivedMessage);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(notificationIntent);
                break;
            case "buttonDeny":
                Toast.makeText(context,action,Toast.LENGTH_SHORT).show();
                break;
        }
        NotificationCreator.cancelNotification();
    }
}
