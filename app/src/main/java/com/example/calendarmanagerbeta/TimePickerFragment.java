package com.example.calendarmanagerbeta;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private TimePickerFragment.OnTimeReceiveCallback mListener;
    private Context context;
    private static Calendar mCalendar;

    public interface OnTimeReceiveCallback{
        void onTimeReceive(int hours, int min);
    }

    public static TimePickerFragment newInstance(Calendar cal){
        TimePickerFragment fragment = new TimePickerFragment();

        mCalendar = cal;

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int min = mCalendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, min, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;

        try{
            mListener = (TimePickerFragment.OnTimeReceiveCallback)context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnTimeSetListener");
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hours, int min) {
        mListener.onTimeReceive(hours, min);
    }
}
