package com.mihir.alzheimerscaregiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class GameSelectionActivity extends AppCompatActivity {

    // UI Elements
    private ImageButton backButton;
    private RecyclerView gameSelectionRecyclerView;
    private GameSelectionAdapter gameAdapter;
    private List<GameSelectionAdapter.Game> gamesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selection);

        // Initialize UI elements
        initializeViews();

        // Set up RecyclerView
        setupRecyclerView();

        // Create games list
        createGamesList();

        // Set up click listeners
        setupClickListeners();
    }

    /**
     * Initialize all UI elements
     */
    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        gameSelectionRecyclerView = findViewById(R.id.gameSelectionRecyclerView);
    }

    /**
     * Set up RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        // Create empty games list
        gamesList = new ArrayList<>();

        // Initialize adapter
        gameAdapter = new GameSelectionAdapter(gamesList);

        // Set up RecyclerView
        gameSelectionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        gameSelectionRecyclerView.setAdapter(gameAdapter);

        // Set click listener for game items
        gameAdapter.setOnGameClickListener(new GameSelectionAdapter.OnGameClickListener() {
            @Override
            public void onGameClick(GameSelectionAdapter.Game game, int position) {
                handleGameSelection(game, position);
            }
        });
    }

    /**
     * Create the list of available games
     */
    private void createGamesList() {
        gamesList.clear();

        // Card Matching Game
        GameSelectionAdapter.Game cardMatching = new GameSelectionAdapter.Game(
                "Card Matching",
                "Match pairs of cards to test your memory and concentration",
                "🃏",
                MatchingGameActivity.class,
                1 // Memory game type
        );
        gamesList.add(cardMatching);

        // Sequence Memory Game
        GameSelectionAdapter.Game sequenceMemory = new GameSelectionAdapter.Game(
                "Sequence Memory",
                "Remember and repeat the sequence of colors or patterns",
                "🌈",
                SequenceGameActivity.class,
                1 // Memory game type
        );
        gamesList.add(sequenceMemory);

        // Additional games can be added here in the future
        /*
        GameSelectionAdapter.Game wordGame = new GameSelectionAdapter.Game(
                "Word Puzzle",
                "Find words and improve your vocabulary",
                "🔤",
                WordPuzzleActivity.class,
                3 // Word game type
        );
        gamesList.add(wordGame);
        */

        // Notify adapter of data changes
        gameAdapter.notifyDataSetChanged();
    }

    /**
     * Handle game selection and navigation
     */
    private void handleGameSelection(GameSelectionAdapter.Game game, int position) {
        String gameTitle = game.getTitle();

        // Show loading message
        showToast("Starting " + gameTitle + "...");

        // Use if/else to determine which activity to start
        if (gameTitle.equals("Card Matching")) {
            // Start Card Matching Game
            try {
                Intent intent = new Intent(GameSelectionActivity.this, MatchingGameActivity.class);
                // Pass any extra data if needed
                intent.putExtra("GAME_TYPE", "CARD_MATCHING");
                intent.putExtra("GAME_TITLE", gameTitle);
                startActivity(intent);
            } catch (Exception e) {
                // Handle case where MatchingGameActivity doesn't exist yet
                showToast("Card Matching game is coming soon!");
                // TODO: Remove this try-catch once MatchingGameActivity is created
            }
        }
        else if (gameTitle.equals("Sequence Memory")) {
            // Start Sequence Memory Game
            try {
                Intent intent = new Intent(GameSelectionActivity.this, SequenceGameActivity.class);
                // Pass any extra data if needed
                intent.putExtra("GAME_TYPE", "SEQUENCE_MEMORY");
                intent.putExtra("GAME_TITLE", gameTitle);
                startActivity(intent);
            } catch (Exception e) {
                // Handle case where SequenceMemoryActivity doesn't exist yet
                showToast("Sequence Memory game is coming soon!");
                // TODO: Remove this try-catch once SequenceMemoryActivity is created
            }
        }
        else {
            // Handle any other games or fallback
            showToast("Game not yet implemented: " + gameTitle);
        }
    }

    /**
     * Set up click listeners for UI elements
     */
    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add haptic feedback
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                // Go back to previous activity (MainActivity)
                finish();
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
     * Method to add a new game to the list (for future use)
     */
    public void addNewGame(String title, String description, String icon, Class<?> activityClass, int gameType) {
        GameSelectionAdapter.Game newGame = new GameSelectionAdapter.Game(
                title, description, icon, activityClass, gameType
        );
        gameAdapter.addGame(newGame);
    }

    /**
     * Method to refresh the games list (for future use)
     */
    public void refreshGamesList() {
        createGamesList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh games list when returning to this activity
        // This could be useful if games are enabled/disabled based on progress
        refreshGamesList();
    }
}
