package com.midterm.destined;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.midterm.destined.model.UserReal;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    private Context mContext;
    private List<UserReal> mUsers; // Danh sách UserReal
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context context, List<UserReal> users, boolean isFragment) {
        this.mContext = context;
        this.mUsers = users;
        this.isFragment = isFragment;
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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

        // Log giá trị URL để kiểm tra
        String imageUrl = user.getProfilePicture();
        Log.d("UserAdapter", "Image URL: " + imageUrl);

        // Tải ảnh vào CircleImageView với xử lý lỗi
        Glide.with(mContext)
                .load(imageUrl)
                .error(R.drawable.avatardefault) // Hình mặc định nếu có lỗi
                .into(holder.image_profile);

        holder.btn_follow.setVisibility(user.getUid().equals(firebaseUser.getUid()) ? View.GONE : View.VISIBLE);

        // Xử lý sự kiện nhấp vào item và các logic khác...
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

        public ImageViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }

    // Các phương thức khác cho việc theo dõi/không theo dõi...
}
