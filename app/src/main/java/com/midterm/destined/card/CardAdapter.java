
package com.midterm.destined.card;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.midterm.destined.R;
import com.midterm.destined.viewmodel.CalculateCoordinates;

//import com.jopencage.api.JOpenCageGeocoder;
//import com.jopencage.api.JOpenCageReverseRequest;
//import com.jopencage.api.JOpenCageResponse;


import java.security.AccessController;
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
        TextView gioitinh = convertView.findViewById(R.id.gender);

        TextView bio = convertView.findViewById(R.id.bio);
        ImageView profileImage = convertView.findViewById(R.id.profileImage);

        TextView tvDistance = convertView.findViewById(R.id.distance);

        // Lấy UID của người dùng hiện tại đang đăng nhập
        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Tính khoảng cách giữa người dùng hiện tại và người dùng trên card
        CalculateCoordinates.calculateDistance(currentUserUID, card.getCurrentUserID(), distance -> {

            if(distance < 1) {
                tvDistance.setText("  "+String.format("%d", (int)(distance * 1000)) +" MS"+" AWAY"); // đơn vị m
            }
            else{
                tvDistance.setText("  "+String.format("%d", (int)(distance)) +" KMS" +" AWAY"); // đơn vị km
            }
        });

        name.setText(card.getName());
       location.setText( card.getLocation());
        age.setText("🎂 "+ card.getAge());
        bio.setText("🙋🏻‍♀️🙋🏻‍♂️  "+ card.getBio());
        hobby.setText("📑  "+ card.getAllInterest());
        gioitinh.setText("Gender: "+card.getGender());





        if (card.getProfileImageUrl() != null && !card.getProfileImageUrl().equals("")) {
            Glide.with(context).load(card.getProfileImageUrl()).into(profileImage);
        } else {
            Glide.with(context).load(R.drawable.avatardefault).into(profileImage);
        }



        return convertView;
    }



//    JOpenCageGeocoder jOCG = new JOpenCageGeocoder("fc4e36bbe5d241f0937e85501b21190e");
//
//    JOpenCageReverseRequest req = new JOpenCageReverseRequest(41.40015, 2.15765);
//req.setLanguage("es"); // we want Spanish address format
//req.setLimit(5); // only return the first 5 results
//
//    JOpenCageResponse res = jOpenCageGeocoder.reverse(req);
//
//    // get the formatted address of the first result:
//    String fAddress = res.getResults().get(0).getFormatted();
//    System.out.print(fAddress)
// 'Travessera de Gràcia, 142, 08012 Barcelona, España'
}


