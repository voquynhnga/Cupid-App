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

public class CardFragment extends Fragment implements cardView {

    private CardPresenter presenter;
    private final List<Card> cardList = new ArrayList<>();
    private CardAdapter adapter;
    private SwipeFlingAdapterView flingContainer;
    private static CardFragment instance;
    private List<String> savedCardList;

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

        savedCardList = getSavedCardListFromSharedPreferences(DB.getCurrentUser().getUid());
        if (savedCardList == null) {
            savedCardList = new ArrayList<>();
        }

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
                    updateSavedCardList(card);


                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    // Xử lý khi quẹt phải
                    Card card = (Card) dataObject;
                    presenter.handleRightSwipe(card);
                    updateSavedCardList(card);


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
    public void showMatchPopup(String matchedUserName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("It's a Match!");
        builder.setMessage("You and " + matchedUserName + " have liked each other!");

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
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


    private void updateSavedCardList(Card card) {

        String userIDToRemove = card.getUserID();
        // Lấy danh sách savedCardList từ SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String savedCardListJson = sharedPreferences.getString("savedCardList", "[]");

        // Chuyển JSON thành danh sách bằng Gson
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>(){}.getType();
        List<String> savedCardList = gson.fromJson(savedCardListJson, listType);

        // Xóa userID tương ứng trong savedCardList
        savedCardList.remove(userIDToRemove);

        // Lưu lại danh sách đã thay đổi vào SharedPreferences
        String updatedSavedCardListJson = gson.toJson(savedCardList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedCardList", updatedSavedCardListJson);
        editor.apply();
    }



    @Override
    public void saveCardListToSharedPreferences(String userId, List<String> savedCardList) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(savedCardList);
        editor.putString("savedCardList_" + userId, json); // Sử dụng userId để phân biệt danh sách
        editor.apply();
    }


    @Override
    public List<String> getSavedCardListFromSharedPreferences(String userId) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("savedCardList_" + userId, null); // Lấy danh sách theo userId
        if (json == null) {
            return new ArrayList<>(); // Trả về danh sách rỗng nếu chưa có danh sách nào được lưu
        }
        Type type = new TypeToken<List<String>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }


}
