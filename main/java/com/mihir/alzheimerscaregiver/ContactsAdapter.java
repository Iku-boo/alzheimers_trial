package com.mihir.alzheimerscaregiver;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private List<Contact> contacts;
    private Context context;

    /**
     * Constructor for ContactsAdapter
     * @param context The context (activity) using this adapter
     * @param contacts List of contacts to display
     */
    public ContactsAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        // Set contact information
        holder.contactNameText.setText(contact.getContactName());
        holder.phoneNumberText.setText(contact.getPhoneNumber());

        // Set up call button click listener
        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Provide haptic feedback
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                // Make phone call
                makePhoneCall(contact.getPhoneNumber(), contact.getContactName());
            }
        });

        // Optional: Set up click listener for the entire item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // You could show contact details or make a call here too
                makePhoneCall(contact.getPhoneNumber(), contact.getContactName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts != null ? contacts.size() : 0;
    }

    /**
     * Make a phone call to the specified number
     * @param phoneNumber The phone number to call
     * @param contactName The name of the contact being called
     */
    private void makePhoneCall(String phoneNumber, String contactName) {
        try {
            // Create intent to dial the number
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));

            // Check if there's an app that can handle this intent
            if (callIntent.resolveActivity(context.getPackageManager()) != null) {
                // Show confirmation toast
                Toast.makeText(context, "Calling " + contactName + "...", Toast.LENGTH_SHORT).show();

                // Start the call
                context.startActivity(callIntent);
            } else {
                // Fallback to dialer if direct calling isn't available
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));
                context.startActivity(dialIntent);

                Toast.makeText(context, "Opening dialer for " + contactName, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Handle any errors (e.g., no phone app available)
            Toast.makeText(context, "Unable to make call", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * Update the contacts list and refresh the RecyclerView
     * @param newContacts The new list of contacts
     */
    public void updateContacts(List<Contact> newContacts) {
        this.contacts = newContacts;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for contact items
     */
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactNameText;
        TextView phoneNumberText;
        ImageButton callButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            contactNameText = itemView.findViewById(R.id.contactNameText);
            phoneNumberText = itemView.findViewById(R.id.phoneNumberText);
            callButton = itemView.findViewById(R.id.callButton);
        }
    }
}
