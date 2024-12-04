package com.midterm.destined.Presenters;

import static com.midterm.destined.Utils.TextExtensions.normalizeText;
import static com.midterm.destined.Utils.TimeExtensions.getBirthYear;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.midterm.destined.Models.UserReal;
import com.midterm.destined.Utils.TextExtensions;
import com.midterm.destined.Views.Homepage.Search.searchView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchPresenter {
    private final searchView searchView;
    private final FirebaseFirestore db;
    private final String currentUserId;

    public SearchPresenter(searchView searchView) {
        this.searchView = searchView;
        this.db = FirebaseFirestore.getInstance();
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void performSearch(String filter, String detail) {
        if (filter == null || detail.isEmpty()) {
            searchView.showError("Vui lòng nhập đủ thông tin tìm kiếm.");
            return;
        }

        switch (filter) {
            case "Gender":
                searchByGender(detail);
                break;
            case "Interests":
                searchByInterest(detail);
                break;
            case "Location":
                searchByLocation(detail);
                break;
            case "Age":
                searchByAge(detail);
                break;
        }
    }

    private void searchByGender(String gender) {
        db.collection("users")
                .whereEqualTo("gender", capitalizeDetail(gender))
                .get()
                .addOnCompleteListener(task -> updateResults(task));
    }

    private void searchByInterest(String interest) {
        db.collection("users")
                .whereArrayContains("interests", capitalizeDetail(interest))
                .get()
                .addOnCompleteListener(task -> updateResults(task));
    }

    private void searchByLocation(String location) {
        String startText = normalizeText(location);
        String endText = startText + "\uf8ff";

        db.collection("users")
                .whereGreaterThanOrEqualTo("detailAdrress", startText)
                .whereLessThan("detailAdrress", endText)
                .get()
                .addOnCompleteListener(task -> updateResults(task));
    }

    private void searchByAge(String detail) {
        int targetAge;
        try {
            targetAge = Integer.parseInt(detail);
        } catch (NumberFormatException e) {
            searchView.showError("Tuổi không hợp lệ.");
            return;
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<UserReal> filteredUsers = new ArrayList<>();
                        for (UserReal user : task.getResult().toObjects(UserReal.class)) {
                            if (!user.getUid().equals(currentUserId)) {
                                int birthYear = getBirthYear(user.getDateOfBirth());
                                if (birthYear != -1 && currentYear - birthYear == targetAge) {
                                    filteredUsers.add(user);
                                }
                            }
                        }
                        searchView.updateSearchResults(filteredUsers);
                    } else {
                        searchView.showError("Lỗi khi tìm kiếm.");
                    }
                });
    }

    private void updateResults(Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            List<UserReal> users = task.getResult().toObjects(UserReal.class);
            searchView.updateSearchResults(users);
        } else {
            searchView.showError("Lỗi khi tải dữ liệu.");
        }
    }

    private String capitalizeDetail(String detail) {
        return detail.substring(0, 1).toUpperCase() + detail.substring(1).toLowerCase();
    }


}
