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
import com.microsoft.graph.requests.extensions.ICalendarCollectionPage;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class CalendarEventEditDialog extends DialogFragment implements View.OnClickListener{
    private static int DATE_BUTTON_ID = 0;
    private static int TIME_BUTTON_ID = 0;
    private static boolean mAllDay;
    private static boolean nameError = true;
    private CalendarEventEditDialog.EventInputListener mListener;
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

    private static WeekViewEvent inputCalendarEvent = new WeekViewEvent();
    private WeekViewEvent outputCalendarEvent = new WeekViewEvent();
    private static Calendar startTime;
    private static Calendar endTime;

    private String[] mRemindersArrayOptions;
    private String[] mRepeatArrayOptions;

    public interface EventInputListener{
        void onAddPressed(WeekViewEvent event);
    }

    public void setEventInputCallback(CalendarEventEditDialog.EventInputListener eventInputListener){
        this.mListener = eventInputListener;
    }

    static CalendarEventEditDialog newInstance(WeekViewEvent event){
        inputCalendarEvent = event;
        return new CalendarEventEditDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
        mRemindersArrayOptions = getResources().getStringArray(R.array.reminders_array);
        mRepeatArrayOptions = getResources().getStringArray(R.array.repeat_array);
        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();
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
        startTime = inputCalendarEvent.getStartTime();
        endTime = inputCalendarEvent.getEndTime();
        Date currentDate = startTime.getTime();
        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE, MMM dd, yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("h:mm a");
        String currentDateString = sdf1.format(currentDate);
        String currentTimeString = sdf2.format(currentDate);

        Date endDate = endTime.getTime();
        String toDateString = sdf1.format(endDate);
        String toTimeString = sdf2.format(endDate);

        eventName.setText(inputCalendarEvent.getName());
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
                Calendar cal = (Calendar)startTime.clone();
                DialogFragment datePicker = DatePickerFragment.newInstance(cal);
                datePicker.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "datePicker");
            }
        });

        // Initializing fromTime
        fromTime.setText(currentTimeString);
        fromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TIME_BUTTON_ID = 1;
                Calendar cal = (Calendar)startTime.clone();
                DialogFragment timePicker = TimePickerFragment.newInstance(cal);
                timePicker.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "timePicker");
            }
        });

        //Initializing toDate
        toDate.setText(toDateString);
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DATE_BUTTON_ID = 2;
                Calendar cal = (Calendar)endTime.clone();
                DialogFragment datePicker = DatePickerFragment.newInstance(cal);
                datePicker.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "datePicker");
            }
        });

        // Initializing toTime
        toTime.setText(toTimeString);
        toTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TIME_BUTTON_ID = 2;
                Calendar cal = (Calendar)endTime.clone();
                DialogFragment timePicker = TimePickerFragment.newInstance(cal);
                timePicker.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "timePicker");
            }
        });

        allDay.setChecked(inputCalendarEvent.isAllDay());
        allDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    System.out.println("CHECKED TRUE");
                    mAllDay = true;
                    fromTime.setVisibility(View.GONE);
                    toTime.setVisibility(View.GONE);
                    outputCalendarEvent.setAllDay(true);
                } else {
                    System.out.println("CHECKED FALSE");
                    mAllDay = false;
                    fromTime.setVisibility(View.VISIBLE);
                    toTime.setVisibility(View.VISIBLE);
                    outputCalendarEvent.setAllDay(false);
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

        checkCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = (Calendar)startTime.clone();
                DialogFragment checkCalendarDialog = WeekViewDialog.newInstance(cal);
                checkCalendarDialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "checkCalendar");
            }
        });

        locationText.setText(inputCalendarEvent.getLocation());
        descriptionText.setText(inputCalendarEvent.getDescription());

        validateInputs();

        return view;
    }

    public static void updateDateButton(int year, int month, int day) {
        System.out.println("UPDATE DATE BUTTON");
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
                    event.setIdentifier(inputCalendarEvent.getIdentifier());
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

