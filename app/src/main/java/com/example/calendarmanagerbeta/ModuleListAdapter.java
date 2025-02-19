package com.example.calendarmanagerbeta;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ModuleListAdapter extends ArrayAdapter<NUSModuleMain> {
    private Context mContext;
    private int mResource;
    private List<NUSModuleLite> mUserDatabase;
    private int mSemester;
    private onParamsChangedListener mParamsChangedListener;
    String lectureChosenDB;
    String tutorialChosenDB;
    String stChosenDB;
    String recitationChosenDB;
    String laboratoryChosenDB;

    public ModuleListAdapter(Context context, int resource, List<NUSModuleMain> userModules, List<NUSModuleLite> databaseUserModules, int semester) {
        super(context, resource, userModules);
        this.mContext = context;
        this.mResource = resource;
        this.mUserDatabase = databaseUserModules;
        this.mSemester = semester - 1; // Compensate for the index of ArrayList
    }

    public void setOnParamsChangedListener(onParamsChangedListener paramsChangedListener){
        mParamsChangedListener = paramsChangedListener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NUSModuleMain nusModuleMain = getItem(position);
        Boolean isInCurrentSemester = false;
        int semesterIdx = 0;

        ArrayList<String> mModuleLecture = new ArrayList<>();
        ArrayList<String> mModuleTutorial = new ArrayList<>();
        ArrayList<String> mModuleSectionalTeaching = new ArrayList<>();
        ArrayList<String> mModuleRecitation = new ArrayList<>();
        ArrayList<String> mModuleLaboratory = new ArrayList<>();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.nusmods_list_item, null);

            ImageButton removeModuleButton = convertView.findViewById(R.id.delete_module);
            TextView moduleCodeAndTitle = convertView.findViewById(R.id.module_code_and_title);
            TextView examDate = convertView.findViewById(R.id.exam_date);
            TextView moduleCredits = convertView.findViewById(R.id.module_credits);
            TextView textLecture = convertView.findViewById(R.id.module_lecture);
            TextView textTutorial = convertView.findViewById(R.id.module_tutorial);
            TextView textSectionalTeaching = convertView.findViewById(R.id.module_sectional_teaching);
            TextView textRecitation = convertView.findViewById(R.id.module_recitation);
            TextView textLaboratory = convertView.findViewById(R.id.module_laboratory);
            Spinner spinnerLecture = convertView.findViewById(R.id.select_lecture);
            Spinner spinnerTutorial = convertView.findViewById(R.id.select_tutorial);
            Spinner spinnerSectionalTeaching = convertView.findViewById(R.id.select_sectional_teaching);
            Spinner spinnerRecitation = convertView.findViewById(R.id.select_recitation);
            Spinner spinnerLaboratory = convertView.findViewById(R.id.select_laboratory);

            moduleCodeAndTitle.setText(nusModuleMain.getModuleCode() + " " + nusModuleMain.getTitle());

            for(int i = 0; i < nusModuleMain.getSemesterData().size(); i++){
                if(nusModuleMain.getSemesterData().get(i).getSemester() == mSemester){
                    semesterIdx = i;
                    break;
                }
            }

            String dateString = nusModuleMain.getSemesterData().get(semesterIdx).getExamDate();
            if (dateString != null) {
                examDate.setText("Exam: " + parseDate(dateString));
                mParamsChangedListener.examObtained(nusModuleMain.getModuleCode(), "Exam", "Finals");
            } else {
                examDate.setText("No exam");
            }

            moduleCredits.setText(nusModuleMain.getModuleCredit() + " MCs");

            for(int i = 0; i < nusModuleMain.getSemesterData().get(mSemester).getTimetable().size(); i++){
                String classNumber = nusModuleMain.getSemesterData().get(mSemester).getTimetable().get(i).getClassNo();
                switch(whatIsThisClass(nusModuleMain, i)){
                    case "Tutorial":
                        if(!mModuleTutorial.contains(classNumber)){
                            mModuleTutorial.add(classNumber);
                        }
                        break;
                    case "Recitation":
                        if(!mModuleRecitation.contains(classNumber)){
                            mModuleRecitation.add(classNumber);
                        }
                        break;
                    case "Lecture":
                        if(!mModuleLecture.contains(classNumber)){
                            mModuleLecture.add(classNumber);
                        }
                        break;
                    case "Sectional Teaching":
                        if(!mModuleSectionalTeaching.contains(classNumber)){
                            mModuleSectionalTeaching.add(classNumber);
                        }
                        break;
                    case "Laboratory":
                        if(!mModuleLaboratory.contains(classNumber)){
                            mModuleLaboratory.add(classNumber);
                        }
                        break;
                }
            }

            for(int i = 0; i < mUserDatabase.size(); i++){
                if(nusModuleMain.getModuleCode().equals(mUserDatabase.get(i).getModuleCode()) && mUserDatabase.get(i).getClassesSelected() != null){
                    // Module is found in database
                    for(int n = 0; n < mUserDatabase.get(i).getClassesSelected().size(); n++){
                        switch(whatIsThisClassSelection(mUserDatabase.get(i), n)){
                            case "Tutorial":
                                tutorialChosenDB = mUserDatabase.get(i).getClassesSelected().get(n).getClassNo();
                                break;
                            case "Lecture":
                                lectureChosenDB = mUserDatabase.get(i).getClassesSelected().get(n).getClassNo();
                                break;
                            case "Sectional Teaching":
                                stChosenDB = mUserDatabase.get(i).getClassesSelected().get(n).getClassNo();
                                break;
                            case "Recitation":
                                recitationChosenDB = mUserDatabase.get(i).getClassesSelected().get(n).getClassNo();
                                break;
                            case "Laboratory":
                                laboratoryChosenDB = mUserDatabase.get(i).getClassesSelected().get(n).getClassNo();
                                break;
                        }
                    }
                }
            }

            Collections.sort(mModuleLecture);
            Collections.sort(mModuleTutorial);
            Collections.sort(mModuleRecitation);
            Collections.sort(mModuleSectionalTeaching);
            Collections.sort(mModuleLaboratory);

            ArrayAdapter<String> spinnerLectureAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mModuleLecture);
            spinnerLectureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLecture.setAdapter(spinnerLectureAdapter);
            if(mModuleLecture.size() == 0){
                textLecture.setVisibility(View.GONE);
                spinnerLecture.setVisibility(View.GONE);
            } else {
                for(int i = 0; i < spinnerLecture.getCount(); i++){
                    if(spinnerLecture.getItemAtPosition(i).equals(lectureChosenDB)){
                        spinnerLecture.setSelection(i);
                    }
                }

                spinnerLecture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                        String lessonType = "Lecture";
                        String params = parent.getItemAtPosition(pos).toString();
                        mParamsChangedListener.lectureChanged(nusModuleMain.getModuleCode(), lessonType, params);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent){

                    }
                });
            }

            ArrayAdapter<String> spinnerTutorialAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mModuleTutorial);
            spinnerTutorialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTutorial.setAdapter(spinnerTutorialAdapter);
            if(mModuleTutorial.size() == 0){
                textTutorial.setVisibility(View.GONE);
                spinnerTutorial.setVisibility(View.GONE);
            } else {
                for(int i = 0; i < spinnerTutorial.getCount(); i++){
                    if(spinnerTutorial.getItemAtPosition(i).equals(tutorialChosenDB)){
                        spinnerTutorial.setSelection(i);
                    }
                }
                spinnerTutorial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                        String lessonType = "Tutorial";
                        String params = parent.getItemAtPosition(pos).toString();
                        mParamsChangedListener.tutorialChanged(nusModuleMain.getModuleCode(), lessonType, params);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent){

                    }
                });
            }

            ArrayAdapter<String> spinnerSectionalTeachingAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mModuleSectionalTeaching);
            spinnerSectionalTeachingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSectionalTeaching.setAdapter(spinnerSectionalTeachingAdapter);
            if(mModuleSectionalTeaching.size() == 0){
                textSectionalTeaching.setVisibility(View.GONE);
                spinnerSectionalTeaching.setVisibility(View.GONE);
            } else {
                for(int i = 0; i < spinnerSectionalTeaching.getCount(); i++){
                    if(spinnerSectionalTeaching.getItemAtPosition(i).equals(stChosenDB)){
                        spinnerSectionalTeaching.setSelection(i);
                    }
                }
                spinnerSectionalTeaching.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                        String lessonType = "Sectional Teaching";
                        String params = parent.getItemAtPosition(pos).toString();
                        mParamsChangedListener.stChanged(nusModuleMain.getModuleCode(), lessonType, params);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent){

                    }
                });
            }

            ArrayAdapter<String> spinnerRecitationAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mModuleRecitation);
            spinnerRecitationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRecitation.setAdapter(spinnerRecitationAdapter);
            if(mModuleRecitation.size() == 0){
                textRecitation.setVisibility(View.GONE);
                spinnerRecitation.setVisibility(View.GONE);
            } else {
                for(int i = 0; i < spinnerRecitation.getCount(); i++){
                    if(spinnerRecitation.getItemAtPosition(i).equals(recitationChosenDB)){
                        spinnerRecitation.setSelection(i);
                    }
                }
                spinnerRecitation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                        String lessonType = "Recitation";
                        String params = parent.getItemAtPosition(pos).toString();
                        mParamsChangedListener.recitationChanged(nusModuleMain.getModuleCode(), lessonType, params);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent){

                    }
                });
            }

            ArrayAdapter<String> spinnerLaboratoryAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mModuleLaboratory);
            spinnerLaboratoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLaboratory.setAdapter(spinnerLaboratoryAdapter);
            if(mModuleLaboratory.size() == 0){
                System.out.println(nusModuleMain.getModuleCode() + " there are no lab classes");
                textLaboratory.setVisibility(View.GONE);
                spinnerLaboratory.setVisibility(View.GONE);
            } else {
                System.out.println(nusModuleMain.getModuleCode() + " there are lab classes");
                for(int i = 0; i < spinnerLaboratory.getCount(); i++){
                    if(spinnerLaboratory.getItemAtPosition(i).equals(laboratoryChosenDB)){
                        spinnerLaboratory.setSelection(i);
                    }
                }
                spinnerLaboratory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                        String lessonType = "Laboratory";
                        String params = parent.getItemAtPosition(pos).toString();
                        mParamsChangedListener.laboratoryChanged(nusModuleMain.getModuleCode(), lessonType, params);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent){

                    }
                });
            }

            removeModuleButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    String moduleCode = nusModuleMain.getModuleCode();
                    Toast.makeText(mContext, moduleCode, Toast.LENGTH_LONG);
                    mParamsChangedListener.moduleRemoved(moduleCode);
                }
            });
        } else {
            return convertView;
        }

        return convertView;
    }

    private String whatIsThisClass(NUSModuleMain module, int index) {
        return module.getSemesterData().get(mSemester).getTimetable().get(index).getLessonType();
    }

    private String whatIsThisClassSelection(NUSModuleLite module, int index){
        return module.getClassesSelected().get(index).getLessonType();
    }

    private String parseDate(String dateString){
        String INPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String OUTPUT_DATE_FORMAT = "yyyy-MM-dd hh:mm a";
        ZoneId fromTimeZone = ZoneId.of("UTC");
        ZoneId toTimeZone = ZoneId.systemDefault();

        LocalDateTime ldt = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(INPUT_DATE_FORMAT));
        ZonedDateTime providedTime = ldt.atZone(fromTimeZone);
        ZonedDateTime outputTime = providedTime.withZoneSameInstant(toTimeZone);
        String outputDateString = DateTimeFormatter.ofPattern(OUTPUT_DATE_FORMAT).format(outputTime);

        return outputDateString;
    }
}
