package com.midterm.destined.Presenters;

import static com.midterm.destined.Models.ChatObject.checkAndAddChatList;
import static com.midterm.destined.Utils.TimeExtensions.getCurrentTime;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.DocumentSnapshot;
import com.midterm.destined.Models.Card;
import com.midterm.destined.Models.Match;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Utils.Dialog;
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
        DB.getCurrentUserDocument()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();

                        // Lấy danh sách cardList
                        List<String> firebaseCardList = (List<String>) document.get("cardList");

                        Log.d("DEBUG", firebaseCardList.toString());

                        if (firebaseCardList.isEmpty()) {
                            // Nếu cardList rỗng, lấy tất cả người dùng
                            model.fetchAllUsersAndUpdateCardList(new Card.OnCardFetchListener() {
                                @Override
                                public void onSuccess(List<Card> allUsers) {
                                    view.displayCards(allUsers);
                                    Log.d("DEBUG", "khong");
                                    Log.d("DEBUG", "khong || " + allUsers.toString());

                                }

                                @Override
                                public void onError(String errorMessage) {
                                    view.showError(errorMessage);
                                }
                            });
                        } else {
                            // Nếu đã có cardList, lấy thông tin chi tiết các user
                            model.fetchUsersByIds(firebaseCardList, new Card.OnCardFetchListener() {
                                @Override
                                public void onSuccess(List<Card> cards) {
                                    Log.d("DEBUG", "co");
                                    Log.d("DEBUG", "co || " + cards.toString());
                                    view.displayCards(cards);
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    view.showError(errorMessage);
                                }
                            });
                        }
                    } else {
                        // Xử lý lỗi nếu không thể lấy tài liệu người dùng hiện tại
                        view.showError("Error fetching saved cards: " + task.getException().getMessage());
                    }
                });
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



                Dialog.showMatchPopup(view.getContext(), favoritedUserName, view.getNavController());

                checkAndAddChatList(currentUserId, favoritedUserId, match.getTimestamp());
            } else {
                view.hideLoading();
            }
        });
    }
}