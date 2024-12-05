package com.midterm.destined.Presenters;

import static com.midterm.destined.Models.ChatObject.checkAndAddChatList;
import static com.midterm.destined.Utils.TimeExtensions.getCurrentTime;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.midterm.destined.Models.Card;
import com.midterm.destined.Models.Match;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Views.Homepage.Card.cardView;

import java.util.List;

public class CardPresenter {
    private final cardView view;
    private final Card model;

    public CardPresenter(cardView view) {
        this.view = view;
        this.model = new Card();
    }

    public void loadCards() {
        List<String> savedCardList = view.getSavedCardListFromSharedPreferences(DB.getCurrentUser().getUid());

        if (savedCardList != null && !savedCardList.isEmpty()) {
            Log.d("DEBUG", savedCardList.toString());
            // Nếu có savedCardList từ local, hiển thị thẻ từ danh sách này
            model.fetchUsersByIds(savedCardList, new Card.OnCardFetchListener() {
                @Override
                public void onSuccess(List<Card> allUsers, List<String> savedCardList) {
                    view.displayCards(allUsers);  // Hiển thị danh sách thẻ từ local
                }

                @Override
                public void onError(String errorMessage) {
                    view.showError(errorMessage);
                }
            });
        } else {
            // Nếu không có savedCardList trong local, kiểm tra cardList trên Firebase
            DB.getCurrentUserDocument().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> firebaseCardList = (List<String>) document.get("cardList");

                        if (firebaseCardList == null || firebaseCardList.isEmpty()) {
                            model.setAllUserToDB();  // Lấy tất cả userId và cập nhật Firebase
                        }

                        // Sau khi đã lấy cardList từ Firebase hoặc cập nhật, lấy tất cả người dùng từ Firebase
                        model.fetchAllUsers(new Card.OnCardFetchListener() {
                            @Override
                            public void onSuccess(List<Card> allUsers, List<String> savedCardList) {
                                // Lưu danh sách card vào local (Gson hoặc SharedPreferences)
                                view.saveCardListToSharedPreferences(DB.getCurrentUser().getUid(),savedCardList);  // Hàm này cần được bạn triển khai để lưu vào local

                                // Hiển thị danh sách thẻ
                                view.displayCards(allUsers);
                            }

                            @Override
                            public void onError(String errorMessage) {
                                view.showError(errorMessage);
                            }
                        });

                    }
                } else {
                    view.showError("Error fetching saved cards: " + task.getException().getMessage());
                }
            });
        }
    }




    public void handleLeftSwipe(String cardID) {
        Card.removeCardList(cardID);
    }

    public void handleRightSwipe(Card card) {
        Card.addToFavoritedList(card.getUserID());
        checkIfMatched(card.getUserID(), card.getName());
    }

    public void checkIfMatched(String favoritedUserId, String favoritedUserName) {
        view.showLoading();
        String currentUserId = DB.getCurrentUser().getUid();

        model.getFavoritedCardList(favoritedUserId, favoritedCardList -> {
            if (favoritedCardList.contains(currentUserId)) {
                String matchId = currentUserId.compareTo(favoritedUserId) < 0
                        ? currentUserId + "_" + favoritedUserId
                        : favoritedUserId + "_" + currentUserId;

                Match match = new Match(matchId, currentUserId, favoritedUserId, getCurrentTime());

                model.saveMatchToDB(match, favoritedUserName);
                view.showMatchPopup(favoritedUserName);

                checkAndAddChatList(currentUserId, favoritedUserId, match.getTimestamp());
            } else {
                view.hideLoading();
            }
        });
    }
}
