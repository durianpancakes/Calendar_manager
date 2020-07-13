package com.example.calendarmanagerbeta;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayEmailFragment extends Fragment {
    View myFragmentView;

    public DisplayEmailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DisplayEmailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayEmailFragment newInstance(String param1, String param2) {
        DisplayEmailFragment fragment = new DisplayEmailFragment();
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

        return myFragmentView;
    }
}