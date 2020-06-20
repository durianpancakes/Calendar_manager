package com.example.calendarmanagerbeta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.microsoft.graph.models.extensions.Event;

import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

public class mailEventAdapter extends ArrayAdapter<mailEvent> {  //change ?
    private Context mContext;
    private int mResource;
    private ZoneId mLocalTimeZoneId;



    static class ViewHolder {
        TextView subject;
        TextView body;
        TextView webLink;
    }

    // changed halfway
    public mailEventAdapter(Context context, int resource, List<mailEvent> mailEvents) {
        super(context, resource, mailEvents);

        //??
        mContext = context;
        mResource = resource;
        mLocalTimeZoneId = TimeZone.getDefault().toZoneId();
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);  //idk how to sub this out

        mailEventAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            holder = new mailEventAdapter.ViewHolder();
            holder.subject = convertView.findViewById(R.id.eventsubject);
            holder.body = convertView.findViewById(R.id.eventbody);
            holder.webLink = convertView.findViewById(R.id.eventwebLink);
            //holder.end = convertView.findViewById(R.id.eventend);

            convertView.setTag(holder);
        } else {
            holder = (mailEventAdapter.ViewHolder) convertView.getTag();
        }


        //
        holder.subject.setText(event.subject);
        holder.organizer.setText(event.organizer.emailAddress.name);
        holder.start.setText(getLocalDateTimeString(event.start));
        holder.end.setText(getLocalDateTimeString(event.end));

        return convertView;
    }




}
