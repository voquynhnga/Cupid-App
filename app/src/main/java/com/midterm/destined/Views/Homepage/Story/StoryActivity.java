package com.midterm.destined.Views.Homepage.Story;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midterm.destined.Models.CardInfo;
import com.midterm.destined.R;
import com.midterm.destined.Views.Homepage.Card.CardFragment;
import com.midterm.destined.Models.Story;

import java.util.ArrayList;
import java.util.List;

public class StoryActivity extends AppCompatActivity {

    int counter = 0;
    long pressTime = 0L;
    long limit = 500L;

    Handler storyHandler = new Handler();
    Runnable storyRunnable;
    ImageView image, story_photo;
    TextView story_username;

    // Linear layout for view count and delete button
    LinearLayout r_seen;
    TextView seen_number;
    ImageView story_delete;
    ProgressBar progressBar;

    List<String> images;
    List<String> storyids;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_activity);

        // Initialize UI elements
        image = findViewById(R.id.image);
        story_photo = findViewById(R.id.story_photo);
        story_username = findViewById(R.id.story_username);
        progressBar = findViewById(R.id.story_progress);

        // View count and delete button
        r_seen = findViewById(R.id.r_seen);
        seen_number = findViewById(R.id.seen_number);
        story_delete = findViewById(R.id.story_delete);

        r_seen.setVisibility(View.GONE);
        story_delete.setVisibility(View.GONE);

        userid = getIntent().getStringExtra("userid");

        // Show view count and delete button for the owner of the story
        if (userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            r_seen.setVisibility(View.VISIBLE);
            story_delete.setVisibility(View.VISIBLE);
        }

        getStories(userid);
        userInfo(userid);

        // Reverse story
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousStory();
            }
        });

        // Skip story
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStory();
            }
        });

        // Click on view count
        r_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoryActivity.this, CardFragment.class);
                intent.putExtra("id", userid);
                intent.putExtra("storyid", storyids.get(counter));
                intent.putExtra("title", "views");
                startActivity(intent);
            }
        });

        // Delete story
        story_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                        .child(userid).child(storyids.get(counter));
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(StoryActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });

    }

    private void nextStory() {
        if (counter < images.size() - 1) {
            counter++;
            Glide.with(getApplicationContext()).load(images.get(counter)).into(image);
            addView(storyids.get(counter));
            seenNumber(storyids.get(counter));
            progressBar.setProgress(0); // reset progress for the next story
            startStoryTimer();
        }
    }

    private void previousStory() {
        if (counter > 0) {
            counter--;
            Glide.with(getApplicationContext()).load(images.get(counter)).into(image);
            seenNumber(storyids.get(counter));
            progressBar.setProgress(0);
            startStoryTimer();
        }
    }

    private void startStoryTimer() {
        progressBar.setMax(100);
        storyRunnable = new Runnable() {
            int progress = 0;

            @Override
            public void run() {
                if (progress < 100) {
                    progressBar.setProgress(++progress);
                    storyHandler.postDelayed(this, 50);
                } else {
                    nextStory();
                }
            }
        };
        storyHandler.post(storyRunnable);
    }

    @Override
    protected void onPause() {
        storyHandler.removeCallbacks(storyRunnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
        startStoryTimer();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        storyHandler.removeCallbacks(storyRunnable);
        super.onDestroy();
    }

    private void getStories(String userid) {
        images = new ArrayList<>();
        storyids = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                images.clear();
                storyids.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    long timecurrent = System.currentTimeMillis();
                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                        images.add(story.getImageurl());
                        storyids.add(story.getStoryid());
                    }
                }

                // Start the first story
                if (!images.isEmpty()) {
                    Glide.with(getApplicationContext()).load(images.get(counter)).into(image);
                    addView(storyids.get(counter));
                    seenNumber(storyids.get(counter));
                    startStoryTimer();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void userInfo(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CardInfo cardInfo = dataSnapshot.getValue(CardInfo.class);
                Glide.with(getApplicationContext()).load(cardInfo.getImageurl()).into(story_photo);
                story_username.setText(cardInfo.getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addView(String storyid) {
        FirebaseDatabase.getInstance().getReference().child("Story").child(userid)
                .child(storyid).child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
    }

    private void seenNumber(String storyid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid).child(storyid).child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                seen_number.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
