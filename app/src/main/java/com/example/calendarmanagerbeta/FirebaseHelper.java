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
    //@Override
    public ArrayList<WeekViewEvent> pullEvents() {
        final ArrayList<WeekViewEvent> eventArrayList = new ArrayList<>();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mEventsDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid).child("events");

        mEventsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //String moduleString = new String();
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                        WeekViewEvent mWeekViewEvent = new WeekViewEvent();
                        Calendar startCal = Calendar.getInstance();
                        Calendar endCal = Calendar.getInstance();

                        if (userSnapshot.child("startDayOfMonth").exists()) {
                            startCal.set(Calendar.DAY_OF_MONTH , userSnapshot.child("startDayOfMonth").getValue(int.class));

                        }
                        if (userSnapshot.child("startMonth").exists()) {
                            startCal.set(Calendar.MONTH , userSnapshot.child("startMonth").getValue(int.class));

                        }
                        if (userSnapshot.child("startYear").exists()) {
                            startCal.set(Calendar.YEAR, userSnapshot.child("startYear").getValue(int.class));

                        }
                        if (userSnapshot.child("startHour").exists()) {
                            startCal.set(Calendar.HOUR_OF_DAY, userSnapshot.child("startHour").getValue(int.class));

                        }
                        if (userSnapshot.child("startMinute").exists()) {
                            startCal.set(Calendar.YEAR, userSnapshot.child("startYear").getValue(int.class));

                        }
                        if (userSnapshot.child("endDayOfMonth").exists()) {
                            endCal.set(Calendar.MINUTE, userSnapshot.child("endDayOfMonth").getValue(int.class));

                        }
                        if (userSnapshot.child("endMonth").exists()) {
                            endCal.set(Calendar.YEAR, userSnapshot.child("endMonth").getValue(int.class));

                        }
                        if (userSnapshot.child("endYear").exists()) {
                            endCal.set(Calendar.YEAR, userSnapshot.child("endYear").getValue(int.class));

                        }
                        if (userSnapshot.child("endHour").exists()) {
                            endCal.set(Calendar.HOUR_OF_DAY, userSnapshot.child("endHour").getValue(int.class));

                        }
                        if (userSnapshot.child("endMinute").exists()) {
                            endCal.set(Calendar.YEAR, userSnapshot.child("endMinute").getValue(int.class));

                        }
                        if (userSnapshot.child("Description").exists()) {
                            mWeekViewEvent.setDescription(userSnapshot.child("Description").getValue(String.class));
                        }
                        if (userSnapshot.child("Name").exists()) {
                            mWeekViewEvent.setName(userSnapshot.child("Name").getValue(String.class));
                        }
                        if (userSnapshot.child("Location").exists()) {
                            mWeekViewEvent.setLocation(userSnapshot.child("Location").getValue(String.class));
                        }
                        if (userSnapshot.child("Weblink").exists()) {
                            mWeekViewEvent.setmWeblink(userSnapshot.child("Weblink").getValue(String.class));

                        }
                        if (userSnapshot.child("AllDay").exists()) {
                            mWeekViewEvent.setAllDay(userSnapshot.child("AllDay").getValue(Boolean.class));
                        }

                        mWeekViewEvent.setStartTime(startCal);
                        mWeekViewEvent.setEndTime(endCal);
                        eventArrayList.add(mWeekViewEvent);
                    }
                } else {
                    System.out.println("There are no events");
                }

                //test eventArrayList here (DEBUG)
                for (WeekViewEvent weekViewEvent : eventArrayList) {
                    System.out.println("Name of event : " + weekViewEvent.getName());
                    System.out.println("Location : " + weekViewEvent.getLocation());
                    //System.out.println("StartHour : " + weekViewEvent.getStartTime().get(Calendar.HOUR_OF_DAY));
                    //System.out.println("StartMinute : " + weekViewEvent.getStartTime().get(Calendar.MINUTE));
                    // lazy to check for others + might not have some fields so this may crash

                }

                // dont know where to pass this to..
                // seen from function below
                /*if(callbackHelper != null){
                    callbackHelper.onGetModuleSuccess(allModules);
                }*/
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

                        if (userSnapshot.child("Sectional Teaching").exists()) {
                            ModuleInfo moduleInfo = new ModuleInfo("Sectional Teaching",
                                    userSnapshot.child("Sectional Teaching").getValue(String.class));
                            moduleInfoList.add(moduleInfo);

                        }

                        if (userSnapshot.child("Tutorial").exists()) {
                            ModuleInfo moduleInfo = new ModuleInfo("Tutorial",
                                    userSnapshot.child("Tutorial").getValue(String.class));
                            moduleInfoList.add(moduleInfo);
                        }

                        if (userSnapshot.child("Recitation").exists()) {
                            ModuleInfo moduleInfo = new ModuleInfo("Recitation",
                                    userSnapshot.child("Recitation").getValue(String.class));
                            moduleInfoList.add(moduleInfo);
                        }

                        if (userSnapshot.child("Lecture").exists()) {
                            ModuleInfo moduleInfo = new ModuleInfo("Lecture",
                                    userSnapshot.child("Lecture").getValue(String.class));
                            moduleInfoList.add(moduleInfo);
                        }


                        //System.out.println(moduleInfoList.get(0).lessonType);

                        // get(1) works if the mod has a 2nd lessontype (tested)
                        nusModuleLite.setClassesSelected(moduleInfoList);

                        allModules.add(nusModuleLite);
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

                if(callbackHelper != null){
                    callbackHelper.onGetModuleSuccess(allModules);
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
}
