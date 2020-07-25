package com.example.calendarmanagerbeta;

import android.content.Context;

import androidx.annotation.NonNull;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class FirebaseHelper {
    private static FirebaseHelper INSTANCE = null;
    private FirebaseCallback callbackHelper;

    private Context mContext;

    private FirebaseHelper(Context context) {
        mContext = context;
    }

    public void setFirebaseCallbackListener(FirebaseCallback firebaseCallbackListener){
        callbackHelper = firebaseCallbackListener;
    }


    public static synchronized FirebaseHelper getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new FirebaseHelper(context);
        }
        return INSTANCE;
    }
    public ArrayList<WeekViewEvent> pullEventsByDay (final Calendar currentDate) {
        final ArrayList<WeekViewEvent> eventArrayList = new ArrayList<>();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid);
        // include another reference for the module events to pull (NOT DONE)

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("events").getChildren() != null) {

                    ArrayList<WeekViewEvent> tempArrayList;
                    tempArrayList = extractEventsByDay(snapshot.child("events"), currentDate);
                    for(WeekViewEvent event : tempArrayList) {
                        eventArrayList.add(event);
                    }

                } else {
                    System.out.println("There are no events by day");
                }
                if (snapshot.child("modules").getChildren() != null) {

                    for (DataSnapshot userSnapshot : snapshot.child("modules").getChildren()) {

                        if(userSnapshot.child("Laboratory").exists()) {
                            ArrayList<WeekViewEvent> tempArrayList;
                            tempArrayList = extractEventsByDay(userSnapshot.child("Laboratory").child("lessons"), currentDate);
                            for(WeekViewEvent event : tempArrayList) {
                                eventArrayList.add(event);
                            }
                        }
                        if(userSnapshot.child("Sectional Teaching").exists()) {
                            ArrayList<WeekViewEvent> tempArrayList;
                            tempArrayList = extractEventsByDay(userSnapshot.child("Sectional Teaching").child("lessons"), currentDate);
                            for(WeekViewEvent event : tempArrayList) {
                                eventArrayList.add(event);
                            }
                        }
                        if(userSnapshot.child("Tutorial").exists()) {
                            ArrayList<WeekViewEvent> tempArrayList;
                            tempArrayList = extractEventsByDay(userSnapshot.child("Tutorial").child("lessons"), currentDate);
                            for(WeekViewEvent event : tempArrayList) {
                                eventArrayList.add(event);
                            }
                        }
                        if(userSnapshot.child("Recitation").exists()) {
                            ArrayList<WeekViewEvent> tempArrayList;
                            tempArrayList = extractEventsByDay(userSnapshot.child("Recitation").child("lessons"), currentDate);
                            for(WeekViewEvent event : tempArrayList) {
                                eventArrayList.add(event);
                            }
                        }
                        if(userSnapshot.child("Lecture").exists()) {
                            ArrayList<WeekViewEvent> tempArrayList;
                            tempArrayList = extractEventsByDay(userSnapshot.child("Lecture").child("lessons"), currentDate);
                            for(WeekViewEvent event : tempArrayList) {
                                eventArrayList.add(event);
                            }
                        }


                    }
                } else {
                    System.out.println("There are no lesson events by day");
                }



                //test eventArrayList here (DEBUG)
                for (WeekViewEvent weekViewEvent : eventArrayList) {
                    System.out.println("Name of event in byday : " + weekViewEvent.getName());
                    //System.out.println("Location : " + weekViewEvent.getLocation());
                }
                callbackHelper.onGetEvents(eventArrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: @pullEvents" + error.getCode());
            }

        });


        return eventArrayList;

    }


    public ArrayList<WeekViewEvent> pullEvents() {
        final ArrayList<WeekViewEvent> eventArrayList = new ArrayList<>();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid);


        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("events").getChildren() != null) {

                    ArrayList<WeekViewEvent> tempArrayList;
                    tempArrayList = extractEvents(snapshot.child("events"));
                    for(WeekViewEvent event : tempArrayList) {
                        eventArrayList.add(event);
                    }

                } else {
                    System.out.println("There are no events");
                }
                if (snapshot.child("modules").getChildren() != null) {

                    for (DataSnapshot userSnapshot : snapshot.child("modules").getChildren()) {

                        if(userSnapshot.child("Laboratory").exists()) {
                            ArrayList<WeekViewEvent> tempArrayList;
                            tempArrayList = extractEvents(userSnapshot.child("Laboratory").child("lessons"));
                            for(WeekViewEvent event : tempArrayList) {
                                eventArrayList.add(event);
                            }
                        }
                        if(userSnapshot.child("Sectional Teaching").exists()) {
                            ArrayList<WeekViewEvent> tempArrayList;
                            tempArrayList = extractEvents(userSnapshot.child("Sectional Teaching").child("lessons"));
                            for(WeekViewEvent event : tempArrayList) {
                                eventArrayList.add(event);
                            }
                        }
                        if(userSnapshot.child("Tutorial").exists()) {
                            ArrayList<WeekViewEvent> tempArrayList;
                            tempArrayList = extractEvents(userSnapshot.child("Tutorial").child("lessons"));
                            for(WeekViewEvent event : tempArrayList) {
                                eventArrayList.add(event);
                            }
                        }
                        if(userSnapshot.child("Recitation").exists()) {
                            ArrayList<WeekViewEvent> tempArrayList;
                            tempArrayList = extractEvents(userSnapshot.child("Recitation").child("lessons"));
                            for(WeekViewEvent event : tempArrayList) {
                                eventArrayList.add(event);
                            }
                        }
                        if(userSnapshot.child("Lecture").exists()) {
                            ArrayList<WeekViewEvent> tempArrayList;
                            tempArrayList = extractEvents(userSnapshot.child("Lecture").child("lessons"));
                            for(WeekViewEvent event : tempArrayList) {
                                eventArrayList.add(event);
                            }
                        }


                    }
                } else {
                    System.out.println("There are no lesson events");
                }

                //test eventArrayList here (DEBUG)
                for (WeekViewEvent weekViewEvent : eventArrayList) {
                    System.out.println("Name of event : " + weekViewEvent.getName());
                    //System.out.println("Location : " + weekViewEvent.getLocation());
                }

                callbackHelper.onGetEvents(eventArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: @pullEvents" + error.getCode());
            }

        });


        return eventArrayList;

    }

    public void getAllModules() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mModulesDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid);

        final ArrayList<NUSModuleLite> allModules = new ArrayList<>();

        mModulesDatabaseReference.child("modules").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //String moduleString = new String();
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                        // original testing
                        String modCode = userSnapshot.child("Module Name").getValue(String.class);
                        //moduleString = moduleString + " " + modCode;
                        //System.out.println(moduleString);
                        //

                        NUSModuleLite nusModuleLite = new NUSModuleLite();
                        nusModuleLite.setModuleCode(modCode);

                        ArrayList<ModuleInfo> moduleInfoList = new ArrayList<>();

                        if (userSnapshot.child("Laboratory").exists()) {
                            ModuleInfo moduleInfo = new ModuleInfo("Laboratory",
                                    userSnapshot.child("Laboratory").child("classNo").getValue(String.class));
                            moduleInfoList.add(moduleInfo);

                        }

                        if (userSnapshot.child("Sectional Teaching").exists()) {
                            ModuleInfo moduleInfo = new ModuleInfo("Sectional Teaching",
                                    userSnapshot.child("Sectional Teaching").child("classNo").getValue(String.class));
                            moduleInfoList.add(moduleInfo);

                        }

                        if (userSnapshot.child("Tutorial").exists()) {
                            ModuleInfo moduleInfo = new ModuleInfo("Tutorial",
                                    userSnapshot.child("Tutorial").child("classNo").getValue(String.class));
                            moduleInfoList.add(moduleInfo);
                        }

                        if (userSnapshot.child("Recitation").exists()) {
                            ModuleInfo moduleInfo = new ModuleInfo("Recitation",
                                    userSnapshot.child("Recitation").child("classNo").getValue(String.class));
                            moduleInfoList.add(moduleInfo);
                        }

                        if (userSnapshot.child("Lecture").exists()) {
                            ModuleInfo moduleInfo = new ModuleInfo("Lecture",
                                    userSnapshot.child("Lecture").child("classNo").getValue(String.class));
                            moduleInfoList.add(moduleInfo);
                        }


                        //System.out.println(moduleInfoList.get(0).lessonType);


                        nusModuleLite.setClassesSelected(moduleInfoList);

                        allModules.add(nusModuleLite);
                    }

                    if(callbackHelper != null){
                        callbackHelper.onGetModuleSuccess(allModules);
                    }
                } else {
                    System.out.println("There are no children");
                }

                //test allModules here (DEBUG)
                for (NUSModuleLite nusModuleLite : allModules) {
                    System.out.println(nusModuleLite.getModuleCode() + " in test zone");
                    // works up to here
                    ArrayList<ModuleInfo> moduleInfoArrayList = nusModuleLite.getClassesSelected();
                    for (ModuleInfo moduleInfo : moduleInfoArrayList) {
                        System.out.println(moduleInfo.lessonType + " " +
                                moduleInfo.classNo);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: @getAllModules" + error.getCode());
            }

        });
    }

    public void getAllKeywords() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mModulesDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid);
        final ArrayList<String> allKeywords = new ArrayList<>();

        mModulesDatabaseReference.child("modules").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildren() != null) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String modCode = userSnapshot.child("Module Name").getValue(String.class);
                        allKeywords.add(modCode);
                    }
                }
                else {
                    System.out.println("getallkeywords no children");
                }

                if(callbackHelper != null){
                    callbackHelper.onGetKeyword(allKeywords);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: @getkeywords " + error.getCode());

            }
        });

    }

    public void removeEvent(final WeekViewEvent event) {
        final ArrayList<WeekViewEvent> eventArrayList = new ArrayList<>();
        final FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        final DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid);
        //final DatabaseReference mEventsDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid).child("events");
        System.out.println("entered removeEvent");
        //System.out.println(event.getIdentifier());

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                if(snapshot.child("events").getChildren() != null) {
                    for (DataSnapshot userSnapshot : snapshot.child("events").getChildren()) {
                        System.out.println("Event identifier: " + event.getIdentifier());
                        System.out.println("for loop looking at : " + userSnapshot.child("identifier").getValue());
                        if(userSnapshot.child("identifier").getValue().equals(event.getIdentifier())) {
                            mDatabaseReference.child("events").child(userSnapshot.getKey()).removeValue();
                            callbackHelper.onEventDeleted();
                            System.out.println("event with key is removed :" + userSnapshot.getKey());
                        }

                    }

                } else {
                    System.out.println("There are no events");
                }
                if(snapshot.child("modules").getChildren() != null) {
                    for (DataSnapshot userSnapshot : snapshot.child("modules").getChildren()) {

                        if(userSnapshot.child("Laboratory").exists()) {
                            for(DataSnapshot mUserSnapshot : userSnapshot.child("Laboratory").child("lessons").getChildren()) {
                                if(mUserSnapshot.child("identifier").getValue().equals(event.getIdentifier())) {
                                    System.out.println("Event identifier: " + event.getIdentifier());
                                    System.out.println("for loop looking at : " + mUserSnapshot.child("identifier").getValue());
                                    //mDatabaseReference.child("modules").child("Laboratory").child("lessons").child(mUserSnapshot.getKey()).removeValue();
                                    mDatabaseReference.child("modules").child(userSnapshot.child("Module Name").getValue(String.class)).child("Laboratory").child("lessons").child(mUserSnapshot.getKey()).removeValue();
                                    callbackHelper.onEventDeleted();

                                }
                            }

                        }
                        if(userSnapshot.child("Sectional Teaching").exists()) {
                            for(DataSnapshot mUserSnapshot : userSnapshot.child("Sectional Teaching").child("lessons").getChildren()) {
                                if(mUserSnapshot.child("identifier").getValue().equals(event.getIdentifier())) {
                                    System.out.println("Event identifier: " + event.getIdentifier());
                                    System.out.println("for loop looking at : " + mUserSnapshot.child("identifier").getValue());

                                    mDatabaseReference.child("modules").child(userSnapshot.child("Module Name").getValue(String.class)).child("Sectional Teaching").child("lessons").child(mUserSnapshot.getKey()).removeValue();
                                    callbackHelper.onEventDeleted();

                                }
                            }

                        }
                        if(userSnapshot.child("Tutorial").exists()) {
                            for(DataSnapshot mUserSnapshot : userSnapshot.child("Tutorial").child("lessons").getChildren()) {
                                if(mUserSnapshot.child("identifier").getValue().equals(event.getIdentifier())) {
                                    System.out.println("Event identifier: " + event.getIdentifier());
                                    System.out.println("for loop looking at : " + mUserSnapshot.child("identifier").getValue());
                                    mDatabaseReference.child("modules").child(userSnapshot.child("Module Name").getValue(String.class)).child("Tutorial").child("lessons").child(mUserSnapshot.getKey()).removeValue();
                                    callbackHelper.onEventDeleted();

                                }
                            }

                        }
                        if(userSnapshot.child("Recitation").exists()) {
                            for(DataSnapshot mUserSnapshot : userSnapshot.child("Recitation").child("lessons").getChildren()) {
                                if(mUserSnapshot.child("identifier").getValue().equals(event.getIdentifier())) {
                                    System.out.println("Event identifier: " + event.getIdentifier());
                                    System.out.println("for loop looking at : " + mUserSnapshot.child("identifier").getValue());
                                    mDatabaseReference.child("modules").child(userSnapshot.child("Module Name").getValue(String.class)).child("Recitation").child("lessons").child(mUserSnapshot.getKey()).removeValue();
                                    callbackHelper.onEventDeleted();

                                }
                            }

                        }
                        if(userSnapshot.child("Lecture").exists()) {
                            for(DataSnapshot mUserSnapshot : userSnapshot.child("Lecture").child("lessons").getChildren()) {
                                if(mUserSnapshot.child("identifier").getValue().equals(event.getIdentifier())) {
                                    System.out.println("Event identifier: " + event.getIdentifier());
                                    System.out.println("for loop looking at : " + mUserSnapshot.child("identifier").getValue());
                                    mDatabaseReference.child("modules").child(userSnapshot.child("Module Name").getValue(String.class)).child("Lecture").child("lessons").child(mUserSnapshot.getKey()).removeValue();
                                    callbackHelper.onEventDeleted();
                                }
                            }

                        }


                    }

                } else {
                    System.out.println("There are no events");
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: @removeEvent" + error.getCode());
            }

        });

    }
    public ArrayList<WeekViewEvent> extractEvents(DataSnapshot userSnapshot) {

        final ArrayList<WeekViewEvent> eventArrayList = new ArrayList<>();

        for(DataSnapshot mUserSnapshot : userSnapshot.getChildren()) {

            WeekViewEvent mWeekViewEvent = new WeekViewEvent();
            Calendar startCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();

            if (mUserSnapshot.child("startDayOfMonth").exists()) {
                startCal.set(Calendar.DAY_OF_MONTH, mUserSnapshot.child("startDayOfMonth").getValue(int.class));

            }
            if (mUserSnapshot.child("startMonth").exists()) {
                startCal.set(Calendar.MONTH, mUserSnapshot.child("startMonth").getValue(int.class));

            }
            if (mUserSnapshot.child("startYear").exists()) {
                startCal.set(Calendar.YEAR, mUserSnapshot.child("startYear").getValue(int.class));

            }
            if (mUserSnapshot.child("startHour").exists()) {
                startCal.set(Calendar.HOUR_OF_DAY, mUserSnapshot.child("startHour").getValue(int.class));

            }
            if (mUserSnapshot.child("startMinute").exists()) {
                startCal.set(Calendar.MINUTE, mUserSnapshot.child("startMinute").getValue(int.class));

            }
            if (mUserSnapshot.child("endDayOfMonth").exists()) {
                endCal.set(Calendar.DAY_OF_MONTH, mUserSnapshot.child("endDayOfMonth").getValue(int.class));

            }
            if (mUserSnapshot.child("endMonth").exists()) {
                endCal.set(Calendar.MONTH, mUserSnapshot.child("endMonth").getValue(int.class));

            }
            if (mUserSnapshot.child("endYear").exists()) {
                endCal.set(Calendar.YEAR, mUserSnapshot.child("endYear").getValue(int.class));

            }
            if (mUserSnapshot.child("endHour").exists()) {
                endCal.set(Calendar.HOUR_OF_DAY, mUserSnapshot.child("endHour").getValue(int.class));

            }
            if (mUserSnapshot.child("endMinute").exists()) {
                endCal.set(Calendar.MINUTE, mUserSnapshot.child("endMinute").getValue(int.class));

            }
            if (mUserSnapshot.child("Description").exists()) {
                mWeekViewEvent.setDescription(mUserSnapshot.child("Description").getValue(String.class));
            }
            if (mUserSnapshot.child("Name").exists()) {
                mWeekViewEvent.setName(mUserSnapshot.child("Name").getValue(String.class));
            }
            if (mUserSnapshot.child("Location").exists()) {
                mWeekViewEvent.setLocation(mUserSnapshot.child("Location").getValue(String.class));
            }
            if (mUserSnapshot.child("Weblink").exists()) {
                mWeekViewEvent.setmWeblink(mUserSnapshot.child("Weblink").getValue(String.class));

            }
            if (mUserSnapshot.child("AllDay").exists()) {
                mWeekViewEvent.setAllDay(mUserSnapshot.child("AllDay").getValue(Boolean.class));
            }

            mWeekViewEvent.setIdentifier(mUserSnapshot.child("identifier").getValue(String.class));
            //System.out.println("event identifier is " + mWeekViewEvent.getIdentifier());

            mWeekViewEvent.setStartTime(startCal);
            mWeekViewEvent.setEndTime(endCal);
            eventArrayList.add(mWeekViewEvent);
        }

        return eventArrayList;
    }
    public ArrayList<WeekViewEvent> extractEventsByDay(DataSnapshot userSnapshot, Calendar currentDate) {

        final ArrayList<WeekViewEvent> eventArrayList = new ArrayList<>();

        for(DataSnapshot mUserSnapshot : userSnapshot.getChildren()) {
            //System.out.println("in extractbyday");

            if((currentDate.get(Calendar.MONTH) == mUserSnapshot.child("startMonth").getValue(int.class)
                    && currentDate.get(Calendar.DAY_OF_MONTH) == mUserSnapshot.child("startDayOfMonth").getValue(int.class)
                    && currentDate.get(Calendar.YEAR) == mUserSnapshot.child("startYear").getValue(int.class)) ||
                    (currentDate.get(Calendar.MONTH) == mUserSnapshot.child("endMonth").getValue(int.class)
                            && currentDate.get(Calendar.DAY_OF_MONTH) == mUserSnapshot.child("endDayOfMonth").getValue(int.class)
                            && currentDate.get(Calendar.YEAR) == mUserSnapshot.child("endYear").getValue(int.class))) {

                // checks if end or start of event is today.

                WeekViewEvent mWeekViewEvent = new WeekViewEvent();
                Calendar startCal = Calendar.getInstance();
                Calendar endCal = Calendar.getInstance();

                if (mUserSnapshot.child("startDayOfMonth").exists()) {
                    startCal.set(Calendar.DAY_OF_MONTH, mUserSnapshot.child("startDayOfMonth").getValue(int.class));

                }
                if (mUserSnapshot.child("startMonth").exists()) {
                    startCal.set(Calendar.MONTH, mUserSnapshot.child("startMonth").getValue(int.class));

                }
                if (mUserSnapshot.child("startYear").exists()) {
                    startCal.set(Calendar.YEAR, mUserSnapshot.child("startYear").getValue(int.class));

                }
                if (mUserSnapshot.child("startHour").exists()) {
                    startCal.set(Calendar.HOUR_OF_DAY, mUserSnapshot.child("startHour").getValue(int.class));

                }
                if (mUserSnapshot.child("startMinute").exists()) {
                    startCal.set(Calendar.MINUTE, mUserSnapshot.child("startMinute").getValue(int.class));

                }
                if (mUserSnapshot.child("endDayOfMonth").exists()) {
                    endCal.set(Calendar.DAY_OF_MONTH, mUserSnapshot.child("endDayOfMonth").getValue(int.class));

                }
                if (mUserSnapshot.child("endMonth").exists()) {
                    endCal.set(Calendar.MONTH, mUserSnapshot.child("endMonth").getValue(int.class));

                }
                if (mUserSnapshot.child("endYear").exists()) {
                    endCal.set(Calendar.YEAR, mUserSnapshot.child("endYear").getValue(int.class));

                }
                if (mUserSnapshot.child("endHour").exists()) {
                    endCal.set(Calendar.HOUR_OF_DAY, mUserSnapshot.child("endHour").getValue(int.class));

                }
                if (mUserSnapshot.child("endMinute").exists()) {
                    endCal.set(Calendar.MINUTE, mUserSnapshot.child("endMinute").getValue(int.class));

                }
                if (mUserSnapshot.child("Description").exists()) {
                    mWeekViewEvent.setDescription(mUserSnapshot.child("Description").getValue(String.class));
                }
                if (mUserSnapshot.child("Name").exists()) {
                    mWeekViewEvent.setName(mUserSnapshot.child("Name").getValue(String.class));
                }
                if (mUserSnapshot.child("Location").exists()) {
                    mWeekViewEvent.setLocation(mUserSnapshot.child("Location").getValue(String.class));
                }
                if (mUserSnapshot.child("Weblink").exists()) {
                    mWeekViewEvent.setmWeblink(mUserSnapshot.child("Weblink").getValue(String.class));

                }
                if (mUserSnapshot.child("AllDay").exists()) {
                    mWeekViewEvent.setAllDay(mUserSnapshot.child("AllDay").getValue(Boolean.class));
                }

                mWeekViewEvent.setIdentifier(mUserSnapshot.child("identifier").getValue(String.class));
                //System.out.println("event identifier is " + mWeekViewEvent.getIdentifier());

                mWeekViewEvent.setStartTime(startCal);
                mWeekViewEvent.setEndTime(endCal);
                eventArrayList.add(mWeekViewEvent);
            }
        }

        return eventArrayList;
    }
}
