package com.example.calendarmanagerbeta;

import android.graphics.RectF;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarWeekFragment extends Fragment {
    View myFragmentView;
    private WeekView mWeekView;
    private TextView weekNumber;
    private TextView monthYearString;
    private int currentWeekNumber;
    private int firstVisibleDayYear;
    private int lastVisibleDayYear;
    private int firstVisibleDayMonth;
    private int lastVisibleDayMonth;
    private String[] monthStrings = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    public CalendarWeekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_calendar_week, container, false);
        mWeekView = (WeekView)myFragmentView.findViewById(R.id.calendar_week_view);
        weekNumber = (TextView)myFragmentView.findViewById(R.id.week_view_weekNumber);
        monthYearString = (TextView)myFragmentView.findViewById(R.id.week_view_monthYear);

        setupWeekView();

        return myFragmentView;
    }

    private void setupWeekView(){
        if(mWeekView != null){
            mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
                @Override
                public List<? extends WeekViewEvent> onMonthChange(int i, int i1) {
                    return new ArrayList<WeekViewEvent>();
                    //TODO: Handle month change (return new list of events)
                }
            });

            //set listener for event click
            mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
                @Override
                public void onEventClick(WeekViewEvent event, RectF eventRect) {
                    //TODO: Handle event click
                }
            });

            //set event long press listener
            mWeekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
                @Override
                public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
                    //TODO: Handle event long press
                }
            });

            //set scroll listener
            mWeekView.setScrollListener(new WeekView.ScrollListener(){
                @Override
                public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay){
                    //TODO: Handle scroll
                    refreshHeaderTexts();
                }
            });
        }

        // setup necessary characteristics of the week view
        mWeekView.setShowFirstDayOfWeekFirst(true);
    }

    public void refreshHeaderTexts(){
        currentWeekNumber = mWeekView.getCurrentWeekNumber();
        firstVisibleDayMonth = mWeekView.getCurrentFirstVisibleDayMonth();
        lastVisibleDayMonth = mWeekView.getCurrentLastVisibleDayMonth();
        firstVisibleDayYear = mWeekView.getCurrentFirstVisibleDayYear();
        lastVisibleDayYear = mWeekView.getCurrentLastVisibleDayYear();

        weekNumber.setText("W" + currentWeekNumber);

        if(firstVisibleDayMonth != lastVisibleDayMonth){
            monthYearString.setText(monthStrings[firstVisibleDayMonth] + "/" + monthStrings[lastVisibleDayMonth]);
        }
        else{
            monthYearString.setText(monthStrings[firstVisibleDayMonth]);
        }

        if(firstVisibleDayMonth != lastVisibleDayMonth){
            if(firstVisibleDayYear != lastVisibleDayYear){
                monthYearString.setText(monthStrings[firstVisibleDayMonth] + " " + firstVisibleDayYear + "/" + monthStrings[lastVisibleDayMonth] + " " + lastVisibleDayYear);
            }
            else{
                monthYearString.setText(monthStrings[firstVisibleDayMonth] + "/" + monthStrings[lastVisibleDayMonth] + " " + firstVisibleDayYear);
            }
        }
        else{
            monthYearString.setText(monthStrings[firstVisibleDayMonth] + " " + firstVisibleDayYear);
        }
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
}
