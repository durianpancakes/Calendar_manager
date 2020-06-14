package com.alamkanak.weekview;

//import android.support.annotation.ColorInt;
import androidx.annotation.ColorInt;

public interface TextColorPicker {

    @ColorInt
    int getTextColor(WeekViewEvent event);

}
