package com.example.calendarmanagerbeta;

import java.util.List;

public class ModuleSemesterData {
    private int semester;
    private List<ModuleTimetable> timetable;

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
}
