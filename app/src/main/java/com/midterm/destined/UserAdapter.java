package com.midterm.destined;

import static java.security.AccessController.getContext;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.midterm.destined.card.Card;
import com.midterm.destined.card.CardFragment;
import com.midterm.destined.model.UserReal;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    private Context mContext;
    private List<UserReal> mUsers;
    private boolean isFragment;
    private FirebaseUser firebaseUser;
    private CardFragment cf;
    private FirebaseFirestore db;

    public UserAdapter(Context context, List<UserReal> users, boolean isFragment) {
        this.mContext = context;
        this.mUsers = users;
        this.isFragment = isFragment;
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.cf = CardFragment.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
        final UserReal user = mUsers.get(position);

        holder.username.setText(user.getUserName());
        holder.fullname.setText(user.getFullName());

        Glide.with(mContext)
                .load(user.getProfilePicture())
                .error(R.drawable.avatardefault)
                .into(holder.image_profile);

        // Set button visibility based on UID
        if (user.getUid().equals(firebaseUser.getUid())) {
            holder.btn_follow.setVisibility(View.GONE);
            holder.btn_unfollow.setVisibility(View.GONE);
        } else {
            holder.btn_follow.setVisibility(View.VISIBLE);
            holder.btn_unfollow.setVisibility(View.GONE);
        }

        holder.btn_unfollow.setOnClickListener(v -> {
            if (cf != null) {

                String favoritedUserId = user.getUid();
                Log.d("DEBUG", "CardList1: " + favoritedUserId);
                String currentUserId = firebaseUser.getUid();
                Log.d("DEBUG", "CardList2: " + currentUserId);

                if (db != null) {
                    db.collection("users").document(currentUserId)
                            .update("favoritedCardList", FieldValue.arrayRemove(favoritedUserId),
                                    "cardList", FieldValue.arrayUnion(favoritedUserId))
                            .addOnSuccessListener(aVoid -> {
                                holder.btn_follow.setVisibility(View.VISIBLE);
                                holder.btn_unfollow.setVisibility(View.GONE);
                            });
                }

                db.collection("matches").whereEqualTo("userId1", currentUserId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot matchDocument : task.getResult()) {
                                    matchDocument.getReference().delete();

                                }
                            } else {
                                Log.e("MATCH_CHECK_ERROR", "Error checking matches", task.getException());
                            }
                        });
                db.collection("matches").whereEqualTo("userId2", currentUserId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot matchDocument : task.getResult()) {
                                    matchDocument.getReference().delete();


                                }
                            } else {
                                Log.e("MATCH_CHECK_ERROR", "Error checking matches", task.getException());
                            }
                        });
            }
        });

        holder.btn_follow.setOnClickListener(v -> {
            if (cf != null) {

                String favoritedUserId = user.getUid();
                Log.d("DEBUG", "CardList1: " + favoritedUserId);
                String currentUserId = firebaseUser.getUid();
                Log.d("DEBUG", "CardList2: " + currentUserId);

                if (db != null) {
                    db.collection("users").document(currentUserId)
                            .update("cardList", FieldValue.arrayRemove(favoritedUserId),
                                    "favoritedCardList", FieldValue.arrayUnion(favoritedUserId))
                            .addOnSuccessListener(aVoid -> {
                                cf.checkIfMatched(favoritedUserId, currentUserId, mContext);

                                holder.btn_follow.setVisibility(View.GONE);
                                holder.btn_unfollow.setVisibility(View.VISIBLE);
                            });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_follow;
        public Button btn_unfollow;

        public ImageViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
            btn_unfollow = itemView.findViewById(R.id.btn_unfollow);
        }
    }
}