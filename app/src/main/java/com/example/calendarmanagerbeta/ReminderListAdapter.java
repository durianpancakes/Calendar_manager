package com.example.calendarmanagerbeta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder> {
    class ReminderViewHolder extends RecyclerView.ViewHolder{
        private TextView mReminderTitle;
        private TextView mReminderDateTime;
        private ImageView mReminderDelete;

        public ReminderViewHolder(View itemView){
            super(itemView);
            mReminderTitle = itemView.findViewById(R.id.recycler_reminder_title);
            mReminderDateTime = itemView.findViewById(R.id.recycler_reminder_datetime);
            mReminderDelete = itemView.findViewById(R.id.recycler_reminder_delete);
        }
    }

    public interface ReminderListAdapterCallback{
        void onDeletePressed(Reminder reminder);
    }

    public void setReminderListAdapterCallback(ReminderListAdapterCallback callback){
        this.mCallback = callback;
    }

    private Context mContext;
    private ArrayList<Reminder> mReminderArrayList;
    private ReminderListAdapterCallback mCallback;

    public ReminderListAdapter(Context context, ArrayList<Reminder> arrayList){
        this.mContext = context;
        this.mReminderArrayList = arrayList;
        System.out.println("RLA: " + arrayList.size());
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        System.out.println("ENTERED OCVH");
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReminderViewHolder holder, int position) {
        final Reminder reminder = mReminderArrayList.get(position);
        System.out.println("ENTERED ONBVH");
        Boolean allDay = reminder.getAllDay();
        String titleText = reminder.getTitle();
        Calendar cal = reminder.getTime();
        String dateTimeString;
        if(allDay){
            Date date = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
            dateTimeString = sdf.format(date);
        } else {
            Date date = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy h:mm a");
            dateTimeString = sdf.format(date);
        }

        holder.mReminderTitle.setText(titleText);
        holder.mReminderDateTime.setText(dateTimeString);
        holder.mReminderDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onDeletePressed(reminder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReminderArrayList.size();
    }
}
