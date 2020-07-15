package com.example.calendarmanagerbeta;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.graph.models.extensions.Message;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayEmailFragment extends Fragment {
    View myFragmentView;
    private static Message mMessage;

    public DisplayEmailFragment() {
        // Required empty public constructor
    }


    public static DisplayEmailFragment newInstance(Message message) {
        DisplayEmailFragment fragment = new DisplayEmailFragment();

        mMessage = message;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_display_email, container, false);
        TextView mEmailSubject = myFragmentView.findViewById(R.id.email_subject);
        TextView mEmailFrom = myFragmentView.findViewById(R.id.email_from);
        TextView mEmailRecDateTime = myFragmentView.findViewById(R.id.email_received_datetime);
        TextView mEmailWeblink = myFragmentView.findViewById(R.id.email_weblink);
        TextView mEmailBody = myFragmentView.findViewById(R.id.email_body);

        mEmailSubject.setText(mMessage.subject);
        mEmailFrom.setText(mMessage.sender.emailAddress.name + " <" + mMessage.sender.emailAddress.address + ">");
        mEmailRecDateTime.setText(mMessage.receivedDateTime.get(Calendar.DAY_OF_MONTH) + "/" + mMessage.receivedDateTime.get(Calendar.MONTH) + "/" + mMessage.receivedDateTime.get(Calendar.YEAR) + " " + mMessage.receivedDateTime.get(Calendar.HOUR_OF_DAY) + ":" + mMessage.receivedDateTime.get(Calendar.MINUTE));
        mEmailWeblink.setText(mMessage.webLink);
        mEmailBody.setText(mMessage.body.content);

        return myFragmentView;
    }
}