package com.example.calendarmanagerbeta;

import android.content.DialogInterface;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class WeekViewDialog extends DialogFragment implements View.OnClickListener{
    private WeekView mDayView;
    private Calendar startTime;
    private ArrayList<WeekViewEvent> mEvents = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.view_calendar_dialog, container, false);
        mDayView = myFragmentView.findViewById(R.id.dialog_day_view);
        ImageButton cancelBtn = myFragmentView.findViewById(R.id.calendar_view_cancel);

        cancelBtn.setOnClickListener(this);
        refreshDatabase();
        setupWeekView();

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

    private void setupWeekView(){
        if(mDayView != null){
            mDayView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
                @Override
                public List<? extends WeekViewEvent> onMonthChange(int year, int month) {
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

                }
            });

            //set scroll listener
            mDayView.setScrollListener(new WeekView.ScrollListener(){
                @Override
                public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay){

                }
            });


        }

        mDayView.goToDate(startTime);
        mDayView.setHourHeight(25);
    }

    public static WeekViewDialog newInstance(Calendar calendar){
        WeekViewDialog weekViewDialog = new WeekViewDialog();
        weekViewDialog.setCalendar(calendar);

        return weekViewDialog;
    }

    public void setCalendar(Calendar calendar){
        startTime = calendar;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id){
            case R.id.calendar_view_cancel:
                dismiss();
        }
    }
}
