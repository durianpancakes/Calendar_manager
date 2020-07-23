package com.example.calendarmanagerbeta;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toolbar;

import com.alamkanak.weekview.WeekViewEvent;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NusmodsFragment extends Fragment{
    View myFragmentView;
    private String currentAcadYear = "2020/2021";
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
    private GetModuleClassesListener getModuleClassesListener;
    private ListView moduleList;
    ModuleListAdapter adapter;
    // Temporary holder
    private ArrayList<NUSModuleMain> userModulesAdded = new ArrayList<NUSModuleMain>();
    private ArrayList<NUSModuleLite> databaseUserModules = new ArrayList<NUSModuleLite>();

    public NusmodsFragment() {
        // Required empty public constructor
    }

    public interface GetModuleClassesListener{
        void onClassesObtained(ArrayList<WeekViewEvent> classes);
    }

    public interface removeModuleListener{
        void onModuleRemove(CharSequence moduleCode);
    }

    public interface moduleParamsChangedListener{
        void onParamsChanged(String moduleCode, String lessonType, String classNo, ArrayList<WeekViewEvent> classes);
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
        nusmodsHelper.setOnRefreshFullListener(new onRefreshFullListener() {
            @Override
            public void onRefresh(List<NUSModuleLite> nusModulesLite) {
                nusModuleLiteList = nusModulesLite;
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
            if(moduleCode.equals(databaseUserModules.get(i).getModuleCode())){
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
        semesterData = myFragmentView.findViewById(R.id.current_semester);
        moduleEditText = myFragmentView.findViewById(R.id.module_input);
        addModuleButton = myFragmentView.findViewById(R.id.add_module);
        moduleList = myFragmentView.findViewById(R.id.full_module_list);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Modules");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

        semesterData.setText(getSemesterString());

        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance(getContext());
        firebaseHelper.getAllModules();
        firebaseHelper.setFirebaseCallbackListener(new FirebaseCallback() {
            @Override
            public void onGetModuleSuccess(ArrayList<NUSModuleLite> userModules) {
                databaseUserModules = userModules;
                for(int i = 0; i < databaseUserModules.size(); i++){
                    addModule(databaseUserModules.get(i).getModuleCode());
                }

                if(adapter == null){
                    adapter = new ModuleListAdapter(getActivity(), R.layout.nusmods_list_item, userModulesAdded, databaseUserModules, currentSemester);
                    moduleList.setAdapter(adapter);
                }

                adapter.setOnParamsChangedListener(new onParamsChangedListener() {
                    @Override
                    public void lectureChanged(final String moduleCode, final String lessonType, final String classNo) {
                        getModuleClassesListener = new GetModuleClassesListener() {
                            @Override
                            public void onClassesObtained(ArrayList<WeekViewEvent> classes) {
                                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo, classes);
                            }
                        };
                        getModuleClasses(moduleCode, lessonType, classNo);
                    }

                    @Override
                    public void tutorialChanged(final String moduleCode, final String lessonType, final String classNo) {
                        getModuleClassesListener = new GetModuleClassesListener() {
                            @Override
                            public void onClassesObtained(ArrayList<WeekViewEvent> classes) {
                                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo, classes);
                            }
                        };
                        getModuleClasses(moduleCode, lessonType, classNo);
                    }

                    @Override
                    public void stChanged(final String moduleCode, final String lessonType, final String classNo) {
                        getModuleClassesListener = new GetModuleClassesListener() {
                            @Override
                            public void onClassesObtained(ArrayList<WeekViewEvent> classes) {
                                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo, classes);
                            }
                        };
                        getModuleClasses(moduleCode, lessonType, classNo);
                    }

                    @Override
                    public void recitationChanged(final String moduleCode, final String lessonType, final String classNo) {
                        getModuleClassesListener = new GetModuleClassesListener() {
                            @Override
                            public void onClassesObtained(ArrayList<WeekViewEvent> classes) {
                                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo, classes);
                            }
                        };
                        getModuleClasses(moduleCode, lessonType, classNo);
                    }

                    @Override
                    public void laboratoryChanged(final String moduleCode, final String lessonType, final String classNo) {
                        getModuleClassesListener = new GetModuleClassesListener() {
                            @Override
                            public void onClassesObtained(ArrayList<WeekViewEvent> classes) {
                                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo, classes);
                            }
                        };
                        getModuleClasses(moduleCode, lessonType, classNo);
                    }

                    @Override
                    public void moduleRemoved(String moduleCode) {
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
            }

            @Override
            public void onGetKeyword(ArrayList<String> userKeywords) {
                return;
            }

            @Override
            public void onGetEvents(ArrayList<WeekViewEvent> userEvents) {
                return;
            }

            @Override
            public void onEventDeleted() {

            }
        });

        return myFragmentView;
    }

    private void removeModule(String moduleCode){
        for(int i = 0; i < userModulesAdded.size(); i++){
            System.out.println(userModulesAdded.get(i).getModuleCode());
            if(moduleCode.equals(userModulesAdded.get(i).getModuleCode())){
                NUSModuleMain temp = userModulesAdded.get(i);
                userModulesAdded.remove(i);
                databaseUserModules.remove(i);
                adapter.notifyDataSetChanged();
                moduleList.setAdapter(adapter);
                // Need to remove event instances from Firebase as well
                return;
            }
        }
    }

    private void addModule(String moduleCode){
        final NUSmodsHelper nusmodsHelper = NUSmodsHelper.getInstance(getContext());

        nusmodsHelper.refreshSpecificModule(moduleCode);
        nusmodsHelper.setOnRefreshSpecificListener(new onRefreshSpecificListener() {
            @Override
            public void onRefresh(NUSModuleMain nusModule) {
                NUSModuleLite nusModuleConverted = new NUSModuleLite();

                nusModule = nusmodsHelper.getNusModuleFull();
                if(!isFoundInDatabase(nusModule.getModuleCode())){
                    userModulesAdded.add(nusModule);
                    nusModuleConverted.setModuleCode(nusModule.getModuleCode());
                    databaseUserModules.add(nusModuleConverted);
                    adapter.notifyDataSetChanged();
                } else {
                    // Initialization falls under here.
                    userModulesAdded.add(nusModule);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void getModuleClasses(final String moduleCode, final String lessonType, final String classNo){
        final NUSmodsHelper nusmodsHelper = NUSmodsHelper.getInstance(getContext());

        nusmodsHelper.setOnRefreshSpecificListener(new onRefreshSpecificListener() {
            @Override
            public void onRefresh(NUSModuleMain nusModuleFull) {
                final ArrayList<WeekViewEvent> events = new ArrayList<>();
                List<ModuleTimetable> moduleTimetableList = nusModuleFull.getSemesterData().get(currentSemester).getTimetable();
                System.out.println("TOTAL NUMBER OF CLASSES: " + moduleTimetableList.size());
                for(int i = 0; i < moduleTimetableList.size(); i++){
                    ModuleTimetable moduleTimetable = moduleTimetableList.get(i);
                    System.out.println("USER LESSONTYPE: " + lessonType);
                    System.out.println("USER CLASSNO: " + classNo);
                    System.out.println("CURRENT LESSONTYPE: " + moduleTimetable.getLessonType());
                    System.out.println("CURRENT CLASSNO: " + moduleTimetable.getClassNo());
                    if(lessonType.equals(moduleTimetable.getLessonType()) && classNo.equals(moduleTimetable.getClassNo())){
                        // The class is a match
                        System.out.println("MATCHED");
                        int weeks[] = moduleTimetable.getWeeks();
                        System.out.println("CLASSES WEEKS: " + weeks.length);
                        for(int n = 0; n < weeks.length; n++){
                            WeekViewEvent event = new WeekViewEvent();
                            int actualWeek = getActualWeek(weeks[n]);
                            int startTime = moduleTimetable.getStartTime();
                            int endTime = moduleTimetable.getEndTime();
                            Calendar startCal = Calendar.getInstance();
                            startCal.setWeekDate(2020, actualWeek, getActualDayOfWeek(moduleTimetable.getDay()));
                            startCal.set(Calendar.HOUR_OF_DAY, startTime / 100);
                            startCal.set(Calendar.MINUTE, startTime % 100);
                            event.setStartTime(startCal);
                            Calendar endCal = Calendar.getInstance();
                            endCal.setWeekDate(2020, actualWeek, getActualDayOfWeek(moduleTimetable.getDay()));
                            endCal.set(Calendar.HOUR_OF_DAY, endTime / 100);
                            endCal.set(Calendar.MINUTE, endTime % 100);
                            event.setEndTime(endCal);
                            event.setName(moduleCode + " " + lessonType + " " + classNo);
                            event.setLocation(moduleTimetable.getVenue());
                            event.setDescription(moduleTimetable.getLessonType() + " @ " + moduleTimetable.getVenue());
                            events.add(event);
                            System.out.println("EVENT ADDED");
                        }
                    }
                }
                if(getModuleClassesListener != null){
                    getModuleClassesListener.onClassesObtained(events);
                }
            }
        });
        nusmodsHelper.refreshSpecificModule(moduleCode);
    }

    private int getActualDayOfWeek(String day){
        switch(day){
            case "Monday": return Calendar.MONDAY;
            case "Tuesday": return Calendar.TUESDAY;
            case "Wednesday": return Calendar.WEDNESDAY;
            case "Thursday": return Calendar.THURSDAY;
            case "Friday": return Calendar.FRIDAY;
            case "Saturday": return Calendar.SATURDAY;
            case "Sunday": return Calendar.SUNDAY;
        }
        return 0;
    }

    private int getActualWeek(int acadWeek){
        switch(acadWeek){
            case 1: return 33;
            case 2: return 34;
            case 3: return 35;
            case 4: return 36;
            case 5: return 37;
            case 6: return 38;
            case 7: return 40;
            case 8: return 41;
            case 9: return 42;
            case 10: return 43;
            case 11: return 44;
            case 12: return 45;
            case 13: return 46;
        }
        return 0;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof moduleParamsChangedListener){
            changedModuleParamsListener = (moduleParamsChangedListener)context;
            moduleRemoveListener = (removeModuleListener)context;
        }
        else{
            throw new RuntimeException(context.toString() + " must implement listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        moduleRemoveListener = null;
        changedModuleParamsListener = null;
    }
}