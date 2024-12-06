package com.midterm.destined.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.midterm.destined.R;
import com.midterm.destined.Models.Card;
import com.midterm.destined.Utils.CalculateCoordinates;
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
        ViewPager2 profileImagePager = convertView.findViewById(R.id.profileImagePager);
        TextView tvDistance = convertView.findViewById(R.id.distance);
        ImageView genderIcon = convertView.findViewById(R.id.genderIcon);
        ImageView profileImage1 = convertView.findViewById(R.id.profileImage1);



        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CalculateCoordinates.calculateDistance(currentUserUID, card.getUserID(), distance -> {

            if (distance < 10) {
                tvDistance.setText("📍 < 10Km");
            } else {
                tvDistance.setText("📍 " + String.format("%d", (int) (distance)) + " Km");

            }
        });

        if (card.getImageUrls() != null && !card.getImageUrls().isEmpty()) {
            if (card.getImageUrls().size() > 1) {
                profileImagePager.setAdapter(new ImagePagerAdapter(card.getImageUrls(), context));
                profileImagePager.setVisibility(View.VISIBLE);
                profileImage1.setVisibility(View.GONE); // Ẩn ảnh đơn khi có ViewPager
            } else {
                profileImagePager.setVisibility(View.GONE);
                profileImage1.setVisibility(View.VISIBLE); // Hiển thị ảnh đơn
                Glide.with(context).load(card.getImageUrls().get(0)).into(profileImage1);
            }
        } else {
            profileImagePager.setVisibility(View.GONE);
            profileImage1.setVisibility(View.VISIBLE); // Hiển thị ảnh mặc định khi không có ảnh
            Glide.with(context).load(R.drawable.avatardefault).into(profileImage1);
        }

        name.setText(card.getName());
        location.setText("🏠" +card.getLocation());
        age.setText(card.getAge());
        bio.setText("📝️ " + card.getBio());
        hobby.setText(card.getAllInterest().toString());

        if ("Male".equals(card.getGender())) {
            genderIcon.setImageResource(R.drawable.male_picture);
        } else {
            genderIcon.setImageResource(R.drawable.female_picture);
        }



        return convertView;
    }


}