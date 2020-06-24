package com.example.calendarmanagerbeta;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.List;

public class NusmodsFragment extends Fragment {
    View myFragmentView;
    private ProgressBar mProgress = null;
    private List<NUSModuleLite> nusModuleLiteList;
    private String[] moduleDatabase;
    private AutoCompleteTextView moduleEditText;
    private Button addModuleButton;
    private addModuleListener moduleAddListener;

    public NusmodsFragment() {
        // Required empty public constructor
    }

    public interface addModuleListener{
        void onModuleAdd (CharSequence moduleCode);
    }

    private void showProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgress = getActivity().findViewById(R.id.progressbar);
        showProgressBar();

        // Initializing NUSmodsHelper
        final NUSmodsHelper nusmodsHelper = NUSmodsHelper.getInstance(getContext());
        nusmodsHelper.refreshModulesDatabase();

        // Refreshing full module database
        System.out.println("Called1");
        nusmodsHelper.setOnRefreshListener(new onRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("Called");
                nusModuleLiteList = nusmodsHelper.getNusModulesLite();
                moduleDatabase = new String[nusModuleLiteList.size()];
                int i = 0;
                for(NUSModuleLite nusModule : nusModuleLiteList){
                    moduleDatabase[i] = nusModule.getModuleCode() + " " + nusModule.getTitle();
                    i++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, moduleDatabase);
                moduleEditText.setAdapter(adapter);
                hideProgressBar();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_nusmods, container, false);
        moduleEditText = (AutoCompleteTextView)myFragmentView.findViewById(R.id.module_input);
        addModuleButton = (Button)myFragmentView.findViewById(R.id.add_module);
        addModuleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CharSequence input = moduleEditText.getText();
                String inputString = input.toString();
                moduleAddListener.onModuleAdd(input);
            }
        });

        return myFragmentView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof addModuleListener){
            moduleAddListener = (addModuleListener)context;
        }
        else{
            throw new RuntimeException(context.toString() + " must immplement addModuleListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        moduleAddListener = null;
    }
}