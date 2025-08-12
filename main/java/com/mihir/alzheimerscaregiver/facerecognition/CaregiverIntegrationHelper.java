package com.mihir.alzheimerscaregiver.facerecognition;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.util.HashSet;
import java.util.Set;

public class CaregiverIntegrationHelper {
    private static final String PREFS_NAME = "caregiver_prefs";
    private static final String KEY_CAREGIVERS = "registered_caregivers";
    private static final String KEY_PATIENTS = "registered_patients";

    private Context context;
    private FaceRecognitionHelper faceRecognitionHelper;
    private SharedPreferences sharedPreferences;

    public CaregiverIntegrationHelper(Context context) {
        this.context = context;
        this.faceRecognitionHelper = new FaceRecognitionHelper(context);
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean registerCaregiver(String name, Bitmap faceBitmap) {
        if (faceRecognitionHelper.registerFace(name, faceBitmap)) {
            addToRole(name, KEY_CAREGIVERS);
            return true;
        }
        return false;
    }

    public boolean registerPatient(String name, Bitmap faceBitmap) {
        if (faceRecognitionHelper.registerFace(name, faceBitmap)) {
            addToRole(name, KEY_PATIENTS);
            return true;
        }
        return false;
    }

    public UserRole recognizeUser(Bitmap faceBitmap) {
        FaceRecognitionHelper.RecognitionResult result = faceRecognitionHelper.recognizeFace(faceBitmap);

        if (result.isRecognized()) {
            String name = result.getName();
            if (isCaregiver(name)) {
                return new UserRole(name, UserRole.Role.CAREGIVER, result.getConfidence());
            } else if (isPatient(name)) {
                return new UserRole(name, UserRole.Role.PATIENT, result.getConfidence());
            } else {
                return new UserRole(name, UserRole.Role.FAMILY, result.getConfidence());
            }
        }

        return new UserRole("Unknown", UserRole.Role.UNKNOWN, 0f);
    }

    private void addToRole(String name, String roleKey) {
        Set<String> users = sharedPreferences.getStringSet(roleKey, new HashSet<>());
        users.add(name);
        sharedPreferences.edit().putStringSet(roleKey, users).apply();
    }

    private boolean isCaregiver(String name) {
        Set<String> caregivers = sharedPreferences.getStringSet(KEY_CAREGIVERS, new HashSet<>());
        return caregivers.contains(name);
    }

    private boolean isPatient(String name) {
        Set<String> patients = sharedPreferences.getStringSet(KEY_PATIENTS, new HashSet<>());
        return patients.contains(name);
    }

    public void close() {
        if (faceRecognitionHelper != null) {
            faceRecognitionHelper.close();
        }
    }

    // User role class
    public static class UserRole {
        public enum Role {
            CAREGIVER, PATIENT, FAMILY, UNKNOWN
        }

        private String name;
        private Role role;
        private float confidence;

        public UserRole(String name, Role role, float confidence) {
            this.name = name;
            this.role = role;
            this.confidence = confidence;
        }

        public String getName() { return name; }
        public Role getRole() { return role; }
        public float getConfidence() { return confidence; }

        public boolean isAuthorized() {
            return role != Role.UNKNOWN;
        }

        public String getRoleString() {
            switch (role) {
                case CAREGIVER: return "Caregiver";
                case PATIENT: return "Patient";
                case FAMILY: return "Family Member";
                default: return "Unknown";
            }
        }
    }
}