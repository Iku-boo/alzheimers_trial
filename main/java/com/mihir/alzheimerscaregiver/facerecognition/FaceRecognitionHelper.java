package com.mihir.alzheimerscaregiver.facerecognition;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class FaceRecognitionHelper {
    private static final String TAG = "FaceRecognitionHelper";
    private static final String PREFS_NAME = "face_recognition_prefs";
    private static final String KEY_REGISTERED_FACES = "registered_faces";
    private static final float SIMILARITY_THRESHOLD = 0.75f;

    private Context context;
    private static FaceNetModel faceNetModel;
    private static HashMap<String, float[]> registeredFaces;
    private static SharedPreferences sharedPreferences;
    private static Gson gson;

    public FaceRecognitionHelper(Context context) {
        this.context = context;
        this.faceNetModel = new FaceNetModel(context);
        this.registeredFaces = new HashMap<>();
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        loadRegisteredFaces();
    }

    public boolean registerFace(String personName, Bitmap faceBitmap) {
        try {
            float[] embedding = faceNetModel.getFaceEmbedding(faceBitmap);
            if (embedding != null) {
                registeredFaces.put(personName, embedding);
                saveRegisteredFaces();
                Log.d(TAG, "Face registered successfully for " + personName);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to register face for " + personName, e);
        }
        return false;
    }

    public RecognitionResult recognizeFace(Bitmap faceBitmap) {
        try {
            if (registeredFaces.isEmpty()) {
                return new RecognitionResult("No registered faces", 0f, false);
            }

            float[] currentEmbedding = faceNetModel.getFaceEmbedding(faceBitmap);
            if (currentEmbedding == null) {
                return new RecognitionResult("Failed to process face", 0f, false);
            }

            String bestMatch = "Unknown";
            float bestSimilarity = 0f;

            for (Map.Entry<String, float[]> entry : registeredFaces.entrySet()) {
                float similarity = FaceNetModel.calculateSimilarity(currentEmbedding, entry.getValue());

                if (similarity > bestSimilarity) {
                    bestMatch = entry.getKey();
                    bestSimilarity = similarity;
                }
            }

            boolean isRecognized = bestSimilarity > SIMILARITY_THRESHOLD;
            if (!isRecognized) {
                bestMatch = "Unknown";
            }

            return new RecognitionResult(bestMatch, bestSimilarity, isRecognized);

        } catch (Exception e) {
            Log.e(TAG, "Failed to recognize face", e);
            return new RecognitionResult("Recognition failed", 0f, false);
        }
    }

    public static boolean deleteFace(String personName) {
        if (registeredFaces.containsKey(personName)) {
            registeredFaces.remove(personName);
            saveRegisteredFaces();
            return true;
        }
        return false;
    }

    public static String[] getRegisteredFaceNames() {
        return registeredFaces.keySet().toArray(new String[0]);
    }

    public int getRegisteredFaceCount() {
        return registeredFaces.size();
    }

    private static void saveRegisteredFaces() {
        try {
            String json = gson.toJson(registeredFaces);
            sharedPreferences.edit().putString(KEY_REGISTERED_FACES, json).apply();
            Log.d(TAG, "Registered faces saved successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to save registered faces", e);
        }
    }

    private void loadRegisteredFaces() {
        try {
            String json = sharedPreferences.getString(KEY_REGISTERED_FACES, "{}");
            Type type = new TypeToken<HashMap<String, float[]>>(){}.getType();
            HashMap<String, float[]> loaded = gson.fromJson(json, type);

            if (loaded != null) {
                registeredFaces = loaded;
                Log.d(TAG, "Loaded " + registeredFaces.size() + " registered faces");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load registered faces", e);
            registeredFaces = new HashMap<>();
        }
    }

    public static void close() {
        if (faceNetModel != null) {
            faceNetModel.close();
        }
    }

    // Result class for recognition
    public static class RecognitionResult {
        private String name;
        private float confidence;
        private boolean isRecognized;

        public RecognitionResult(String name, float confidence, boolean isRecognized) {
            this.name = name;
            this.confidence = confidence;
            this.isRecognized = isRecognized;
        }

        public String getName() { return name; }
        public float getConfidence() { return confidence; }
        public boolean isRecognized() { return isRecognized; }

        @Override
        public String toString() {
            return name + " (" + String.format("%.2f", confidence * 100) + "%)";
        }
    }
}