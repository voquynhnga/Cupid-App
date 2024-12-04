package com.midterm.destined.Views.Homepage.Story;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.midterm.destined.Presenters.StoryPresenter;
import com.midterm.destined.R;
import com.midterm.destined.Models.Story;

import java.util.List;

public class StoryActivity extends AppCompatActivity implements StoryView {

    private StoryPresenter presenter;
    private int counter = 0;
    private List<String> images;
    private List<String> storyIds;

    private ImageView image;
    private ProgressBar progressBar;
    private TextView seenNumber;
    private LinearLayout rSeen;
    private ImageView storyDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_activity);

        image = findViewById(R.id.image);
        progressBar = findViewById(R.id.story_progress);
        rSeen = findViewById(R.id.r_seen);
        seenNumber = findViewById(R.id.seen_number);
        storyDelete = findViewById(R.id.story_delete);

        presenter = new StoryPresenter(this);

        String userid = getIntent().getStringExtra("userid");
        presenter.getStories(userid);

        rSeen.setOnClickListener(v -> {
            // Show view count for the story
        });

        storyDelete.setOnClickListener(v -> {
            presenter.deleteStory(userid, storyIds.get(counter));
        });
    }

    @Override
    public void showStoryImages(List<String> images, List<String> storyIds) {
        this.images = images;
        this.storyIds = storyIds;

        if (!images.isEmpty()) {
            Glide.with(getApplicationContext()).load(images.get(counter)).into(image);
            progressBar.setMax(100);
            startStoryTimer();
        }
    }

    private void startStoryTimer() {
        progressBar.setProgress(0);
        // Implement timer logic to update progress bar and move to next story
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStoryDeleted() {
        Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
