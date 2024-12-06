package com.midterm.destined.Models;

import android.text.TextUtils;
import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Utils.TimeExtensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Card {

    private String name;
    private String age;
    private String bio;
    private String location;
    private String profileImageUrl;
    private List<String> imageUrls;
    private String userID;
    private String allInterest;
    private String gender;

    public Card() {}

//    public Card(String name, String profileImageUrl, String allInterest, String location, String gender, String bio, String age, String userID, List<String> imageUrls) {
//        this.name = name;
//        this.age = age;
//        this.bio = bio;
//        this.location = location;
//        this.allInterest = allInterest;
//        this.gender = gender;
//        this.profileImageUrl = profileImageUrl;
//        this.userID = userID;
//        this.imageUrls = imageUrls;
//    }

    public Card(UserReal user){
        this.name = user.getFullName();
        this.age = String.valueOf(TimeExtensions.calculateAge(user.getDateOfBirth()));
        this.bio = (user.getBio() != null && !user.getBio().isEmpty()) ? user.getBio() : "Bio";
        this.location = user.getDetailAddress();
        this.profileImageUrl = (user.getImageUrls() != null && !user.getImageUrls().isEmpty() && user.getImageUrls().get(0) != null)
                ? user.getImageUrls().get(0)
                : "https://firebasestorage.googleapis.com/v0/b/cupid-app-ad700.appspot.com/o/avatar_default.jpg?alt=media&token=70caf3c1-ebd8-4151-ad82-bc70365d87cf";

        this.imageUrls = user.getImageUrls();
        this.userID = user.getUid();
        this.allInterest = user.getInterests() != null && !user.getInterests().isEmpty()? TextUtils.join(" - ", user.getInterests()) : "Hobby";
        this.gender = user.getGender();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAllInterest() {
        return allInterest;
    }

    public void setAllInterest(String allInterest) {
        this.allInterest = allInterest;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public interface OnCardFetchListener {
        void onSuccess(List<Card> cards);
        void onError(String errorMessage);
    }

    public void fetchAllUsersAndUpdateCardList(OnCardFetchListener listener) {
        DB.getCurrentUserDocument().get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // Lấy danh sách favoritedCardList
                List<String> favoritedCardList = (List<String>) task.getResult().get("favoritedCardList");
                if (favoritedCardList == null) {
                    favoritedCardList = new ArrayList<>();
                }

                List<String> finalFavoritedCardList = favoritedCardList;

                // Lấy danh sách tất cả người dùng
                DB.getUsersCollection().get().addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful() && userTask.getResult() != null) {
                        List<Card> allUsers = new ArrayList<>();
                        List<String> newCardList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : userTask.getResult()) {
                            UserReal user = document.toObject(UserReal.class);

                            // Loại trừ chính mình và các user đã favorited
                            if (!user.getUid().equals(DB.getCurrentUser().getUid())
                                    && !finalFavoritedCardList.contains(user.getUid())) {
                                newCardList.add(user.getUid());

                                allUsers.add(new Card(user));

                            }
                        }

                        // Cập nhật cardList trong Firestore
                        if (!newCardList.isEmpty()) {
                            DB.getCurrentUserDocument()
                                    .update("cardList", newCardList)
                                    .addOnSuccessListener(aVoid -> Log.d("DEBUG", "Card list updated successfully."))
                                    .addOnFailureListener(e -> Log.e("DEBUG", "Error updating card list", e));
                        }

                        // Trả kết quả qua callback
                        listener.onSuccess(allUsers);
                    } else {
                        listener.onError("Error fetching all users: " + userTask.getException().getMessage());
                    }
                });
            } else {
                listener.onError("Error fetching current user: " + task.getException().getMessage());
            }
        });
    }

    public static void fetchUsersByIds(List<String> userIds, OnCardFetchListener listener) {

            int batchSize = 30;
            List<List<String>> batches = new ArrayList<>();

            // Chia nhỏ danh sách userIds thành các nhóm
            for (int i = 0; i < userIds.size(); i += batchSize) {
                int end = Math.min(i + batchSize, userIds.size());
                batches.add(userIds.subList(i, end));
            }

            // Thực hiện các truy vấn cho từng nhóm
            List<Card> allCards = new ArrayList<>();
            final int totalBatches = batches.size();
            AtomicInteger completedBatches = new AtomicInteger(0);  // Đếm số lượng batch đã hoàn thành

            for (List<String> batch : batches) {
                DB.getUsersCollection()
                        .whereIn("uid", batch)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    UserReal user = document.toObject(UserReal.class);
                                    allCards.add(new Card(user));
                                }
                            } else {
                                listener.onError("Error fetching users by IDs: " + task.getException().getMessage());
                            }

                            // Kiểm tra xem tất cả các batch đã hoàn thành chưa
                            if (completedBatches.incrementAndGet() == totalBatches) {
                                listener.onSuccess(allCards);
                            }
                        });
            }

    }


    public static void saveMatchToDB(Match match, String fullNameUser2) {
        Map<String, Object> matchData = new HashMap<>();
        matchData.put("userId1", match.getUserId1());
        matchData.put("userId2", match.getUserId2());
        matchData.put("timestamp", match.getTimestamp());

        DB.getMatchesCollection().document(match.getMatchId()).set(matchData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Match saved with " + fullNameUser2);
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding match", e);
                });
    }

    public static void removeCardList(String removedUserId) {
        DB.getUsersCollection().document(DB.getCurrentUser().getUid())
                .update("cardList", FieldValue.arrayRemove(removedUserId))
                .addOnSuccessListener(aVoid -> Log.d("DEBUG", "User removed from card list"))
                .addOnFailureListener(e -> Log.e("DEBUG", "Error removing user from card list", e));
    }
    public static void addCardList(String addedUserId) {
        DB.getUsersCollection().document(DB.getCurrentUser().getUid())
                .update("cardList", FieldValue.arrayUnion(addedUserId))
                .addOnSuccessListener(aVoid -> Log.d("DEBUG", "User added into card list"))
                .addOnFailureListener(e -> Log.e("DEBUG", "Error adding user into card list", e));
    }

    public static void addToFavoritedList(String favoritedUserId) {
        DB.getCurrentUserDocument()
                .update("cardList", FieldValue.arrayRemove(favoritedUserId),
                        "favoritedCardList", FieldValue.arrayUnion(favoritedUserId))
                .addOnSuccessListener(aVoid -> Log.d("DEBUG", "Card list updated successfully"))
                .addOnFailureListener(e -> Log.e("DEBUG", "Error updating favorited card list", e));
    }
    public static void removeToFavoritedList(String favoritedUserId) {
        DB.getCurrentUserDocument()
                .update("cardList", FieldValue.arrayUnion(favoritedUserId),
                        "favoritedCardList", FieldValue.arrayRemove(favoritedUserId))
                .addOnSuccessListener(aVoid -> Log.d("DEBUG", "Card list updated successfully"))
                .addOnFailureListener(e -> Log.e("DEBUG", "Error updating favorited card list", e));
    }


    public static void getFavoritedCardList(String userId, final OnFavoritedCardListListener listener) {
        DB.getUserDocument(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot document = task.getResult();
                List<String> favoritedCardList = (List<String>) document.get("favoritedCardList");

                if (favoritedCardList == null) {
                    favoritedCardList = new ArrayList<>();
                }


                listener.onCardListFetched(favoritedCardList);
            } else {
                listener.onCardListFetched(new ArrayList<>());
            }
        });
    }


    public interface OnFavoritedCardListListener {
        void onCardListFetched(List<String> favoritedCardList);
    }
}
