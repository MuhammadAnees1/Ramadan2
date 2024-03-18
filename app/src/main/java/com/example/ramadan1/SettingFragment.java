package com.example.ramadan1;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;
public class SettingFragment extends Fragment {
    RecyclerView recyclerView;
    SettingsAdapter adapter;
    List<SettingItem> settingItems;

    public SettingFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);


        recyclerView = view.findViewById(R.id.recyclerView3);
        settingItems = new ArrayList<>();
        // Manually add SettingItem objects
        settingItems.add(new SettingItem(R.drawable.baseline_feedback_24,"Build Version", "Version: "+getAppVersion()));
        settingItems.add(new SettingItem(R.drawable.baseline_get_app_24, "Update", "check the latest update"));
        settingItems.add(new SettingItem(R.drawable.baseline_star_rate_24, "Rate Us", "please support us with your positive review"));
        settingItems.add(new SettingItem(R.drawable.baseline_share_24, "Share app", "Share our app with your Friends"));
        settingItems.add(new SettingItem(R.drawable.baseline_more_24, "More Apps", "check our other application on plays tore"));
        settingItems.add(new SettingItem(R.drawable.baseline_contact_support_24, "Contact Us", "Help us to improve our application"));
        settingItems.add(new SettingItem(R.drawable.baseline_privacy_tip_24, "Privacy Policy", "Please check our privacy policy here"));
        // Add more items as needed
        adapter = new SettingsAdapter(settingItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }
    private String getAppVersion() {
        String versionName = "";
        try {
            PackageInfo packageInfo = requireContext().getPackageManager().getPackageInfo(
                    requireContext().getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}