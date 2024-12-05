package com.midterm.destined.Presenters;

import static com.midterm.destined.Utils.TimeExtensions.getBirthYear;

import android.util.Log;
import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.midterm.destined.Models.Card;
import com.midterm.destined.Models.UserReal;
import com.midterm.destined.Utils.CalculateCoordinates;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Utils.DistanceExtension;
import com.midterm.destined.Utils.TimeExtensions;
import com.midterm.destined.Views.Homepage.Search.searchView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SearchPresenter {
    private final searchView searchView;

    public SearchPresenter(searchView searchView) {
        this.searchView = searchView;
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
            case "Interest":
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
         DB.getUsersCollection()
                .whereEqualTo("gender", capitalizeDetail(gender))
                .get()
                .addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful()) {
                        List<UserReal> filteredUsers = new ArrayList<>();
                        for (QueryDocumentSnapshot document : userTask.getResult()) {
                            UserReal user = document.toObject(UserReal.class);
                            if (!user.getUid().equals(DB.getCurrentUser().getUid())) {
                                filteredUsers.add(user);
                            }
                        }
                        searchView.updateSearchResults(filteredUsers);
                    } else {
                        searchView.showError("Error fetching users");
                    }
                });
    }


    private void searchByInterest(String interest) {
            DB.getUsersCollection()
                    .whereArrayContains("interests", capitalizeDetail(interest))
                    .get()
                    .addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful()) {
                            List<UserReal> filteredUsers = new ArrayList<>();
                            for (QueryDocumentSnapshot document : userTask.getResult()) {
                                UserReal user = document.toObject(UserReal.class);
                                if (!user.getUid().equals(DB.getCurrentUser().getUid())) {
                                    filteredUsers.add(user);
                                }
                            }
                            searchView.updateSearchResults(filteredUsers);
                        } else {
                            searchView.showError("Error fetching users");
                        }
                    });

    }




    private void searchByLocation(String location) {
        Pair<String, Integer> parsed = DistanceExtension.parseDistance(location);
        String operator = parsed.first;
        int distanceLimit = parsed.second;
        String currentUserId = DB.getCurrentUser().getUid();

            DB.getUsersCollection()
                    .get()
                    .addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful()) {
                            List<UserReal> filteredUsers = new ArrayList<>();
                            List<UserReal> usersToProcess = userTask.getResult().toObjects(UserReal.class).stream()
                                    .filter(user -> !user.getUid().equals(currentUserId))
                                    .collect(Collectors.toList());

                            AtomicInteger pendingCallbacks = new AtomicInteger(usersToProcess.size());
                            for (UserReal user : usersToProcess) {
                                CalculateCoordinates.calculateDistance(currentUserId, user.getUid(), distance -> {
                                    if ((operator.equals("<") && distance <= distanceLimit) ||
                                            (operator.equals(">") && distance > distanceLimit)) {
                                        synchronized (filteredUsers) {
                                            filteredUsers.add(user);
                                        }
                                    }

                                    if (pendingCallbacks.decrementAndGet() == 0) {
                                        searchView.updateSearchResults(filteredUsers);
                                    }
                                });
                            }
                        } else {
                            searchView.showError("Error fetching users");
                        }
                    });

    }





    private void searchByAge(String detail) {
        Pair<Integer, Integer> ageRange = TimeExtensions.parseAgeRange(detail);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int minAge = ageRange.first;
        int maxAge = ageRange.second;
        String currentUserId = DB.getCurrentUser().getUid();

            DB.getUsersCollection()
                    .get()
                    .addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful()) {
                            List<UserReal> filteredUsers = new ArrayList<>();
                            for (QueryDocumentSnapshot document : userTask.getResult()) {
                                UserReal user = document.toObject(UserReal.class);
                                if (!user.getUid().equals(currentUserId)) {
                                    int birthYear = getBirthYear(user.getDateOfBirth());
                                    if (birthYear != -1) {
                                        int age = currentYear - birthYear;
                                        if (age >= minAge && (maxAge == -1 || age <= maxAge)) {
                                            filteredUsers.add(user);
                                        }
                                    }
                                }
                            }
                            searchView.updateSearchResults(filteredUsers);
                        } else {
                            searchView.showError("Error fetching users");
                        }
                    });

    }


    private void updateResults(Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            List<UserReal> users = task.getResult().toObjects(UserReal.class);
            searchView.updateSearchResults(users);
        } else {
            searchView.showError("Error");
        }
    }

    private String capitalizeDetail(String detail) {
        return detail.substring(0, 1).toUpperCase() + detail.substring(1).toLowerCase();
    }


}
