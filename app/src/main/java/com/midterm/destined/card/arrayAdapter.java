package com.midterm.destined.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.midterm.destined.R;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<Card> {

    Context context;

    public arrayAdapter(Context context, int resourceID, List<Card> items) {
        super(context, resourceID, items);
        this.context = context;
    }

    private static class ViewHolder {
        TextView name;
        ImageView image;
        TextView age;
        TextView location;
        TextView bio;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Card card_item = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_card, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.userName);
            viewHolder.image = convertView.findViewById(R.id.profileImage);
            viewHolder.location = convertView.findViewById(R.id.location);
            viewHolder.age = convertView.findViewById(R.id.age);
            viewHolder.bio = convertView.findViewById(R.id.bio);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.name.setText(card_item.getName());
        viewHolder.age.setText(card_item.getAge());
        viewHolder.location.setText(card_item.getLocation());
        viewHolder.bio.setText(card_item.getBio());

        if (card_item.getProfileImageUrl().equals("default")) {
            Glide.with(context).load(R.drawable.avatardefault).into(viewHolder.image);
        } else {
            Glide.with(context).clear(viewHolder.image);
            Glide.with(context).load(card_item.getProfileImageUrl()).into(viewHolder.image);
        }

        return convertView;
    }
}
