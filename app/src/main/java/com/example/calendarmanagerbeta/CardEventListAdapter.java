package com.example.calendarmanagerbeta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CardEventListAdapter extends RecyclerView.Adapter<CardEventListAdapter.EventViewHolder> {
    class EventViewHolder extends RecyclerView.ViewHolder{
        private TextView mEventSubject;
        private TextView mEventFrom;
        private TextView mEventTo;
        private TextView mEventLocation;

        public EventViewHolder(View itemView){
            super(itemView);
            mEventSubject = itemView.findViewById(R.id.eventsubject);
            mEventFrom = itemView.findViewById(R.id.eventstart);
            mEventTo = itemView.findViewById(R.id.eventend);
            mEventLocation = itemView.findViewById(R.id.eventlocation);
        }
    }

    private Context mContext;
    private ArrayList<WeekViewEvent> arrayList;

    public CardEventListAdapter(Context context, ArrayList<WeekViewEvent> arrayList){
        this.mContext = context;
        this.arrayList = arrayList;
    }

    @Override
    public CardEventListAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new CardEventListAdapter.EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardEventListAdapter.EventViewHolder holder, int position){
        WeekViewEvent event = arrayList.get(position);
        String eventName = event.getName();
        Calendar startCal = event.getStartTime();
        Date startDate = startCal.getTime();
        Calendar endCal = event.getEndTime();
        Date endDate = endCal.getTime();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String eventStartString = sdf1.format(startDate);
        String eventEndString = sdf1.format(endDate);
        String eventLocation = event.getLocation();
        Boolean eventAllDay = event.isAllDay();

        holder.mEventSubject.setText(eventName);
        if(eventAllDay){
            holder.mEventFrom.setText(sdf2.format(startDate));
            holder.mEventTo.setText(sdf2.format(endDate));
        } else {
            holder.mEventFrom.setText(eventStartString);
            holder.mEventTo.setText(eventEndString);
        }
        holder.mEventLocation.setText(eventLocation);
    }

    @Override
    public int getItemCount(){
        return arrayList.size();
    }
}
