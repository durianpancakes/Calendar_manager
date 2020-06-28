package com.example.calendarmanagerbeta;

public interface onParamsChangedListener {
    public void lectureChanged(String moduleCode, String lessonType, String classNo);
    public void tutorialChanged(String moduleCode, String lessonType, String classNo);
    public void stChanged(String moduleCode, String lessonType, String classNo);
    public void recitationChanged(String moduleCode, String lessonType, String classNo);
}
