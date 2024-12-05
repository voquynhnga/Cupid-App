package com.midterm.destined.Views.Homepage.Search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.destined.Adapters.UserAdapter;
import com.midterm.destined.Models.UserReal;
import com.midterm.destined.Presenters.CardPresenter;
import com.midterm.destined.Presenters.HomepagePresenter;
import com.midterm.destined.Presenters.SearchPresenter;
import com.midterm.destined.R;
import com.midterm.destined.Views.Homepage.Card.CardFragment;
import com.midterm.destined.Views.Homepage.HomepageFragment;

import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment implements searchView {
    private Spinner detailInput;
    private RecyclerView resultsRecyclerView;
    private UserAdapter userAdapter;
    private Spinner filterSpinner;
    private SearchPresenter searchPresenter;
    private ImageView btnBack;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search, container, false);

        detailInput = view.findViewById(R.id.filterInfo);
        resultsRecyclerView = view.findViewById(R.id.resultsRecyclerView);
        filterSpinner = view.findViewById(R.id.filterSpinner);
        btnBack = view.findViewById(R.id.btn_backSearch);



        searchPresenter = new SearchPresenter(this);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ArrayAdapter<CharSequence> adapter;

                    switch (position) {
                        case 0: // Gender
                            adapter = ArrayAdapter.createFromResource(
                                    Objects.requireNonNull(getContext()),
                                    R.array.gender_options,
                                    android.R.layout.simple_spinner_item
                            );
                            break;

                        case 1: // Age
                            adapter = ArrayAdapter.createFromResource(
                                    Objects.requireNonNull(getContext()),
                                    R.array.age_options,
                                    android.R.layout.simple_spinner_item
                            );
                            break;

                        case 2: // Interests
                            adapter = ArrayAdapter.createFromResource(
                                    Objects.requireNonNull(getContext()),
                                    R.array.interests_options,
                                    android.R.layout.simple_spinner_item
                            );
                            break;

                        case 3: // Location
                            adapter = ArrayAdapter.createFromResource(
                                    Objects.requireNonNull(getContext()),
                                    R.array.location_options,
                                    android.R.layout.simple_spinner_item
                            );
                            break;

                        default:
                            return;
                    }

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    detailInput.setAdapter(adapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do nothing
                }
            });

        view.findViewById(R.id.ok).setOnClickListener(v -> {
            String filter = filterSpinner.getSelectedItem().toString().trim();
            String detail = detailInput.getSelectedItem().toString().trim();
            searchPresenter.performSearch(filter, detail);
        });

        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_SearchFragment_to_HomepageFragment);


        });


        return view;
    }

    @Override
    public void updateSearchResults(List<UserReal> users) {
        userAdapter = new UserAdapter(getContext(), users, Navigation.findNavController(requireView()));
        resultsRecyclerView.setAdapter(userAdapter);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setLoading(boolean isLoading) {

    }
}
