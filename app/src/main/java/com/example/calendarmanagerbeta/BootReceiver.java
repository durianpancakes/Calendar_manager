package com.example.calendarmanagerbeta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

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
                Toast.makeText(context, "(Calendar Manager) There are " + userEvents.size() + " event(s) today", Toast.LENGTH_LONG).show();
                if(userEvents.size() != 0){
                    ReminderHelper reminderHelper = ReminderHelper.getInstance(context);
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
