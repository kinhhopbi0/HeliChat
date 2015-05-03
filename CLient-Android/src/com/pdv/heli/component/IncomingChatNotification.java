package com.pdv.heli.component;



import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.pdv.heli.R;
import com.pdv.heli.activity.conversation.ConversationFragment;
import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.message.detail.TextMessage;

/**
 * Created by via on 2/5/15.
 */
public class CustomNotification {
    
    private static int notifiIndex = 1;
    private Context context = HeliApplication.getInstance();  
    private String ticker = "";
    
    
    private NotificationManagerCompat notificationManager;

    public CustomNotification(Context context) {
       try {
    	   this.context = context;
           notificationManager =NotificationManagerCompat.from(context) ;
       }catch (Exception ex){
           ex.printStackTrace();
       }
    }
   
    


   
    public void pushTextMessage(TextMessage message){
        this.pushTextMessage(message.getContent(),message.getUserId());
    }
    public void pushTextMessage(String  contentText, String titleText){
        if(notificationManager!=null){
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

            Intent resultIntent = new Intent(context, ConversationFragment.class);
            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
          //  stackBuilder.addParentStack(ConversationActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent  = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentText(contentText);
            mBuilder.setContentTitle(titleText);
            mBuilder.setSmallIcon(R.drawable.ic_notifi_textsms);
            mBuilder.setNumber(++notifiIndex);
            mBuilder.setTicker(ticker);
            mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);

            notificationManager.notify("tag",1, mBuilder.build());
        }
    }  

    public Context getContext() {
        return context;
    }
}
