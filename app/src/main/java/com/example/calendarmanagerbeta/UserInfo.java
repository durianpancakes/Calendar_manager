package com.example.calendarmanagerbeta;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    private String name;
    List<String> modules = new ArrayList<>();
    String pushId;

    public UserInfo() {}

    public UserInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPushId() {
        return pushId;
    }
    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public List<String> getModules() {
        return modules;
    }




}