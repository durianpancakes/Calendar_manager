package com.example.calendarmanagerbeta;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeKeywordRecyclerViewAdapter extends RecyclerView.Adapter<HomeKeywordRecyclerViewAdapter.KeywordViewHolder>{
    class KeywordViewHolder extends RecyclerView.ViewHolder{
        private TextView mKeyword;
        private TextView mNumDeltaEmails;
        private TextView mNumDeltaEvents;
        private Button mEmailArrowBtn;
        private Button mEventArrowBtn;
        private ConstraintLayout mMainView;
        private ConstraintLayout mExpandedEmailView;
        private ConstraintLayout mExpandedEventView;
        private RecyclerView mExpandedEmailsRecycler;
        private RecyclerView mExpandedEventsRecycler;

        public KeywordViewHolder(View itemView){
            super(itemView);
            mKeyword = itemView.findViewById(R.id.keyword_card_title);
            mNumDeltaEmails = itemView.findViewById(R.id.keyword_num_email);
            mNumDeltaEvents = itemView.findViewById(R.id.keyword_num_event);
            mEmailArrowBtn = itemView.findViewById(R.id.keyword_email_btn);
            mEventArrowBtn = itemView.findViewById(R.id.keyword_event_btn);
            mMainView = itemView.findViewById(R.id.keyword_card_main);
            mExpandedEmailView = itemView.findViewById(R.id.keyword_email_expandable_view);
            mExpandedEventView = itemView.findViewById(R.id.keyword_event_expandable_view);
            mExpandedEmailsRecycler = itemView.findViewById(R.id.keyword_email_expandable_body);
            mExpandedEventsRecycler = itemView.findViewById(R.id.keyword_event_expandable_body);
        }
    }

    private Context context;
    private ArrayList<KeywordInfo> arrayList;

    public HomeKeywordRecyclerViewAdapter(Context context, ArrayList<KeywordInfo> arrayList){
        this.context = context;
        this.arrayList = arrayList;
        Collections.sort(this.arrayList);
    }

    @Override
    public HomeKeywordRecyclerViewAdapter.KeywordViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_keyword_row_layout, parent, false);
        return new HomeKeywordRecyclerViewAdapter.KeywordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HomeKeywordRecyclerViewAdapter.KeywordViewHolder holder, int position) {
        KeywordInfo keyword = arrayList.get(position);

        holder.mKeyword.setText(keyword.getmKeyword());

        int numOfDeltaEmails = keyword.getmNumOfDeltaEmails();
        int numOfDeltaEvents = keyword.getmNumOfEventsAdded();
        if(numOfDeltaEmails == 0){
            holder.mEmailArrowBtn.setVisibility(View.GONE);
            holder.mNumDeltaEmails.setText("You have no new emails");
        } else {
            if(numOfDeltaEmails == 1){
                holder.mEmailArrowBtn.setVisibility(View.VISIBLE);
                holder.mNumDeltaEmails.setText("You have " + numOfDeltaEmails + " new email");
            } else {
                holder.mEmailArrowBtn.setVisibility(View.VISIBLE);
                holder.mNumDeltaEmails.setText("You have " + numOfDeltaEmails + " new emails");
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.mExpandedEmailsRecycler.setLayoutManager(linearLayoutManager);
            HomeEmailListAdapter adapter = new HomeEmailListAdapter(context, keyword.getmDeltaMessages());
            holder.mExpandedEmailsRecycler.setAdapter(adapter);
            holder.mEmailArrowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.mExpandedEmailView.getVisibility() == View.GONE){
                        TransitionManager.beginDelayedTransition(holder.mMainView, new AutoTransition());
                        holder.mExpandedEmailView.setVisibility(View.VISIBLE);
                        holder.mEmailArrowBtn.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    } else {
                        holder.mExpandedEmailView.setVisibility(View.GONE);
                        TransitionManager.beginDelayedTransition(holder.mMainView, new AutoTransition());
                        holder.mEmailArrowBtn.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    }
                }
            });
        }
        if(numOfDeltaEvents == 0){
            holder.mEventArrowBtn.setVisibility(View.GONE);
            holder.mNumDeltaEvents.setText("You have no new events");
        } else {
            if(numOfDeltaEmails == 1){
                holder.mEventArrowBtn.setVisibility(View.VISIBLE);
                holder.mNumDeltaEvents.setText("You have " + numOfDeltaEvents + " new event");
            } else {
                holder.mEventArrowBtn.setVisibility(View.VISIBLE);
                holder.mNumDeltaEvents.setText("You have " + numOfDeltaEvents + " new events");
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.mExpandedEventsRecycler.setLayoutManager(linearLayoutManager);
            HomeEventListAdapter adapter = new HomeEventListAdapter(context, keyword.getmDeltaEventsAdded());
            holder.mExpandedEventsRecycler.setAdapter(adapter);
            holder.mEventArrowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.mExpandedEventView.getVisibility() == View.GONE){
                        TransitionManager.beginDelayedTransition(holder.mMainView, new AutoTransition());
                        holder.mExpandedEventView.setVisibility(View.VISIBLE);
                        holder.mEventArrowBtn.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    } else {
                        holder.mExpandedEventView.setVisibility(View.GONE);
                        TransitionManager.beginDelayedTransition(holder.mMainView, new AutoTransition());
                        holder.mEventArrowBtn.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return arrayList.size();
    }
}
