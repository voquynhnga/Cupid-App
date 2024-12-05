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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.midterm.destined.Models.Card;
import com.midterm.destined.Models.UserReal;
import com.midterm.destined.Presenters.CardPresenter;
import com.midterm.destined.R;
import com.midterm.destined.Utils.DB;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    private final Context mContext;
    private final List<UserReal> mUsers;


    private final CardPresenter cardPresenter;


    public UserAdapter(Context context, List<UserReal> users, CardPresenter presenter) {
        this.mContext = context;
        this.mUsers = users;
        this.cardPresenter = presenter;
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

        if (user.getUid().equals(DB.getCurrentUser().getUid())) {
            holder.btn_match.setVisibility(View.GONE);
            holder.btn_unmatch.setVisibility(View.GONE);
            return;
        }

        updateButtonVisibility(user, holder);

        holder.btn_match.setOnClickListener(v -> handleMatchAction(user, holder));

        holder.btn_unmatch.setOnClickListener(v -> handleUnMatchAction(user, holder));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    private void updateButtonVisibility(UserReal user, ImageViewHolder holder) {
        DB.getMatchesCollection()
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        holder.btn_match.setVisibility(View.GONE);
                        holder.btn_unmatch.setVisibility(View.VISIBLE);
                    } else {
                        holder.btn_match.setVisibility(View.VISIBLE);
                        holder.btn_unmatch.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Failed to fetch user data", e));
    }


    private void handleMatchAction(UserReal user, ImageViewHolder holder) {
        String favoritedUserId = user.getUid();

        try {
            Card.addToFavoritedList(favoritedUserId);
            cardPresenter.checkIfMatched(user.getUid(), user.getFullName());
            holder.btn_match.setVisibility(View.GONE);
            holder.btn_unmatch.setVisibility(View.VISIBLE);
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

                        holder.btn_match.setVisibility(View.VISIBLE);
                        holder.btn_unmatch.setVisibility(View.GONE);

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
        public final Button btn_match;
        public final Button btn_unmatch;

        public ImageViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_match = itemView.findViewById(R.id.btn_match);
            btn_unmatch = itemView.findViewById(R.id.btn_unmatch);
        }
    }
}
