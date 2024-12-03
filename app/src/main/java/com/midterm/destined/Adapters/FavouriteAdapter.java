package com.midterm.destined.Adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.midterm.destined.Models.UserReal;
import com.midterm.destined.R;

import java.util.ArrayList;

public class FavouriteAdapter  extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder>{

    private ArrayList<UserReal> listfavourite;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_row_item, parent, false);

        return new ViewHolder(view);
    }

    public FavouriteAdapter(ArrayList<UserReal> listfavourite) {
        this.listfavourite = listfavourite;
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.ViewHolder holder, int position) {
        UserReal user = listfavourite.get(position);
        holder.tvName.setText(user.getFullName());

        Glide.with(holder.itemView.getContext())
                .load(user.getProfilePicture()) // Lấy đường dẫn ảnh từ đối tượng UserReal
                // Hình ảnh hiển thị trong khi tải
                // Hình ảnh hiển thị khi tải không thành công
                .into(holder.imAvatar);


        holder.imRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                int currentPosition = holder.getAdapterPosition();
//                listfavourite.remove(currentPosition);
//                notifyItemRemoved(currentPosition);
//                notifyItemRangeChanged(currentPosition, listfavourite.size());
//                Toast.makeText(view.getContext(), "Remove person", Toast.LENGTH_SHORT).show();

                holder.imRemove.setVisibility(View.GONE); // ẩn icon
                holder.tvAccept.setText("Accepted".toUpperCase());
                holder.tvAccept.setTypeface(holder.tvAccept.getTypeface(), Typeface.BOLD);
                holder.tvAccept.setTextColor(Color.parseColor("#13ab28"));

            }
        });
    }

    @Override
    public int getItemCount() {
        return listfavourite.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public ImageView imRemove;
        public TextView tvAccept;
        public ImageView imAvatar;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            tvName = (TextView) view.findViewById(R.id.tv_Name);
            imRemove = (ImageView) view.findViewById(R.id.im_removeItem);
          tvAccept = (TextView) view.findViewById(R.id.tv_Accept);
          imAvatar = (ImageView) view.findViewById(R.id.im_Avatar);

        }

        public TextView getTextView() {
            return tvName;
        }
    }
}
