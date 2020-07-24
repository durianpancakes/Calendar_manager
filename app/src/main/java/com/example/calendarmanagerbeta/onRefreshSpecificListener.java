package com.example.calendarmanagerbeta;

public interface onRefreshSpecificListener {
    void onRefresh(NUSModuleMain nusModuleFull);
    void onRefreshSpecial(NUSModuleMain nusModuleFull, String lessonType, String classNo);
}
