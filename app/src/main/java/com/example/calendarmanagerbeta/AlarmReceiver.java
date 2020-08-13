package com.example.calendarmanagerbeta;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "event_reminder";
    private NotificationManagerCompat mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        String event_name;
        String event_location;
        String event_start;
        String event_end;
        String content_text;
        int id;

        id = intent.getIntExtra("ID", 0);

        event_name = intent.getStringExtra("EVENT_NAME");
        if(intent.hasExtra("EVENT_LOCATION")){
            event_location = intent.getStringExtra("EVENT_LOCATION");
            event_start = intent.getStringExtra("EVENT_START");
            event_end = intent.getStringExtra("EVENT_END");

            content_text = event_start + "-" + event_end + " @ " + event_location;
        } else {
            event_start = intent.getStringExtra("EVENT_START");
            event_end = intent.getStringExtra("EVENT_END");
            content_text = event_start + "-" + event_end;
        }


        createNotificationChannels(context);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
                .setContentTitle(event_name)
                .setContentText(content_text)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true).build();

        mNotificationManager = NotificationManagerCompat.from(context);
        mNotificationManager.notify(id, notification);
    }

    private void createNotificationChannels(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID, "Event Reminder", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("Show reminders of events");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}
