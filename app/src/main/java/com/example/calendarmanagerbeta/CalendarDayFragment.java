package com.example.calendarmanagerbeta;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toolbar;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarDayFragment extends Fragment {
    View myFragmentView;
    private WeekView mDayView;
    private TextView dayNumber;
    private TextView monthYearString;
    private int currentDayNumber;
    private int currentMonth;
    private int currentYear;
    private FloatingActionButton addEventButton;
    private List<WeekViewEvent> mEvents = new ArrayList<>();
    private EventAddedListener mEventAddedListener;
    private LongPressListener mLongPressListener;
    private String[] monthStrings = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    private Calendar datePickerDialogCalendar;

    public CalendarDayFragment() {
        // Required empty public constructor
    }

    public interface LongPressListener{
        void onDeletePressed();
        void onEditPressed();
    }

    public void showEventLongPressDialog(){
        String[] options = {"Edit event", "Delete event"};

        new MaterialAlertDialogBuilder(getContext()).setTitle("Event").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i){
                    case 0:
                        // Edit pressed
                        mLongPressListener.onEditPressed();
                        break;
                    case 1:
                        // Delete pressed
                        mLongPressListener.onDeletePressed();
                        break;
                }
            }
        }).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_calendar_day, container, false);
        mDayView = myFragmentView.findViewById(R.id.calendar_day_view);
        dayNumber = myFragmentView.findViewById(R.id.day_view_dayNumber);
        monthYearString = myFragmentView.findViewById(R.id.day_view_monthYear);
        addEventButton = myFragmentView.findViewById(R.id.day_view_add);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Calendar");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Day View");

        refreshDatabase();
        setupDayView();

        return myFragmentView;
    }

    public void refreshDatabase(){
        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance(getContext());
        firebaseHelper.setFirebaseCallbackListener(new FirebaseCallback() {
            @Override
            public void onGetModuleSuccess(ArrayList<NUSModuleLite> userModules) {

            }

            @Override
            public void onGetKeyword(ArrayList<String> userKeywords) {

            }

            @Override
            public void onGetEvents(ArrayList<WeekViewEvent> userEvents) {
                mEvents = userEvents;
                mDayView.getMonthChangeListener().onMonthChange(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH));
                mDayView.notifyDatasetChanged();
            }

            @Override
            public void onEventDeleted() {

            }

            @Override
            public void onKeywordDeleted() {

            }
        });
        firebaseHelper.pullEvents();
    }

    private void setupDayView(){
        if(mDayView != null){
            mDayView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
                @Override
                public List<? extends WeekViewEvent> onMonthChange(int i, int i1) {
                return mEvents;
                }
            });

            //set listener for event click
            mDayView.setOnEventClickListener(new WeekView.EventClickListener() {
                @Override
                public void onEventClick(WeekViewEvent event, RectF eventRect) {
                    DialogFragment viewEventDialog = DisplayEventDialog.newInstance(event);
                    viewEventDialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "viewEvent");
                }
            });

            //set event long press listener
            mDayView.setEventLongPressListener(new WeekView.EventLongPressListener() {
                @Override
                public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
                    mLongPressListener = new CalendarDayFragment.LongPressListener() {
                        @Override
                        public void onDeletePressed() {
                            FirebaseHelper firebaseHelper = FirebaseHelper.getInstance(getContext());
                            firebaseHelper.setFirebaseCallbackListener(new FirebaseCallback() {
                                @Override
                                public void onGetModuleSuccess(ArrayList<NUSModuleLite> userModules) {

                                }

                                @Override
                                public void onGetKeyword(ArrayList<String> userKeywords) {

                                }

                                @Override
                                public void onGetEvents(ArrayList<WeekViewEvent> userEvents) {

                                }

                                @Override
                                public void onEventDeleted() {
                                    refreshDatabase();
                                }

                                @Override
                                public void onKeywordDeleted() {

                                }
                            });
                            firebaseHelper.removeEvent(event);
                        }

                        @Override
                        public void onEditPressed() {
                            DialogFragment editEventDialog = CalendarEventEditDialog.newInstance(event);
                            ((CalendarEventEditDialog) editEventDialog).setEventInputCallback(new CalendarEventEditDialog.EventInputListener() {
                                @Override
                                public void onEditPressed(final WeekViewEvent oldEvent, final WeekViewEvent newEvent) {
                                    FirebaseHelper firebaseHelper = FirebaseHelper.getInstance(getContext());
                                    firebaseHelper.setFirebaseCallbackListener(new FirebaseCallback() {
                                        @Override
                                        public void onGetModuleSuccess(ArrayList<NUSModuleLite> userModules) {

                                        }

                                        @Override
                                        public void onGetKeyword(ArrayList<String> userKeywords) {

                                        }

                                        @Override
                                        public void onGetEvents(ArrayList<WeekViewEvent> userEvents) {

                                        }

                                        @Override
                                        public void onEventDeleted() {
                                            mEventAddedListener.eventAdded(newEvent);
                                            refreshDatabase();
                                        }

                                        @Override
                                        public void onKeywordDeleted() {

                                        }
                                    });
                                    firebaseHelper.removeEvent(oldEvent);
                                }
                            });
                            editEventDialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "eventEdit");
                        }
                    };
                    showEventLongPressDialog();
                }
            });

            //set scroll listener
            mDayView.setScrollListener(new WeekView.ScrollListener(){
                @Override
                public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay){
                refreshHeaderTexts();
                }
            });

            addEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment addEventDialog = CalendarEventInputDialog.newInstance();
                    ((CalendarEventInputDialog) addEventDialog).setEventInputCallback(new CalendarEventInputDialog.EventInputListener() {
                        @Override
                        public void onAddPressed(WeekViewEvent event) {
                            mEventAddedListener.eventAdded(event);
                            refreshDatabase();
                        }
                    });
                    addEventDialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "eventInput");
                }
            });
        }
        mDayView.goToToday();
        Calendar currentCal = Calendar.getInstance();
        mDayView.goToHour(currentCal.get(Calendar.HOUR_OF_DAY));
        // setup necessary characteristics of the week view
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_cal_btn :
            {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getContext(), null, datePickerDialogCalendar.get(Calendar.YEAR), datePickerDialogCalendar.get(Calendar.MONTH), datePickerDialogCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        System.out.println(year + " " + month + " " + day);
                        datePickerDialogCalendar.set(Calendar.YEAR, year);
                        datePickerDialogCalendar.set(Calendar.MONTH, month);
                        datePickerDialogCalendar.set(Calendar.DAY_OF_MONTH, day);
                        mDayView.goToDate(datePickerDialogCalendar);
                    }
                });
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshHeaderTexts(){
        currentDayNumber = mDayView.getCurrentDayNumber();
        currentMonth = mDayView.getCurrentFirstVisibleDayMonth();
        currentYear = mDayView.getCurrentDayYear();

        dayNumber.setText("D" + currentDayNumber);
        monthYearString.setText(monthStrings[currentMonth] + " " + String.valueOf(currentYear));
    }

    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        datePickerDialogCalendar = Calendar.getInstance();
    }

    public void onPause(){
        super.onPause();
    }

    public interface EventAddedListener{
        void eventAdded(WeekViewEvent event);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof CalendarDayFragment.EventAddedListener){
            mEventAddedListener = (CalendarDayFragment.EventAddedListener)context;
        }
        else{
            throw new RuntimeException(context.toString() + " must implement listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEventAddedListener = null;
    }
}
