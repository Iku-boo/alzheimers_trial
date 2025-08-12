package com.mihir.alzheimerscaregiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhotosActivity extends AppCompatActivity {

    // UI Elements
    private ImageButton backButton;
    private RecyclerView photosRecyclerView;
    private LinearLayout emptyStateLayout;
    private FloatingActionButton addPhotoFab;

    // Adapter
    private PhotosAdapter photosAdapter;

    // Sample photo data
    private List<Integer> samplePhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        // Initialize UI elements
        initializeViews();

        // Set up RecyclerView
        setupRecyclerView();

        // Set up click listeners
        setupClickListeners();

        // Load sample photos
        loadSamplePhotos();
    }

    /**
     * Initialize all UI elements
     */
    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        photosRecyclerView = findViewById(R.id.photosRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        addPhotoFab = findViewById(R.id.addPhotoFab);
    }

    /**
     * Set up the RecyclerView with GridLayoutManager and adapter
     */
    private void setupRecyclerView() {
        // Set GridLayoutManager with 2 columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        photosRecyclerView.setLayoutManager(layoutManager);

        // Create sample photo list
        createSamplePhotoList();

        // Create and set adapter
        photosAdapter = new PhotosAdapter(samplePhotos);
        photosRecyclerView.setAdapter(photosAdapter);

        // Set photo click listener
        photosAdapter.setOnPhotoClickListener(new PhotosAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(int position, int resourceId) {
                // Handle photo click - could open full screen view
                showToast("Photo clicked: Position " + position);

                // TODO: Open full-screen photo view
                // Intent intent = new Intent(PhotosActivity.this, FullScreenPhotoActivity.class);
                // intent.putExtra("photo_resource_id", resourceId);
                // intent.putExtra("photo_position", position);
                // startActivity(intent);
            }
        });

        // Show/hide empty state based on photo count
        updateEmptyState();
    }

    /**
     * Create a sample list of photo drawable resources
     * Note: You'll need to add these drawable resources to your res/drawable folder
     */
    private void createSamplePhotoList() {
        samplePhotos = new ArrayList<>(Arrays.asList(
                // Sample drawable resources - replace with actual family photo drawables
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_foreground
                // Add more sample photos here once you add them to res/drawable
                // R.drawable.family_photo_1,
                // R.drawable.family_photo_2,
                // R.drawable.family_photo_3,
                // R.drawable.family_photo_4,
                // R.drawable.family_photo_5,
                // R.drawable.family_photo_6
        ));
    }

    /**
     * Load sample photos and update the adapter
     */
    private void loadSamplePhotos() {
        // In a real app, you might load photos from:
        // - Internal storage
        // - External storage (with permissions)
        // - Database
        // - Cloud storage

        // For now, we'll use the sample drawable resources
        if (photosAdapter != null) {
            photosAdapter.updatePhotos(samplePhotos);
            updateEmptyState();
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
                // Add haptic feedback
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                // Finish activity to go back
                finish();
            }
        });

        // Add photo FAB
        addPhotoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add haptic feedback
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                // Handle add photo action
                showAddPhotoOptions();
            }
        });
    }

    /**
     * Show options for adding a new photo
     */
    private void showAddPhotoOptions() {
        // For now, just show a toast
        showToast("Add Photo feature coming soon!");

        // TODO: Implement photo selection/capture functionality
        // This could include:
        // - Taking a photo with camera
        // - Selecting from gallery
        // - Selecting from predefined family photos

        // Example implementation:
        // showPhotoSourceDialog();
    }

    /**
     * Update the visibility of empty state based on photo count
     */
    private void updateEmptyState() {
        if (samplePhotos != null && samplePhotos.size() > 0) {
            // Show photos, hide empty state
            photosRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        } else {
            // Show empty state, hide photos
            photosRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Helper method to show toast messages
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Add a new photo to the collection (for future use)
     */
    public void addNewPhoto(int drawableResourceId) {
        if (samplePhotos != null && photosAdapter != null) {
            photosAdapter.addPhoto(drawableResourceId);
            updateEmptyState();

            // Scroll to the newly added photo
            photosRecyclerView.scrollToPosition(samplePhotos.size() - 1);

            showToast("Photo added successfully!");
        }
    }

    /**
     * Remove a photo from the collection (for future use)
     */
    public void removePhoto(int position) {
        if (samplePhotos != null && photosAdapter != null) {
            photosAdapter.removePhoto(position);
            updateEmptyState();
            showToast("Photo removed");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh photos when returning to this activity
        // In a real app, you might reload from storage here
        updateEmptyState();
    }
}