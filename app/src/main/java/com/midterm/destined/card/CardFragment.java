package com.midterm.destined.card;
import static java.lang.reflect.Array.get;
import static java.util.Collections.addAll;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.midterm.destined.R;
import com.midterm.destined.model.UserReal;

import java.lang.reflect.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class CardFragment extends Fragment {

    private SwipeFlingAdapterView flingContainer;
    private CardAdapter cardAdapter;
    private List<Card> cardList = new ArrayList<>();
    private List<Card> favoritedCardList = new ArrayList<>();
    private List<String> savedCardList = new ArrayList<>();

    private FirebaseFirestore db;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_card, container, false);
        flingContainer = view.findViewById(R.id.frame);
        db = FirebaseFirestore.getInstance();
        String currentUserId = Card.fetchCurrentUserID();
        if (currentUserId != null) {
            fetchUsersFromFirebase(currentUserId);
        } else {
            Log.e("DEBUG", "User is not logged in.");
        }

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        flingContainer = view.findViewById(R.id.frame);
        cardAdapter = new CardAdapter(getContext(), cardList);
        flingContainer.setAdapter(cardAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                cardList.remove(0);
                cardAdapter.notifyDataSetChanged();

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Card card = (Card) dataObject;
                String removedUserId = card.getCurrentUserID();

                db.collection("users").document(card.fetchCurrentUserID())
                        .update("cardList", FieldValue.arrayRemove(removedUserId))
                        .addOnSuccessListener(aVoid -> {
                            cardList.remove(card);
                            cardAdapter.notifyDataSetChanged();
                            saveCardListToFirestore(card.fetchCurrentUserID());


                        })
                        .addOnFailureListener(e -> Log.e("DEBUG", "Error removing user from cardList", e));
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Card card = (Card) dataObject;
                String favoritedUserId = card.getCurrentUserID();

                db.collection("users").document(card.fetchCurrentUserID())
                        .update("cardList", FieldValue.arrayRemove(favoritedUserId))
                        .addOnSuccessListener(aVoid -> {
                            favoritedCardList.add(card);
                            cardList.remove(card);
                            cardAdapter.notifyDataSetChanged();

                            saveCardListToFirestore(card.fetchCurrentUserID());
                            saveFavoritedCardListToFirestore(card.fetchCurrentUserID());

                            Log.d("DEBUG", "User removed from cardList and added to favoritedCardList: " + favoritedUserId);
                        })
                        .addOnFailureListener(e -> Log.e("DEBUG", "Error removing user from cardList", e));

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Tự động load thêm dữ liệu nếu cần
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                // Xử lý khi người dùng cuộn
            }
        });
    }

    private void fetchUsersFromFirebase(String currentUserId) {
        db.collection("users").document(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    savedCardList = (List<String>) document.get("cardList");
                    if (savedCardList != null) {
                        cardList.clear();
                        fetchUsersByIds(savedCardList);


                    }
                    else {
                        fetchAllUsers(currentUserId);
                    }

                }
                else{
                    fetchAllUsers(currentUserId);
                }

            } else {
                Log.e("DEBUG", "Error getting cardList: ", task.getException());
            }
        });
    }
    private void fetchUsersByIds(List<String> userIds) {
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

        for (String userId : userIds) {
            tasks.add(db.collection("users").document(userId).get());
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (Task<?> userTask : tasks) {
                    if (userTask.isSuccessful()) {
                        DocumentSnapshot userDocument = (DocumentSnapshot) userTask.getResult();
                        if (userDocument.exists()) {
                            UserReal user = userDocument.toObject(UserReal.class);
                            List<String> imageUrls = user.getImageUrls();
                            String firstImageUrl = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;
                            Card card = new Card(user.getFullName(), firstImageUrl, user.getBio(), String.valueOf(calculateAge(user.getDateOfBirth())), user.getUid());
                            cardList.add(card);
                        }
                    }
                }
                cardAdapter.notifyDataSetChanged();
            } else {
                Log.e("DEBUG", "Error fetching users by IDs", task.getException());
            }
        });
    }




    private void fetchAllUsers(String currentUserId){
        db.collection("users").get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                for (QueryDocumentSnapshot userDocument : userTask.getResult()) {
                    UserReal user = userDocument.toObject(UserReal.class);

                    if (!user.getUid().equals(currentUserId)) {
                        List<String> imageUrls = user.getImageUrls();
                        String firstImageUrl = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;
                        Card card = new Card(user.getFullName(), firstImageUrl, user.getBio(), String.valueOf(calculateAge(user.getDateOfBirth())), user.getUid());
                        cardList.add(card);
                    }
                }
                cardAdapter.notifyDataSetChanged();
            }
        });
    }



    private void saveCardListToFirestore(String currentUserId) {
        List<String> userIds = new ArrayList<>();
        for (Card card : cardList) {
            userIds.add(card.getCurrentUserID());
        }

        db.collection("users").document(currentUserId)
                .update("cardList", userIds);
    }

    private void saveFavoritedCardListToFirestore(String currentUserId) {
        List<String> userIds = new ArrayList<>();
        for (Card card : favoritedCardList) {
            userIds.add(card.getCurrentUserID());
        }

        db.collection("users").document(currentUserId)
                .update("favoritedCardList", userIds);
    }





    public static int calculateAge(String dateOfBirth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        LocalDate birthDate = LocalDate.parse(dateOfBirth, formatter);
        LocalDate currentDate = LocalDate.now();
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();

        }
        else{
            return 0;
        }
    }

}
