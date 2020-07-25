package com.example.calendarmanagerbeta;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.microsoft.graph.models.extensions.DateTimeTimeZone;

import java.util.List;

public class ModuleSemesterData {
    private int semester;
    private List<ModuleTimetable> timetable;
    private String examDate;
    private int examDuration;

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getExamDate() {
        return examDate;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public List<ModuleTimetable> getTimetable() {
        return timetable;
    }

    public void setTimetable(List<ModuleTimetable> timetable) {
        this.timetable = timetable;
    }

    public int getExamDuration() {
        return examDuration;
    }

    public void setExamDuration(int examDuration) {
        this.examDuration = examDuration;
    }
}
