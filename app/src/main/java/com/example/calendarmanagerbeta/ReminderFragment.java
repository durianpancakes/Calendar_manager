package com.example.calendarmanagerbeta;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ReminderFragment extends Fragment {
    private View myFragmentView;
    private ReminderFragment.ToolbarCalendarButtonCallback mToolbarButtonCallback;
    private ProgressBar mProgress;

    public interface ToolbarCalendarButtonCallback{
        void onToolbarCalendarClicked();
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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Reminders");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        return myFragmentView;
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
