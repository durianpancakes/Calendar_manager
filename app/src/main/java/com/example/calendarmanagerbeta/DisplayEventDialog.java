package com.example.calendarmanagerbeta;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DisplayEventDialog extends DialogFragment implements View.OnClickListener{
    private WeekViewEvent mEvent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.view_event_dialog, container, false);
        TextView mEventSubject = myFragmentView.findViewById(R.id.event_subject);
        TextView mEventDateTime = myFragmentView.findViewById(R.id.event_date_time);
        TextView mEventLocationHeader = myFragmentView.findViewById(R.id.event_location);
        TextView mEventLocation = myFragmentView.findViewById(R.id.event_location_body);
        TextView mEventDescriptionHeader = myFragmentView.findViewById(R.id.event_description);
        TextView mEventDescription = myFragmentView.findViewById(R.id.event_description_body);
        TextView mEventWeblinkHeader = myFragmentView.findViewById(R.id.event_weblink);
        TextView mEventWeblink = myFragmentView.findViewById(R.id.event_weblink_body);
        ImageButton close = myFragmentView.findViewById(R.id.event_view_cancel);

        close.setOnClickListener(this);

        Calendar startCal = mEvent.getStartTime();
        Date startDate = startCal.getTime();
        Calendar endCal = mEvent.getEndTime();
        Date endDate = endCal.getTime();

        if(mEvent.isAllDay()){
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            mEventDateTime.setText(sdf.format(startDate) + " - " + sdf.format(endDate));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
            mEventDateTime.setText(sdf.format(startDate) + " - " + sdf.format(endDate));
        }

        mEventSubject.setText(mEvent.getName());

        if(mEvent.getmWeblink() == null){
            mEventWeblinkHeader.setVisibility(View.GONE);
            mEventWeblink.setVisibility(View.GONE);
        } else {
            mEventWeblink.setText(mEvent.getmWeblink());
        }

        if(mEvent.getLocation().equals("")) {
            mEventLocationHeader.setVisibility(View.GONE);
            mEventLocation.setVisibility(View.GONE);
        } else {
            mEventLocation.setText(mEvent.getLocation());
        }

        // mEventReminder and mEventRepeat not implemented yet

        if(mEvent.getDescription() == null) {
            mEventDescriptionHeader.setVisibility(View.GONE);
            mEventDescription.setVisibility(View.GONE);
        } else {
            mEventDescription.setText(mEvent.getDescription());
        }

        return myFragmentView;
    }

    public static DisplayEventDialog newInstance(WeekViewEvent event){
        DisplayEventDialog dialog = new DisplayEventDialog();
        dialog.setEvent(event);

        return dialog;
    }

    public void setEvent(WeekViewEvent event){
        mEvent = event;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id){
            case R.id.event_view_cancel:
                dismiss();
        }
    }
}
