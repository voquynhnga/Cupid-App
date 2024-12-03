package com.midterm.destined.card;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.midterm.destined.R;
import com.midterm.destined.chat.ChatFragment;
import com.midterm.destined.model.UserReal;
import com.midterm.destined.viewmodel.CalculateCoordinates;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardFragment extends Fragment {

    private SwipeFlingAdapterView flingContainer;
    public CardAdapter cardAdapter;
    public List<Card> cardList = new ArrayList<>();
    public List<Card> favoritedCardList = new ArrayList<>();
    public List<String> savedFavoritedCardList = new ArrayList<>();
    public List<String> savedCardList = new ArrayList<>();
    private String currentUserId = Card.fetchCurrentUserID();
    private ChatFragment chatFragment = new ChatFragment();
    private String userId;
    private String userName;
    private String avatarUser;
    private String bio;
    private String dateOfBirth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();



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
        View view =  inflater.inflate(R.layout.fragment_card, container, false);


        cardAdapter = new CardAdapter(getContext(), cardList);

        if (currentUserId != null) {
            fetchUsersFromFirebase(currentUserId);
//            checkForMatches(currentUserId);

        }

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        flingContainer = view.findViewById(R.id.frame);

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
                db.collection("users").document(currentUserId)
                        .update("cardList", FieldValue.arrayRemove(favoritedUserId),
                                "favoritedCardList", FieldValue.arrayUnion(favoritedUserId))

                        .addOnSuccessListener(aVoid -> {

                            cardList.remove(card);

                            favoritedCardList.add(card);

                            cardAdapter.notifyDataSetChanged();
                            saveCardListToFirestore(currentUserId);
                            checkIfMatched(favoritedUserId, currentUserId, getContext());
                        });


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
    public SwipeFlingAdapterView getFlingContainer() {
        return flingContainer;
    }

    public void fetchUsersFromFirebase(String currentUserId) {
        db.collection("users").document(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    savedCardList = (List<String>) document.get("cardList");

                    if (savedCardList == null || savedCardList.isEmpty()) {
                        fetchAllUsers(currentUserId);
                        Log.d("DEBUG", "1");
                    } else {
                        Log.d("DEBUG", "Saved card list: " + savedCardList.toString());
                        cardList.clear();
                        fetchUsersByIds(savedCardList);
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
    public void fetchUsersByIds(List<String> userIds) {
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
                            String detailAddress = userDocument.getString("detailAdrress");

                            Card card = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                card = new Card(user.getFullName(), firstImageUrl, user.displayInterest(), detailAddress, user.getGender(), user.getBio(), String.valueOf(calculateAge(user.getDateOfBirth())), user.getUid());
                            }
                            cardList.add(card);


                        }
                    }
                }
                cardAdapter.notifyDataSetChanged();
                Log.d("DEBUG", cardList.toString());
            } else {
                Log.e("DEBUG", "Error fetching users by IDs", task.getException());
            }
        });
        Log.d("DEBUG", "Fetched users by IDs");
    }




    public void fetchAllUsers(String currentUserId) {
        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        savedFavoritedCardList = (List<String>) documentSnapshot.get("favoritedCardList");
                    }
                    else{
                        savedFavoritedCardList = new ArrayList<>();
                    }

                    Log.d("DEBUG", "hd1");
                    db.collection("users").get().addOnCompleteListener(userTask -> {
                        if (userTask.isSuccessful()) {
                            Log.d("DEBUG", "hd2");
                            for (QueryDocumentSnapshot userDocument : userTask.getResult()) {
                                UserReal user = userDocument.toObject(UserReal.class);
                                if(user != null) {
                                    Log.d("DEBUG", "hd3");

                                    if (!user.getUid().equals(currentUserId) &&
                                            (savedFavoritedCardList == null || !savedFavoritedCardList.contains(user.getUid()))) {
                                        Log.d("DEBUG", "hd4");

                                        List<String> imageUrls = user.getImageUrls();
                                        String firstImageUrl = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;
                                        String detailAddress = userDocument.getString("detailAdrress");
                                        Card card = null;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                            card = new Card(user.getFullName(), firstImageUrl, user.displayInterest(),
                                                    detailAddress, user.getGender(), user.getBio(),
                                                    String.valueOf(calculateAge(user.getDateOfBirth())), user.getUid());
                                        }

                                        cardList.add(card);
                                    }
                                }

                            }
                            cardAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("DEBUG", "Error fetching users: ", userTask.getException());
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("DEBUG", "Error getting favoritedCardList for current user", e);
                });
    }






    public void saveCardListToFirestore(String currentUserId) {
        List<String> userIds = new ArrayList<>();
        for (Card card : cardList) {
            userIds.add(card.getCurrentUserID());
        }

        db.collection("users").document(currentUserId)
                .update("cardList", userIds);
    }

    public void saveFavoritedCardListToFirestore(String currentUserId) {
        List<String> userIds = new ArrayList<>();
        for (Card card : favoritedCardList) {
            userIds.add(card.getCurrentUserID());
        }

        db.collection("users").document(currentUserId)
                .update("favoritedCardList", userIds);
    }


    public void checkIfMatched(String favoritedUserId, String currentUserId, Context context) {
        db.collection("users").document(favoritedUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                String fullNameUser2 = document.getString("fullName");
                if (document.exists()) {
                    List<String> favoritedCardListOfA = (List<String>) document.get("favoritedCardList");
                    if (favoritedCardListOfA != null && favoritedCardListOfA.contains(currentUserId)) {
                        String matchId = currentUserId.compareTo(favoritedUserId) < 0 ? currentUserId + "" + favoritedUserId : favoritedUserId+ "" + currentUserId;


                        db.collection("matches").document(matchId).get().addOnCompleteListener(matchTask -> {
                            if (matchTask.isSuccessful() && !matchTask.getResult().exists()) {
                                saveMatchToDatabase(currentUserId, favoritedUserId, fullNameUser2);
                                checkAndAddChatList(currentUserId, favoritedUserId);
                                showMatchPopup(fullNameUser2, context);
                            } else if (matchTask.isSuccessful() && matchTask.getResult().exists()) {
                                showMatchPopup(fullNameUser2, context);
                            } else {
                                Log.e("ERROR", "Failed to check match existence", matchTask.getException());
                            }
                        });
                    }
                } else {
                    Log.d("NO_DOCUMENT", "No document found for user " + favoritedUserId);
                }
            } else {
                Log.e("ERROR", "Failed to fetch favoritedCardList of user " + favoritedUserId, task.getException());
            }
        });
    }

    private void checkAndAddChatList(String userID1, String userID2) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats");
        String chatID = userID1.compareTo(userID2) < 0 ? userID1 + "" + userID2 : userID2 + "" + userID1;

        chatRef.child(chatID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // If chat does not exist, create it
                    Map<String, Object> chatData = new HashMap<>();
                    chatData.put("userID1", userID1);
                    chatData.put("userID2", userID2);
                    chatData.put("lastMessage", "Let's start chat!");

                    chatRef.child(chatID).setValue(chatData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("RealtimeDB", "Chat created successfully between " + userID1 + " and " + userID2);
                        } else {
                            Log.e("RealtimeDB", "Failed to create chat", task.getException());
                        }
                    });
                } else {
                    Log.d("RealtimeDB", "Chat between " + userID1 + " and " + userID2 + " already exists, skipping creation.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RealtimeDB", "Failed to check for existing chat", error.toException());
            }
        });
    }


    private void saveMatchToDatabase(String userId1, String userId2, String fullNameUser2) {
        Map<String, Object> match = new HashMap<>();
        match.put("userId1", userId1);
        match.put("userId2", userId2);
        match.put("timestamp", FieldValue.serverTimestamp());

        String matchId = userId1 + "_" + userId2;
        db.collection("matches").document(matchId).set(match)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Match saved with " + fullNameUser2))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding match", e));
    }

    private void showMatchPopup(String fullNameUser2, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("It's a Match!");
        String message = "You and " + fullNameUser2 + " have liked each other!";
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
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
    // private void checkForMatches(String currentUserId) {
//        db.collection("matches").whereEqualTo("userId1", currentUserId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot matchDocument : task.getResult()) {
//                            String fullNameUser2 = matchDocument.getString("fullName");
//                            showMatchPopup(fullNameUser2);
//
//                        }
//                    } else {
//                        Log.e("MATCH_CHECK_ERROR", "Error checking matches", task.getException());
//                    }
//                });
//    }

}