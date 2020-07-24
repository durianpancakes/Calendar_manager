package com.example.calendarmanagerbeta;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class KeywordManagerFragment extends Fragment {
    View myFragmentView;
    Context context;

    public KeywordManagerFragment(Context context){
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_keyword, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Keywords");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        return myFragmentView;
    }
}
