package com.midterm.destined;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainUIActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ui);


        findViewById(R.id.search).setOnClickListener(v -> {
            Intent intent = new Intent(MainUIActivity.this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        findViewById(R.id.story).setOnClickListener(v -> {
            Intent intent = new Intent(MainUIActivity.this, AddStoryActivity.class);
            startActivity(intent);
            });
    }
}
