package com.midterm.destined.Presenters;

import static com.midterm.destined.Models.ChatObject.checkAndAddChatList;
import static com.midterm.destined.Utils.TimeExtensions.getCurrentTime;

import com.midterm.destined.Models.Card;
import com.midterm.destined.Models.Match;
import com.midterm.destined.Models.UserReal;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Views.Homepage.Card.cardView;

import java.util.List;

public class CardPresenter {
    private cardView view;
    private Card model;

    public CardPresenter(cardView view) {
        this.view = view;
        this.model = new Card();
    }

    public void loadCards() {
        view.showLoading();
        model.fetchSavedCards(new Card.OnCardFetchListener() {
            @Override
            public void onSuccess(List<Card> cards) {
//                view.hideLoading();
                view.displayCards(cards);
            }

            @Override
            public void onError(String errorMessage) {
//                view.hideLoading();
                view.showError(errorMessage);
            }
        });
    }
    public void handleLeftSwipe(String cardID){
        model.updateCardList(cardID);
    }
    public void handleRightSwipe(String cardID){
        model.addToFavoritedList(cardID);
    }

    public void checkIfMatched(UserReal user) {
        view.showLoading();
        String favoritedUserId = user.getUid();
        String currentUserId = DB.getCurrentUser().getUid();

        model.getFavoritedCardList(favoritedUserId, favoritedCardList -> {
            if (favoritedCardList.contains(currentUserId)) {
                String matchId = currentUserId.compareTo(favoritedUserId) < 0
                        ? currentUserId + "_" + favoritedUserId
                        : favoritedUserId + "_" + currentUserId;

                Match match = new Match(matchId, currentUserId, favoritedUserId, getCurrentTime());

                model.saveMatchToDB(match,user.getFullName());
//                            view.hideLoading();
                        view.showMatchPopup(user.getFullName());

                checkAndAddChatList(currentUserId, favoritedUserId, match.getTimestamp());
            } else {
                view.hideLoading();
            }
        });
    }


}

