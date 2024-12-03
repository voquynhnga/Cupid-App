package com.midterm.destined.Models;

public class Match {
    private String currentUserId;
    private String matchedUserId;

    public Match(String currentUserId, String matchedUserId) {
        this.currentUserId = currentUserId;
        this.matchedUserId = matchedUserId;
    }


    public String getCurrentUserId() {
        return currentUserId;
    }

    public String getMatchedUserId() {
        return matchedUserId;
    }
}

