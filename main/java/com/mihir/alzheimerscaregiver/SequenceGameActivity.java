package com.mihir.alzheimerscaregiver;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SequenceGameActivity extends AppCompatActivity {

    // UI Elements
    private ImageButton backButton;
    private TextView levelText, scoreText, statusText;
    private Button button1, button2, button3, button4;
    private Button startRoundButton;
    private Button[] gameButtons;

    // Game State Variables
    private List<Integer> currentSequence;
    private List<Integer> userSequence;
    private int currentLevel;
    private int score;
    private int sequenceIndex;
    private boolean isShowingSequence;
    private boolean isUserInputMode;
    private Random random;
    private Handler handler;

    // Game Configuration
    private static final int BUTTON_HIGHLIGHT_DURATION = 600; // milliseconds
    private static final int SEQUENCE_DELAY = 800; // milliseconds between highlights
    private static final int POINTS_PER_CORRECT_SEQUENCE = 100;
    private static final int BONUS_POINTS_PER_LEVEL = 50;

    // Button Colors
    private int normalColor;
    private int highlightColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence_game);

        // Initialize game
        initializeViews();
        initializeGame();
        setupClickListeners();
    }

    /**
     * Initialize all UI elements
     */
    private void initializeViews() {
        // Toolbar elements
        backButton = findViewById(R.id.backButton);

        // Game info elements
        levelText = findViewById(R.id.levelText);
        scoreText = findViewById(R.id.scoreText);
        statusText = findViewById(R.id.statusText);

        // Game buttons
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        // Control button
        startRoundButton = findViewById(R.id.startRoundButton);

        // Create array for easier handling
        gameButtons = new Button[]{button1, button2, button3, button4};

        // Set button colors
        normalColor = getResources().getColor(R.color.button_normal, null);
        highlightColor = getResources().getColor(R.color.primary_color, null);
    }

    /**
     * Initialize game variables and state
     */
    private void initializeGame() {
        currentSequence = new ArrayList<>();
        userSequence = new ArrayList<>();
        currentLevel = 1;
        score = 0;
        sequenceIndex = 0;
        isShowingSequence = false;
        isUserInputMode = false;
        random = new Random();
        handler = new Handler();

        // Update UI
        updateUI();
        setGameButtonsEnabled(false);

        // Set initial button colors
        for (int i = 0; i < gameButtons.length; i++) {
            setButtonColor(i, normalColor);
            gameButtons[i].setText(String.valueOf(i + 1)); // Optional: show numbers
        }
    }

    /**
     * Set up click listeners for all interactive elements
     */
    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                finish();
            }
        });

        // Start round button
        startRoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                startNewRound();
            }
        });

        // Game buttons click listeners
        for (int i = 0; i < gameButtons.length; i++) {
            final int buttonIndex = i;
            gameButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isUserInputMode) {
                        v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                        handleUserInput(buttonIndex);
                    }
                }
            });
        }
    }

    /**
     * Generate a random sequence of button presses
     */
    private void generateRandomSequence() {
        currentSequence.clear();

        // Sequence length increases with level (minimum 2, maximum 8)
        int sequenceLength = Math.min(currentLevel + 1, 8);

        for (int i = 0; i < sequenceLength; i++) {
            currentSequence.add(random.nextInt(4)); // 0-3 for four buttons
        }

        showToast("New sequence generated! Length: " + sequenceLength);
    }

    /**
     * Play the sequence to the user by highlighting buttons
     */
    private void playSequenceToUser() {
        isShowingSequence = true;
        isUserInputMode = false;
        sequenceIndex = 0;
        setGameButtonsEnabled(false);
        startRoundButton.setEnabled(false);

        statusText.setText("Watch the sequence carefully...");

        // Start showing sequence after a brief delay
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNextButtonInSequence();
            }
        }, 1000);
    }

    /**
     * Show the next button in the sequence
     */
    private void showNextButtonInSequence() {
        if (sequenceIndex < currentSequence.size()) {
            int buttonIndex = currentSequence.get(sequenceIndex);
            highlightButton(buttonIndex);

            sequenceIndex++;

            // Schedule next button highlight
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showNextButtonInSequence();
                }
            }, SEQUENCE_DELAY);
        } else {
            // Sequence finished, enable user input
            finishSequencePlayback();
        }
    }

    /**
     * Highlight a specific button temporarily
     */
    private void highlightButton(int buttonIndex) {
        // Set highlight color
        setButtonColor(buttonIndex, highlightColor);

        // Add scale animation for better visibility
        animateButton(gameButtons[buttonIndex]);

        // Return to normal color after highlight duration
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setButtonColor(buttonIndex, normalColor);
            }
        }, BUTTON_HIGHLIGHT_DURATION);
    }

    /**
     * Animate button press for visual feedback
     */
    private void animateButton(Button button) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.2f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(BUTTON_HIGHLIGHT_DURATION);
        animatorSet.start();
    }

    /**
     * Set button background color
     */
    private void setButtonColor(int buttonIndex, int color) {
        if (buttonIndex >= 0 && buttonIndex < gameButtons.length) {
            gameButtons[buttonIndex].setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }

    /**
     * Finish sequence playback and enable user input
     */
    private void finishSequencePlayback() {
        isShowingSequence = false;
        isUserInputMode = true;
        userSequence.clear();

        setGameButtonsEnabled(true);
        statusText.setText("Now repeat the sequence by tapping the buttons");
    }

    /**
     * Handle user input when they tap a game button
     */
    private void handleUserInput(int buttonIndex) {
        // Add user's choice to their sequence
        userSequence.add(buttonIndex);

        // Provide visual feedback
        highlightButton(buttonIndex);

        // Check if user's input matches the correct sequence so far
        if (isUserInputCorrect()) {
            if (userSequence.size() == currentSequence.size()) {
                // User completed the sequence correctly
                handleCorrectSequence();
            } else {
                // User is on track, continue
                statusText.setText("Good! Continue the sequence... (" +
                        userSequence.size() + "/" + currentSequence.size() + ")");
            }
        } else {
            // User made a mistake
            handleIncorrectSequence();
        }
    }

    /**
     * Check if user's current input sequence is correct
     */
    private boolean isUserInputCorrect() {
        if (userSequence.size() > currentSequence.size()) {
            return false;
        }

        for (int i = 0; i < userSequence.size(); i++) {
            if (!userSequence.get(i).equals(currentSequence.get(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Handle correct sequence completion
     */
    private void handleCorrectSequence() {
        isUserInputMode = false;
        setGameButtonsEnabled(false);

        // Calculate score
        int roundScore = POINTS_PER_CORRECT_SEQUENCE + (currentLevel * BONUS_POINTS_PER_LEVEL);
        score += roundScore;

        // Level up
        currentLevel++;

        // Update UI
        updateUI();
        statusText.setText("Excellent! +"+roundScore+" points. Level up!");

        // Show success message
        showToast("Perfect! Moving to Level " + currentLevel);

        // Enable start button for next round
        startRoundButton.setEnabled(true);
        startRoundButton.setText("Next Level");
    }

    /**
     * Handle incorrect sequence
     */
    private void handleIncorrectSequence() {
        isUserInputMode = false;
        setGameButtonsEnabled(false);

        // Show the correct sequence briefly
        statusText.setText("Oops! Let's try again. The correct sequence was:");
        showCorrectSequence();

        // Reset level if desired (optional - you can remove this line to keep progress)
        // currentLevel = Math.max(1, currentLevel - 1);

        updateUI();
        showToast("Don't worry! Try again when you're ready.");

        // Enable start button to try again
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusText.setText("Tap 'Try Again' when you're ready");
                startRoundButton.setEnabled(true);
                startRoundButton.setText("Try Again");
            }
        }, 3000);
    }

    /**
     * Show the correct sequence to help the user learn
     */
    private void showCorrectSequence() {
        handler.postDelayed(new Runnable() {
            int index = 0;
            @Override
            public void run() {
                if (index < currentSequence.size()) {
                    highlightButton(currentSequence.get(index));
                    index++;
                    handler.postDelayed(this, SEQUENCE_DELAY);
                }
            }
        }, 500);
    }

    /**
     * Start a new round of the game
     */
    private void startNewRound() {
        // Reset user input
        userSequence.clear();

        // Generate new sequence
        generateRandomSequence();

        // Start playing sequence to user
        playSequenceToUser();

        // Update start button
        startRoundButton.setText("Playing...");
        startRoundButton.setEnabled(false);
    }

    /**
     * Enable or disable all game buttons
     */
    private void setGameButtonsEnabled(boolean enabled) {
        for (Button button : gameButtons) {
            button.setEnabled(enabled);
            button.setAlpha(enabled ? 1.0f : 0.6f);
        }
    }

    /**
     * Update UI elements with current game state
     */
    private void updateUI() {
        levelText.setText("Level " + currentLevel);
        scoreText.setText("Score: " + score);
    }

    /**
     * Helper method to show toast messages
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Reset game to initial state
     */
    private void resetGame() {
        currentLevel = 1;
        score = 0;
        currentSequence.clear();
        userSequence.clear();
        isShowingSequence = false;
        isUserInputMode = false;

        updateUI();
        statusText.setText("Tap 'Start Round' to begin");
        startRoundButton.setText("Start Round");
        startRoundButton.setEnabled(true);
        setGameButtonsEnabled(false);

        // Reset button colors
        for (int i = 0; i < gameButtons.length; i++) {
            setButtonColor(i, normalColor);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause any ongoing sequences
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume game state if needed
        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up handler
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
