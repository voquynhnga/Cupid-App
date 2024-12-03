package com.midterm.destined.Views.Homepage;

import com.midterm.destined.Models.Card;
import com.midterm.destined.Views.Homepage.Card.CardFragment;

import java.util.List;

public interface HomepageContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showCards(List<Card> cards);
        void showError(String error);
        void showMessage(String message);
    }

    interface Presenter {
        void likeCard(CardFragment cardFragment);
        void dislikeCard(CardFragment cardFragment);
    }
}
