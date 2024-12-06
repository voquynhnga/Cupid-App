package com.midterm.destined.Adapters;

import com.midterm.destined.Models.Card;
import com.midterm.destined.Models.UserReal;

public interface OnUserFetchListener {
    void onCardFetched(UserReal user, Card card);
    void onError(String errorMessage);
}
