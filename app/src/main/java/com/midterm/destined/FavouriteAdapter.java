package com.midterm.destined;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavouriteAdapter  extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder>{

    private ArrayList<String> listfavourite;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_row_item, parent, false);

        return new ViewHolder(view);
    }

    public FavouriteAdapter(ArrayList<String> listfavourite) {
        this.listfavourite = listfavourite;
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(listfavourite.get(position));
    }

    @Override
    public int getItemCount() {
        return listfavourite.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            tvName = (TextView) view.findViewById(R.id.tv_Name);
        }

        public TextView getTextView() {
            return tvName;
        }
    }
}
