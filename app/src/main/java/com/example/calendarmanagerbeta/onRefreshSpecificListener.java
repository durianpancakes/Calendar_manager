package com.example.calendarmanagerbeta;

public interface onRefreshSpecificListener {
    void onRefresh(String moduleCode, NUSModuleMain nusModuleFull);
    void onRefreshSpecial(String moduleCode, NUSModuleMain nusModuleFull, String lessonType, String classNo);
}
