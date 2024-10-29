package com.midterm.destined.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.midterm.destined.R;
import com.midterm.destined.viewmodel.CalculateCoordinates;
import java.util.List;

public class CardAdapter extends BaseAdapter {

    private List<Card> cards;
    private Context context;

    public CardAdapter(Context context, List<Card> cards) {
        this.context = context;
        this.cards = cards;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int position) {
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        }

        Card card = cards.get(position);
        TextView name = convertView.findViewById(R.id.userName);
        TextView age = convertView.findViewById(R.id.age);
        TextView location = convertView.findViewById(R.id.location);
        TextView hobby = convertView.findViewById(R.id.hobby);
        TextView bio = convertView.findViewById(R.id.bio);
        ImageView profileImage = convertView.findViewById(R.id.profileImage);
        TextView tvDistance = convertView.findViewById(R.id.distance);
        ImageView genderIcon = convertView.findViewById(R.id.genderIcon);

        // Lấy UID của người dùng hiện tại đang đăng nhập
        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Tính khoảng cách giữa người dùng hiện tại và người dùng trên card
        CalculateCoordinates.calculateDistance(currentUserUID, card.getCurrentUserID(), distance -> {
            if (distance < 1) {
                tvDistance.setText("  " + String.format("%d", (int) (distance * 1000)) + " MS" + " AWAY");
            } else {
                tvDistance.setText("  " + String.format("%d", (int) (distance)) + " KMS" + " AWAY");
            }
        });

        // Gán dữ liệu vào các view
        name.setText(card.getName());
        location.setText(card.getLocation());
        age.setText("🎂 " + card.getAge());
        bio.setText("🙋🏻‍♀️🙋🏻‍♂️ " + card.getBio());
        hobby.setText("📑 " + card.getAllInterest());

        // Gán icon giới tính
        if ("Male".equals(card.getGender())) {
            genderIcon.setImageResource(R.drawable.male_picture);
        } else {
            genderIcon.setImageResource(R.drawable.female_picture);
        }

        // Gán ảnh profile với Glide
        if (card.getProfileImageUrl() != null && !card.getProfileImageUrl().isEmpty()) {
            Glide.with(context).load(card.getProfileImageUrl()).into(profileImage);
        } else {
            Glide.with(context).load(R.drawable.avatardefault).into(profileImage);
        }

        return convertView;
    }
}
