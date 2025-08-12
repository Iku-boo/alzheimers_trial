package com.mihir.alzheimerscaregiver;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchingGameAdapter extends RecyclerView.Adapter<MatchingGameAdapter.CardViewHolder> {

    // Card states
    public enum CardState {
        FACE_DOWN,    // Card is face down (showing back)
        FACE_UP,      // Card is face up (showing front)
        MATCHED       // Card has been matched and stays face up
    }

    // Card data model
    public static class Card {
        private int faceImageResource;
        private int backImageResource;
        private CardState state;
        private int pairId; // Cards with same pairId are matches
        private int position;

        public Card(int faceImageResource, int backImageResource, int pairId, int position) {
            this.faceImageResource = faceImageResource;
            this.backImageResource = backImageResource;
            this.pairId = pairId;
            this.position = position;
            this.state = CardState.FACE_DOWN;
        }

        // Getters and setters
        public int getFaceImageResource() { return faceImageResource; }
        public int getBackImageResource() { return backImageResource; }
        public CardState getState() { return state; }
        public void setState(CardState state) { this.state = state; }
        public int getPairId() { return pairId; }
        public int getPosition() { return position; }
        public void setPosition(int position) { this.position = position; }
        public boolean isMatched() { return state == CardState.MATCHED; }
        public boolean isFaceUp() { return state == CardState.FACE_UP || state == CardState.MATCHED; }
    }

    // Game state tracking
    private List<Card> cards;
    private List<Card> flippedCards;
    private int moveCount;
    private int matchedPairs;
    private boolean isProcessingMove;
    private Handler handler;

    // Game configuration
    private static final int FLIP_ANIMATION_DURATION = 300;
    private static final int MISMATCH_DELAY = 1000; // Time to show mismatched cards before flipping back
    private static final int MATCH_DELAY = 500; // Delay before marking cards as matched

    // Interfaces for communication with activity
    public interface OnGameEventListener {
        void onMoveCountChanged(int moveCount);
        void onGameCompleted(int totalMoves);
        void onCardsFlipped(int flippedCount);
        void onMatchFound(int matchedPairs, int totalPairs);
    }

    private OnGameEventListener gameEventListener;

    // Constructor
    public MatchingGameAdapter() {
        this.cards = new ArrayList<>();
        this.flippedCards = new ArrayList<>();
        this.moveCount = 0;
        this.matchedPairs = 0;
        this.isProcessingMove = false;
        this.handler = new Handler();
    }

    // Set game event listener
    public void setOnGameEventListener(OnGameEventListener listener) {
        this.gameEventListener = listener;
    }

    /**
     * Initialize the game with a set of card pairs
     */
    public void initializeGame(int[] cardImages, int cardBackImage) {
        cards.clear();
        flippedCards.clear();
        moveCount = 0;
        matchedPairs = 0;
        isProcessingMove = false;

        // Create pairs of cards
        int pairId = 0;
        int position = 0;
        for (int imageResource : cardImages) {
            // Add first card of the pair
            cards.add(new Card(imageResource, cardBackImage, pairId, position++));
            // Add second card of the pair
            cards.add(new Card(imageResource, cardBackImage, pairId, position++));
            pairId++;
        }

        // Shuffle the cards
        Collections.shuffle(cards);

        // Update positions after shuffle
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setPosition(i);
        }

        notifyDataSetChanged();

        // Notify listener
        if (gameEventListener != null) {
            gameEventListener.onMoveCountChanged(moveCount);
        }
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.bind(card);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    /**
     * Handle card click
     */
    private void handleCardClick(int position) {
        // Don't process clicks during animations or if game is processing
        if (isProcessingMove || position < 0 || position >= cards.size()) {
            return;
        }

        Card clickedCard = cards.get(position);

        // Don't flip if card is already face up or matched
        if (clickedCard.isFaceUp()) {
            return;
        }

        // Don't allow more than 2 cards to be flipped at once
        if (flippedCards.size() >= 2) {
            return;
        }

        // Flip the card
        flipCard(clickedCard, position);
    }

    /**
     * Flip a card to face up
     */
    private void flipCard(Card card, int position) {
        card.setState(CardState.FACE_UP);
        flippedCards.add(card);

        // Animate and update the view
        notifyItemChanged(position);

        // Notify listener about flipped cards
        if (gameEventListener != null) {
            gameEventListener.onCardsFlipped(flippedCards.size());
        }

        // Check if we have two flipped cards
        if (flippedCards.size() == 2) {
            moveCount++;
            if (gameEventListener != null) {
                gameEventListener.onMoveCountChanged(moveCount);
            }

            // Process the move after a short delay
            isProcessingMove = true;
            handler.postDelayed(() -> processMove(), MATCH_DELAY);
        }
    }

    /**
     * Process the move when two cards are flipped
     */
    private void processMove() {
        if (flippedCards.size() != 2) {
            isProcessingMove = false;
            return;
        }

        Card firstCard = flippedCards.get(0);
        Card secondCard = flippedCards.get(1);

        if (areCardsMatching(firstCard, secondCard)) {
            // Cards match!
            handleMatchedCards(firstCard, secondCard);
        } else {
            // Cards don't match
            handleMismatchedCards(firstCard, secondCard);
        }
    }

    /**
     * Check if two cards are a match
     */
    private boolean areCardsMatching(Card card1, Card card2) {
        return card1.getPairId() == card2.getPairId();
    }

    /**
     * Handle matched cards
     */
    private void handleMatchedCards(Card card1, Card card2) {
        // Mark cards as matched
        card1.setState(CardState.MATCHED);
        card2.setState(CardState.MATCHED);

        matchedPairs++;

        // Update views with matched state
        notifyItemChanged(card1.getPosition());
        notifyItemChanged(card2.getPosition());

        // Clear flipped cards
        flippedCards.clear();
        isProcessingMove = false;

        // Notify listener
        if (gameEventListener != null) {
            int totalPairs = cards.size() / 2;
            gameEventListener.onMatchFound(matchedPairs, totalPairs);

            // Check if game is completed
            if (matchedPairs == totalPairs) {
                handler.postDelayed(() -> {
                    if (gameEventListener != null) {
                        gameEventListener.onGameCompleted(moveCount);
                    }
                }, 500);
            }
        }
    }

    /**
     * Handle mismatched cards
     */
    private void handleMismatchedCards(Card card1, Card card2) {
        // Keep cards face up for a moment, then flip back
        handler.postDelayed(() -> {
            // Flip cards back to face down
            card1.setState(CardState.FACE_DOWN);
            card2.setState(CardState.FACE_DOWN);

            // Update views
            notifyItemChanged(card1.getPosition());
            notifyItemChanged(card2.getPosition());

            // Clear flipped cards
            flippedCards.clear();
            isProcessingMove = false;

            // Notify listener
            if (gameEventListener != null) {
                gameEventListener.onCardsFlipped(0);
            }
        }, MISMATCH_DELAY);
    }

    /**
     * Reset the game to initial state
     */
    public void resetGame() {
        // Reset all cards to face down
        for (Card card : cards) {
            card.setState(CardState.FACE_DOWN);
        }

        // Shuffle cards again
        Collections.shuffle(cards);
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setPosition(i);
        }

        // Reset game state
        flippedCards.clear();
        moveCount = 0;
        matchedPairs = 0;
        isProcessingMove = false;

        // Update all views
        notifyDataSetChanged();

        // Notify listener
        if (gameEventListener != null) {
            gameEventListener.onMoveCountChanged(moveCount);
            gameEventListener.onCardsFlipped(0);
        }
    }

    /**
     * Get current game statistics
     */
    public int getMoveCount() {
        return moveCount;
    }

    public int getMatchedPairs() {
        return matchedPairs;
    }

    public int getTotalPairs() {
        return cards.size() / 2;
    }

    public boolean isGameCompleted() {
        return matchedPairs == getTotalPairs();
    }

    // ViewHolder class
    public class CardViewHolder extends RecyclerView.ViewHolder {
        private ImageView cardImageView;
        private View cardBorder;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImageView = itemView.findViewById(R.id.cardImageView);
            cardBorder = itemView.findViewById(R.id.cardBorder);

            // Set click listener
            itemView.setOnClickListener(v -> {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    handleCardClick(position);
                }
            });
        }

        public void bind(Card card) {
            // Set the appropriate image based on card state
            if (card.isFaceUp()) {
                cardImageView.setImageResource(card.getFaceImageResource());

                // Show border for matched cards
                if (card.isMatched()) {
                    cardBorder.setVisibility(View.VISIBLE);
                    cardImageView.setAlpha(0.8f);
                } else {
                    cardBorder.setVisibility(View.GONE);
                    cardImageView.setAlpha(1.0f);
                }
            } else {
                cardImageView.setImageResource(card.getBackImageResource());
                cardBorder.setVisibility(View.GONE);
                cardImageView.setAlpha(1.0f);
            }

            // Add flip animation when card state changes
            if (card.getState() == CardState.FACE_UP && flippedCards.contains(card)) {
                animateCardFlip(cardImageView);
            }
        }

        /**
         * Animate card flip
         */
        private void animateCardFlip(View view) {
            ObjectAnimator flipOut = ObjectAnimator.ofFloat(view, "rotationY", 0f, 90f);
            ObjectAnimator flipIn = ObjectAnimator.ofFloat(view, "rotationY", -90f, 0f);

            flipOut.setDuration(FLIP_ANIMATION_DURATION / 2);
            flipIn.setDuration(FLIP_ANIMATION_DURATION / 2);

            AnimatorSet flipAnimation = new AnimatorSet();
            flipAnimation.playSequentially(flipOut, flipIn);
            flipAnimation.start();
        }
    }

    /**
     * Cleanup method
     */
    public void cleanup() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
