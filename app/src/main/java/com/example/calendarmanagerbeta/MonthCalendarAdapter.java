package com.example.calendarmanagerbeta;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MonthCalendarAdapter extends BaseAdapter {
    private final Context mContext;
    private GregorianCalendar mCalendar;
    private Calendar mCalendarToday;
    private List<String> mItems;
    private List<String> tempItems;
    private int mMonth;
    private int mYear;
    private int mDaysShown;
    private int mDaysLastMonth;
    private int mDaysNextMonth;
    private final int[] mDaysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};


    public MonthCalendarAdapter(Context context, int month, int year, GregorianCalendar monthCalendar){
        mContext = context;
        mCalendarToday = Calendar.getInstance();
        mMonth = month;
        mYear = year;
        mCalendar = new GregorianCalendar(mYear, mMonth, 1);
        mItems = new ArrayList<String>();
        System.out.println(mMonth + " " + mYear);

        populateMonth(mItems);
    }

    private void populateMonth(List<String> itemArray){
        int firstDay = getDay(mCalendar.get(Calendar.DAY_OF_WEEK));
        int prevDay;
        if(mMonth == 0)
            prevDay = _daysInMonth(11) - firstDay + 1;
        else
            prevDay = _daysInMonth(mMonth - 1) - firstDay + 1;
        for(int i = 0; i < firstDay; i++){
            itemArray.add(String.valueOf(prevDay + i));
            mDaysLastMonth++;
            mDaysShown++;
        }

        int daysInMonth = _daysInMonth(mMonth);
        for(int i = 1; i <= daysInMonth; i++){
            itemArray.add(String.valueOf(i));
            mDaysShown++;
        }

        mDaysNextMonth = 1;
        while(mDaysShown % 7 != 0){
            itemArray.add(String.valueOf(mDaysNextMonth));
            mDaysShown++;
            mDaysNextMonth++;
        }
    }

    private boolean isToday(int day, int month, int year) {
        if (mCalendarToday.get(Calendar.MONTH) == month
                && mCalendarToday.get(Calendar.YEAR) == year
                && mCalendarToday.get(Calendar.DAY_OF_MONTH) == day) {
            return true;
        }
        return false;
    }

    private int[] getToday(){
        int date[] = new int[3];
        date[0] = mCalendarToday.get(Calendar.DAY_OF_MONTH);
        date[1] = mCalendarToday.get(Calendar.MONTH);
        date[2] = mCalendarToday.get(Calendar.YEAR);

        return date;
    }

    private int[] getDate(int position) {
        int date[] = new int[3];
        if (position < mDaysLastMonth) {
            // previous month
            date[0] = Integer.parseInt(mItems.get(position));
            if (mMonth == 0) {
                date[1] = 11;
                date[2] = mYear - 1;
            } else {
                date[1] = mMonth - 1;
                date[2] = mYear;
            }
        } else if (position <= mDaysShown - mDaysNextMonth  ) {
            // current month
            date[0] = position - mDaysLastMonth + 1;
            date[1] = mMonth;
            date[2] = mYear;
        } else {
            // next month
            date[0] = Integer.parseInt(mItems.get(position));
            if (mMonth == 11) {
                date[1] = 0;
                date[2] = mYear + 1;
            } else {
                date[1] = mMonth + 1;
                date[2] = mYear;
            }
        }
        return date;
    }

    private int _daysInMonth(int month) {
        int daysInMonth = mDaysInMonth[month];
        if (month == 1 && mCalendar.isLeapYear(mYear))
            daysInMonth++;
        return daysInMonth;
    }

    private int getDay(int day) {
        switch (day) {
            case Calendar.SUNDAY:
                return 0;
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            default:
                return 0;
        }
    }

    @Override
    public int getCount(){
        return mDaysShown;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.calendar_month_item, null);
        }

        final TextView dayTextView = (TextView)convertView.findViewById(R.id.month_date);
        dayTextView.setText(mItems.get(position));

        int[] date = getDate(position);
        if (date != null) {
            if (date[1] != mMonth) {
                // previous or next month
                dayTextView.setBackgroundColor(Color.rgb(234, 234, 250));
            } else {
                // current month
                dayTextView.setBackgroundColor(Color.rgb(244, 244, 244));
                if (isToday(date[0], date[1], date[2] )) {
                    dayTextView.setTextColor(Color.RED);
                }
            }
        } else {
            dayTextView.setBackgroundColor(Color.argb(100, 10, 80, 255));
        }

        return convertView;
    }
}