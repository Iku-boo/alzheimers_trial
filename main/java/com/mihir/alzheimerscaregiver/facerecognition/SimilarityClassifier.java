package com.mihir.alzheimerscaregiver.facerecognition;

import android.graphics.RectF;
import java.util.List;

public class SimilarityClassifier {

    /**
     * Recognition class represents a single face recognition result
     * This is a data class that holds information about a recognized face
     */
    public static class Recognition {
        private final String id;
        private final String title;
        private final Float confidence;
        private RectF location;
        private Integer color;
        private Object extra;

        /**
         * Constructor for Recognition object
         * @param id Unique identifier for the recognition
         * @param title Name/label of the recognized person
         * @param confidence Confidence score of the recognition (0.0 to 1.0)
         * @param location Bounding box of the detected face
         */
        public Recognition(String id, String title, Float confidence, RectF location) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.location = location;
            this.color = null;
            this.extra = null;
        }

        /**
         * Get the unique identifier
         * @return Recognition ID
         */
        public String getId() {
            return id;
        }

        /**
         * Get the title/name of the recognized person
         * @return Person's name
         */
        public String getTitle() {
            return title;
        }

        /**
         * Get the confidence score of the recognition
         * @return Confidence value between 0.0 and 1.0
         */
        public Float getConfidence() {
            return confidence;
        }

        /**
         * Get the bounding box location of the face
         * @return RectF containing the face coordinates
         */
        public RectF getLocation() {
            return new RectF(location);
        }

        /**
         * Set the location of the detected face
         * @param location RectF with face coordinates
         */
        public void setLocation(RectF location) {
            this.location = location;
        }

        /**
         * Get the color associated with this recognition (for UI display)
         * @return Color integer value
         */
        public Integer getColor() {
            return color;
        }

        /**
         * Set the color for this recognition (for UI display)
         * @param color Color integer value
         */
        public void setColor(Integer color) {
            this.color = color;
        }

        /**
         * Get extra data associated with this recognition (like face embeddings)
         * @return Extra object data
         */
        public Object getExtra() {
            return extra;
        }

        /**
         * Set extra data for this recognition (like face embeddings)
         * @param extra Object containing additional data
         */
        public void setExtra(Object extra) {
            this.extra = extra;
        }

        /**
         * Convert the recognition to a readable string
         * @return String representation of the recognition
         */
        @Override
        public String toString() {
            String resultString = "";
            if (id != null) {
                resultString += "[" + id + "] ";
            }
            if (title != null) {
                resultString += title + " ";
            }
            if (confidence != null) {
                resultString += String.format("(%.1f%%) ", confidence * 100.0f);
            }
            if (location != null) {
                resultString += location + " ";
            }
            return resultString.trim();
        }

        /**
         * Check if this recognition represents the same person as another
         * @param other Another Recognition object to compare with
         * @return true if they represent the same person
         */
        public boolean isSamePerson(Recognition other) {
            if (other == null) return false;
            if (this.title == null || other.title == null) return false;
            return this.title.equals(other.title);
        }

        /**
         * Check if the recognition confidence is above a threshold
         * @param threshold Minimum confidence threshold (0.0 to 1.0)
         * @return true if confidence is above threshold
         */
        public boolean isConfidenceAboveThreshold(float threshold) {
            return confidence != null && confidence >= threshold;
        }

