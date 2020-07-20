package com.example.calendarmanagerbeta;

import androidx.annotation.Nullable;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;

public class HomeSectionModel {
    private String sectionTitle;
    private String sectionSubtitle;
    private ArrayList<WeekViewEvent> eventArrayList;
    private ArrayList<KeywordInfo> keywordInfoArrayList;

    public HomeSectionModel(String sectionTitle, String sectionSubtitle, @Nullable ArrayList<WeekViewEvent> eventArrayList, @Nullable ArrayList<KeywordInfo> keywordInfoArrayList){
        this.sectionTitle = sectionTitle;
        this.sectionSubtitle = sectionSubtitle;
        this.eventArrayList = eventArrayList;
        this.keywordInfoArrayList = keywordInfoArrayList;
    }

    public ArrayList<KeywordInfo> getKeywordInfoArrayList() {
        return keywordInfoArrayList;
    }

    public void setKeywordInfoArrayList(ArrayList<KeywordInfo> keywordInfoArrayList) {
        this.keywordInfoArrayList = keywordInfoArrayList;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getSectionSubtitle() {
        return sectionSubtitle;
    }

    public void setSectionSubtitle(String sectionSubtitle) {
        this.sectionSubtitle = sectionSubtitle;
    }

    public ArrayList<WeekViewEvent> getEventArrayList() {
        return eventArrayList;
    }

    public void setEventArrayList(ArrayList<WeekViewEvent> itemArrayList) {
        this.eventArrayList = itemArrayList;
    }
}
