package com.example.calendarmanagerbeta;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReminderEditDialog extends DialogFragment implements View.OnClickListener {
    private EditText mTitleText;
    private Button mDatePicker, mTimePicker;
    private Switch mAllDaySwitch;
    private int mReceivedID;
    private Calendar mCalendar;
    private Reminder mReceivedReminder;

    private String mTitle;
    private Boolean mAllDay;
    private String mDate;
    private String mTime;
    private Boolean mActive;

    private ReminderDialogInterface mReminderDialogInterface;


    public interface ReminderDialogInterface{
        void onEditPressed(Reminder receivedReminder, Reminder newReminder);
    }

    public void setReminderDialogInterface(ReminderDialogInterface reminderDialogInterface){
        this.mReminderDialogInterface = reminderDialogInterface;
    }

    ReminderEditDialog newInstance(Reminder reminder){
        this.mReceivedReminder = reminder;

        return new ReminderEditDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_reminder_dialog, container, false);
        mTitleText = view.findViewById(R.id.reminder_edittext);
        mDatePicker = view.findViewById(R.id.reminder_datepicker_btn);
        mTimePicker = view.findViewById(R.id.reminder_timepicker_btn);
        mAllDaySwitch = view.findViewById(R.id.reminder_all_day_switch);

        TextView save = view.findViewById(R.id.reminder_save);
        ImageButton close = view.findViewById(R.id.reminder_cancel);

        save.setOnClickListener(this);
        close.setOnClickListener(this);

        mTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mReceivedID = mReceivedReminder.getID();
        mTitle = mReceivedReminder.getTitle();
        mCalendar = Calendar.getInstance();
        mAllDay = mReceivedReminder.getAllDay();
        mActive = mReceivedReminder.getActive();

        mTitleText.setText(mTitle);
        mDatePicker.setText(getDateString(mCalendar));
        mTimePicker.setText(getTimeString(mCalendar));
        mAllDaySwitch.setChecked(mAllDay);

        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.DAY_OF_MONTH, day);
                        mDatePicker.setText(getDateString(mCalendar));
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        mTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mCalendar.set(Calendar.MINUTE, minute);
                        mTimePicker.setText(getTimeString(mCalendar));
                    }
                }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false);
                dialog.show();
            }
        });

        return view;
    }

    private String getDateString(Calendar calendar){
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
        return sdf.format(date);
    }

    private String getTimeString(Calendar calendar){
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        return sdf.format(date);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id){
            case R.id.reminder_save:
                Reminder newReminder = new Reminder();
                newReminder.setTitle(mTitleText.getText().toString());
                newReminder.setAllDay(mAllDay);
                newReminder.setTime(mCalendar);
                newReminder.setActive(true);
                newReminder.setID(mReceivedID);
                if(mReminderDialogInterface != null){
                    mReminderDialogInterface.onEditPressed(mReceivedReminder, newReminder);
                }
                dismiss();
                break;
            case R.id.reminder_cancel:
                dismiss();
                break;
        }
    }
}
