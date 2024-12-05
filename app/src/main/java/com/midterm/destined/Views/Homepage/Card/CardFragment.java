package com.midterm.destined.Views.Homepage.Card;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.midterm.destined.Adapters.CardAdapter;
import com.midterm.destined.Presenters.CardPresenter;
import com.midterm.destined.R;
import com.midterm.destined.Models.Card;
import com.midterm.destined.Utils.DB;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CardFragment extends Fragment implements cardView {

    private CardPresenter presenter;
    private final List<Card> cardList = new ArrayList<>();
    private CardAdapter adapter;
    private SwipeFlingAdapterView flingContainer;
    private static CardFragment instance;

    public static CardFragment getInstance() {
        if (instance == null) {
            instance = new CardFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);

        presenter = new CardPresenter(this);
        adapter = new CardAdapter(getContext(), cardList);
        flingContainer = view.findViewById(R.id.frame);




        if (flingContainer == null) {
            Log.e("CardFragment", "flingContainer is null. Check the layout file.");
        } else {
            flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    cardList.remove(0);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onLeftCardExit(Object dataObject) {
                    // Xử lý khi quẹt trái
                    Card card = (Card) dataObject;
                    presenter.handleLeftSwipe(card.getUserID());




                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    // Xử lý khi quẹt phải
                    Card card = (Card) dataObject;
                    presenter.handleRightSwipe(card);




                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) {
                    // Thêm logic nếu cần khi adapter gần hết dữ liệu
                    Log.d("Swipe", "Adapter is about to empty");
                }

                @Override
                public void onScroll(float scrollProgressPercent) {
                    // Xử lý khi thẻ đang được kéo
                }
            });

            flingContainer.setAdapter(adapter);
        }
        presenter.loadCards();


        return view;
    }

    @Override
    public void showLoading() {
        // Hiển thị loading
    }

    @Override
    public void hideLoading() {
        // Ẩn loading
    }


    @Override
    public void displayCards(List<Card> cards) {
        cardList.clear();
        cardList.addAll(cards);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        Log.e("CardFragment", message);
    }

    @Override
    public NavController getNavController() {
        return Navigation.findNavController(requireView());

    }


    @Override
    public SwipeFlingAdapterView getFlingContainer() {
        if (flingContainer != null) {
            return flingContainer;
        } else {
            Log.e("CardFragment", "FlingContainer is null in getFlingContainer()");
            return null;
        }
    }









}
