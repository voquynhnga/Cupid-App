package com.midterm.destined.Models;

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

public class Card {

    private String name;
    private String age;
    private String bio;
    private String location;
    private String profileImageUrl;
    private String userID;
    private String allInterest;
    private String gender;

    public Card() {}

    public Card(String name, String profileImageUrl, String allInterest, String location, String gender, String bio, String age, String userID) {
        this.name = name;
        this.age = age;
        this.bio = bio;
        this.location = location;
        this.allInterest = allInterest;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
        this.userID = userID;
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

    public interface OnCardFetchListener {
        void onSuccess(List<Card> cards, List<String> savedCardList);
        void onError(String errorMessage);
    }

    public void getCardListFromDB(OnCardFetchListener listener) {
        DB.getCurrentUserDocument().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> savedCardList = (List<String>) document.get("cardList");
                    if (savedCardList == null || savedCardList.isEmpty()) {
                        setAllUserToDB();
                        fetchAllUsers(listener);
                    } else {
                        fetchUsersByIds(savedCardList, listener);
                    }
                } else {
                    fetchAllUsers(listener);
                }
            } else {
                listener.onError("Error fetching saved cards: " + task.getException().getMessage());
            }
        });
    }

    public void setAllUserToDB() {
        // Lấy tài liệu của người dùng hiện tại
        DB.getCurrentUserDocument()
                .get()
                .addOnCompleteListener(task -> {
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
                                List<String> allUserIds = new ArrayList<>();

                                for (QueryDocumentSnapshot document : userTask.getResult()) {
                                    String userId = document.getString("uid");
                                    if (userId != null
                                            && !userId.equals(DB.getCurrentUser().getUid()) // Loại trừ chính mình
                                            && !finalFavoritedCardList.contains(userId)) { // Loại trừ user trong favoritedCardList
                                        allUserIds.add(userId);
                                    }
                                }

                                // Cập nhật cardList với danh sách đã lọc
                                if (!allUserIds.isEmpty()) {
                                    DB.getCurrentUserDocument()
                                            .update("cardList", FieldValue.arrayUnion(allUserIds.toArray()))
                                            .addOnSuccessListener(aVoid -> Log.d("DEBUG", "All user IDs added to card list successfully."))
                                            .addOnFailureListener(e -> Log.e("DEBUG", "Error adding user IDs to card list", e));
                                }
                            } else {
                                Log.e("DEBUG", "Error fetching all users", userTask.getException());
                            }
                        });
                    } else {
                        Log.e("DEBUG", "Error fetching favoritedCardList", task.getException());
                    }
                });
    }



    public void fetchUsersByIds(List<String> userIds, OnCardFetchListener listener) {
        if (userIds.isEmpty()) {
            listener.onSuccess(new ArrayList<>(), userIds);
            return;
        }

        DB.getUsersCollection()
                .whereIn("uid", userIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Card> cards = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserReal user = document.toObject(UserReal.class);
                            String firstImageUrl = (user.getImageUrls() != null && !user.getImageUrls().isEmpty()) ? user.getImageUrls().get(0) : null;
                            cards.add(new Card(user.getFullName(), firstImageUrl,
                                    user.displayInterest(), document.getString("detailAddress"),
                                    user.getGender(), user.getBio(),
                                    String.valueOf(TimeExtensions.calculateAge(user.getDateOfBirth())), user.getUid()));
                        }
                        listener.onSuccess(cards, userIds);
                    } else {
                        listener.onError("Error fetching users by IDs: " + task.getException().getMessage());
                    }
                });
    }

    public void fetchAllUsers(OnCardFetchListener listener) {
        // Lấy tài liệu của người dùng hiện tại để lấy danh sách favoritedCardList
        DB.getCurrentUserDocument().get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // Lấy danh sách favoritedCardList
                List<String> favoritedCardList = (List<String>) task.getResult().get("favoritedCardList");
                if (favoritedCardList == null) {
                    favoritedCardList = new ArrayList<>();
                }

                // Lấy danh sách tất cả người dùng
                List<String> finalFavoritedCardList = favoritedCardList;
                DB.getUsersCollection().get().addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful()) {
                        List<Card> cards = new ArrayList<>();

                        for (QueryDocumentSnapshot document : userTask.getResult()) {
                            UserReal user = document.toObject(UserReal.class);

                            // Lấy URL hình ảnh đầu tiên
                            String firstImageUrl = (user.getImageUrls() != null && !user.getImageUrls().isEmpty()) ? user.getImageUrls().get(0) : null;

                            // Loại trừ chính mình và những người trong favoritedCardList
                            if (!user.getUid().equals(DB.getCurrentUser().getUid())
                                    && !finalFavoritedCardList.contains(user.getUid())) {
                                cards.add(new Card(
                                        user.getFullName(),
                                        firstImageUrl,
                                        user.displayInterest(),
                                        document.getString("detailAddress"),
                                        user.getGender(),
                                        user.getBio(),
                                        String.valueOf(user.getDateOfBirth()),
                                        user.getUid()
                                ));
                            }
                        }

                        // Gọi callback thành công
                        listener.onSuccess(cards, new ArrayList<>());
                    } else {
                        // Gọi callback lỗi khi không lấy được danh sách tất cả người dùng
                        listener.onError("Error fetching all users: " + userTask.getException().getMessage());
                    }
                });
            } else {
                // Gọi callback lỗi khi không lấy được tài liệu người dùng hiện tại
                listener.onError("Error fetching current user data: " + task.getException().getMessage());
            }
        });
    }

    public void saveMatchToDB(Match match, String fullNameUser2) {
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


    public void getFavoritedCardList(String userId, final OnFavoritedCardListListener listener) {
        DB.getUserDocument(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot document = task.getResult();
                List<String> favoritedCardList = (List<String>) document.get("favoritedCardList");
                if (favoritedCardList != null) {
                    listener.onCardListFetched(favoritedCardList);
                } else {
                    listener.onCardListFetched(new ArrayList<>());
                }
            } else {
                listener.onCardListFetched(new ArrayList<>());
            }
        });
    }

    public interface OnFavoritedCardListListener {
        void onCardListFetched(List<String> favoritedCardList);
    }
}
