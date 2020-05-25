package com.example.calendarmanagerbeta;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarMonthFragment extends Fragment {
    public CalendarMonthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myFragmentView = inflater.inflate(R.layout.fragment_calendar_month, container, false);
        GridView gridview = (GridView)myFragmentView.findViewById(R.id.month_gridview);
        MonthCalendarAdapter monthAdapter = new MonthCalendarAdapter(getActivity(), (GregorianCalendar)GregorianCalendar.getInstance());
        gridview.setAdapter(monthAdapter);
        return myFragmentView;
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
}
