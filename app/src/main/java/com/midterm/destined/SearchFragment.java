package com.midterm.destined;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.midterm.destined.card.Card;
import com.midterm.destined.card.CardFragment;
import com.midterm.destined.model.UserReal;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.auth.FirebaseAuth;

public class SearchFragment extends Fragment {

    private EditText detailInput;
    private RecyclerView resultsRecyclerView;
    private UserAdapter userAdapter;
    private List<UserReal> userList;
    private String selectedFilter;
    private Spinner filterSpinner;
    public CardFragment cf ;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search, container, false);

        detailInput = view.findViewById(R.id.editTextInput);
        resultsRecyclerView = view.findViewById(R.id.resultsRecyclerView);
        filterSpinner = view.findViewById(R.id.filterSpinner);

        userList = new ArrayList<>();
        cf = CardFragment.getInstance();
        userAdapter = new UserAdapter(getContext(), userList, true);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsRecyclerView.setAdapter(userAdapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilter = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFilter = null;
            }
        });

        detailInput.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                performSearch();
                return true;
            }
            return false;
        });

        return view;
    }

    private String capitalizeDetail(String detail) {
        if (detail == null || detail.isEmpty()) {
            return detail;
        }
        return detail.substring(0, 1).toUpperCase() + detail.substring(1).toLowerCase();
    }

    private void performSearch() {
        String detail = detailInput.getText().toString().toLowerCase().trim(); // Trim input

        if (selectedFilter == null) {
            Toast.makeText(getContext(), "Vui lòng chọn tiêu chí lọc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (detail.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập chi tiết tìm kiếm", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseFirestore.getInstance().collection("users");

        switch (selectedFilter) {
            case "Gender":
                detail = capitalizeDetail(detail);
                query = query.whereEqualTo("gender", detail);
                break;
            case "Interests":
                detail = capitalizeDetail(detail);
                query = query.whereArrayContains("interests", detail);
                break;
            case "Location":
                query = query.orderBy("detailAdrress").startAt(detail).endAt(detail + "\uf8ff");
                break;
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    UserReal user = document.toObject(UserReal.class);
                    List<String> favoritedCardList = (List<String>) document.get("favoritedCardList");
                    if (!user.getUid().equals(currentUserId) &&
                            (favoritedCardList == null || !favoritedCardList.contains(user.getUid()))) {
                        Log.d("DEBUG", "search" + user.getFullName());
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Lỗi khi tìm kiếm dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
