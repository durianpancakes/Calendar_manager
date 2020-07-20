package com.example.calendarmanagerbeta;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeEventRecyclerViewAdapter extends RecyclerView.Adapter<HomeEventRecyclerViewAdapter.EventViewHolder> {
    class EventViewHolder extends RecyclerView.ViewHolder{
        private TextView mEventTitle;
        private TextView mEventDate;
        private TextView mEventTime;
        private TextView mEventLocation;
        private TextView mDescription;
        private Button mArrowBtn;
        private ConstraintLayout mMainView;
        private ConstraintLayout mExpandableView;

        public EventViewHolder(View itemView){
            super(itemView);
            mEventTitle = itemView.findViewById(R.id.event_card_title);
            mEventDate = itemView.findViewById(R.id.event_card_date);
            mEventTime = itemView.findViewById(R.id.event_card_time);
            mEventLocation = itemView.findViewById(R.id.event_card_location);
            mDescription = itemView.findViewById(R.id.event_expandable_view_description_body);
            mArrowBtn = itemView.findViewById(R.id.event_arrow_btn);
            mMainView = itemView.findViewById(R.id.event_card_main);
            mExpandableView = itemView.findViewById(R.id.event_expandable_view);
        }
    }

    private Context context;
    private ArrayList<WeekViewEvent> arrayList;

    public HomeEventRecyclerViewAdapter(Context context, ArrayList<WeekViewEvent> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_event_row_layout, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position) {
        WeekViewEvent event = arrayList.get(position);

        holder.mEventTitle.setText(event.getName());

        Date startDate = event.getStartTime().getTime();
        Date endDate = event.getEndTime().getTime();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
        String startDateString = sdf1.format(startDate);
        String startTimeString = sdf2.format(startDate);
        String endDateString = sdf1.format(endDate);
        String endTimeString = sdf2.format(endDate);
        if (startDateString.equals(endDateString) && event.isAllDay()) {
            holder.mEventDate.setText(startDateString);
        } else {
            if(!event.isAllDay()){
                holder.mEventDate.setText(startDateString);
                holder.mEventTime.setText(startTimeString + " - " + endTimeString);
            } else {
                holder.mEventDate.setText(startDateString + " - " + endDateString);
                holder.mEventTime.setText(startTimeString + " - " + endTimeString);
            }
        }

        holder.mEventLocation.setText(event.getLocation());
        holder.mDescription.setText(event.getDescription());

        holder.mArrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.mExpandableView.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(holder.mMainView, new AutoTransition());
                    holder.mExpandableView.setVisibility(View.VISIBLE);
                    holder.mArrowBtn.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                } else {
                    TransitionManager.beginDelayedTransition(holder.mMainView, new AutoTransition());
                    holder.mExpandableView.setVisibility(View.GONE);
                    holder.mArrowBtn.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return arrayList.size();
    }
}

