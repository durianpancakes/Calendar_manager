package com.example.calendarmanagerbeta;

import com.alamkanak.weekview.WeekViewEvent;
import com.microsoft.graph.models.extensions.Message;

import java.util.ArrayList;

public class KeywordInfo implements Comparable<KeywordInfo>{
    private String mKeyword;
    private int mNumOfDeltaEmails;
    private int mNumOfEventsAdded;
    private ArrayList<Message> mDeltaMessages;

    public String getmKeyword() {
        return mKeyword;
    }

    public void setmKeyword(String mKeyword) {
        this.mKeyword = mKeyword;
    }

    public int getmNumOfDeltaEmails() {
        return mNumOfDeltaEmails;
    }

    public void setmNumOfDeltaEmails(int mNumOfDeltaEmails) {
        this.mNumOfDeltaEmails = mNumOfDeltaEmails;
    }

    public int getmNumOfEventsAdded() {
        return mNumOfEventsAdded;
    }

    public void setmNumOfEventsAdded(int mNumOfEventsAdded) {
        this.mNumOfEventsAdded = mNumOfEventsAdded;
    }

    public ArrayList<Message> getmDeltaMessages() {
        return mDeltaMessages;
    }

    public void setmDeltaMessages(ArrayList<Message> mDeltaMessages) {
        this.mDeltaMessages = mDeltaMessages;
    }

    public ArrayList<WeekViewEvent> getmDeltaEventsAdded() {
        return mDeltaEventsAdded;
    }

    public void setmDeltaEventsAdded(ArrayList<WeekViewEvent> mDeltaEventsAdded) {
        this.mDeltaEventsAdded = mDeltaEventsAdded;
    }

    private ArrayList<WeekViewEvent> mDeltaEventsAdded;


    @Override
    public int compareTo(KeywordInfo keywordInfo) {
        return this.mKeyword.compareTo(keywordInfo.getmKeyword());
    }
}
