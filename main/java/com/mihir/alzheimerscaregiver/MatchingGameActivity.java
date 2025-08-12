package com.mihir.alzheimerscaregiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MatchingGameActivity extends AppCompatActivity {

    // UI Elements
    private ImageButton backButton;
    private TextView movesCounterText;
    private TextView gameStatusText;
    private RecyclerView gameGridRecyclerView;
    private Button resetGameButton;
    private Button hintButton;
    private LinearLayout gameCompletionOverlay;
    private TextView completionStatsText;

    // Game Components
    private MatchingGameAdapter gameAdapter;
    private GridLayoutManager gridLayoutManager;

    // Game Configuration
    private static final int GRID_COLUMNS = 4; // 4x4 grid = 16 cards (8 pairs)
    private static final int EASY_GRID_COLUMNS = 3; // 3x2 grid = 6 cards (3 pairs) - for easier gameplay

    // Card Images - Array of drawable resources for card faces
    private int[] cardFaceImages = {
            R.drawable.card_apple,      // Apple
            R.drawable.card_banana,     // Banana
            R.drawable.card_cherry,     // Cherry
            R.drawable.card_grapes,     // Grapes
            R.drawable.card_lemon,      // Lemon
            R.drawable.card_orange,     // Orange
            R.drawable.card_strawberry, // Strawberry
            R.drawable.card_watermelon  // Watermelon
    };

    // Alternative simple card images (using emojis or simple shapes)
    private int[] simpleCardImages = {
            R.drawable.card_heart,      // Heart
            R.drawable.card_star,       // Star
            R.drawable.card_circle,     // Circle
            R.drawable.card_square,     // Square
            R.drawable.card_triangle,   // Triangle
            R.drawable.card_diamond     // Diamond
    };

    private int cardBackImage = R.drawable.card_back_default;

    // Game State
    private boolean isEasyMode = false; // Can be set based on user preference
    private int currentDifficulty = GRID_COLUMNS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_game);

        // Initialize UI elements
        initializeViews();

        // Set up RecyclerView
        setupRecyclerView();

        // Set up click listeners
        setupClickListeners();

        // Start new game
        startNewGame();
    }

    /**
     * Initialize all UI elements
     */
    private void initializeViews() {
        // Toolbar elements
        backButton = findViewById(R.id.backButton);

        // Game info elements
        movesCounterText = findViewById(R.id.movesCounterText);
        gameStatusText = findViewById(R.id.gameStatusText);

        // Game elements
        gameGridRecyclerView = findViewById(R.id.gameGridRecyclerView);
        resetGameButton = findViewById(R.id.resetGameButton);
        hintButton = findViewById(R.id.hintButton);

        // Completion overlay
        gameCompletionOverlay = findViewById(R.id.gameCompletionOverlay);
        completionStatsText = findViewById(R.id.completionStatsText);
    }

    /**
     * Set up RecyclerView with GridLayoutManager and adapter
     */
    private void setupRecyclerView() {
        // Determine grid size based on difficulty
        currentDifficulty = isEasyMode ? EASY_GRID_COLUMNS : GRID_COLUMNS;

        // Set up GridLayoutManager
        gridLayoutManager = new GridLayoutManager(this, currentDifficulty);
        gameGridRecyclerView.setLayoutManager(gridLayoutManager);

        // Initialize adapter
        gameAdapter = new MatchingGameAdapter();
        gameGridRecyclerView.setAdapter(gameAdapter);

        // Set up game event listener
        gameAdapter.setOnGameEventListener(new MatchingGameAdapter.OnGameEventListener() {
            @Override
            public void onMoveCountChanged(int moveCount) {
                updateMovesCounter(moveCount);
            }

            @Override
            public void onGameCompleted(int totalMoves) {
                handleGameCompletion(totalMoves);
            }

            @Override
            public void onCardsFlipped(int flippedCount) {
                updateGameStatus(flippedCount);
            }

            @Override
            public void onMatchFound(int matchedPairs, int totalPairs) {
                handleMatchFound(matchedPairs, totalPairs);
            }
        });
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

        // Reset game button
        resetGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                resetGame();
            }
        });

        // Hint button (optional feature)
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                showHint();
            }
        });

        // Completion overlay click (to dismiss)
        gameCompletionOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCompletionOverlay();
            }
        });
    }

    /**
     * Start a new game with shuffled cards
     */
    private void startNewGame() {
        // Hide completion overlay if visible
        hideCompletionOverlay();

        // Create shuffled list of card pairs
        int[] gameCards = createShuffledCardPairs();

        // Initialize the game with the shuffled cards
        gameAdapter.initializeGame(gameCards, cardBackImage);

        // Update UI
        updateGameStatus(0);
        gameStatusText.setText("Find all pairs!");

        // Show start message
        showToast("Game started! Find all matching pairs.");
    }

    /**
     * Create a shuffled list of matching card pairs
     */
    private int[] createShuffledCardPairs() {
        // Determine number of pairs based on difficulty
        int numberOfPairs;
        int[] sourceImages;

        if (isEasyMode) {
            numberOfPairs = 3; // 6 cards total
            sourceImages = simpleCardImages;
        } else {
            numberOfPairs = 8; // 16 cards total
            sourceImages = cardFaceImages;
        }

        // Ensure we don't exceed available card images
        numberOfPairs = Math.min(numberOfPairs, sourceImages.length);

        // Create list with the required number of unique card images
        List<Integer> cardImagesList = new ArrayList<>();
        for (int i = 0; i < numberOfPairs; i++) {
            cardImagesList.add(sourceImages[i]);
        }

        // Convert to array
        int[] gameCards = new int[cardImagesList.size()];
        for (int i = 0; i < cardImagesList.size(); i++) {
            gameCards[i] = cardImagesList.get(i);
        }

        return gameCards;
    }

    /**
     * Reset the game (reshuffle cards and start over)
     */
    private void resetGame() {
        // Show reset confirmation
        showToast("Reshuffling cards...");

        // Reset the adapter (this reshuffles the cards)
        gameAdapter.resetGame();

        // Update UI
        updateGameStatus(0);
        gameStatusText.setText("Find all pairs!");

        // Hide completion overlay
        hideCompletionOverlay();
    }

    /**
     * Update moves counter display
     */
    private void updateMovesCounter(int moveCount) {
        movesCounterText.setText("Moves: " + moveCount);
    }

    /**
     * Update game status based on current state
     */
    private void updateGameStatus(int flippedCount) {
        if (flippedCount == 0) {
            gameStatusText.setText("Tap cards to flip them");
        } else if (flippedCount == 1) {
            gameStatusText.setText("Find the matching card");
        } else if (flippedCount == 2) {
            gameStatusText.setText("Checking for match...");
        }
    }

    /**
     * Handle when a match is found
     */
    private void handleMatchFound(int matchedPairs, int totalPairs) {
        if (matchedPairs < totalPairs) {
            gameStatusText.setText("Great match! " + matchedPairs + "/" + totalPairs + " pairs found");
            showToast("Nice match! Keep going!");
        }
    }

    /**
     * Handle game completion
     */
    private void handleGameCompletion(int totalMoves) {
        // Update completion stats
        String statsText = "Completed in " + totalMoves + " moves!\\n";

        // Calculate performance rating
        int totalPairs = gameAdapter.getTotalPairs();
        int perfectMoves = totalPairs; // Perfect would be finding each pair in one try

        if (totalMoves <= perfectMoves + 2) {
            statsText += "Excellent memory!";
        } else if (totalMoves <= perfectMoves * 2) {
            statsText += "Great job!";
        } else {
            statsText += "Well done!";
        }

        completionStatsText.setText(statsText);

        // Show completion overlay
        showCompletionOverlay();

        // Show toast message
        showToast("🎉 Congratulations! Game completed!");
    }

    /**
     * Show hint to help the player
     */
    private void showHint() {
        int matchedPairs = gameAdapter.getMatchedPairs();
        int totalPairs = gameAdapter.getTotalPairs();
        int remainingPairs = totalPairs - matchedPairs;

        if (gameAdapter.isGameCompleted()) {
            showToast("Game already completed!");
        } else {
            showToast("Hint: " + remainingPairs + " pairs remaining. Look for similar images!");
        }
    }

    /**
     * Show game completion overlay
     */
    private void showCompletionOverlay() {
        gameCompletionOverlay.setVisibility(View.VISIBLE);
        resetGameButton.setText("New Game");
    }

    /**
     * Hide game completion overlay
     */
    private void hideCompletionOverlay() {
        gameCompletionOverlay.setVisibility(View.GONE);
        resetGameButton.setText("Reset Game");
    }

    /**
     * Toggle difficulty mode
     */
    private void toggleDifficulty() {
        isEasyMode = !isEasyMode;
        currentDifficulty = isEasyMode ? EASY_GRID_COLUMNS : GRID_COLUMNS;

        // Update grid layout
        gridLayoutManager.setSpanCount(currentDifficulty);

        // Start new game with new difficulty
        startNewGame();

        String difficultyText = isEasyMode ? "Easy Mode" : "Normal Mode";
        showToast("Switched to " + difficultyText);
    }

    /**
     * Helper method to show toast messages
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Get game statistics for sharing or display
     */
    private String getGameStats() {
        return "Memory Game Stats:\\n" +
                "Moves: " + gameAdapter.getMoveCount() + "\\n" +
                "Pairs Found: " + gameAdapter.getMatchedPairs() + "/" + gameAdapter.getTotalPairs() + "\\n" +
                "Difficulty: " + (isEasyMode ? "Easy" : "Normal");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Game automatically pauses when activity is paused
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update UI when returning to the activity
        updateMovesCounter(gameAdapter.getMoveCount());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up adapter resources
        if (gameAdapter != null) {
            gameAdapter.cleanup();
        }
    }
}
