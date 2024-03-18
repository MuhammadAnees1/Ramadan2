package com.example.ramadan1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SehriIftarAdapter extends RecyclerView.Adapter<SehriIftarAdapter.ViewHolder> {

    private List<Sehri_iftari_class> dataList;


    public SehriIftarAdapter(List<Sehri_iftari_class> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sehri_iftari_time_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sehri_iftari_class item = dataList.get(position);
        holder.id.setText(String.valueOf(item.getId()));
        holder.textViewSehriTime.setText(item.getSehri());
        holder.textViewIftarTime.setText(item.getIftari());
        holder.textViewDate.setText(item.getDate());
        holder.textViewDay.setText(item.getDay());
        Log.d(TAG, "onBindViewHolder: data"+item.data+item.getIftari() + item.getSehri());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSehriTime ,id;
        TextView textViewIftarTime;
        TextView textViewDate;
        TextView textViewDay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id =itemView.findViewById(R.id.idOfDays);
            textViewSehriTime = itemView.findViewById(R.id.sehriTime);
            textViewIftarTime = itemView.findViewById(R.id.iftariTime);
            textViewDate = itemView.findViewById(R.id.dateOfRamadan);
            textViewDay = itemView.findViewById(R.id.dayOfRamadan);
        }
    }
}