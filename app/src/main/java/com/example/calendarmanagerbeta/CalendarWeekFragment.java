package com.example.calendarmanagerbeta;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarWeekFragment extends Fragment {
    View myFragmentView;
    private WeekView mWeekView;
    private TextView weekNumber;
    private TextView monthYearString;
    private FloatingActionButton addEventButton;
    private int currentWeekNumber;
    private int firstVisibleDayYear;
    private int lastVisibleDayYear;
    private int firstVisibleDayMonth;
    private int lastVisibleDayMonth;
    private List<WeekViewEvent> mEvents = new ArrayList<>();
    private CalendarWeekFragment.EventAddedListener mEventAddedListener;
    private LongPressListener mLongPressListener;
    private String[] monthStrings = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    public CalendarWeekFragment() {
        // Required empty public constructor
    }

    public interface LongPressListener{
        void onDeletePressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_calendar_week, container, false);
        mWeekView = myFragmentView.findViewById(R.id.calendar_week_view);
        weekNumber = myFragmentView.findViewById(R.id.week_view_weekNumber);
        monthYearString = myFragmentView.findViewById(R.id.week_view_monthYear);
        addEventButton = myFragmentView.findViewById(R.id.week_view_add);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Calendar");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Week View");

        refreshDatabase();
        setupWeekView();

        return myFragmentView;
    }

    public void showEventLongPressDialog(){
        String[] options = {"Edit event", "Delete event"};

        new MaterialAlertDialogBuilder(getContext()).setTitle("Event").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i){
                    case 0:
                        // Edit pressed
                    case 1:
                        // Delete pressed
                        mLongPressListener.onDeletePressed();
                }
            }
        }).show();
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
                mWeekView.getMonthChangeListener().onMonthChange(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH));
                mWeekView.notifyDatasetChanged();
            }

            @Override
            public void onEventDeleted() {

            }
        });
        firebaseHelper.pullEvents();
    }

    private void setupWeekView(){
        if(mWeekView != null){
            mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
                @Override
                public List<? extends WeekViewEvent> onMonthChange(int year, int month) {
                    return mEvents;
                }
            });

            //set listener for event click
            mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
                @Override
                public void onEventClick(WeekViewEvent event, RectF eventRect) {
                    DialogFragment viewEventDialog = DisplayEventDialog.newInstance(event);
                    viewEventDialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "viewEvent");
                }
            });

            //set event long press listener
            mWeekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
                @Override
                public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
                    mLongPressListener = new LongPressListener() {
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
                            });
                            firebaseHelper.removeEvent(event);
                        }
                    };
                    showEventLongPressDialog();
                }
            });

            //set scroll listener
            mWeekView.setScrollListener(new WeekView.ScrollListener(){
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

        // setup necessary characteristics of the week view
        mWeekView.setShowFirstDayOfWeekFirst(true);
        Calendar currentCal = Calendar.getInstance();
        mWeekView.goToHour(currentCal.get(Calendar.HOUR_OF_DAY));
    }

    public void refreshHeaderTexts(){
        currentWeekNumber = mWeekView.getCurrentWeekNumber();
        firstVisibleDayMonth = mWeekView.getCurrentFirstVisibleDayMonth();
        lastVisibleDayMonth = mWeekView.getCurrentLastVisibleDayMonth();
        firstVisibleDayYear = mWeekView.getCurrentFirstVisibleDayYear();
        lastVisibleDayYear = mWeekView.getCurrentLastVisibleDayYear();

        weekNumber.setText("W" + currentWeekNumber);

        if(firstVisibleDayMonth != lastVisibleDayMonth){
            monthYearString.setText(monthStrings[firstVisibleDayMonth] + "/" + monthStrings[lastVisibleDayMonth]);
        }
        else{
            monthYearString.setText(monthStrings[firstVisibleDayMonth]);
        }

        if(firstVisibleDayMonth != lastVisibleDayMonth){
            if(firstVisibleDayYear != lastVisibleDayYear){
                monthYearString.setText(monthStrings[firstVisibleDayMonth] + " " + firstVisibleDayYear + "/" + monthStrings[lastVisibleDayMonth] + " " + lastVisibleDayYear);
            }
            else{
                monthYearString.setText(monthStrings[firstVisibleDayMonth] + "/" + monthStrings[lastVisibleDayMonth] + " " + firstVisibleDayYear);
            }
        }
        else{
            monthYearString.setText(monthStrings[firstVisibleDayMonth] + " " + firstVisibleDayYear);
        }
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public interface EventAddedListener{
        void eventAdded(WeekViewEvent event);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof CalendarWeekFragment.EventAddedListener){
            mEventAddedListener = (CalendarWeekFragment.EventAddedListener)context;
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
