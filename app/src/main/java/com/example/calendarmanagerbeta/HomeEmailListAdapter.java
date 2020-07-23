package com.example.calendarmanagerbeta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.microsoft.graph.models.extensions.Message;

import java.util.ArrayList;

public class HomeEmailListAdapter extends RecyclerView.Adapter<HomeEmailListAdapter.EmailHolder> {
    private Context context;
    private ArrayList<Message> messagesArrayList;

    class EmailHolder extends RecyclerView.ViewHolder{
        private TextView mSubject;
        private TextView mSender;
        private TextView mBodyPreview;

        public EmailHolder(View itemView){
            super(itemView);
            mSubject = itemView.findViewById(R.id.messagesubject);
            mSender = itemView.findViewById(R.id.messagesender);
            mBodyPreview = itemView.findViewById(R.id.messagebodypreview);
        }
    }

    public HomeEmailListAdapter(Context context, ArrayList<Message> messageArrayList){
        this.context = context;
        this.messagesArrayList = messageArrayList;
    }

    @NonNull
    @Override
    public EmailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.email_list_item, parent, false);
        return new HomeEmailListAdapter.EmailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailHolder holder, int position) {
        Message message = messagesArrayList.get(position);

        holder.mSubject.setText(message.subject);
        holder.mSender.setText(message.sender.emailAddress.name + " <" + message.sender.emailAddress.address + ">");
        holder.mBodyPreview.setText(message.bodyPreview);
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }
}
