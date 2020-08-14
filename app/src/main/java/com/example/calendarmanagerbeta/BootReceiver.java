package com.example.calendarmanagerbeta;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        final FirebaseHelper firebaseHelper = FirebaseHelper.getInstance(context);
        firebaseHelper.pullEventsByDay(Calendar.getInstance());
        firebaseHelper.setFirebaseCallbackListener(new FirebaseCallback() {
            @Override
            public void onGetModuleSuccess(ArrayList<NUSModuleLite> userModules) {

            }

            @Override
            public void onGetKeyword(ArrayList<String> userKeywords) {

            }

            @Override
            public void onGetEvents(ArrayList<WeekViewEvent> userEvents) {
                if(userEvents.size() != 0){
                    ReminderHelper reminderHelper = ReminderHelper.getInstance(context);
                    Toast.makeText(context, "(Calendar Manager) There are " + userEvents.size() + " event(s) today", Toast.LENGTH_LONG).show();
                    for(int i = 0; i < userEvents.size(); i++){
                        reminderHelper.setAlarm(userEvents.get(i), i);
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
    }
}
