package com.example.calendarmanagerbeta;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class KeywordListAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private int mResource;
    private ArrayList<String> userKeywords;
    private KeywordManagerAdapterCallback keywordManagerAdapterCallback;

    public KeywordListAdapter(Context context, int resource, ArrayList<String> userKeywords){
        super(context, resource, userKeywords);
        this.mContext = context;
        this.mResource = resource;
        this.userKeywords = userKeywords;
    }

    public void setKeywordManagerAdapterCallback(KeywordManagerAdapterCallback keywordManagerAdapterCallback){
        this.keywordManagerAdapterCallback = keywordManagerAdapterCallback;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final String keyword = userKeywords.get(position);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.keyword_list_item, null);
            TextView keywordDisplay = convertView.findViewById(R.id.keyword);
            Button keywordDelete = convertView.findViewById(R.id.keyword_delete);

            keywordDisplay.setText(keyword);

            keywordDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    keywordManagerAdapterCallback.onKeywordRemoved(keyword);
                }
            });
        }

        return convertView;
    }
}
