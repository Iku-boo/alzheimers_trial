// ManageFacesActivity.java
package com.mihir.alzheimerscaregiver.facerecognition;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mihir.alzheimerscaregiver.R;

import java.util.Arrays;
import java.util.List;

public class ManageFacesActivity extends AppCompatActivity {

    private ListView listViewFaces;
    private TextView tvFaceCount;
    private Button btnBack, btnClearAll;

    private FaceRecognitionHelper faceRecognitionHelper;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_faces);

        initViews();
        setupClickListeners();

        faceRecognitionHelper = new FaceRecognitionHelper(this);
        loadFaces();
    }

    private void initViews() {
        listViewFaces = findViewById(R.id.listViewFaces);
        tvFaceCount = findViewById(R.id.tvFaceCount);
        btnBack = findViewById(R.id.btnBack);
        btnClearAll = findViewById(R.id.btnClearAll);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnClearAll.setOnClickListener(v -> showClearAllDialog());

        listViewFaces.setOnItemClickListener((parent, view, position, id) -> {
            String faceName = adapter.getItem(position);
            if (faceName != null) {
                showDeleteFaceDialog(faceName);
            }
        });

        listViewFaces.setOnItemLongClickListener((parent, view, position, id) -> {
            String faceName = adapter.getItem(position);
            if (faceName != null) {
                showFaceDetailsDialog(faceName);
            }
            return true;
        });
    }

    private void loadFaces() {
        String[] faceNames = faceRecognitionHelper.getRegisteredFaceNames();

        if (faceNames.length == 0) {
            // Show empty state
            String[] emptyState = {"No faces registered yet", "Tap 'Add Face' to get started"};
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emptyState);
            listViewFaces.setAdapter(adapter);
            listViewFaces.setEnabled(false);
            btnClearAll.setEnabled(false);
        } else {
            List<String> faceList = Arrays.asList(faceNames);
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, faceList);
            listViewFaces.setAdapter(adapter);
            listViewFaces.setEnabled(true);
            btnClearAll.setEnabled(true);
        }

        updateFaceCount();
    }

    private void updateFaceCount() {
        int count = faceRecognitionHelper.getRegisteredFaceCount();
        tvFaceCount.setText("Registered Faces: " + count);

        if (count == 0) {
            tvFaceCount.append("\nTap a face name to delete it");
        } else {
            tvFaceCount.append("\nTap a face name to delete it\nLong press for details");
        }
    }

    private void showDeleteFaceDialog(String faceName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Face");
        builder.setMessage("Are you sure you want to delete the face data for '" + faceName + "'?\n\nThis action cannot be undone.");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            boolean success = faceRecognitionHelper.deleteFace(faceName);
            if (success) {
                Toast.makeText(this, faceName + " deleted successfully", Toast.LENGTH_SHORT).show();
                loadFaces(); // Refresh list
            } else {
                Toast.makeText(this, "Failed to delete " + faceName, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Add neutral button for face details
        builder.setNeutralButton("View Details", (dialog, which) -> {
            showFaceDetailsDialog(faceName);
        });

        builder.show();
    }

    private void showFaceDetailsDialog(String faceName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Details: " + faceName);

        // You can add more details here like registration date, last recognized, etc.
        String details = "Name: " + faceName + "\n\n" +
                "Status: Active\n" +
                "Face data: Stored securely\n\n" +
                "This person can be recognized by the app's face recognition system.";

        builder.setMessage(details);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        builder.setNegativeButton("Delete", (dialog, which) -> {
            showDeleteFaceDialog(faceName);
        });

        builder.show();
    }

    private void showClearAllDialog() {
        int faceCount = faceRecognitionHelper.getRegisteredFaceCount();

        if (faceCount == 0) {
            Toast.makeText(this, "No faces to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Clear All Faces");
        builder.setMessage("Are you sure you want to delete ALL " + faceCount + " registered faces?\n\n" +
                "This will remove all face recognition data and cannot be undone.\n\n" +
                "You will need to re-register all faces after this action.");

        builder.setPositiveButton("Clear All", (dialog, which) -> {
            String[] faceNames = faceRecognitionHelper.getRegisteredFaceNames();
            int deletedCount = 0;

            for (String name : faceNames) {
                if (faceRecognitionHelper.deleteFace(name)) {
                    deletedCount++;
                }
            }

            if (deletedCount == faceNames.length) {
                Toast.makeText(this, "All " + deletedCount + " faces deleted successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Deleted " + deletedCount + " out of " + faceNames.length + " faces", Toast.LENGTH_LONG).show();
            }

            loadFaces(); // Refresh list
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Make the dialog more prominent for destructive action
        AlertDialog dialog = builder.create();
        dialog.show();

        // Make delete button red to indicate destructive action
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_red_dark));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        loadFaces();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (faceRecognitionHelper != null) {
            faceRecognitionHelper.close();
        }
    }
}
