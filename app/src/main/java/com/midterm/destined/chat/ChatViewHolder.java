package com.midterm.destined.chat;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.destined.R;

public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mMessage;
    public LinearLayout mContainer;



    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mMessage = itemView.findViewById(R.id.et_message);
//        mContainer = itemView.findViewById(R.id.);
    }

    @Override
    public void onClick(View view) {

    }
}
