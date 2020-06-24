package com.example.calendarmanagerbeta;

public class ModuleTimetable {
    private String classNo;
    private int startTime;
    private int endTime;
    // private int[] weeks;
    private String venue;
    private String day;
    private String lessonType;
    private int size;

    public String getClassNo() {
        return classNo;
    }

    public void setClassNo(String classNo) {
        this.classNo = classNo;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

//    public int[] getWeeks() {
//        return weeks;
//    }
//
//    public void setWeeks(int[] weeks) {
//        this.weeks = weeks;
//    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
