/*
 * ***************************************************************************************
 *  ** Hyundai Motor India Engineering Pvt Ltd
 *  ** Electronics Engineering Dept-1., - Software Development Team
 *  ** Do Not Copy Without Prior Permission
 *  *****************************************************************************************
 *  ** Project Name: ESDS Development
 *  ** Target: Proof Of Concept (NU Head Unit)
 *  ** File Name: SmsBroadcastReceiver.java
 *  ** @Author: Sivaram Boina
 *  ** @Co-Author: Sai Sriram Madhiraju
 *  ** Completion Date: 29-12-2017
 *  ***************************************************************************************
 */
package hyundai.esds;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;


/**
 * Receiver which responds when the message is received from the ESDS app
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";
    String smsBody,address;
    public static String recPhoneNumber;

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        //this will update the UI with message
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                smsBody = smsMessage.getMessageBody().toString();
                address = smsMessage.getOriginatingAddress();
                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
            }
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

            if(smsBody.contains("ESDS Service Message")){
                Toast.makeText(context,"You have achieved",Toast.LENGTH_SHORT).show();
                recPhoneNumber=address;
                NotificationCreator.showNotification(smsBody,context);
            }else{
                recPhoneNumber=null;
            }

        }
    }
}