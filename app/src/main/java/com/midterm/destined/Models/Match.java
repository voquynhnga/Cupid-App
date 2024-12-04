package com.midterm.destined.Models;

public class Match {
    private String matchId;
    private String userId1;
    private String userId2;
    private String timestamp;



    public Match(){};

    public Match(String matchId,String userId1, String userId2, String timestamp) {
        this.matchId = matchId;
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.timestamp = timestamp;
    }


    public String getUserId1() {
        return userId1;
    }

    public void setUserId1(String userId1) {
        this.userId1 = userId1;
    }

    public String getUserId2() {
        return userId2;
    }

    public void setUserId2(String userId2) {
        this.userId2 = userId2;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }


}

