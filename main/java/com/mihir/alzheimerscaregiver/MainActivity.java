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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mihir.alzheimerscaregiver.facerecognition.FaceRecognitionActivity;
    import com.mihir.alzheimerscaregiver.facerecognition.FaceRecognitionHelper;
import com.mihir.alzheimerscaregiver.facerecognition.ManageFacesActivity;

public class MainActivity extends AppCompatActivity {

    // UI Elements
    private TextView welcomeText, tvWelcome, tvFaceRecognitionStatus;
    private TextView nameText;

    private Button btnFaceRecognition, btnManageFaces, btnDailyTasks, btnReminders, btnSettings;
    private Toolbar toolbar;
    private CardView medicationCard, tasksCard, memoryCard, photosCard, emergencyCard, settingsCard;


    private FaceRecognitionHelper faceRecognitionHelper;


    private void initViews() {
        // Initialize all views
        toolbar = findViewById(R.id.toolbar);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvFaceRecognitionStatus = findViewById(R.id.tvFaceRecognitionStatus);

        // Main feature buttons
        //btnFaceRecognition = findViewById(R.id.btnFaceRecognition);
        //btnManageFaces = findViewById(R.id.btnManageFaces);
        //btnDailyTasks = findViewById(R.id.tasksCard);
        //btnReminders = findViewById(R.id.btnReminders);
        //btnSettings = findViewById(R.id.btnSettings);
    }

    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Alzheimer's Caregiver");
        }
    }

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

        initViews();
        setupToolbar();
        setupClickListeners();

        // Initialize face recognition helper
        faceRecognitionHelper = new FaceRecognitionHelper(this);

        updateFaceRecognitionStatus();
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
        nameText.setText("Samasti");
    }

    /**
     * Set up click listeners for all feature cards
     */
    private void setupClickListeners() {


        // Face Recognition Features
        btnFaceRecognition.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FaceRecognitionActivity.class);
            startActivity(intent);
        });

        btnManageFaces.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManageFacesActivity.class);
            startActivity(intent);
        });

        // Long click for face recognition quick actions
        btnFaceRecognition.setOnLongClickListener(v -> {
            showFaceRecognitionQuickActions();
            return true;
        });

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
                // Add haptic feedback
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                // Create intent to start TasksActivity
                Intent intent = new Intent(MainActivity.this, TasksActivity.class);
                startActivity(intent);
            }
        });

        // Memory Games Card
        memoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                // Navigate to GameSelectionActivity
                Intent intent = new Intent(MainActivity.this, GameSelectionActivity.class);
                startActivity(intent);
            }
        });

        // Family Photos Card
        photosCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add haptic feedback
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                Intent intent = new Intent(MainActivity.this, FaceRecognitionActivity.class);
                startActivity(intent);
            }
        });
        // Emergency Card
        emergencyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                // Create intent to start EmergencyActivity
                Intent intent = new Intent(MainActivity.this, EmergencyActivity.class);
                startActivity(intent);
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

    private void updateFaceRecognitionStatus() {
        if (faceRecognitionHelper != null) {
            int faceCount = faceRecognitionHelper.getRegisteredFaceCount();

            if (faceCount == 0) {
                tvFaceRecognitionStatus.setText("Face Recognition: Not Set Up");
                tvFaceRecognitionStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                btnFaceRecognition.setText("Set Up Face Recognition");
            } else {
                tvFaceRecognitionStatus.setText("Face Recognition: " + faceCount + " faces registered");
                tvFaceRecognitionStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                btnFaceRecognition.setText("Use Face Recognition");
            }
        }
    }

    private void showFaceRecognitionQuickActions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Recognition Options");

        String[] options = {
                "Start Face Recognition",
                "Add New Face",
                "Manage Faces",
                "View Face Count"
        };
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Start Face Recognition
                    startActivity(new Intent(this, FaceRecognitionActivity.class));
                    break;

                case 1: // Add New Face
                    Intent addIntent = new Intent(this, FaceRecognitionActivity.class);
                    addIntent.putExtra("action", "add_face");
                    startActivity(addIntent);
                    break;

                case 2: // Manage Faces
                    startActivity(new Intent(this, ManageFacesActivity.class));
                    break;

                case 3: // View Face Count
                    showFaceCountDialog();
                    break;
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showFaceCountDialog() {
        int faceCount = faceRecognitionHelper.getRegisteredFaceCount();
        String[] faceNames = faceRecognitionHelper.getRegisteredFaceNames();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registered Faces");

        StringBuilder message = null;
        if (faceCount == 0) {
            builder.setMessage("No faces are currently registered.\n\nTap 'Set Up Face Recognition' to get started.");
        } else {
            message = new StringBuilder();
            message.append("Total registered faces: ").append(faceCount).append("\n\n");
            message.append("Registered people:\n");

            for (int i = 0; i < faceNames.length; i++) {
                message.append("• ").append(faceNames[i]);
                if (i < faceNames.length - 1) {
                    message.append("\n");
                }
            }
        }

        builder.setMessage(message.toString());
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        if (faceCount > 0) {
            builder.setNeutralButton("Manage", (dialog, which) -> {
                startActivity(new Intent(this, ManageFacesActivity.class));
            });
        }

        builder.show();
    }

    // Placeholder method for features not yet implemented
    private void showFeatureComingSoon(String featureName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(featureName);
        builder.setMessage("This feature is coming soon!\n\n" +
                "For now, you can use the Face Recognition features to:\n" +
                "• Register family members and caregivers\n" +
                "• Recognize people through the camera\n" +
                "• Manage registered faces");

        builder.setPositiveButton("Try Face Recognition", (dialog, which) -> {
            startActivity(new Intent(this, FaceRecognitionActivity.class));
        });

        builder.setNegativeButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_face_recognition:
                startActivity(new Intent(this, FaceRecognitionActivity.class));
                return true;

            case R.id.action_manage_faces:
                startActivity(new Intent(this, ManageFacesActivity.class));
                return true;

            case R.id.action_about:
                showAboutDialog();
                return true;

            case R.id.action_help:
                showHelpDialog();
                return true;

            // Add your existing menu items here
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About Alzheimer's Caregiver");
        builder.setMessage("This app helps Alzheimer's patients and their caregivers with daily tasks and face recognition.\n\n" +
                "Face Recognition Features:\n" +
                "• Register family members and caregivers\n" +
                "• Real-time face recognition\n" +
                "• Secure local storage of face data\n" +
                "• Easy management of registered faces\n\n" +
                "Version 1.0");

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How to Use Face Recognition");
        builder.setMessage("Getting Started:\n" +
                "1. Tap 'Set Up Face Recognition'\n" +
                "2. Tap 'Add Face' and enter a name\n" +
                "3. Position the face in the camera view\n" +
                "4. The face will be registered automatically\n\n" +
                "Recognition:\n" +
                "1. Tap 'Recognize' in the face recognition screen\n" +
                "2. Point the camera at a person's face\n" +
                "3. The app will show who it recognizes\n\n" +
                "Tips:\n" +
                "• Ensure good lighting\n" +
                "• Keep the camera steady\n" +
                "• Face the camera directly");

        builder.setPositiveButton("Got it", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("Try Now", (dialog, which) -> {
            startActivity(new Intent(this, FaceRecognitionActivity.class));
        });

        builder.show();
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

        // Update face recognition status when returning to main activity
        updateFaceRecognitionStatus();

        // You could also refresh task counts, medication reminders, etc.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (faceRecognitionHelper != null) {
            FaceRecognitionHelper.close();
        }
    }
}