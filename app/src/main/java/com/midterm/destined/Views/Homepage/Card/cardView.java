package com.midterm.destined.Views.Homepage.Card;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.navigation.NavController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.midterm.destined.Models.Card;

import java.lang.reflect.Type;
import java.util.List;

public interface cardView {
    void showLoading();
    void hideLoading();
    void displayCards(List<Card> cards);
    void showError(String message);
    Context getContext();
    NavController getNavController();

    SwipeFlingAdapterView getFlingContainer();
}

