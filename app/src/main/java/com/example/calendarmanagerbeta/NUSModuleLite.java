package com.example.calendarmanagerbeta;

import java.util.List;

public class NUSModuleLite {
    private String moduleCode;
    private String title;
    private List<ModuleInfo> classesSelected;
    private List<Integer> semesters;

    public List<ModuleInfo> getClassesSelected() {
        return classesSelected;
    }

    public void setClassesSelected(List<ModuleInfo> classesSelected) {
        this.classesSelected = classesSelected;
    }

    public List<Integer> getSemesters() {
        return semesters;
    }

    public void setSemesters(List<Integer> semesters) {
        this.semesters = semesters;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}