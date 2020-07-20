package com.example.calendarmanagerbeta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alamkanak.weekview.WeekViewEvent;
import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.Event;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class EventListAdapter extends ArrayAdapter<WeekViewEvent> {
    private Context mContext;
    private int mResource;
    private ZoneId mLocalTimeZoneId;

    // Used for the ViewHolder pattern
    // https://developer.android.com/training/improving-layouts/smooth-scrolling
    static class ViewHolder {
        TextView subject;
        TextView start;
        TextView end;
        TextView location;
    }

    public EventListAdapter(Context context, int resource, List<WeekViewEvent> events) {
        super(context, resource, events);
        mContext = context;
        mResource = resource;
        mLocalTimeZoneId = TimeZone.getDefault().toZoneId();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeekViewEvent event = getItem(position);

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            holder = new ViewHolder();
            holder.subject = convertView.findViewById(R.id.eventsubject);
            holder.start = convertView.findViewById(R.id.eventstart);
            holder.end = convertView.findViewById(R.id.eventend);
            holder.location = convertView.findViewById(R.id.eventlocation);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.subject.setText(event.getName());

        Calendar startCalendar = event.getStartTime();
        Calendar endCalendar = event.getEndTime();
        Date startDate = startCalendar.getTime();
        Date endDate = endCalendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String startDateString = sdf.format(startDate);
        String endDateString = sdf.format(endDate);

        holder.start.setText(startDateString);
        holder.end.setText(endDateString);
        holder.location.setText(event.getLocation());

        return convertView;
    }

    // Convert Graph's DateTimeTimeZone format to
    // a LocalDateTime, then return a formatted string
    private String getLocalDateTimeString(DateTimeTimeZone dateTime) {
        ZonedDateTime localDateTime = LocalDateTime.parse(dateTime.dateTime)
                .atZone(ZoneId.of(dateTime.timeZone))
                .withZoneSameInstant(mLocalTimeZoneId);

        return String.format("%s %s",
                localDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                localDateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
    }
}