package com.example.calendarmanagerbeta;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;

public interface FirebaseCallback {
    void onGetModuleSuccess(ArrayList<NUSModuleLite> userModules);
    void onGetKeyword(ArrayList<String> userKeywords);
    void onGetEvents(ArrayList<WeekViewEvent> userEvents);
    void onEventDeleted();
    void onKeywordDeleted();
}
