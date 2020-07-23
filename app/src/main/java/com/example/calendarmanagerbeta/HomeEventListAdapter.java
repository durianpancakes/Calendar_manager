package com.example.calendarmanagerbeta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alamkanak.weekview.WeekViewEvent;
import com.microsoft.graph.models.extensions.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeEventListAdapter extends RecyclerView.Adapter<HomeEventListAdapter.EventHolder> {
    private Context context;
    private ArrayList<WeekViewEvent> eventArrayList;

    public HomeEventListAdapter(Context context, ArrayList<WeekViewEvent> eventArrayList){
        this.context = context;
        this.eventArrayList = eventArrayList;
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new HomeEventListAdapter.EventHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
        WeekViewEvent event = eventArrayList.get(position);

        holder.mSubject.setText(event.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
        Calendar startCal = event.getStartTime();
        Date startDate = startCal.getTime();
        holder.mStart.setText(sdf.format(startDate));
        Calendar endCal = event.getEndTime();
        Date endDate = endCal.getTime();
        holder.mEnd.setText(sdf.format(endDate));
        holder.mLocation.setText(event.getLocation());
    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder{
        private TextView mSubject;
        private TextView mStart;
        private TextView mEnd;
        private TextView mLocation;

        public EventHolder(View itemView){
            super(itemView);
            mSubject = itemView.findViewById(R.id.eventsubject);
            mStart = itemView.findViewById(R.id.eventstart);
            mEnd = itemView.findViewById(R.id.eventend);
            mLocation = itemView.findViewById(R.id.eventlocation);
        }
    }


}
