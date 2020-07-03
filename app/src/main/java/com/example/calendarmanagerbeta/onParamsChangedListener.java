package com.example.calendarmanagerbeta;

public interface onParamsChangedListener {
    void lectureChanged(String moduleCode, String lessonType, String classNo);
    void tutorialChanged(String moduleCode, String lessonType, String classNo);
    void stChanged(String moduleCode, String lessonType, String classNo);
    void recitationChanged(String moduleCode, String lessonType, String classNo);
    void moduleRemoved(String moduleCode);
}
