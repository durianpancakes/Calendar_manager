package com.example.calendarmanagerbeta;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NusmodsFragment extends Fragment{
    View myFragmentView;
    private String currentAcadYear = "2019/2020";
    private int currentSemester = 1;
    private ProgressBar mProgress = null;
    private List<NUSModuleLite> nusModuleLiteList;
    private ArrayList<String> moduleDatabase;
    private TextView semesterData;
    private AutoCompleteTextView moduleEditText;
    private Button addModuleButton;
    private ImageButton deleteModuleButton;
    private removeModuleListener moduleRemoveListener;
    private moduleParamsChangedListener changedModuleParamsListener;
    private ListView moduleList;
    ModuleListAdapter adapter;
    // Temporary holder
    private ArrayList<NUSModuleMain> userModulesAdded = new ArrayList<NUSModuleMain>();
    private ArrayList<String> databaseUserModules = new ArrayList<String>();

    public NusmodsFragment() {
        // Required empty public constructor
    }

    public interface removeModuleListener{
        void onModuleRemove(CharSequence moduleCode);
    }

    public interface moduleParamsChangedListener{
        void onParamsChanged(String moduleCode, String lessonType, String classNo);
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
        nusmodsHelper.setOnRefreshListener(new onRefreshListener() {
            @Override
            public void onRefresh() {
                nusModuleLiteList = nusmodsHelper.getNusModulesLite();
                moduleDatabase = new ArrayList<String>();
                int i = 0;
                for(NUSModuleLite nusModule : nusModuleLiteList){
                    if(isInCurrentSemester(nusModule)) {
                        moduleDatabase.add(nusModule.getModuleCode() + " " + nusModule.getTitle());
                        i++;
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, moduleDatabase);
                moduleEditText.setAdapter(adapter);

                for(i = 0; i < databaseUserModules.size(); i++){
                    addModule(databaseUserModules.get(i));
                }

                hideProgressBar();
            }
        });
    }

    public boolean isInCurrentSemester(NUSModuleLite nusModule){
        for(int i = 0; i < nusModule.getSemesters().size(); i++){
            if(currentSemester == nusModule.getSemesters().get(i)){
                return true;
            }
        }
        return false;
    }

    public String getSemesterString(){
        String semesterString = null;

        switch(currentSemester){
            case 1: semesterString = "Semester 1"; break;
            case 2: semesterString = "Semester 2"; break;
            case 3: semesterString = "Special Term 1"; break;
            case 4: semesterString = "Special Term 2"; break;
        }

        return "AY" + currentAcadYear + ", " + semesterString;
    }

    public boolean isFoundInDatabase(String moduleCode){
        for(int i = 0; i < databaseUserModules.size(); i++){
            if(moduleCode.equals(databaseUserModules.get(i))){
                return true;
            }
        }
        return false;
    }

    public boolean isValidModule(String moduleCode){
        for(int i = 0; i < nusModuleLiteList.size(); i++){
            if(moduleCode.equals(nusModuleLiteList.get(i).getModuleCode())){
                return true;
            }
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_nusmods, container, false);
        semesterData = (TextView)myFragmentView.findViewById(R.id.current_semester);
        moduleEditText = (AutoCompleteTextView)myFragmentView.findViewById(R.id.module_input);
        addModuleButton = (Button)myFragmentView.findViewById(R.id.add_module);
        moduleList = (ListView)myFragmentView.findViewById(R.id.full_module_list);

        semesterData.setText(getSemesterString());

        if(adapter == null){
            adapter = new ModuleListAdapter(getActivity(), R.layout.nusmods_list_item, userModulesAdded, currentSemester);
            moduleList.setAdapter(adapter);
        }

        adapter.setOnParamsChangedListener(new onParamsChangedListener() {
            @Override
            public void lectureChanged(String moduleCode, String lessonType, String classNo) {
                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo);
            }

            @Override
            public void tutorialChanged(String moduleCode, String lessonType, String classNo) {
                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo);
            }

            @Override
            public void stChanged(String moduleCode, String lessonType, String classNo) {
                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo);
            }

            @Override
            public void recitationChanged(String moduleCode, String lessonType, String classNo) {
                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo);
            }

            @Override
            public void moduleRemoved(String moduleCode) {
                System.out.println("moduleremoved");
                removeModule(moduleCode);
                moduleRemoveListener.onModuleRemove(moduleCode);
            }
        });

        addModuleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CharSequence input = moduleEditText.getText();
                String inputString = input.toString();
                String[] moduleData = inputString.split("\\s+");
                String moduleCode = moduleData[0];

                if(isValidModule(moduleCode)){
                    if(isFoundInDatabase(moduleCode) == true){
                        Toast.makeText(getActivity(), "Module already exists", Toast.LENGTH_LONG).show();
                        moduleEditText.setText("");
                    } else {
                        addModule(moduleCode);
                        moduleEditText.setText("");
                        Toast.makeText(getActivity(), "Module successfully added", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Invalid module code", Toast.LENGTH_LONG).show();
                }
            }
        });

        return myFragmentView;
    }

    private void removeModule(String moduleCode){
        System.out.println("entered");
        for(int i = 0; i < userModulesAdded.size(); i++){
            System.out.println(userModulesAdded.get(i).getModuleCode());
            if(moduleCode.equals(userModulesAdded.get(i).getModuleCode())){
                System.out.println("found");
                userModulesAdded.remove(i);
                databaseUserModules.remove(i);
                adapter.notifyDataSetChanged();
                return;
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void addModule(String moduleCode){
        final NUSmodsHelper nusmodsHelper = NUSmodsHelper.getInstance(getContext());

        nusmodsHelper.refreshSpecificModule(moduleCode);
        nusmodsHelper.setOnRefreshListener(new onRefreshListener() {
            @Override
            public void onRefresh() {
                NUSModuleMain nusModule;
                nusModule = nusmodsHelper.getNusModuleFull();
                userModulesAdded.add(nusModule);
                if(isFoundInDatabase(nusModule.getModuleCode()) == false){
                    databaseUserModules.add(nusModule.getModuleCode());
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof moduleParamsChangedListener){
            changedModuleParamsListener = (moduleParamsChangedListener)context;
            moduleRemoveListener = (removeModuleListener)context;
        }
        else{
            throw new RuntimeException(context.toString() + " must implement addModuleListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        moduleRemoveListener = null;
        changedModuleParamsListener = null;
    }
}