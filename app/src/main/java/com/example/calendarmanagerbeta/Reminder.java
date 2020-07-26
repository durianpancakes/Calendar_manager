package com.example.calendarmanagerbeta;

import java.util.Calendar;

public class Reminder {
    private int mID;
    private String mTitle;
    private Calendar mTime;
    private Boolean mActive;
    private Boolean mAllDay;

    public Reminder(int ID, String Title, Calendar Date, Boolean Active, Boolean AllDay){
        mID = ID;
        mTitle = Title;
        mTime = Date;
        mActive = Active;
        mAllDay = AllDay;
    }

    public Reminder(String Title, Calendar Date, Boolean Active, Boolean AllDay){
        mTitle = Title;
        mTime = Date;
        mActive = Active;
        mAllDay = AllDay;
    }

    public Reminder(){}

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Calendar getTime() {
        return mTime;
    }

    public void setTime(Calendar time) {
        mTime = time;
    }

    public Boolean getActive() {
        return mActive;
    }

    public void setActive(Boolean active) {
        mActive = active;
    }

    public Boolean getAllDay(){
        return mAllDay;
    }

    public void setAllDay(Boolean allDay){
        mAllDay = allDay;
    }
}
