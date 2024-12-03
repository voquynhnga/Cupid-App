package com.midterm.destined.Models;

public interface OnMatchSaveListener {
    void onSuccess(String fullName);
    void onError(String errorMessage);
}
