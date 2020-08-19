package com.example.calendarmanagerbeta;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.calendarmanagerbeta.MainActivity.CHANNEL_ID;

public class BootReceiver extends BroadcastReceiver {
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            ReminderHelper reminderHelper = ReminderHelper.getInstance(context);
            reminderHelper.refreshDailyAlarms();
        }
    }
}
