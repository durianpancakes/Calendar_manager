package com.example.calendarmanagerbeta;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class CalendarEventInputDialog extends DialogFragment implements View.OnClickListener{
    private EventInputListener eventInputListener;
    private static int DATE_BUTTON_ID = 0;
    private static int TIME_BUTTON_ID = 0;
    private static boolean mAllDay;
    private static boolean nameError = true;
    private EventInputListener mListener;
    private static EditText eventName;
    @SuppressLint("StaticFieldLeak")
    private static Button fromDate;
    @SuppressLint("StaticFieldLeak")
    private static Button fromTime;
    @SuppressLint("StaticFieldLeak")
    private static Button toDate;
    @SuppressLint("StaticFieldLeak")
    private static Button toTime;
    private CheckBox allDay;
    private Button checkCal;
    @SuppressLint("StaticFieldLeak")
    private static TextView errorMsg;
    @SuppressLint("StaticFieldLeak")
    private static TextView nameErrorMsg;
    private EditText locationText;
    private EditText descriptionText;
    private Button reminderButton;
    private Button repeatButton;
    private int reminderSelectedIndex = 4;
    private int repeatSelectedIndex = 0;

    private WeekViewEvent calendarEvent = new WeekViewEvent();
    private static Calendar startTime = Calendar.getInstance();
    private static Calendar endTime = Calendar.getInstance();

    private String[] mRemindersArrayOptions;
    private String[] mRepeatArrayOptions;

    public interface EventInputListener{
        void onAddPressed(WeekViewEvent event);
    }

    public void setEventInputCallback(EventInputListener eventInputListener){
        this.mListener = eventInputListener;
    }

    static CalendarEventInputDialog newInstance(){
        return new CalendarEventInputDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
        mRemindersArrayOptions = getResources().getStringArray(R.array.reminders_array);
        mRepeatArrayOptions = getResources().getStringArray(R.array.repeat_array);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_calendar_event_dialog, container, false);
        ImageButton close = view.findViewById(R.id.event_input_cancel);
        TextView save = view.findViewById(R.id.event_input_save);

        close.setOnClickListener(this);
        save.setOnClickListener(this);

        eventName = view.findViewById(R.id.input_event_name);
        fromDate = view.findViewById(R.id.input_from_date);
        fromTime = view.findViewById(R.id.input_from_time);
        toDate = view.findViewById(R.id.input_to_date);
        toTime = view.findViewById(R.id.input_to_time);
        allDay = view.findViewById(R.id.input_all_day_checkbox);
        checkCal = view.findViewById(R.id.input_check_cal);
        errorMsg = view.findViewById(R.id.input_time_error_msg);
        nameErrorMsg = view.findViewById(R.id.event_name_error);
        locationText = view.findViewById(R.id.input_location);
        descriptionText = view.findViewById(R.id.input_description);
        reminderButton = view.findViewById(R.id.input_reminder);
        repeatButton = view.findViewById(R.id.input_repeat);

        // Initializing current day parameters
        startTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY) + 1);
        LocalDateTime currentDateTime = LocalDateTime.now();
        DayOfWeek currentDateTimeDayOfWeek = currentDateTime.getDayOfWeek();
        String currentDayOfWeek = currentDateTimeDayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        int currentDayOfMonth = currentDateTime.getDayOfMonth();
        Month currentDateTimeMonth = currentDateTime.getMonth();
        String currentMonth = currentDateTimeMonth.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        int currentYear = currentDateTime.getYear();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm a");
        String currentDateString = currentDayOfWeek + ", " + currentMonth + " " + currentDayOfMonth + ", " + currentYear;
        String currentTimeString = currentDateTime.format(dtf);

        LocalDateTime toDateTime = currentDateTime.plusHours(1);
        DayOfWeek toDateTimeDayOfWeek = toDateTime.getDayOfWeek();
        String toDayOfWeek = toDateTimeDayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        int toDayOfMonth = toDateTime.getDayOfMonth();
        Month toDateTimeMonth = toDateTime.getMonth();
        String toMonth = toDateTimeMonth.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        int toYear = toDateTime.getYear();
        String toDateString = toDayOfWeek + ", " + toMonth + " " + toDayOfMonth + ", " + toYear;
        String toTimeString = toDateTime.format(dtf);

        eventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    nameError = true;
                    nameErrorMsg.setVisibility(View.VISIBLE);
                } else {
                    nameError = false;
                    nameErrorMsg.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Initializing fromDate
        fromDate.setText(currentDateString);
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DATE_BUTTON_ID = 1;
                DialogFragment datePicker = DatePickerFragment.newInstance(startTime);
                datePicker.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "datePicker");
            }
        });

        // Initializing fromTime
        fromTime.setText(currentTimeString);
        fromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TIME_BUTTON_ID = 1;
                DialogFragment timePicker = TimePickerFragment.newInstance(startTime);
                timePicker.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "timePicker");
            }
        });

        //Initializing toDate
        toDate.setText(toDateString);
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DATE_BUTTON_ID = 2;
                DialogFragment datePicker = DatePickerFragment.newInstance(endTime);
                datePicker.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "datePicker");
            }
        });

        // Initializing toTime
        toTime.setText(toTimeString);
        toTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TIME_BUTTON_ID = 2;
                DialogFragment timePicker = TimePickerFragment.newInstance(endTime);
                timePicker.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "timePicker");
            }
        });

        allDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    System.out.println("CHECKED TRUE");
                    mAllDay = true;
                    fromTime.setVisibility(View.GONE);
                    toTime.setVisibility(View.GONE);
                    calendarEvent.setAllDay(true);
                } else {
                    System.out.println("CHECKED FALSE");
                    mAllDay = false;
                    fromTime.setVisibility(View.VISIBLE);
                    toTime.setVisibility(View.VISIBLE);
                    calendarEvent.setAllDay(false);
                }
                validateInputs();
            }
        });

        reminderButton.setText(mRemindersArrayOptions[reminderSelectedIndex]);
        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SelectorDialog sd = SelectorDialog.newInstance(R.array.reminders_array, reminderSelectedIndex);
                sd.setDialogSelectorListener(new SelectorDialog.OnDialogSelectorListener() {
                    @Override
                    public void onSelectedOption(int dialogId) {
                        reminderSelectedIndex = dialogId;
                        reminderButton.setText(mRemindersArrayOptions[reminderSelectedIndex]);
                    }
                });
                sd.show(getActivity().getSupportFragmentManager(), "SelectorDialog");
            }
        });

        repeatButton.setText(mRepeatArrayOptions[repeatSelectedIndex]);
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SelectorDialog sd = SelectorDialog.newInstance(R.array.repeat_array, repeatSelectedIndex);
                sd.setDialogSelectorListener(new SelectorDialog.OnDialogSelectorListener() {
                    @Override
                    public void onSelectedOption(int dialogId) {
                        repeatSelectedIndex = dialogId;
                        repeatButton.setText(mRepeatArrayOptions[repeatSelectedIndex]);
                    }
                });
                sd.show(getActivity().getSupportFragmentManager(), "SelectorDialog");
            }
        });

        validateInputs();

        return view;
    }

    public static void updateDateButton(int year, int month, int day) {
        Calendar newCal = Calendar.getInstance(TimeZone.getDefault());
        newCal.set(year, month, day);
        String newDayOfWeek = newCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT_FORMAT, Locale.ENGLISH);
        String newMonth = newCal.getDisplayName(Calendar.MONTH, Calendar.SHORT_FORMAT, Locale.ENGLISH);
        switch(DATE_BUTTON_ID){
            case 1:
                fromDate.setText(newDayOfWeek + ", " + newMonth + " " + day + ", " + year);
                startTime.set(year, month, day);
                validateInputs();
                break;
            case 2:
                toDate.setText(newDayOfWeek + ", " + newMonth + " " + day + ", " + year);
                endTime.set(year, month, day);
                validateInputs();
                break;
        }
    }

    public static void updateTimeButton(int hour, int min){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        Calendar newCal = Calendar.getInstance(TimeZone.getDefault());
        newCal.set(Calendar.HOUR_OF_DAY, hour);
        newCal.set(Calendar.MINUTE, min);
        String newTimeString = sdf.format(newCal.getTime());
        switch(TIME_BUTTON_ID){
            case 1:
                fromTime.setText(newTimeString);
                startTime.set(Calendar.HOUR_OF_DAY, hour);
                startTime.set(Calendar.MINUTE, min);
                validateInputs();
                break;
            case 2:
                toTime.setText(newTimeString);
                endTime.set(Calendar.HOUR_OF_DAY, hour);
                endTime.set(Calendar.MINUTE, min);
                validateInputs();
                break;
        }
    }

    public static Boolean validateInputs(){
        if(endTime.before(startTime) && !mAllDay){
            errorMsg.setVisibility(View.VISIBLE);
            return false;
        } else {
            errorMsg.setVisibility(View.GONE);
            return true;
        }
    }

    @Override
    public void onClick(View v){
        int id = v.getId();

        switch(id){
            case R.id.event_input_save:
                if(validateInputs() && !nameError){
                    WeekViewEvent event = new WeekViewEvent();
                    event.setName(eventName.getText().toString());
                    event.setStartTime(startTime);
                    event.setEndTime(endTime);
                    event.setDescription(descriptionText.getText().toString());
                    event.setLocation(locationText.getText().toString());
                    event.setAllDay(mAllDay);
                    mListener.onAddPressed(event);
                    dismiss();
                } else {
                    break;
                }
                break;
            case R.id.event_input_cancel:
                dismiss();
                break;
        }
    }
}

