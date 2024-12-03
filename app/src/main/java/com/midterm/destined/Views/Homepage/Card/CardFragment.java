package com.midterm.destined.Views.Homepage.Card;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.midterm.destined.Adapters.CardAdapter;
import com.midterm.destined.Presenters.CardPresenter;
import com.midterm.destined.R;
import com.midterm.destined.Models.Card;

import java.util.ArrayList;
import java.util.List;

public class CardFragment extends Fragment implements cardView {

    private CardPresenter presenter;
    private List<Card> cardList = new ArrayList<>();
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

        // Khởi tạo presenter và adapter
        presenter = new CardPresenter(this);
        adapter = new CardAdapter(getContext(), cardList);

        // Lấy flingContainer từ layout
        flingContainer = view.findViewById(R.id.frame);

        // Kiểm tra xem flingContainer có null không
        if (flingContainer == null) {
            Log.e("CardFragment", "flingContainer is null. Check the layout file.");
        } else {
            // Nếu flingContainer không null, thiết lập các sự kiện
            flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    // Xóa item đầu tiên trong danh sách
                    cardList.remove(0);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onLeftCardExit(Object dataObject) {
                    // Xử lý khi quẹt trái
                    Card card = (Card) dataObject;
                    presenter.handleLeftSwipe(card.getCurrentUserID());
                }

                @Override
                public void onRightCardExit(Object dataObject) {
                    // Xử lý khi quẹt phải
                    Card card = (Card) dataObject;
                    presenter.handleRightSwipe(card.getCurrentUserID());
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

        presenter.loadCards(Card.fetchCurrentUserID());

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
        // Trả về flingContainer nếu không null
        if (flingContainer != null) {
            return flingContainer;
        } else {
            Log.e("CardFragment", "FlingContainer is null in getFlingContainer()");
            return null;
        }
    }
}
