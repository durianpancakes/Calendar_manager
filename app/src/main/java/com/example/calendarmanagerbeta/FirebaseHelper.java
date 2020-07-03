package com.example.calendarmanagerbeta;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

    public void getAllModules() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mModulesDatabaseReference = mFirebaseDatabase.getReference().child("users").child(uid);

        final ArrayList<NUSModuleLite> allModules = new ArrayList<>();

        mModulesDatabaseReference.child("modules").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String moduleString = new String();
                if (snapshot.getChildren() != null) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                        // original testing
                        String modCode = userSnapshot.child("Module Name").getValue(String.class);
                        moduleString = moduleString + " " + modCode;
                        System.out.println(moduleString);
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


                        System.out.println(moduleInfoList.get(0).lessonType);

                        // works up to here
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
                System.out.println("The read failed: " + error.getCode());
            }

        });
    }
}
