package com.example.calendarmanagerbeta;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ModuleListAdapter extends ArrayAdapter<NUSModuleMain> {
    private Context mContext;
    private int mResource;
    private List<NUSModuleMain> mModules;
    private int mSemester = 4;

    public ModuleListAdapter(Context context, int resource, List<NUSModuleMain> userModules, int semester) {
        super(context, resource, userModules);
        this.mContext = context;
        this.mResource = resource;
        this.mModules = userModules;
        this.mSemester = semester;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NUSModuleMain nusModuleMain = getItem(position);
        Boolean isInCurrentSemester = false;
        int semesterIdx = 0;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.nusmods_list_item, null);

        TextView moduleCodeAndTitle = (TextView) view.findViewById(R.id.module_code_and_title);
        TextView examDate = (TextView) view.findViewById(R.id.exam_date);
        TextView moduleCredits = (TextView) view.findViewById(R.id.module_credits);

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
        } else {
            examDate.setText("No exam");
        }

        moduleCredits.setText(nusModuleMain.getModuleCredit() + " MCs");

        return view;
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
