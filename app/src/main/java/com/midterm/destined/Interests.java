package com.midterm.destined;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.midterm.destined.databinding.ActivityInterestsBinding;

import java.util.ArrayList;
import java.util.List;

public class Interests extends AppCompatActivity {

    private ToggleButton reading, music, travel, cook, game, photo, chat, shop, fashion, sport, pet,
    paint;
    private int selectedCount = 0;
    private final int MAX_SELECTION = 3;
    private List<ToggleButton> selectedButtons = new ArrayList<>();
    private ActivityInterestsBinding binding;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInterestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        reading = binding.toggleButtonReading;
        music = binding.toggleButtonMusic;
        travel = binding.toggleButtonTravel;
        cook = binding.toggleButtonCook;
        game = binding.toggleButtonGame;
        photo = binding.toggleButtonPhoto;
        chat = binding.toggleButtonChat;
        shop = binding.toggleButtonShop;
        paint = binding.toggleButtonPainting;
        pet = binding.toggleButtonPet;
        fashion = binding.toggleButtonFashion;
        sport = binding.toggleButtonSport;
        continueButton = binding.btncontinue;

        setupToggleButton(reading);
        setupToggleButton(music);
        setupToggleButton(photo);
        setupToggleButton(game);
        setupToggleButton(travel);
        setupToggleButton(paint);
        setupToggleButton(cook);
        setupToggleButton(pet);
        setupToggleButton(shop);
        setupToggleButton(chat);
        setupToggleButton(fashion);
        setupToggleButton(sport);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Interests.this, UploadPhoto.class);
                startActivity(intent);
            }
        });
    }



    private void setupToggleButton(ToggleButton tgBtn) {
        tgBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                if(selectedCount >= MAX_SELECTION) {
                    Toast.makeText(Interests.this, "Chỉ có thể chọn tối đa 3 sở thích", Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);

                }
                else {
                    buttonView.setBackgroundColor(Color.RED);
                    buttonView.setTextColor(Color.WHITE);
                    selectedCount++;
                    selectedButtons.add(tgBtn);
                }
            }
            else {
                buttonView.setBackgroundColor(Color.WHITE);
                buttonView.setTextColor(Color.RED);
                selectedCount--;
                selectedButtons.remove(tgBtn);
            }
        });
    }
}