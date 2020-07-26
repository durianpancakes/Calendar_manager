package com.example.calendarmanagerbeta;

public class Reminder {
    private String mIdentifier;
    private String mTitle;
    private String mDate;
    private String mActive;

    public Reminder(String identifier, String title, String date, String active){
        mIdentifier = identifier;
        mTitle = title;
        mDate = date;
        mActive = active;
    }

    public Reminder(){}

    public String getmIdentifier() {
        return mIdentifier;
    }

    public void setmIdentifier(String mIdentifier) {
        this.mIdentifier = mIdentifier;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmActive() {
        return mActive;
    }

    public void setmActive(String mActive) {
        this.mActive = mActive;
    }
}
