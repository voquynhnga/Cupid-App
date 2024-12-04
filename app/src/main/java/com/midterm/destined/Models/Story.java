package com.midterm.destined.Models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.midterm.destined.Utils.DB;

import java.util.ArrayList;
import java.util.List;

public class Story {
    private String imageurl;
    private long timestart;
    private long timeend;
    private String storyid;
    private String userid;

    public Story(String imageurl, long timestart, long timeend, String storyid, String userid) {
        this.imageurl = imageurl;
        this.timestart = timestart;
        this.timeend = timeend;
        this.storyid = storyid;
        this.userid = userid;
    }

    public Story() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public long getTimestart() {
        return timestart;
    }

    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    public long getTimeend() {
        return timeend;
    }

    public void setTimeend(long timeend) {
        this.timeend = timeend;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public interface OnStoryDataLoaded {
        void onStoryDataLoaded(List<String> images, List<String> storyIds);
    }

    public void getStories(String userid, final OnStoryDataLoaded listener) {
        List<String> images = new ArrayList<>();
        List<String> storyIds = new ArrayList<>();
        DB.getStoryRef().child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                images.clear();
                storyIds.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    long timecurrent = System.currentTimeMillis();
                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                        images.add(story.getImageurl());
                        storyIds.add(story.getStoryid());
                    }
                }
                listener.onStoryDataLoaded(images, storyIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}

