package com.midterm.destined.Adapters;

import static com.midterm.destined.Models.ChatObject.deleteChat;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.midterm.destined.Models.Card;
import com.midterm.destined.Models.UserReal;
import com.midterm.destined.R;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Utils.Dialog;

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
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    private void checkIfMatched(UserReal user, ImageViewHolder holder) {
        String currentUserId = DB.getCurrentUser().getUid();

        DB.getMatchesCollection()
                .whereEqualTo("userId1", user.getUid())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String userId2 = document.getString("userId2");
                            if (currentUserId.equals(userId2)) { // Kiểm tra người còn lại
                                user.setMatched(true);
                                matchedUsersCache.put(user.getUid(), true);
                                updateButtonState(user, holder);
                                return; // Dừng kiểm tra nếu đã tìm thấy
                            }
                        }
                    }

                    // Tiếp tục kiểm tra với userId2
                    DB.getMatchesCollection()
                            .whereEqualTo("userId2", user.getUid())
                            .get()
                            .addOnSuccessListener(querySnapshot2 -> {
                                if (!querySnapshot2.isEmpty()) {
                                    for (QueryDocumentSnapshot document : querySnapshot2) {
                                        String userId1 = document.getString("userId1");
                                        if (currentUserId.equals(userId1)) { // Kiểm tra người còn lại
                                            user.setMatched(true);
                                            matchedUsersCache.put(user.getUid(), true);
                                            updateButtonState(user, holder);

                                            return; // Dừng kiểm tra nếu đã tìm thấy
                                        }
                                    }
                                }

                                // Nếu không tìm thấy kết nối nào
                                user.setMatched(false);
                                matchedUsersCache.put(user.getUid(), false);
                                checkFavoritedList(user, holder);
                            });
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

        try {
            Card.addToFavoritedList(favoritedUserId);
            checkIfMatched(user, holder);
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
