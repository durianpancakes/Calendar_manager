package com.example.calendarmanagerbeta;

import java.util.ArrayList;

public interface FirebaseCallback {
    void onGetModuleSuccess(ArrayList<NUSModuleLite> userModules);
    void onGetKeyword(ArrayList<String> userKeywords);
}
