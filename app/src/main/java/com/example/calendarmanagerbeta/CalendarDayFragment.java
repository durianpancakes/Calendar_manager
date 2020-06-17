package com.example.calendarmanagerbeta;

import android.graphics.RectF;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
public class CalendarDayFragment extends Fragment {
    View myFragmentView;
    private WeekView mDayView;
    private TextView dayNumber;
    private TextView monthString;
    private int currentDayNumber;
    private int currentMonth;
    private int currentYear;
    private String[] monthStrings = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    public CalendarDayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_calendar_day, container, false);
        mDayView = (WeekView)myFragmentView.findViewById(R.id.calendar_day_view);
        dayNumber = (TextView)myFragmentView.findViewById(R.id.day_view_dayNumber);
        monthString = (TextView)myFragmentView.findViewById(R.id.day_view_month);

        setupDayView();

        return myFragmentView;
    }

    private void setupDayView(){
        if(mDayView != null){
            mDayView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
                @Override
                public List<? extends WeekViewEvent> onMonthChange(int i, int i1) {
                return new ArrayList<WeekViewEvent>();
                //TODO: Handle month change (return new list of events)
                }
            });

            //set listener for event click
            mDayView.setOnEventClickListener(new WeekView.EventClickListener() {
                @Override
                public void onEventClick(WeekViewEvent event, RectF eventRect) {
                //TODO: Handle event click
                }
            });

            //set event long press listener
            mDayView.setEventLongPressListener(new WeekView.EventLongPressListener() {
                @Override
                public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
                //TODO: Handle event long press
                }
            });

            //set scroll listener
            mDayView.setScrollListener(new WeekView.ScrollListener(){
                @Override
                public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay){
                //TODO: Handle scroll
                refreshHeaderTexts();
                }
            });
        }

        // setup necessary characteristics of the week view
    }

    public void refreshHeaderTexts(){
        currentDayNumber = mDayView.getCurrentDayNumber();
        currentMonth = mDayView.getCurrentFirstVisibleDayMonth();
        currentYear = mDayView.getCurrentDayYear();

        dayNumber.setText("D" + currentDayNumber);
        monthString.setText(monthStrings[currentMonth] + " " + currentYear);
    }

    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public void onPause(){
        super.onPause();
    }
}
