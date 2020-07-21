package com.example.calendarmanagerbeta;

import com.alamkanak.weekview.WeekViewEvent;

public interface ParserCallback {
    void onEventAdded(WeekViewEvent event);
    void onEmpty();
}
