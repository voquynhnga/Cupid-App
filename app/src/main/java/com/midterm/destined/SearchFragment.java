package com.midterm.destined;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.KeyEvent; // Thêm import cho KeyEvent
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
import com.midterm.destined.model.UserReal;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText detailInput; // Đổi tên biến thành detailInput
    private RecyclerView resultsRecyclerView;
    private UserAdapter userAdapter;
    private List<UserReal> userList;
    private String selectedFilter; // Tiêu chí lọc được chọn
    private Spinner filterSpinner; // Spinner cho tiêu chí lọc

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search, container, false);

        detailInput = view.findViewById(R.id.editTextInput); // Sử dụng ID đúng
        resultsRecyclerView = view.findViewById(R.id.resultsRecyclerView);
        filterSpinner = view.findViewById(R.id.filterSpinner); // Spinner cho tiêu chí lọc

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), userList, true);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsRecyclerView.setAdapter(userAdapter);

        // Thiết lập cho Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        // Thiết lập hành động cho Spinner
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilter = parent.getItemAtPosition(position).toString(); // Gán giá trị cho selectedFilter
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFilter = null; // Đặt lại khi không có lựa chọn nào
            }
        });

        // Thiết lập sự kiện nhấn phím Enter cho EditText
        detailInput.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                performSearch();
                return true; // Trả về true để chỉ ra rằng sự kiện đã được xử lý
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
        String detail = detailInput.getText().toString().trim(); // Sử dụng biến detailInput
        detail = capitalizeDetail(detail);
        Log.d("SearchFragment", "Detail Address: " + detail);
        if (selectedFilter == null) {
            Toast.makeText(getContext(), "Vui lòng chọn tiêu chí lọc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (detail.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập chi tiết tìm kiếm", Toast.LENGTH_SHORT).show();
            return;
        }

        Query query = FirebaseFirestore.getInstance().collection("users");

        switch (selectedFilter) {
            case "Gender":
                query = query.whereEqualTo("gender", detail);
                break;
            case "Interests":
                query = query.whereArrayContains("interests", detail);
                break;
            case "Location":
                query = query.whereEqualTo("detailAddress", detail);
                break;
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    UserReal user = document.toObject(UserReal.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Lỗi khi tìm kiếm dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
