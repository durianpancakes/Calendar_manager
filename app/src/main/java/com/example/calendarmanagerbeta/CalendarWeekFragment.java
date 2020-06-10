package com.example.calendarmanagerbeta;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.WeekView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarWeekFragment extends Fragment {
    View myFragmentView;
    WeekView mWeekView;

    public CalendarWeekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_calendar_week, container, false);
        mWeekView = (WeekView)myFragmentView.findViewById(R.id.weekView);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(mMonthChangeListener);
        mWeekView.setEventLongPressListener(mEventLongPressListener);
        return myFragmentView;
    }

}
