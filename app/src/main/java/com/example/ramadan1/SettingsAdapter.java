package com.example.ramadan1;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {

    private static List<SettingItem> settingItems;

    public SettingsAdapter(List<SettingItem> settingItems) {
        SettingsAdapter.settingItems = settingItems;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settings, parent, false);
        return new SettingsViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        SettingItem item = settingItems.get(position);
        holder.bind(item);
    }
    @Override
    public int getItemCount() {
        return settingItems.size();
    }
    public static class SettingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iconImageView;
        TextView titleTextView;
        TextView descriptionTextView;

        public SettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize your views here
            iconImageView = itemView.findViewById(R.id.button_image);
            titleTextView = itemView.findViewById(R.id.button_name);
            descriptionTextView = itemView.findViewById(R.id.button_text);

            // Set the click listener for the entire item view
            itemView.setOnClickListener(this);
        }

        public void bind(SettingItem item) {
            // Bind data to your views here
            iconImageView.setImageResource(item.getIconResId());
            titleTextView.setText(item.getTitle());
            descriptionTextView.setText(item.getDescription());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                SettingItem clickedItem = settingItems.get(position);
                Context context = v.getContext();


                if (clickedItem.getTitle().equals("Share app")) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareMessage = "Check out this awesome app: " + context.getPackageName();
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    context.startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
                if (clickedItem.getTitle().equals("More Apps")) {
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=Software+Flare")));
                    } catch (ActivityNotFoundException e) {
                        // Handle if Play Store app is not installed
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Software+Flare")));
                    }
                }
                if (clickedItem.getTitle().equals("Rate Us")) {
                    // Open the app's Play Store page for rating
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                    } catch (ActivityNotFoundException e) {
                        // Handle if Play Store app is not installed
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
                    }
                }
                if (clickedItem.getTitle().equals("Contact Us")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "info@softwareflare.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Feedback");
                    context.startActivity(Intent.createChooser(emailIntent, "Send Email"));
                }

                if (clickedItem.getTitle().equals("Privacy Policy")) {
                    // Open the privacy policy link
                    String privacyPolicyUrl = "https://legal.softwareflare.com/app-privacy.html";
                    Intent privacyPolicyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
                    context.startActivity(privacyPolicyIntent);
                }

            }
        }
    }
}
