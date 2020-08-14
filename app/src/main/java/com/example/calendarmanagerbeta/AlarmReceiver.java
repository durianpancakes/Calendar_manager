package com.example.calendarmanagerbeta;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.calendarmanagerbeta.MainActivity.CHANNEL_ID;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManagerCompat mNotificationManager;

    @Override
    public void onReceive(final Context context, Intent intent) {
        int command_id = intent.getIntExtra("COMMAND_ID", 0);

        switch(command_id){
            case 0: // Received alarm
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

                    content_text = event_start + " - " + event_end + " @ " + event_location;
                } else {
                    event_start = intent.getStringExtra("EVENT_START");
                    event_end = intent.getStringExtra("EVENT_END");
                    content_text = event_start + " - " + event_end;
                }

                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
                        .setContentTitle(event_name)
                        .setContentText(content_text)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true).build();

                mNotificationManager = NotificationManagerCompat.from(context);
                mNotificationManager.notify(id, notification);
                break;
        }
    }
}
