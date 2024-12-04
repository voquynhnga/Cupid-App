package com.midterm.destined.Utils;

import androidx.recyclerview.widget.DiffUtil;

import com.midterm.destined.Models.ChatObject;

import java.util.ArrayList;

public class ChatDiffCallback extends DiffUtil.Callback {

    private final ArrayList<ChatObject> oldList;
    private final ArrayList<ChatObject> newList;

    public ChatDiffCallback(ArrayList<ChatObject> oldList, ArrayList<ChatObject> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getChatId()
                .equals(newList.get(newItemPosition).getChatId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}

