package com.midterm.destined.Views.Homepage.Story;

import java.util.List;

public interface StoryView {
    void onStoryDeleted();
    void showError(String error);
    void showStoryImages(List<String> images, List<String> storyIds);
}
