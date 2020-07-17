package com.example.calendarmanagerbeta;

import android.content.Context;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private FloatingActionButton addEventButton;
    private addEventListener mAddEventListener;
    private int currentWeekNumber;
    private int firstVisibleDayYear;
    private int lastVisibleDayYear;
    private int firstVisibleDayMonth;
    private int lastVisibleDayMonth;
    private String[] monthStrings = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    public CalendarWeekFragment() {
        // Required empty public constructor
    }

    public interface addEventListener{
        void onAddEventButtonPressed();
        void onEventClicked(WeekViewEvent event);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_calendar_week, container, false);
        mWeekView = (WeekView)myFragmentView.findViewById(R.id.calendar_week_view);
        weekNumber = (TextView)myFragmentView.findViewById(R.id.week_view_weekNumber);
        monthYearString = (TextView)myFragmentView.findViewById(R.id.week_view_monthYear);
        addEventButton = (FloatingActionButton)myFragmentView.findViewById(R.id.week_view_add);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Calendar");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Week View");

        setupWeekView();

        return myFragmentView;
    }

    private void setupWeekView(){
        if(mWeekView != null){
            mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
                @Override
                public List<? extends WeekViewEvent> onMonthChange(int year, int month) {
                    ArrayList<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
                    // To add events, simply add the events into the ArrayList
                    // Required fields:
                    // 1. Event name
                    // 2. Event start time
                    // 3. Event end time
                    // 4. Event boolean allDay
                    // Optional fields:
                    // 1. Event description
                    // 2. Event location
                    WeekViewEvent event = new WeekViewEvent();
                    event.setName("CS1231 Lecture");
                    Calendar startCal = Calendar.getInstance();
                    startCal.set(2020, 6, 13, 7, 00);
                    event.setStartTime(startCal);
                    Calendar endCal = Calendar.getInstance();
                    endCal.set(2020, 6, 13, 8, 00);
                    event.setEndTime(endCal);
                    event.setAllDay(false);
                    event.setLocation("LT26");
                    events.add(event);

                    return events;
                }
            });

            //set listener for event click
            mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
                @Override
                public void onEventClick(WeekViewEvent event, RectF eventRect) {
                    //TODO: Handle event click
                    mAddEventListener.onEventClicked(event);
                    System.out.println("OnEventClick");
                }
            });

            //set event long press listener
            mWeekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
                @Override
                public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
                    //TODO: Handle event long press
                    System.out.println("Long press");
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

            addEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAddEventListener.onAddEventButtonPressed();
                }
            });
        }

        // setup necessary characteristics of the week view
        mWeekView.setShowFirstDayOfWeekFirst(true);
        mWeekView.goToToday();
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof CalendarWeekFragment.addEventListener){
            mAddEventListener = (CalendarWeekFragment.addEventListener)context;
        }
        else{
            throw new RuntimeException(context.toString() + " must implement listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAddEventListener = null;
    }
}
