package com.example.calendarmanagerbeta;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alamkanak.weekview.WeekViewEvent;
import com.microsoft.graph.models.extensions.Message;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayEventFragment extends Fragment {
    View myFragmentView;
    private static WeekViewEvent mEvent;

    public DisplayEventFragment() {
        // Required empty public constructor
    }


    public static DisplayEventFragment newInstance(WeekViewEvent event) {
        DisplayEventFragment fragment = new DisplayEventFragment();
        mEvent = event;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("View Event");
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle("");
//        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setHomeButtonEnabled(true);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressed();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }

        return true;
    }

    private void onBackPressed(){
        assert getFragmentManager() != null;
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_display_event, container, false);
        TextView mEventName = myFragmentView.findViewById(R.id.event_display_name);
        TextView mEventAllDay = myFragmentView.findViewById(R.id.event_display_all_day);
        TextView mEventDateTime = myFragmentView.findViewById(R.id.event_display_datetime);
        TextView mEventLocation = myFragmentView.findViewById(R.id.event_display_location);
        TextView mEventRepeat = myFragmentView.findViewById(R.id.event_repeat_content);
        TextView mEventReminder = myFragmentView.findViewById(R.id.event_reminder_content);
        TextView mEventDescriptionHeader = myFragmentView.findViewById(R.id.event_description);
        TextView mEventDescription = myFragmentView.findViewById(R.id.event_description_content);

        Calendar startCal = mEvent.getStartTime();
        Date startDate = startCal.getTime();
        Calendar endCal = mEvent.getEndTime();
        Date endDate = endCal.getTime();

        mEventName.setText(mEvent.getName());

        if(mEvent.isAllDay()){
            mEventAllDay.setVisibility(View.VISIBLE);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            mEventDateTime.setText(sdf.format(startDate) + " - " + sdf.format(endDate));
        } else {
            mEventAllDay.setVisibility(View.GONE);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
            mEventDateTime.setText(sdf.format(startDate) + " - " + sdf.format(endDate));
        }

        if(mEvent.getLocation() != null) {
            mEventLocation.setText(mEvent.getLocation());
        } else {
            mEventLocation.setVisibility(View.GONE);
        }

        // mEventReminder and mEventRepeat not implemented yet

        if(mEvent.getDescription() != null) {
            mEventDescription.setText(mEvent.getDescription());
        } else {
            mEventDescriptionHeader.setVisibility(View.GONE);
            mEventDescription.setVisibility(View.GONE);
        }

        return myFragmentView;
    }
}