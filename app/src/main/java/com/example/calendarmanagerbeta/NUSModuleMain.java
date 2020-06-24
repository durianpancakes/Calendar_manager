package com.example.calendarmanagerbeta;

import java.util.List;

public class NUSModuleMain {
    private String title;
    private String department;
    private String faculty;
    private String moduleCode;
    private List<ModuleSemesterData> semesterData;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public List<ModuleSemesterData> getSemesterData() {
        return semesterData;
    }

    public void setSemesterData(List<ModuleSemesterData> semesterData) {
        this.semesterData = semesterData;
    }
}
