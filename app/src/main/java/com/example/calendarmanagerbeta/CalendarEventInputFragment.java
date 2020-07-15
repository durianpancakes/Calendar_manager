package com.example.calendarmanagerbeta;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.DateFormat;
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

public class CalendarEventInputFragment extends Fragment implements SelectorDialog.OnDialogSelectorListener{
    private static int DATE_BUTTON_ID = 0;
    private static int TIME_BUTTON_ID = 0;
    private static boolean mAllDay;
    private static boolean nameError = true;
    private View myFragmentView;
    private eventInputListener mEventListener;
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
    private Button saveButton;
    private Button cancelButton;
    private int reminderSelectedIndex = 4;
    private int repeatSelectedIndex = 0;

    private WeekViewEvent calendarEvent = new WeekViewEvent();
    private static Calendar startTime = Calendar.getInstance();
    private static Calendar endTime = Calendar.getInstance();

    private String[] mRemindersArrayOptions;
    private String[] mRepeatArrayOptions;

    public interface eventInputListener{
        void onCancelPressed();
        void onAddPressed(WeekViewEvent event);
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
        System.out.println(nameError);
        if(endTime.before(startTime) && !mAllDay){
            errorMsg.setVisibility(View.VISIBLE);
            return false;
        } else {
            errorMsg.setVisibility(View.GONE);
            return true;
        }
    }

    @Override
    public void onSelectedOption(int dialogId) {
        System.out.println(dialogId);
    }

    public CalendarEventInputFragment() {
        // Required empty public constructor
    }

    public String getCurrentDateTimeString(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        return nowAsISO;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRemindersArrayOptions = getResources().getStringArray(R.array.reminders_array);
        mRepeatArrayOptions = getResources().getStringArray(R.array.repeat_array);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_calendar_event_input, container, false);
        eventName = (EditText)myFragmentView.findViewById(R.id.input_event_name);
        fromDate = (Button)myFragmentView.findViewById(R.id.input_from_date);
        fromTime = (Button)myFragmentView.findViewById(R.id.input_from_time);
        toDate = (Button)myFragmentView.findViewById(R.id.input_to_date);
        toTime = (Button)myFragmentView.findViewById(R.id.input_to_time);
        allDay = (CheckBox)myFragmentView.findViewById(R.id.input_all_day_checkbox);
        checkCal = (Button)myFragmentView.findViewById(R.id.input_check_cal);
        errorMsg = (TextView)myFragmentView.findViewById(R.id.input_time_error_msg);
        nameErrorMsg = (TextView)myFragmentView.findViewById(R.id.event_name_error);
        locationText = (EditText)myFragmentView.findViewById(R.id.input_location);
        descriptionText = (EditText)myFragmentView.findViewById(R.id.input_description);
        reminderButton = (Button)myFragmentView.findViewById(R.id.input_reminder);
        repeatButton = (Button)myFragmentView.findViewById(R.id.input_repeat);
        saveButton = (Button)myFragmentView.findViewById(R.id.event_input_save);
        cancelButton = (Button)myFragmentView.findViewById(R.id.event_input_cancel);


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
                    mAllDay = true;
                    fromTime.setVisibility(View.GONE);
                    toTime.setVisibility(View.GONE);
                    calendarEvent.setAllDay(true);
                } else {
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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs() && !nameError){
                    // Inputs are valid, allow item to be saved.
                    WeekViewEvent event = new WeekViewEvent();
                    event.setName(eventName.getText().toString());
                    event.setStartTime(startTime);
                    event.setEndTime(endTime);
                    event.setDescription(descriptionText.getText().toString());
                    event.setLocation(locationText.getText().toString());
                    mEventListener.onAddPressed(event);
                } else {
                    Toast.makeText(getActivity(), "Please check if all fields are valid", Toast.LENGTH_LONG);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEventListener.onCancelPressed();
            }
        });

        return myFragmentView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof CalendarEventInputFragment.eventInputListener){
            mEventListener = (CalendarEventInputFragment.eventInputListener)context;
        }
        else{
            throw new RuntimeException(context.toString() + " must implement listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEventListener = null;
    }
}