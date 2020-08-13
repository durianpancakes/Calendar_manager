package com.example.calendarmanagerbeta;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReminderHelper {
    private Context mContext;
    private static ReminderHelper INSTANCE = null;
    private AlarmManager mAlarmManager;

    public static synchronized ReminderHelper getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new ReminderHelper(context);
        }
        return INSTANCE;
    }

    private ReminderHelper(Context context){
        mContext = context;
    }

    public void setAlarm(WeekViewEvent event){
        mAlarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);

        // Create an Intent object by giving the context and the class of the next activity to be opened
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        String event_name = event.getName();
        intent.putExtra("EVENT_NAME", event_name);
        if(event.getLocation().length() != 0){
            String event_location = event.getLocation();
            intent.putExtra("EVENT_LOCATION", event_location);
        }
        Calendar startCal = event.getStartTime();
        Date startDate = startCal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String event_start = sdf.format(startDate);
        intent.putExtra("EVENT_START", event_start);

        Calendar endCal = event.getEndTime();
        Date endDate = endCal.getTime();
        String event_end = sdf.format(endDate);
        intent.putExtra("EVENT_END", event_end);

        // Default alarm to be set at 10 minutes before for simplicity sake
        Calendar currentCal = Calendar.getInstance();
        long currentTime = currentCal.getTimeInMillis();
        Calendar notifyAtCal = startCal;
        notifyAtCal.add(Calendar.MINUTE, -10);
        long diffTime = notifyAtCal.getTimeInMillis() - currentTime;
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MMM/yyyy hh:mm a");

        intent.putExtra("ID", diffTime);

        PendingIntent mPendingIntent = PendingIntent.getBroadcast(mContext, (int) diffTime, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Toast.makeText(mContext, "Alarm set at: " + sdf2.format(notifyAtCal.getTime()), Toast.LENGTH_LONG).show();

        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + diffTime, mPendingIntent);
    }
}
