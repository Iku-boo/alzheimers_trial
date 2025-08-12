package com.mihir.alzheimerscaregiver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GameSelectionAdapter extends RecyclerView.Adapter<GameSelectionAdapter.GameViewHolder> {

    // Game data model
    public static class Game {
        private String title;
        private String description;
        private String icon;
        private Class<?> activityClass; // For navigation
        private int gameType; // For identifying game type

        public Game(String title, String description, String icon, Class<?> activityClass, int gameType) {
            this.title = title;
            this.description = description;
            this.icon = icon;
            this.activityClass = activityClass;
            this.gameType = gameType;
        }

        // Getters
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
        public Class<?> getActivityClass() { return activityClass; }
        public int getGameType() { return gameType; }
    }

    // Interface for handling click events
    public interface OnGameClickListener {
        void onGameClick(Game game, int position);
    }

    private List<Game> gamesList;
    private OnGameClickListener clickListener;

    // Constructor
    public GameSelectionAdapter(List<Game> gamesList) {
        this.gamesList = gamesList;
    }

    // Set click listener
    public void setOnGameClickListener(OnGameClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_item, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = gamesList.get(position);
        holder.bind(game, position);
    }

    @Override
    public int getItemCount() {
        return gamesList != null ? gamesList.size() : 0;
    }

    // ViewHolder class
    public class GameViewHolder extends RecyclerView.ViewHolder {
        private TextView gameTitleText;
        private TextView gameDescriptionText;
        private TextView gameIconText;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            gameTitleText = itemView.findViewById(R.id.gameTitleText);
            gameDescriptionText = itemView.findViewById(R.id.gameDescriptionText);
            gameIconText = itemView.findViewById(R.id.gameIconText);

            // Set click listener for the entire item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Add haptic feedback
                    v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && clickListener != null) {
                        Game game = gamesList.get(position);
                        clickListener.onGameClick(game, position);
                    }
                }
            });
        }

        public void bind(Game game, int position) {
            // Set game title and description
            gameTitleText.setText(game.getTitle());
            gameDescriptionText.setText(game.getDescription());

            // Set game icon (emoji or text)
            if (game.getIcon() != null && !game.getIcon().isEmpty()) {
                gameIconText.setText(game.getIcon());
            } else {
                // Default icon based on position or game type
                String defaultIcon = getDefaultIcon(game.getGameType());
                gameIconText.setText(defaultIcon);
            }
        }

        /**
         * Get default icon based on game type
         */
        private String getDefaultIcon(int gameType) {
            switch (gameType) {
                case 1: return "🧠"; // Memory games
                case 2: return "🎯"; // Puzzle games
                case 3: return "🔤"; // Word games
                case 4: return "🧮"; // Math games
                case 5: return "🎨"; // Creative games
                default: return "🎮"; // General games
            }
        }
    }

    // Methods for updating the list
    public void updateGames(List<Game> newGamesList) {
        this.gamesList = newGamesList;
        notifyDataSetChanged();
    }

    public void addGame(Game game) {
        if (gamesList != null) {
            gamesList.add(game);
            notifyItemInserted(gamesList.size() - 1);
        }
    }

    public void removeGame(int position) {
        if (gamesList != null && position >= 0 && position < gamesList.size()) {
            gamesList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Get game at specific position
    public Game getGame(int position) {
        if (gamesList != null && position >= 0 && position < gamesList.size()) {
            return gamesList.get(position);
        }
        return null;
    }
}
