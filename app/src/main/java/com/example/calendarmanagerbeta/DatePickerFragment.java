package com.example.calendarmanagerbeta;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    private DatePickerFragment.OnDateReceiveCallback mListener;
    private Context context;
    private static Calendar mCalendar;

    public static DatePickerFragment newInstance(Calendar cal){
        DatePickerFragment fragment = new DatePickerFragment();

        mCalendar = cal;

        return fragment;
    }

    public interface OnDateReceiveCallback{
        void onDateReceive(int year, int month, int day);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;

        try{
            mListener = (DatePickerFragment.OnDateReceiveCallback)context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnDateSetListener");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mListener.onDateReceive(year, month, day);
    }
}
