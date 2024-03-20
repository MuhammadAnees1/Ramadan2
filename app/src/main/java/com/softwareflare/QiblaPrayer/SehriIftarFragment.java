package com.softwareflare.QiblaPrayer;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SehriIftarFragment extends Fragment {
    private TextView english_date1, Islamic_date, location_name;
    private RecyclerView recyclerView;
     String selectedRadioButton;
    private ProgressBar progressBar;
    private boolean dataLoaded = false;

    public SehriIftarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sehri_iftar, container, false);

        english_date1 = view.findViewById(R.id.english_date1);
        Islamic_date = view.findViewById(R.id.Islamic_date);
        recyclerView = view.findViewById(R.id.recyclerView2);
        location_name = view.findViewById(R.id.location_name);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        RadioButton radioButtonHanafi = view.findViewById(R.id.radioButtonHanafi);
        RadioButton radioButtonJaffari = view.findViewById(R.id.radioButtonJaffari);
        progressBar = view.findViewById(R.id.progressBar);

        SharedPreferences preferences = requireActivity().getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE);
        String city = preferences.getString("city", "");
        String country = preferences.getString("country", "");
        location_name.setText(city + ", " + country);
        radioButtonHanafi.setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                progressBar.setVisibility(View.VISIBLE);
                RadioButton radioButton = view.findViewById(checkedId);
                selectedRadioButton = radioButton.getText().toString();
                Sehri_iftar_JsonHelper jsonHelper = new Sehri_iftar_JsonHelper();
                MainActivity activity = (MainActivity) getActivity();
                assert activity != null;
                jsonHelper.updateData(getContext(),activity.currentLocation, new Sehri_iftar_JsonHelper.DataLoadListener() {
                    @Override
                    public void onDataLoaded(List<Sehri_iftari_class> sehriIftarList) {
                        SehriIftarAdapter adapter1 = new SehriIftarAdapter(sehriIftarList);
                        recyclerView.setAdapter(adapter1);
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    }
                },SehriIftarFragment.this);
            }
        });

        String todayDateEnglish = DateHelper.getCurrentDateEnglish();
        english_date1.setText(todayDateEnglish);

        String todayDateIslamic = DateHelper.getCurrentDateIslamic();
        Islamic_date.setText(todayDateIslamic);

        Sehri_iftar_JsonHelper jsonHelper = new Sehri_iftar_JsonHelper();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            jsonHelper.updateData(getContext(), activity.currentLocation, sehriIftarList -> {
                SehriIftarAdapter adapter1 = new SehriIftarAdapter(sehriIftarList);
                recyclerView.setAdapter(adapter1);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            }, this);
        }
        return view;
    }
    public void updateAdapter(List<Sehri_iftari_class> sehriIftarList) {
        if (isAdded() && getContext() != null) {
            SehriIftarAdapter adapter1 = new SehriIftarAdapter(sehriIftarList);
            if (recyclerView != null) {
                recyclerView.setAdapter(adapter1);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!dataLoaded) {
            loadData();
        }
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        Sehri_iftar_JsonHelper jsonHelper = new Sehri_iftar_JsonHelper();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            jsonHelper.updateData(getContext(), activity.currentLocation, sehriIftarList -> {
                SehriIftarAdapter adapter = new SehriIftarAdapter(sehriIftarList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                dataLoaded = true; // Mark data as loaded
                progressBar.setVisibility(View.GONE);
            }, this);
        }
    }
}
