package com.mihir.alzheimerscaregiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MedicationActivity extends AppCompatActivity {

    // UI Elements
    private ImageButton backButton;
    private TextView todayDateText, currentTimeText;
    private TextView med2StatusText;
    private Button med2Button;
    private CardView addMedicationCard;

    // Handler for updating time
    private Handler timeHandler;
    private Runnable timeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        // Initialize UI elements
        initializeViews();

        // Set up current date
        setupCurrentDate();

        // Start time updates
        startTimeUpdates();

        // Set up click listeners
        setupClickListeners();
    }

    /**
     * Initialize all UI elements
     */
    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        todayDateText = findViewById(R.id.todayDateText);
        currentTimeText = findViewById(R.id.currentTimeText);
        med2StatusText = findViewById(R.id.med2StatusText);
        med2Button = findViewById(R.id.med2Button);
        addMedicationCard = findViewById(R.id.addMedicationCard);
    }

    /**
     * Set up current date display
     */
    private void setupCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        String todayDate = "Today, " + dateFormat.format(calendar.getTime());
        todayDateText.setText(todayDate);
    }

    /**
     * Start updating time every minute
     */
    private void startTimeUpdates() {
        timeHandler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateCurrentTime();
                // Update every minute (60000 milliseconds)
                timeHandler.postDelayed(this, 60000);
            }
        };

        // Start immediately
        updateCurrentTime();
        timeHandler.post(timeRunnable);
    }

    /**
     * Update current time display
     */
    private void updateCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());
        currentTimeText.setText(currentTime);
    }

    /**
     * Set up click listeners for all interactive elements
     */
    private void setupClickListeners() {

        // Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to main activity
                finish();
            }
        });

        // Take medication button (for the active medication)
        med2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Provide haptic feedback
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                // Mark medication as taken
                markMedicationAsTaken();
            }
        });

        // Add new medication card
        addMedicationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                showToast("Add Medication feature coming soon!");

                // TODO: Open add medication dialog or activity
                // showAddMedicationDialog();
            }
        });
    }

    /**
     * Mark the current medication as taken
     */
    private void markMedicationAsTaken() {
        // Update UI to show medication was taken
        med2StatusText.setText("✓ Taken");
        med2StatusText.setTextColor(getResources().getColor(R.color.success));

        // Update button
        med2Button.setText("Taken");
        med2Button.setBackgroundTintList(getResources().getColorStateList(R.color.success));
        med2Button.setEnabled(false);

        // Show confirmation message
        showToast("Great! Vitamin D marked as taken.");

        // You could also:
        // - Save to database
        // - Send notification to caregiver
        // - Update medication history
        // - Cancel any pending alarms for this medication

        // Simulate sending notification to caregiver
        simulateCaregiverNotification();
    }

    /**
     * Simulate sending notification to caregiver
     */
    private void simulateCaregiverNotification() {
        // In a real app, this would send a notification to the caregiver's phone
        // For now, we'll just show a toast

        // Delay the message slightly for better UX
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showToast("✓ Caregiver notified: Medication taken");
            }
        }, 1500);
    }

    /**
     * Helper method to show toast messages
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Get greeting based on time of day
     */
    private String getTimeBasedGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour < 12) {
            return "Good Morning";
        } else if (hour < 17) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop time updates when activity is destroyed
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update time when returning to this activity
        updateCurrentTime();
    }
}