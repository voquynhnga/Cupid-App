package com.midterm.destined.Presenters;


import com.midterm.destined.Models.Story;
import com.midterm.destined.Views.Homepage.Story.StoryView;

import java.util.List;

public class StoryPresenter {

    private StoryView view;
    private Story model;

    public StoryPresenter(StoryView view) {
        this.view = view;
        this.model = new Story();
    }

    public void getStories(String userid) {
        model.getStories(userid, new Story.OnStoryDataLoaded() {
            @Override
            public void onStoryDataLoaded(List<String> images, List<String> storyIds) {
                if (images.isEmpty()) {
                    view.showError("No stories available.");
                } else {
                    view.showStoryImages(images, storyIds);
                }
            }
        });
    }

    public void deleteStory(String userid, String storyId) {
        view.onStoryDeleted();
    }
}
