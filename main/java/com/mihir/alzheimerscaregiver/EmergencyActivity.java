package com.mihir.alzheimerscaregiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EmergencyActivity extends AppCompatActivity {

    // UI Elements
    private ImageButton backButton;
    private RecyclerView emergencyContactsRecyclerView;

    // Adapter and data
    private ContactsAdapter contactsAdapter;
    private List<Contact> emergencyContacts;

    // Permission request code
    private static final int CALL_PHONE_PERMISSION_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        // Initialize UI elements
        initializeViews();

        // Set up click listeners
        setupClickListeners();

        // Create sample contacts
        createSampleContacts();

        // Initialize RecyclerView and adapter
        setupRecyclerView();

        // Check and request phone permission
        checkPhonePermission();
    }

    /**
     * Initialize all UI elements
     */
    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        emergencyContactsRecyclerView = findViewById(R.id.emergencyContactsRecyclerView);
    }

    /**
     * Set up click listeners for interactive elements
     */
    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Provide haptic feedback
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                // Go back to main activity
                finish();
            }
        });
    }

    /**
     * Create sample emergency contacts
     */
    private void createSampleContacts() {
        emergencyContacts = new ArrayList<>();

        // Add sample contacts
        emergencyContacts.add(new Contact("Dr. Sarah Smith", "99009 99009"));
        emergencyContacts.add(new Contact("Jane Doe - Daughter", "99999 99999"));
        emergencyContacts.add(new Contact("Michael Johnson - Son", "88888 88888"));
        emergencyContacts.add(new Contact("Sunrise Care Center", "77777 77777"));
        emergencyContacts.add(new Contact("Home Health Nurse", "66666 66666"));
        emergencyContacts.add(new Contact("Pharmacy", "98765 43210"));
        emergencyContacts.add(new Contact("Mary Wilson - Neighbor", "67890 12345"));
    }

    /**
     * Set up RecyclerView with LinearLayoutManager and ContactsAdapter
     */
    private void setupRecyclerView() {
        // Create and set LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        emergencyContactsRecyclerView.setLayoutManager(layoutManager);

        // Create and set adapter
        contactsAdapter = new ContactsAdapter(this, emergencyContacts);
        emergencyContactsRecyclerView.setAdapter(contactsAdapter);

        // Optional: Add item decoration for better spacing
        // emergencyContactsRecyclerView.addItemDecoration(
        //     new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        // );
    }

    /**
     * Check if CALL_PHONE permission is granted, request if not
     */
    private void checkPhonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    CALL_PHONE_PERMISSION_REQUEST);
        }
    }

    /**
     * Handle permission request result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CALL_PHONE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                showToast("Phone permission granted. You can now make calls directly.");
            } else {
                // Permission denied
                showToast("Phone permission denied. Calls will open the dialer instead.");
            }
        }
    }

    /**
     * Add a new contact to the emergency contacts list
     * @param contact The contact to add
     */
    public void addEmergencyContact(Contact contact) {
        if (emergencyContacts != null && contactsAdapter != null) {
            emergencyContacts.add(contact);
            contactsAdapter.notifyItemInserted(emergencyContacts.size() - 1);

            // Scroll to the newly added contact
            emergencyContactsRecyclerView.scrollToPosition(emergencyContacts.size() - 1);
        }
    }

    /**
     * Remove a contact from the emergency contacts list
     * @param position The position of the contact to remove
     */
    public void removeEmergencyContact(int position) {
        if (emergencyContacts != null && contactsAdapter != null
                && position >= 0 && position < emergencyContacts.size()) {

            String contactName = emergencyContacts.get(position).getContactName();
            emergencyContacts.remove(position);
            contactsAdapter.notifyItemRemoved(position);

            showToast("Removed " + contactName);
        }
    }

    /**
     * Refresh the contacts list (useful when returning from other activities)
     */
    public void refreshContactsList() {
        if (contactsAdapter != null) {
            contactsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Helper method to show toast messages
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        refreshContactsList();
    }
}