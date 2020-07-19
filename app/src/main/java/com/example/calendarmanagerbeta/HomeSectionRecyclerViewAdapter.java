package com.example.calendarmanagerbeta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeSectionRecyclerViewAdapter extends RecyclerView.Adapter<HomeSectionRecyclerViewAdapter.SectionViewHolder> {
    class SectionViewHolder extends RecyclerView.ViewHolder{
        private TextView sectionTitle, sectionSubtitle;
        private RecyclerView itemRecyclerView;

        public SectionViewHolder(View itemView){
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.event_section_label);
            sectionSubtitle = itemView.findViewById(R.id.event_section_sublabel);
            itemRecyclerView = itemView.findViewById(R.id.event_recycler_view);
        }
    }

    private Context mContext;
    private ArrayList<HomeSectionModel> sectionModelArrayList;

    public HomeSectionRecyclerViewAdapter(Context context, ArrayList<HomeSectionModel> homeSectionModelArrayList){
        this.mContext = context;
        this.sectionModelArrayList = homeSectionModelArrayList;
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_custom_row_layout, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position){
        final HomeSectionModel homeSectionModel = sectionModelArrayList.get(position);
        holder.sectionTitle.setText(homeSectionModel.getSectionTitle());
        holder.sectionSubtitle.setText(homeSectionModel.getSectionSubtitle());

        holder.itemRecyclerView.setHasFixedSize(true);
        holder.itemRecyclerView.setNestedScrollingEnabled(false);

        // Using vertical linear
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        holder.itemRecyclerView.setLayoutManager(linearLayoutManager);

        if(homeSectionModel.getEventArrayList() != null) {
            HomeEventRecyclerViewAdapter adapter = new HomeEventRecyclerViewAdapter(mContext, homeSectionModel.getEventArrayList());
            holder.itemRecyclerView.setAdapter(adapter);
        } else if(homeSectionModel.getKeywordInfoArrayList() != null){
            HomeKeywordRecyclerViewAdapter adapter = new HomeKeywordRecyclerViewAdapter(mContext, homeSectionModel.getKeywordInfoArrayList());
            holder.itemRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount(){
        return sectionModelArrayList.size();
    }
}