        /**
         * Get a formatted confidence percentage string
         * @return Confidence as percentage string (e.g., "85.3%")
         */
        public String getConfidencePercentage() {
            if (confidence == null) return "0.0%";
            return String.format("%.1f%%", confidence * 100.0f);
        }
    }

    /**
     * Interface for face recognition classifiers
     * This defines the contract that any face recognition implementation should follow
     */
    public interface Classifier {

        /**
         * Recognize faces in the provided image data
         * @param pixels Raw pixel data of the image
         * @param width Width of the image
         * @param height Height of the image
         * @return List of Recognition objects for detected faces
         */
        List<Recognition> recognizeImage(byte[] pixels, int width, int height);

        /**
         * Enable or disable statistical logging for performance monitoring
         * @param debug true to enable debug logging
         */
        void enableStatLogging(boolean debug);

        /**
         * Get performance statistics as a string
         * @return String containing performance statistics
         */
        String getStatString();

        /**
         * Close and cleanup the classifier resources
         */
        void close();

        /**
         * Set the number of threads to use for processing
         * @param numThreads Number of processing threads
         */
        void setNumThreads(int numThreads);

        /**
         * Enable or disable NNAPI acceleration
         * @param isChecked true to use NNAPI if available
         */
        void setUseNNAPI(boolean isChecked);
    }

    /**
     * Utility methods for similarity calculations and face recognition processing
     */
    public static class Utils {

        /**
         * Calculate cosine similarity between two face embeddings
         * @param embedding1 First face embedding vector
         * @param embedding2 Second face embedding vector
         * @return Similarity score between 0.0 and 1.0
         */
        public static float calculateCosineSimilarity(float[] embedding1, float[] embedding2) {
            if (embedding1.length != embedding2.length) {
                return 0f;
            }

            float dotProduct = 0f;
            float norm1 = 0f;
            float norm2 = 0f;

            for (int i = 0; i < embedding1.length; i++) {
                dotProduct += embedding1[i] * embedding2[i];
                norm1 += embedding1[i] * embedding1[i];
                norm2 += embedding2[i] * embedding2[i];
            }

            norm1 = (float) Math.sqrt(norm1);
            norm2 = (float) Math.sqrt(norm2);

            if (norm1 == 0f || norm2 == 0f) {
                return 0f;
            }

            return dotProduct / (norm1 * norm2);
        }

        /**
         * Calculate Euclidean distance between two face embeddings
         * @param embedding1 First face embedding vector
         * @param embedding2 Second face embedding vector
         * @return Distance value (lower means more similar)
         */
        public static float calculateEuclideanDistance(float[] embedding1, float[] embedding2) {
            if (embedding1.length != embedding2.length) {
                return Float.MAX_VALUE;
            }

            float sum = 0f;
            for (int i = 0; i < embedding1.length; i++) {
                float diff = embedding1[i] - embedding2[i];
                sum += diff * diff;
            }

            return (float) Math.sqrt(sum);
        }

        /**
         * Find the best matching recognition from a list based on similarity threshold
         * @param recognitions List of possible recognitions
         * @param threshold Minimum similarity threshold
         * @return Best matching Recognition or null if none meet threshold
         */
        public static Recognition findBestMatch(List<Recognition> recognitions, float threshold) {
            Recognition bestMatch = null;
            float bestConfidence = threshold;

            for (Recognition recognition : recognitions) {
                if (recognition.getConfidence() != null && recognition.getConfidence() > bestConfidence) {
                    bestMatch = recognition;
                    bestConfidence = recognition.getConfidence();
                }
            }

            return bestMatch;
        }

        /**
         * Filter recognitions by minimum confidence threshold
         * @param recognitions List of recognitions to filter
         * @param minConfidence Minimum confidence threshold
         * @return List of recognitions above the threshold
         */
        public static List<Recognition> filterByConfidence(List<Recognition> recognitions, float minConfidence) {
            return recognitions.stream()
                    .filter(recognition -> recognition.isConfidenceAboveThreshold(minConfidence))
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    // Constants for face recognition
    public static final float DEFAULT_SIMILARITY_THRESHOLD = 0.75f;
    public static final float HIGH_CONFIDENCE_THRESHOLD = 0.85f;
    public static final float LOW_CONFIDENCE_THRESHOLD = 0.60f;

    /**
     * Default constructor
     */
    public SimilarityClassifier() {
        // Empty constructor for utility class
    }
}