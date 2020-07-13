package com.example.calendarmanagerbeta;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alamkanak.weekview.WeekViewEvent;

import org.w3c.dom.Text;

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


    // TODO: Rename and change types and number of parameters
    public static DisplayEventFragment newInstance(WeekViewEvent event) {
        DisplayEventFragment fragment = new DisplayEventFragment();
        mEvent = event;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_display_event, container, false);
        TextView mEventName = (TextView)myFragmentView.findViewById(R.id.event_display_name);
        TextView mEventAllDay = (TextView)myFragmentView.findViewById(R.id.event_display_all_day);
        TextView mEventDateTime = (TextView)myFragmentView.findViewById(R.id.event_display_datetime);
        TextView mEventLocation = (TextView)myFragmentView.findViewById(R.id.event_display_location);
        TextView mEventRepeat = (TextView)myFragmentView.findViewById(R.id.event_repeat_content);
        TextView mEventReminder = (TextView)myFragmentView.findViewById(R.id.event_reminder_content);
        TextView mEventDescription = (TextView)myFragmentView.findViewById(R.id.event_description_content);


        return myFragmentView;
    }
}