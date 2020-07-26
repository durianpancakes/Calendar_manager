package com.example.calendarmanagerbeta;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alamkanak.weekview.WeekViewEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ReminderFragment extends Fragment {
    private View myFragmentView;
    private ReminderFragment.ToolbarCalendarButtonCallback mToolbarButtonCallback;
    private ProgressBar mProgress;
    private FloatingActionButton addReminderBtn;
    private RecyclerView mReminderRecycler;

    public interface ToolbarCalendarButtonCallback{
        void onToolbarCalendarClicked();
        void onReminderAdded(Reminder reminder);
        void onReminderDelete(Reminder reminder);
    }

    public void setToolbarCalendarButtonCallback(ReminderFragment.ToolbarCalendarButtonCallback toolbarCalendarButtonCallback){
        this.mToolbarButtonCallback = toolbarCalendarButtonCallback;
    }

    public ReminderFragment() {

    }

    private void showProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_reminder, container, false);
        mProgress = getActivity().findViewById(R.id.progressbar);
        mReminderRecycler = myFragmentView.findViewById(R.id.reminder_list);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Reminders");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        addReminderBtn = myFragmentView.findViewById(R.id.reminder_add);
        addReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment addReminderDialog = ReminderAddDialog.newInstance();
                ((ReminderAddDialog) addReminderDialog).setReminderDialogInterface(new ReminderAddDialog.ReminderDialogInterface() {
                    @Override
                    public void onAddPressed(Reminder newReminder) {
                        Calendar cal = newReminder.getTime();
                        String dateTimeString = getDateString(cal) + " " + getTimeString(cal);
                        Toast.makeText(getContext(), "Reminder added for " + dateTimeString, Toast.LENGTH_LONG).show();
                        mToolbarButtonCallback.onReminderAdded(newReminder);
                    }
                });
                addReminderDialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "reminderInput");
            }
        });
        ArrayList<Reminder> mReminderArrayList = new ArrayList<>();
        Reminder reminder = new Reminder();
        reminder.setTitle("EAT SUPPER");
        reminder.setAllDay(true);
        reminder.setTime(Calendar.getInstance());
        mReminderArrayList.add(reminder);
        System.out.println(mReminderArrayList.size());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mReminderRecycler.setLayoutManager(linearLayoutManager);
        ReminderListAdapter reminderListAdapter = new ReminderListAdapter(getContext(), mReminderArrayList);
        mReminderRecycler.setAdapter(reminderListAdapter);
        reminderListAdapter.setReminderListAdapterCallback(new ReminderListAdapter.ReminderListAdapterCallback() {
            @Override
            public void onDeletePressed(Reminder reminder) {
                mToolbarButtonCallback.onReminderDelete(reminder);
            }
        });

        return myFragmentView;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_cal_btn :
            {
                mToolbarButtonCallback.onToolbarCalendarClicked();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // START: Need to get reminders from Firebase

    // END

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ReminderFragment.ToolbarCalendarButtonCallback){
            mToolbarButtonCallback = (ReminderFragment.ToolbarCalendarButtonCallback)context;
        }
        else{
            throw new RuntimeException(context.toString() + " must implement listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mToolbarButtonCallback = null;
    }
}
