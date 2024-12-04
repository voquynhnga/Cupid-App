package com.midterm.destined.Presenters;

import com.midterm.destined.Models.Card;
import com.midterm.destined.Models.OnMatchSaveListener;
import com.midterm.destined.Models.UserReal;
import com.midterm.destined.Views.Homepage.Card.cardView;
import java.util.ArrayList;
import java.util.List;

public class CardPresenter {
    private cardView view;
    private Card model;

    public CardPresenter(cardView view) {
        this.view = view;
        this.model = new Card();
    }

    public void loadCards(String currentUserId) {
        view.showLoading();
        model.fetchSavedCards(currentUserId, new Card.OnCardFetchListener() {
            @Override
            public void onSuccess(List<Card> cards) {
                view.hideLoading();
                view.displayCards(cards);
            }

            @Override
            public void onError(String errorMessage) {
                view.hideLoading();
                view.showError(errorMessage);
            }
        });
    }
    public void handleLeftSwipe(String cardID){
        model.updateCardList(cardID);
    }
    public void handleRightSwipe(String cardID){
        model.updateFavoritedCardList(cardID);
    }

    public void checkIfMatched(UserReal user, String currentUserId) {
        view.showLoading();
        String favoritedUserId = user.getUid();

        model.getFavoritedCardList(favoritedUserId, new Card.OnCardFetchListener() {
            @Override
            public void onSuccess(List<Card> favoritedCardListOfA) {
                if (favoritedCardListOfA.contains(currentUserId)) {
                    String matchId = currentUserId.compareTo(favoritedUserId) < 0
                            ? currentUserId + "_" + favoritedUserId
                            : favoritedUserId + "_" + currentUserId;

                    model.saveMatch(matchId, currentUserId, favoritedUserId,user.getFullName(), new OnMatchSaveListener() {
                        @Override
                        public void onSuccess(String matchedUserName) {
                            view.hideLoading();
                            view.showMatchPopup(matchedUserName);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            view.hideLoading();
                            view.showError(errorMessage);
                        }
                    });
                } else {
                    view.hideLoading();
                }
            }

            @Override
            public void onError(String errorMessage) {
                view.hideLoading();
                view.showError(errorMessage);
            }
        });
    }


}

