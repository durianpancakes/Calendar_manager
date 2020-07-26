package com.example.calendarmanagerbeta;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
    private removeModuleListener moduleRemoveListener;
    private moduleParamsChangedListener changedModuleParamsListener;
    private GetExamListener getExamListener;
    private GetLectureListener getLectureListener;
    private GetTutorialListener getTutorialListener;
    private GetLaboratoryListener getLaboratoryListener;
    private GetRecitationListener getRecitationListener;
    private GetSTListener getSTListener;
    private ListView moduleList;
    ModuleListAdapter adapter;
    // Temporary holder
    private ArrayList<NUSModuleMain> userModulesAdded = new ArrayList<NUSModuleMain>();
    private ArrayList<NUSModuleLite> databaseUserModules = new ArrayList<NUSModuleLite>();

    public NusmodsFragment() {
        // Required empty public constructor
    }

    public interface GetLectureListener{
        void onLectureObtained(String moduleCode, String lessonType, String classNo, ArrayList<WeekViewEvent> events);
    }

    public interface GetTutorialListener{
        void onTutorialObtained(String moduleCode, String lessonType, String classNo, ArrayList<WeekViewEvent> events);
    }

    public interface GetLaboratoryListener{
        void onLaboratoryObtained(String moduleCode, String lessonType, String classNo, ArrayList<WeekViewEvent> events);
    }

    public interface GetSTListener{
        void onSTObtained(String moduleCode, String lessonType, String classNo, ArrayList<WeekViewEvent> events);
    }

    public interface GetRecitationListener{
        void onRecitationObtained(String moduleCode, String lessonType, String classNo, ArrayList<WeekViewEvent> events);
    }

    public interface GetExamListener{
        void onExamObtained(String moduleCode, String lessonType, String classNo, ArrayList<WeekViewEvent> events);
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

        setHasOptionsMenu(true);

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

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.toolbar_cal_btn).setVisible(false);
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
        addModuleButton = myFragmentView.findViewById(R.id.add_keyword);
        moduleList = myFragmentView.findViewById(R.id.keywords_listview);

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
                        System.out.println("LECTURE CHANGED" + " " + moduleCode + " " + lessonType + " " + classNo);
                        getLectureListener = new GetLectureListener() {
                            @Override
                            public void onLectureObtained(String moduleCode, String lessonType, String classNo, ArrayList<WeekViewEvent> events) {
                                System.out.println("LECTURE CHANGED1" + " " + moduleCode + " " + lessonType + " " + classNo);
                                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo, events);
                            }
                        };

                        getModuleClasses(moduleCode, lessonType, classNo);
                    }

                    @Override
                    public void tutorialChanged(final String moduleCode, final String lessonType, final String classNo) {
                        System.out.println("TUTORIAL CHANGED" + " " + moduleCode + " " + lessonType + " " + classNo);
                        getTutorialListener = new GetTutorialListener() {
                            @Override
                            public void onTutorialObtained(String moduleCode, String lessonType, String classNo, ArrayList<WeekViewEvent> events) {
                                System.out.println("TUTORIAL CHANGED1" + " " + moduleCode + " " + lessonType + " " + classNo);
                                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo, events);
                            }
                        };

                        getModuleClasses(moduleCode, lessonType, classNo);
                    }

                    @Override
                    public void stChanged(final String moduleCode, final String lessonType, final String classNo) {
                        System.out.println("ST CHANGED" + " " + moduleCode + " " + lessonType + " " + classNo);
                        getSTListener = new GetSTListener() {
                            @Override
                            public void onSTObtained(String moduleCode, String lessonType, String classNo, ArrayList<WeekViewEvent> events) {
                                System.out.println("ST CHANGED1" + " " + moduleCode + " " + lessonType + " " + classNo);
                                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo, events);
                            }
                        };

                        getModuleClasses(moduleCode, lessonType, classNo);
                    }

                    @Override
                    public void recitationChanged(final String moduleCode, final String lessonType, final String classNo) {
                        System.out.println("RECITATION CHANGED" + " " + moduleCode + " " + lessonType + " " + classNo);
                        getRecitationListener = new GetRecitationListener() {
                            @Override
                            public void onRecitationObtained(String moduleCode, String lessonType, String classNo, ArrayList<WeekViewEvent> events) {
                                System.out.println("RECITATION CHANGED1" + " " + moduleCode + " " + lessonType + " " + classNo);
                                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo, events);
                            }
                        };

                        getModuleClasses(moduleCode, lessonType, classNo);
                    }

                    @Override
                    public void laboratoryChanged(final String moduleCode, final String lessonType, final String classNo) {
                        System.out.println("LAB CHANGED" + " " + moduleCode + " " + lessonType + " " + classNo);
                        getLaboratoryListener = new GetLaboratoryListener() {
                            @Override
                            public void onLaboratoryObtained(String moduleCode, String lessonType, String classNo, ArrayList<WeekViewEvent> events) {
                                System.out.println("LAB CHANGED1" + " " + moduleCode + " " + lessonType + " " + classNo);
                                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo, events);
                            }
                        };
                        getModuleClasses(moduleCode, lessonType, classNo);
                    }

                    @Override
                    public void examObtained(final String moduleCode, final String lessonType, final String classNo) {
                        System.out.println("EXAM OBTAINED" + " " + moduleCode + " " + lessonType + " " + classNo);
                        getExamListener = new GetExamListener() {
                            @Override
                            public void onExamObtained(String moduleCode, String lessonType, String classNo, ArrayList<WeekViewEvent> events) {
                                System.out.println("EXAM CHANGED1" + " " + moduleCode + " " + lessonType + " " + classNo);
                                changedModuleParamsListener.onParamsChanged(moduleCode, lessonType, classNo, events);
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

            @Override
            public void onKeywordDeleted() {

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
            public void onRefresh(String moduleCode, NUSModuleMain nusModule) {
                NUSModuleLite nusModuleConverted = new NUSModuleLite();

                nusModule = nusmodsHelper.getNusModuleFull();
                if(!isFoundInDatabase(moduleCode)){
                    userModulesAdded.add(nusModule);
                    nusModuleConverted.setModuleCode(moduleCode);
                    databaseUserModules.add(nusModuleConverted);
                    adapter.notifyDataSetChanged();
                } else {
                    // Initialization falls under here.
                    userModulesAdded.add(nusModule);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onRefreshSpecial(String moduleCode, NUSModuleMain nusModuleFull, String lessonType, String classNo) {

            }
        });
    }

    private void getModuleClasses(final String moduleCode, final String lessonType, final String classNo){
        final NUSmodsHelper nusmodsHelper = NUSmodsHelper.getInstance(getContext());
        System.out.println("Get Module Classes: " + moduleCode + " " + lessonType + " " + classNo);

        nusmodsHelper.setOnRefreshSpecificListener(new onRefreshSpecificListener() {
            @Override
            public void onRefresh(String moduleCode, NUSModuleMain nusModuleFull) {

            }

            @Override
            public void onRefreshSpecial(String moduleCodeReceived, NUSModuleMain nusModuleFull, String receivedlessonType, String receivedclassNo) {
                final ArrayList<WeekViewEvent> events = new ArrayList<>();
                ModuleSemesterData moduleSemesterData = nusModuleFull.getSemesterData().get(currentSemester - 1);

                if(receivedlessonType.equals("Exam")){
                    if(moduleSemesterData.getExamDate() != null){
                        // There is an exam
                        Calendar startCal = parseDate(moduleSemesterData.getExamDate());

                        // Exam duration in minutes
                        int examDuration = moduleSemesterData.getExamDuration();

                        Calendar endCal = startCal;
                        endCal.add(Calendar.MINUTE, examDuration);

                        WeekViewEvent event = new WeekViewEvent();
                        event.setName(moduleCodeReceived + " Finals");
                        event.setStartTime(startCal);
                        event.setEndTime(endCal);
                        events.add(event);

                        if(getExamListener!= null){
                            System.out.println("EXAM LISTENER FIRED");
                            getExamListener.onExamObtained(moduleCodeReceived, "Exam", "Finals", events);
                        }
                    }
                } else {
                    List<ModuleTimetable> moduleTimetableList = moduleSemesterData.getTimetable();
                    for(int i = 0; i < moduleTimetableList.size(); i++){
                        ModuleTimetable moduleTimetable = moduleTimetableList.get(i);
                        if(receivedlessonType.equals(moduleTimetable.getLessonType()) && receivedclassNo.equals(moduleTimetable.getClassNo())){
                            // The class is a match
                            System.out.println(receivedlessonType + " " + receivedclassNo);
                            int weeks[] = moduleTimetable.getWeeks();
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
                                event.setName(moduleCodeReceived + " " + lessonType + " " + classNo);
                                event.setLocation(moduleTimetable.getVenue());
                                event.setDescription(moduleTimetable.getLessonType() + " @ " + moduleTimetable.getVenue());
                                events.add(event);
                                System.out.println("EVENT ADDED");
                            }

                            switch(moduleTimetable.getLessonType()){
                                case "Lecture":
                                    System.out.println("LECTURE LISTENER FIRED");
                                    if(getLectureListener != null){
                                        getLectureListener.onLectureObtained(moduleCodeReceived, receivedlessonType, receivedclassNo, events); break;
                                    }
                                case "Laboratory":
                                    System.out.println("LAB LISTENER FIRED");
                                    if(getLaboratoryListener != null){
                                        getLaboratoryListener.onLaboratoryObtained(moduleCodeReceived, receivedlessonType, receivedclassNo, events);
                                    } break;

                                case "Sectional Teaching":
                                    System.out.println("ST LISTENER FIRED");
                                    if(getSTListener != null){
                                        getSTListener.onSTObtained(moduleCodeReceived, receivedlessonType, receivedclassNo, events);
                                    } break;
                                case "Recitation":
                                    System.out.println("RECITATION LISTENER FIRED");
                                    if(getRecitationListener != null){
                                        getRecitationListener.onRecitationObtained(moduleCodeReceived, receivedlessonType, receivedclassNo, events);
                                    } break;
                                case "Tutorial":
                                    System.out.println("TUTORIAL LISTENER FIRED");
                                    if(getTutorialListener != null){
                                        getTutorialListener.onTutorialObtained(moduleCodeReceived, receivedlessonType, receivedclassNo, events);
                                    } break;
                            }

                        }
                    }
                }
            }
        });
        nusmodsHelper.refreshSpecificModuleSpecial(moduleCode, lessonType, classNo);
    }

    private Calendar parseDate(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar cal = Calendar.getInstance();
        try{
            Date date = sdf.parse(dateString);
            cal.setTime(date);
        } catch (ParseException e){
            e.printStackTrace();
        }


        return cal;
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