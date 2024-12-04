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
    private String currentUserID;
    private String allInterest;
    private String gender;

    public Card() {}

    public Card(String name, String profileImageUrl, String allInterest, String location, String gender, String bio, String age, String currentUserID) {
        this.name = name;
        this.age = age;
        this.bio = bio;
        this.location = location;
        this.allInterest = allInterest;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
        this.currentUserID = currentUserID;
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

    public String getCurrentUserID() {
        return currentUserID;
    }

    public void setCurrentUserID(String currentUserID) {
        this.currentUserID = currentUserID;
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
        void onSuccess(List<Card> cards);
        void onError(String errorMessage);
    }

    public void fetchSavedCards(OnCardFetchListener listener) {
        DB.getCurrentUserDocument().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> savedCardList = (List<String>) document.get("cardList");
                    if (savedCardList == null || savedCardList.isEmpty()) {
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


    private void fetchUsersByIds(List<String> userIds, OnCardFetchListener listener) {
        if (userIds.isEmpty()) {
            listener.onSuccess(new ArrayList<>());
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
                        listener.onSuccess(cards);
                    } else {
                        listener.onError("Error fetching users by IDs: " + task.getException().getMessage());
                    }
                });
    }

    private void fetchAllUsers(OnCardFetchListener listener) {
        DB.getUsersCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Card> cards = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    UserReal user = document.toObject(UserReal.class);
                    String firstImageUrl = (user.getImageUrls() != null && !user.getImageUrls().isEmpty()) ? user.getImageUrls().get(0) : null;
                    if (!user.getUid().equals(DB.getCurrentUser().getUid())) {
                        cards.add(new Card(user.getFullName(), firstImageUrl,
                                user.displayInterest(), document.getString("detailAddress"),
                                user.getGender(), user.getBio(),
                                String.valueOf(user.getDateOfBirth()), user.getUid()));
                    }
                }
                listener.onSuccess(cards);
            } else {
                listener.onError("Error fetching all users: " + task.getException().getMessage());
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




    public static void updateCardList(String removedUserId) {
        DB.getUsersCollection().document(DB.getCurrentUser().getUid())
                .update("cardList", FieldValue.arrayRemove(removedUserId))
                .addOnSuccessListener(aVoid -> Log.d("DEBUG", "User removed from card list"))
                .addOnFailureListener(e -> Log.e("DEBUG", "Error removing user from card list", e));
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
                .update("favoritedCardList", FieldValue.arrayRemove(favoritedUserId),
                        "cardList", FieldValue.arrayUnion(favoritedUserId)
                )
                .addOnSuccessListener(aVoid -> Log.d("DEBUG", "Card list updated successfully"))
                .addOnFailureListener(e -> Log.e("DEBUG", "Error updating favorited card list", e));
    }



    public void getFavoritedCardList(String userId, final OnFavoritedCardListListener listener) {
        DB.getUserDocument(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot document = task.getResult();
                List<String> favoritedCardList = (List<String>) document.get("favoritedCardList");
                listener.onFavoritedCardListLoaded(favoritedCardList);
            } else {
                listener.onFavoritedCardListLoaded(new ArrayList<>());
            }
        });
    }

    public interface OnFavoritedCardListListener {
        void onFavoritedCardListLoaded(List<String> favoritedCardList);
    }

}
