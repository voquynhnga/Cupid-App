package com.midterm.destined;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class ChatDetailFragment extends Fragment {

    private TextView senderTextView;
    private TextView contentTextView;
    private ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        senderTextView = view.findViewById(R.id.senderTextView);
        contentTextView = view.findViewById(R.id.contentTextView);
        btnBack = view.findViewById(R.id.btn_back);

        if (getArguments() != null) {
            String sender = getArguments().getString("sender");
            String content = getArguments().getString("content");

            senderTextView.setText(sender);
            contentTextView.setText(content);
        }

        btnBack.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_chatDetailFragment_to_chatFragment);

        });


        return view;
    }
}
