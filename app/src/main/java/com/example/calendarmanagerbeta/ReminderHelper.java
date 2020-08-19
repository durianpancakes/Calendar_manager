package com.example.calendarmanagerbeta;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.calendarmanagerbeta.MainActivity.CHANNEL_ID;

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

    public void setSync(){

    }

    public void setAlarm(WeekViewEvent event, int id) {
        if (Calendar.getInstance().getTime().before(event.getStartTime().getTime())) {
            mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

            // Create an Intent object by giving the context and the class of the next activity to be opened
            Intent intent = new Intent(mContext, AlarmReceiver.class);
            intent.putExtra("COMMAND_ID", 0);
            String event_name = event.getName();
            intent.putExtra("EVENT_NAME", event_name);
            if (event.getLocation().length() != 0) {
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

            intent.putExtra("ID", id);

            PendingIntent mPendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

//            // DEBUG:
//            NotificationManagerCompat mNotificationManager;
//            Notification notification2 = new NotificationCompat.Builder(mContext, CHANNEL_ID)
//                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
//                    .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
//                    .setContentTitle("Alarm set at: " + sdf2.format(notifyAtCal.getTime()))
//                    .setAutoCancel(true)
//                    .setOnlyAlertOnce(true).build();
//            mNotificationManager = NotificationManagerCompat.from(mContext);
//            mNotificationManager.notify(id, notification2);
//            // END DEBUG:

            mAlarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + diffTime, mPendingIntent);
        }
    }

    public void removeAlarm(int id){
        mAlarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);

        PendingIntent mPendingIntent = PendingIntent.getBroadcast(mContext, id, new Intent(mContext, AlarmReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);
    }

    public void refreshDailyAlarms(){
        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance(mContext);
        firebaseHelper.setFirebaseCallbackListener(new FirebaseCallback() {
            @Override
            public void onGetModuleSuccess(ArrayList<NUSModuleLite> userModules) {

            }

            @Override
            public void onGetKeyword(ArrayList<String> userKeywords) {

            }

            @Override
            public void onGetEvents(ArrayList<WeekViewEvent> userEvents) {
                for(int i = 0; i <= userEvents.size(); i++){
                    removeAlarm(i);
                    if(i != userEvents.size()) {
                        setAlarm(userEvents.get(i), i);
                    }
                }
            }

            @Override
            public void onEventDeleted() {

            }

            @Override
            public void onKeywordDeleted() {

            }
        });
        firebaseHelper.pullEventsByDay(Calendar.getInstance());
    }
}
