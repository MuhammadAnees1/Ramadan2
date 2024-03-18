package com.example.ramadan1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class notification_adaptor extends RecyclerView.Adapter<notification_adaptor.ViewHolder> {
    private final List<soundsModel> dataList1;


    public notification_adaptor(List<soundsModel> dataList) {
        this.dataList1 = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_sound, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        soundsModel item = dataList1.get(position);
        holder.soundName.setText(item.getSoundName());
        holder.volume.setImageResource(R.drawable.baseline_volume_up_24);
    }

    @Override
    public int getItemCount() {
        return dataList1.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
    LinearLayout notificationSound;
    ImageView volume;
    TextView soundName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationSound = itemView.findViewById(R.id.sounds);
            volume = itemView.findViewById(R.id.volume);
            soundName = itemView.findViewById(R.id.soundName);

        }
    }
}
