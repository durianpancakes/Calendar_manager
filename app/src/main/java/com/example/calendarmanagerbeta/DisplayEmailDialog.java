package com.example.calendarmanagerbeta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.microsoft.graph.models.extensions.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DisplayEmailDialog extends DialogFragment implements View.OnClickListener{
    private Message mMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.view_email_dialog, container, false);
        TextView mEmailSubject = myFragmentView.findViewById(R.id.email_subject);
        TextView mEmailSender = myFragmentView.findViewById(R.id.email_sender);
        TextView mEmailRecDateTime = myFragmentView.findViewById(R.id.email_received_datetime);
        TextView mEmailWeblink = myFragmentView.findViewById(R.id.email_weblink);
        TextView mEmailBody = myFragmentView.findViewById(R.id.email_body);
        ImageButton close = myFragmentView.findViewById(R.id.email_view_cancel);

        close.setOnClickListener(this);

        Calendar receivedCal = mMessage.receivedDateTime;
        Date receivedDate = receivedCal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");

        mEmailSubject.setText(mMessage.subject);
        mEmailSender.setText(mMessage.sender.emailAddress.name + " <" + mMessage.sender.emailAddress.address + ">");
        mEmailRecDateTime.setText(sdf.format(receivedDate));
        mEmailWeblink.setText(mMessage.webLink);
        mEmailBody.setText(mMessage.body.content);

        return myFragmentView;
    }

    public static DisplayEmailDialog newInstance(Message message){
        DisplayEmailDialog dialog = new DisplayEmailDialog();
        dialog.setMessage(message);

        return dialog;
    }

    private void setMessage(Message message){
        mMessage = message;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.email_view_cancel:
                dismiss();
        }
    }
}
