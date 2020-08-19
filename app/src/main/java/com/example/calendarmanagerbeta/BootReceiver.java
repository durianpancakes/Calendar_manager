package com.example.calendarmanagerbeta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            ReminderHelper reminderHelper = ReminderHelper.getInstance(mContext);
            reminderHelper.refreshDailyAlarms();
        }
    }
}
