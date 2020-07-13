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

    public interface OnDateReceiveCallback{
        void onDateReceive(int year, int month, int day);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
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
