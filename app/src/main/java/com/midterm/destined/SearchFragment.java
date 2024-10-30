package com.midterm.destined;

import android.os.Bundle;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
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
import android.widget.ImageView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.google.firebase.auth.FirebaseAuth;

public class SearchFragment extends Fragment {

    private EditText detailInput;
    private RecyclerView resultsRecyclerView;
    private ImageView searchButton;
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
        searchButton = view.findViewById(R.id.ok);

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

        searchButton.setOnClickListener(v -> performSearch());

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //final String normalizedSearch = normalizeText(detail);

        switch (selectedFilter) {
            case "Gender":
                detail = capitalizeDetail(detail);
                db.collection("users").whereEqualTo("gender", detail)
                        .get().addOnCompleteListener(task -> updateUserList(task, currentUserId));
                break;

            case "Interests":
                detail = capitalizeDetail(detail);
                db.collection("users").whereArrayContains("interests", detail)
                        .get().addOnCompleteListener(task -> updateUserList(task, currentUserId));
                break;

            case "Location":
                String normalizedSearch = normalizeText(detail); // Chuẩn hóa từ khóa tìm kiếm
                db.collection("users").whereArrayContains("detailAdrress", normalizedSearch).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserReal user = document.toObject(UserReal.class);
                            if (!user.getUid().equals(currentUserId)) {
                                String detailAddress = user.getDetailAddress();
                                if (detailAddress != null) { // Kiểm tra địa chỉ không null
                                    String normalizedAddress = normalizeText(detailAddress); // Chuẩn hóa địa chỉ

                                    if (normalizedAddress.contains(normalizedSearch)) {
                                        userList.add(user);
                                    }
                                }
                            }
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi tìm kiếm dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
                break;


            case "Age":
                int targetAge;
                try {
                    targetAge = Integer.parseInt(detail);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Tuổi không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                db.collection("users").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserReal user = document.toObject(UserReal.class);
                            if (!user.getUid().equals(currentUserId)) {
                                String dob = user.getDateOfBirth();
                                int birthYear = getBirthYear(dob);
                                if (birthYear != -1 && currentYear - birthYear == targetAge) {
                                    userList.add(user);
                                }
                            }
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi tìm kiếm dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    private void updateUserList(@NonNull Task<QuerySnapshot> task, String currentUserId) {
        if (task.isSuccessful()) {
            userList.clear();
            for (QueryDocumentSnapshot document : task.getResult()) {
                UserReal user = document.toObject(UserReal.class);
                if (!user.getUid().equals(currentUserId)) {
                    userList.add(user);
                }
            }
            userAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(), "Lỗi khi tìm kiếm dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm chuẩn hóa chuỗi, chuyển thành chữ thường và loại bỏ dấu tiếng Việt
    private String normalizeText(String input) {
        input = input.toLowerCase();
        input = input.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        input = input.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        input = input.replaceAll("[ìíịỉĩ]", "i");
        input = input.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        input = input.replaceAll("[ùúụủũưừứựửữ]", "u");
        input = input.replaceAll("[ỳýỵỷỹ]", "y");
        input = input.replaceAll("[đ]", "d");
        return input;
    }


    // Helper function to parse birth year from date string
    private int getBirthYear(String dob) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Calendar dobCal = Calendar.getInstance();
            dobCal.setTime(sdf.parse(dob));
            return dobCal.get(Calendar.YEAR);
        } catch (ParseException e) {
            Log.e("SearchFragment", "Invalid date format: " + dob);
            return -1; // Error indicator
        }
    }

}
