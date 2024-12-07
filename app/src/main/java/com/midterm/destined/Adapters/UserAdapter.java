package com.midterm.destined.Adapters;

import static com.midterm.destined.Models.ChatObject.deleteChat;
import static com.midterm.destined.Utils.TimeExtensions.getCurrentTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.midterm.destined.Models.Card;
import com.midterm.destined.Models.ChatObject;
import com.midterm.destined.Models.Match;
import com.midterm.destined.Models.OnChatIdCheckListener;
import com.midterm.destined.Models.UserReal;
import com.midterm.destined.R;
import com.midterm.destined.Utils.CalculateCoordinates;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Utils.Dialog;
import com.midterm.destined.Utils.TimeExtensions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    private final Context mContext;
    private final List<UserReal> mUsers;
    private final NavController mNavController;
    private final Map<String, Boolean> matchedUsersCache = new HashMap<>();
    private final Map<String, Boolean> favoritedUsersCache = new HashMap<>();



    public UserAdapter(Context context, List<UserReal> users, NavController navController) {
        this.mContext = context;
        this.mUsers = users;
        this.mNavController = navController;

    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        UserReal user = mUsers.get(position);

        holder.username.setText(user.getUserName());
        holder.fullname.setText(user.getFullName());
        Glide.with(mContext)
                .load(user.getProfilePicture())
                .error(R.drawable.avatardefault)
                .into(holder.image_profile);

        holder.btn_like.setVisibility(View.INVISIBLE);
        holder.btn_unlike.setVisibility(View.INVISIBLE);
        holder.btn_unmatch.setVisibility(View.INVISIBLE);


        if (!matchedUsersCache.containsKey(user.getUid())) {
            checkIfMatched(user, holder);
        } else {
            updateButtonState(user, holder);
        }

        holder.btn_like.setOnClickListener(v -> handleLikeAction(user, holder));
        holder.btn_unlike.setOnClickListener(v -> handleUnLikeAction(user, holder));

        holder.btn_unmatch.setOnClickListener(v -> handleUnMatchAction(user, holder));

        holder.itemView.setOnClickListener(v -> {
            fetchUsersById(user, user.getUid(), new OnUserFetchListener() {
                @Override
                public void onCardFetched(UserReal user, Card card) {
                    showCardPopup(user, card, v);

                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(mContext, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
    private void showCardPopup(UserReal user, Card card, View view) {
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.dialog_card, null);


        TextView name = popupView.findViewById(R.id.userNameSearch);
        TextView age = popupView.findViewById(R.id.ageSearch);
        TextView location = popupView.findViewById(R.id.locationSearch);
        TextView hobby = popupView.findViewById(R.id.hobbySearch);
        TextView bio = popupView.findViewById(R.id.bioSearch);
        TextView tvDistance = popupView.findViewById(R.id.distanceSearch);
        ImageView genderIcon = popupView.findViewById(R.id.genderIconSearch);
        ImageView btnClose = popupView.findViewById(R.id.closeSearch);
        TextView btnChat = popupView.findViewById(R.id.chatSearch);
        ViewPager2 profileImageSearchPager = popupView.findViewById(R.id.profileImageSearchPager);
        ImageView profileImageSearch1 = popupView.findViewById(R.id.profileImageSearch1);


        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        int width = (int) (screenWidth * 0.9);
        int height = (int) (screenHeight * 0.7);

        PopupWindow popupWindow = new PopupWindow(popupView, width, height);


        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        String currentUserUID = DB.getCurrentUser().getUid();

        CalculateCoordinates.calculateDistance(currentUserUID, card.getUserID(), distance -> {

            if (distance < 10) {
                tvDistance.setText("📍 < 10Km");
            } else {
                tvDistance.setText("📍 " + String.format("%d", (int) (distance)) + " Km");

            }
        });

        name.setText(card.getName());
        location.setText("🏠" + card.getLocation());
        age.setText(card.getAge());
        bio.setText("📝️ " + card.getBio());
        hobby.setText(card.getAllInterest());

        if ("Male".equals(card.getGender())) {
            genderIcon.setImageResource(R.drawable.male_picture);
        } else {
            genderIcon.setImageResource(R.drawable.female_picture);
        }


            if (card.getImageUrls() != null && !card.getImageUrls().isEmpty()) {
                if (card.getImageUrls().size() > 1) {
                    profileImageSearchPager.setAdapter(new ImagePagerAdapter(card.getImageUrls(), view.getContext()));
                    profileImageSearchPager.setVisibility(View.VISIBLE);
                    profileImageSearch1.setVisibility(View.GONE);
                } else {
                    profileImageSearchPager.setVisibility(View.GONE);
                    profileImageSearch1.setVisibility(View.VISIBLE);
                    Glide.with(view.getContext()).load(card.getProfileImageUrl()).into(profileImageSearch1);
                }
            } else {
                profileImageSearchPager.setVisibility(View.GONE);
                profileImageSearch1.setVisibility(View.VISIBLE);
                Glide.with(view.getContext()).load(R.drawable.avatardefault).into(profileImageSearch1);
            }

            if (user.isMatched()) {
                btnChat.setVisibility(popupView.VISIBLE);
            }
            btnChat.setOnClickListener(v -> {
                ChatObject.checkChatId(user.getUid(), new OnChatIdCheckListener() {
                    @Override
                    public void onChatIdFound(String chatId) {
                        Bundle bundle = new Bundle();
                        bundle.putString("chatId", chatId);
                        bundle.putString("userId", card.getUserID());
                        bundle.putString("userName", card.getName());

                        NavController navController = Navigation.findNavController((Activity) v.getContext(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.action_global_ChatFragment, bundle);

                        popupWindow.dismiss();
                    }

                    @Override
                    public void onChatIdNotFound() {
                        Log.e("checkChatId", "Chat ID not found.");
                    }
                });


            });


            btnClose.setOnClickListener(v -> {
                popupWindow.dismiss();
            });



    }



    private void fetchUsersById(UserReal user, String userId, OnUserFetchListener listener) {
        DB.getUsersCollection()
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        UserReal userDB = document.toObject(UserReal.class);

                        if (userDB != null) {
                            Card card = new Card(user);
                            listener.onCardFetched(user,card);
                        } else {
                            listener.onError("User not found");
                        }
                    } else {
                        listener.onError("Error fetching user: " + task.getException().getMessage());
                    }
                });
    }





    private void checkIfMatched(UserReal user, ImageViewHolder holder) {
        String currentUserId = DB.getCurrentUser().getUid();

        // Bước 1: Lấy danh sách favorited của currentUserId
        DB.getCurrentUserDocument().get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> currentUserFavorited = (List<String>) documentSnapshot.get("favoritedCardList");

                        // Bước 2: Kiểm tra nếu user.getUid() nằm trong danh sách favorited của currentUserId
                        if (currentUserFavorited != null && currentUserFavorited.contains(user.getUid())) {
                            // Bước 3: Lấy danh sách favorited của user.getUid()
                            DB.getUserDocument(user.getUid()).get()
                                    .addOnSuccessListener(userSnapshot -> {
                                        if (userSnapshot.exists()) {
                                            List<String> userFavorited = (List<String>) userSnapshot.get("favoritedCardList");

                                            // Bước 4: Kiểm tra nếu currentUserId nằm trong danh sách favorited của user.getUid()
                                            if (userFavorited != null && userFavorited.contains(currentUserId)) {
                                                // Nếu cả hai favorited lẫn nhau, họ được coi là matched
                                                user.setMatched(true);
                                                matchedUsersCache.put(user.getUid(), true);

                                                updateButtonState(user, holder);
                                            } else {
                                                // Nếu không match
                                                user.setMatched(false);
                                                matchedUsersCache.put(user.getUid(), false);
                                                checkFavoritedList(user, holder);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error getting user data", e);
                                    });
                        } else {
                            // Nếu không match
                            user.setMatched(false);
                            matchedUsersCache.put(user.getUid(), false);
                            checkFavoritedList(user, holder);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting current user data", e);
                });
    }

    private void checkFavoritedList(UserReal user, ImageViewHolder holder) {
        DB.getCurrentUserDocument()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> favoritedCardList = (List<String>) task.getResult().get("favoritedCardList");
                        boolean isFavorited = favoritedCardList != null && favoritedCardList.contains(user.getUid());
                        user.setFavorited(isFavorited);
                        favoritedUsersCache.put(user.getUid(), isFavorited);
                        updateButtonState(user, holder);
                    }
                });
    }

    private void updateButtonState(UserReal user, ImageViewHolder holder) {
        if (user.isMatched()) {
            holder.btn_like.setVisibility(View.INVISIBLE);
            holder.btn_unlike.setVisibility(View.INVISIBLE);
            holder.btn_unmatch.setVisibility(View.VISIBLE);
        } else if (user.isFavorited()) {
            holder.btn_like.setVisibility(View.INVISIBLE);
            holder.btn_unlike.setVisibility(View.VISIBLE);
            holder.btn_unmatch.setVisibility(View.INVISIBLE);
        } else {
            holder.btn_like.setVisibility(View.VISIBLE);
            holder.btn_unlike.setVisibility(View.INVISIBLE);
            holder.btn_unmatch.setVisibility(View.INVISIBLE);
        }
    }



    private void handleLikeAction(UserReal user, ImageViewHolder holder) {
        String favoritedUserId = user.getUid();
        String currentUserId = DB.getCurrentUser().getUid();
        try {
            Card.addToFavoritedList(favoritedUserId);
            checkIfMatched(user, holder);
            if (matchedUsersCache.get(user.getUid()) != null && Boolean.TRUE.equals(matchedUsersCache.get(user.getUid()))) {

            String matchId = currentUserId.compareTo(favoritedUserId) < 0
                        ? currentUserId + "_" + favoritedUserId
                        : favoritedUserId + "_" + currentUserId;

                Match match = new Match(matchId, currentUserId, favoritedUserId, getCurrentTime());
                Card.saveMatchToDB(match, user.getFullName());
                ChatObject.checkAndAddChatList(currentUserId, favoritedUserId, getCurrentTime());
            }
            holder.btn_like.setVisibility(View.INVISIBLE);
            holder.btn_unlike.setVisibility(View.VISIBLE);
            holder.btn_unmatch.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.e("FirestoreError", "Failed to update follow action", e);
        }
    }
    private void handleUnLikeAction(UserReal user, ImageViewHolder holder) {
        String favoritedUserId = user.getUid();

        try {
            Card.removeToFavoritedList(favoritedUserId);
            holder.btn_like.setVisibility(View.VISIBLE);
            holder.btn_unlike.setVisibility(View.INVISIBLE);
            holder.btn_unmatch.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.e("FirestoreError", "Failed to update follow action", e);
        }
    }


    private void handleUnMatchAction(UserReal user, ImageViewHolder holder) {
        new AlertDialog.Builder(mContext)
                .setTitle("Confirm Unmatch")
                .setMessage("Are you sure you want to unmatch this user? Your conversation will be permanently deleted.")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String currentUserId = DB.getCurrentUser().getUid();
                    String favoritedUserId = user.getUid();
                    try {
                        Card.removeToFavoritedList(favoritedUserId);

                        deleteMatch(currentUserId, favoritedUserId);
                        deleteChat(currentUserId, favoritedUserId);

                        holder.btn_like.setVisibility(View.VISIBLE);
                        holder.btn_unmatch.setVisibility(View.INVISIBLE);
                        holder.btn_unlike.setVisibility(View.INVISIBLE);

                    } catch (Exception e) {
                        Log.e("FirestoreError", "Failed to update unfollow action", e);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }


    private void deleteMatch(String currentUserId, String favoritedUserId) {
        DB.getMatchesCollection()
                .whereIn("userId1", List.of(currentUserId, favoritedUserId))
                .whereIn("userId2", List.of(currentUserId, favoritedUserId))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Failed to delete match", e));
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public final TextView username;
        public final TextView fullname;
        public final CircleImageView image_profile;
        public final Button btn_like;
        public final Button btn_unlike;
        public final Button btn_unmatch;

        public ImageViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_like = itemView.findViewById(R.id.btn_likeSearch);
            btn_unlike = itemView.findViewById(R.id.btn_unlikeSearch);
            btn_unmatch = itemView.findViewById(R.id.btn_unmatch);
        }
    }
}
