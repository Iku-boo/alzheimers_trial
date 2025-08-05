package com.mihir.alzheimerscaregiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // UI Elements
    private TextView welcomeText;
    private TextView nameText;
    private CardView medicationCard, tasksCard, memoryCard, photosCard, emergencyCard, settingsCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        initializeViews();

        // Set up dynamic welcome message
        setupWelcomeMessage();

        // Set up click listeners for all cards
        setupClickListeners();
    }

    /**
     * Initialize all UI elements
     */
    private void initializeViews() {
        // Text views
        welcomeText = findViewById(R.id.welcomeText);
        nameText = findViewById(R.id.nameText);

        // Feature cards
        medicationCard = findViewById(R.id.medicationCard);
        tasksCard = findViewById(R.id.tasksCard);
        memoryCard = findViewById(R.id.memoryCard);
        photosCard = findViewById(R.id.photosCard);
        emergencyCard = findViewById(R.id.emergencyCard);
        settingsCard = findViewById(R.id.settingsCard);
    }

    /**
     * Set up dynamic welcome message based on time of day
     */
    private void setupWelcomeMessage() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour < 12) {
            greeting = "Good Morning";
        } else if (hour < 17) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }

        welcomeText.setText(greeting);

        // You can customize this name or get it from user preferences
        nameText.setText("Sarah");
    }

    /**
     * Set up click listeners for all feature cards
     */
    private void setupClickListeners() {

        // Medication Card
        medicationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add haptic feedback
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                // Show toast message (we'll replace this with actual navigation later)
                showToast("Opening Medication Reminders...");

                Intent intent = new Intent(MainActivity.this, MedicationActivity.class);
                startActivity(intent);
                // Intent intent = new Intent(MainActivity.this, MedicationActivity.class);
                // startActivity(intent);
            }
        });

        // Daily Tasks Card
        tasksCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                showToast("Opening Daily Tasks...");

                // TODO: Navigate to TasksActivity
            }
        });

        // Memory Games Card
        memoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                showToast("Starting Memory Games...");

                // TODO: Navigate to MemoryGamesActivity
            }
        });

        // Family Photos Card
        photosCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                showToast("Opening Family Photos...");

                // TODO: Navigate to PhotosActivity
            }
        });

        // Emergency Card
        emergencyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                showToast("Emergency Contacts");

                // TODO: Navigate to EmergencyActivity or show emergency dialog
            }
        });

        // Settings Card
        settingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                showToast("Opening Settings...");

                // TODO: Navigate to SettingsActivity
            }
        });
    }

    /**
     * Helper method to show toast messages
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method to update the tasks count (you can call this from other activities)
     */
    public void updateTasksRemaining(int count) {
        // You can add logic here to update the "3 tasks remaining" text
        // This would typically be connected to a database or shared preferences
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update welcome message when returning to the app
        setupWelcomeMessage();

        // You could also refresh task counts, medication reminders, etc.
    }
}