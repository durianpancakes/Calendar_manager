package com.example.calendarmanagerbeta;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.Message;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class EmailListAdapter extends ArrayAdapter<Message> {
    private Context mContext;
    private int mResource;
    private ZoneId mLocalTimeZoneId;

    static class ViewHolder {
        TextView subject;
        TextView sender;
        TextView bodyPreview;
    }

    public EmailListAdapter(Context context, int resource, ArrayList<Message> messages) {
        super(context, resource, messages);
        mContext = context;
        mResource = resource;
        mLocalTimeZoneId = TimeZone.getDefault().toZoneId();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);

        EmailListAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            holder = new EmailListAdapter.ViewHolder();
            holder.subject = convertView.findViewById(R.id.messagesubject);
            holder.sender = convertView.findViewById(R.id.messagesender);
            holder.bodyPreview = convertView.findViewById(R.id.messagebodypreview);

            convertView.setTag(holder);
        } else {
            holder = (EmailListAdapter.ViewHolder) convertView.getTag();
        }

        holder.subject.setText(message.subject);
        if(!message.isRead){
            holder.subject.setTypeface(Typeface.DEFAULT_BOLD);
        }
        holder.sender.setText(message.sender.emailAddress.name + " <" + message.sender.emailAddress.address + ">");
        holder.bodyPreview.setText(message.bodyPreview);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return convertView;
    }
}
