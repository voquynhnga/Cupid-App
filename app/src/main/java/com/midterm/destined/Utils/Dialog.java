package com.midterm.destined.Utils;

import android.app.AlertDialog;
import android.content.Context;

import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.midterm.destined.R;

public class Dialog {
    public static void showMatchPopup(Context context, String matchedUserName, NavController navController) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("It's a Match!");
        builder.setMessage("You and " + matchedUserName + " have liked each other!");

        builder.setPositiveButton("Chat now", (dialog, which) -> {
            // Điều hướng đến ChatFragment
            navController.navigate(R.id.action_global_ChatFragment);
        });

        builder.setNegativeButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
