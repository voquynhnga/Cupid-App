//package com.midterm.destined;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.viewpager2.adapter.FragmentStateAdapter;
//
//import com.midterm.destined.model.User;
//
//import java.util.List;
//
//public class CardAdapter extends FragmentStateAdapter {
//
//    private List<User> profiles;
//
//    public CardAdapter(Fragment fragment, List<User> profiles) {
//        super(fragment);
//        this.profiles = profiles;
//    }
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        return CardFragment.newInstance(profiles.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return profiles.size();
//    }
//}

package com.midterm.destined.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.midterm.destined.R;

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
        TextView location = convertView.findViewById(R.id.location);
        TextView bio = convertView.findViewById(R.id.bio);
        ImageView profileImage = convertView.findViewById(R.id.profileImage);

        name.setText(card.getName());
        location.setText(card.getLocation());
        bio.setText(card.getBio());

        if (card.getProfileImageUrl().equals("default")) {
            Glide.with(context).load(R.drawable.avatardefault).into(profileImage);
        } else {
            Glide.with(context).load(card.getProfileImageUrl()).into(profileImage);
        }

        return convertView;
    }
}


