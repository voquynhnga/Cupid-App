package com.midterm.destined.Views.Homepage.Search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.destined.Adapters.UserAdapter;
import com.midterm.destined.Models.UserReal;
import com.midterm.destined.Presenters.CardPresenter;
import com.midterm.destined.Presenters.SearchPresenter;
import com.midterm.destined.R;
import com.midterm.destined.Views.Homepage.Card.CardFragment;

import java.util.List;

public class SearchFragment extends Fragment implements searchView {
    private EditText detailInput;
    private RecyclerView resultsRecyclerView;
    private UserAdapter userAdapter;
    private Spinner filterSpinner;
    private SearchPresenter searchPresenter;
    private CardPresenter cardPresenter;
    private CardFragment cardFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search, container, false);

        detailInput = view.findViewById(R.id.editTextInput);
        resultsRecyclerView = view.findViewById(R.id.resultsRecyclerView);
        filterSpinner = view.findViewById(R.id.filterSpinner);

        cardFragment = CardFragment.getInstance();
        searchPresenter = new SearchPresenter(this);
        cardPresenter = new CardPresenter(cardFragment);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_options, android.R.layout.simple_spinner_item);
        filterSpinner.setAdapter(adapter);

        view.findViewById(R.id.ok).setOnClickListener(v -> {
            String filter = filterSpinner.getSelectedItem().toString();
            String detail = detailInput.getText().toString().trim();
            searchPresenter.performSearch(filter, detail);
        });

        return view;
    }

    @Override
    public void updateSearchResults(List<UserReal> users) {
        userAdapter = new UserAdapter(getContext(), users, cardPresenter);
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
