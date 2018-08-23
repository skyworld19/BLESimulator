/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: NotificationCreator.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */
package hyundai.esds;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;

/**
 *Class for creating notifications
 */
public class NotificationCreator {

    private static NotificationManager manager;
    public static String receivedMessage;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void showNotification(String smsBody, Context context){
        receivedMessage=smsBody;
        /*Notification notification = new Notification(icon, "Custom Notification", when);*/
        Notification.Builder notifyBuilder = new Notification.Builder(context);
        Notification foregroundNote = notifyBuilder.setContentTitle("ESDS Accept Request")
                .setContentText("Slide down on note to expand")
                .setSmallIcon(R.drawable.esds_app_icon)
                .build();

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.drawable.esds_app_icon);
        contentView.setTextViewText(R.id.title, "ESDS Service Message");
        contentView.setTextViewText(R.id.text, "Click Accept to see the details shared by your friend");

        //notification.contentView = contentView;
        foregroundNote.bigContentView=contentView;

        foregroundNote.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        foregroundNote.defaults |= Notification.DEFAULT_LIGHTS; // LED
        foregroundNote.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        foregroundNote.defaults |= Notification.DEFAULT_SOUND; // Sound

        //button Accept is clicked
        Intent buttonAcceptClickIntent=new Intent(context,NotificationActionResponser.class);
        buttonAcceptClickIntent.putExtra("BUTTONACTIONS","buttonAccept");
        PendingIntent pendingIntentButtonAcceptClickIntent=PendingIntent.getBroadcast(context,0,buttonAcceptClickIntent,0);
        contentView.setOnClickPendingIntent(R.id.buttonAccept,pendingIntentButtonAcceptClickIntent);

        //button Deny is clicked
        Intent buttonDenyClickIntent=new Intent(context,NotificationActionResponser.class);
        buttonDenyClickIntent.putExtra("BUTTONACTIONS","buttonDeny");
        PendingIntent pendingIntentButtonDenyClickIntent= PendingIntent.getBroadcast(context,1,buttonDenyClickIntent,0);
        contentView.setOnClickPendingIntent(R.id.buttonDeny,pendingIntentButtonDenyClickIntent);;

        // Add as notification
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, foregroundNote);
    }

    /*
    Method to cancel the notification if any are opened
     */
    public static void cancelNotification(){
        if(manager!=null){
            manager.cancel(1);
        }
    }
}
