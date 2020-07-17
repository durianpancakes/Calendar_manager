package com.example.calendarmanagerbeta;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarMonthFragment extends Fragment {
    View myFragmentView;
    GridView gridview;
    MonthCalendarAdapter monthAdapter;
    MonthCalendarAdapter prevMonthAdapter;
    MonthCalendarAdapter nextMonthAdapter;
    private Calendar mCalendarToday = Calendar.getInstance();
    private int monthNum = mCalendarToday.get(Calendar.MONTH);
    private int yearNum = mCalendarToday.get(Calendar.YEAR);
    public GregorianCalendar month = (GregorianCalendar)GregorianCalendar.getInstance();

    public CalendarMonthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_calendar_month, container, false);
        gridview = (GridView)myFragmentView.findViewById(R.id.month_gridview);
        monthAdapter = new MonthCalendarAdapter(getActivity(), monthNum, yearNum, month);
        gridview.setAdapter(monthAdapter);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Calendar");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Month View");

        final TextView dateTitle = (TextView)myFragmentView.findViewById(R.id.month_title);
        dateTitle.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        RelativeLayout previousMonth = (RelativeLayout)myFragmentView.findViewById(R.id.previous_month);
        previousMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreviousMonth();
                prevMonthAdapter = new MonthCalendarAdapter(getActivity(), monthNum, yearNum, month);
                dateTitle.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
                System.out.println("Previous" + monthNum + yearNum);
                gridview.setAdapter(prevMonthAdapter);
            }
        });

        RelativeLayout nextMonth = (RelativeLayout)myFragmentView.findViewById(R.id.next_month);
        nextMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextMonth();
                nextMonthAdapter = new MonthCalendarAdapter(getActivity(), monthNum, yearNum, month);
                dateTitle.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
                System.out.println("Next" + monthNum + yearNum);
                gridview.setAdapter(nextMonthAdapter);
            }
        });


        return myFragmentView;
    }

    protected void setPreviousMonth(){
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
            monthNum = 11;
            yearNum--;
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
            monthNum--;

        }
    }

    protected void setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1),
                    month.getActualMinimum(GregorianCalendar.MONTH), 1);
            monthNum = 0;
            yearNum++;
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) + 1);
            monthNum++;


        }

    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
}
