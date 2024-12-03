package com.midterm.destined.Presenters;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.midterm.destined.Models.Card;
import com.midterm.destined.Views.Homepage.Card.CardFragment;
import com.midterm.destined.Views.Homepage.HomepageContract;

import java.util.ArrayList;
import java.util.List;

public class HomepagePresenter implements HomepageContract.Presenter {

    private final HomepageContract.View view;
    private final FirebaseFirestore db;

    public HomepagePresenter(HomepageContract.View view) {
        this.view = view;
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public void fetchCards(String userId) {
        view.showLoading();
        db.collection("cards")
                .whereNotEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    view.hideLoading();
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Card> cards = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            cards.add(doc.toObject(Card.class));
                        }
                        view.showCards(cards);
                    } else {
                        view.showError("Failed to load cards.");
                    }
                })
                .addOnFailureListener(e -> {
                    view.hideLoading();
                    view.showError("Error: " + e.getMessage());
                });
    }

    @Override
    public void likeCard(CardFragment cf) {
//        db.collection("likedCards")
//                .add(card)
//                .addOnSuccessListener(docRef -> view.showMessage("Liked successfully!"))
//                .addOnFailureListener(e -> view.showError("Failed to like card: " + e.getMessage()));
        cf.getFlingContainer().getTopCardListener().selectRight();

    }

    @Override
    public void dislikeCard(CardFragment cf) {
//        db.collection("dislikedCards")
//                .add(card)
//                .addOnSuccessListener(docRef -> view.showMessage("Disliked successfully!"))
//                .addOnFailureListener(e -> view.showError("Failed to dislike card: " + e.getMessage()));
        cf.getFlingContainer().getTopCardListener().selectLeft();

    }
}
