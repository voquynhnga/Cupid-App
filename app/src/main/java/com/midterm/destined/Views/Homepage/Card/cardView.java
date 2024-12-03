package com.midterm.destined.Views.Homepage.Card;


import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.midterm.destined.Models.Card;

import java.util.List;

public interface cardView {
    void showLoading();
    void hideLoading();
    void displayCards(List<Card> cards);
    void showError(String message);
    void showMatchPopup(String matchedUserName);

    SwipeFlingAdapterView getFlingContainer();
}

