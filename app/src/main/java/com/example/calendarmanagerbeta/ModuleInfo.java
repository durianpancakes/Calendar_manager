package com.example.calendarmanagerbeta;

public class ModuleInfo {
    String lessonType;
    String classNo;

    public ModuleInfo(String lessonType, String classNo) {
        this.lessonType = lessonType;
        this.classNo = classNo;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public String getClassNo() {
        return classNo;
    }

    public void setClassNo(String classNo) {
        this.classNo = classNo;
    }
}
