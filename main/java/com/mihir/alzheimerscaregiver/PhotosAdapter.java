package com.mihir.alzheimerscaregiver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {

    private List<Integer> photoResources;
    private OnPhotoClickListener clickListener;

    /**
     * Interface for handling photo click events
     */
    public interface OnPhotoClickListener {
        void onPhotoClick(int position, int resourceId);
    }

    /**
     * Constructor
     * @param photoResources List of drawable resource IDs
     */
    public PhotosAdapter(List<Integer> photoResources) {
        this.photoResources = photoResources;
    }

    /**
     * Set click listener for photo items
     * @param listener Click listener
     */
    public void setOnPhotoClickListener(OnPhotoClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        int resourceId = photoResources.get(position);
        holder.photoImageView.setImageResource(resourceId);

        // Set click listener if provided
        if (clickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Add haptic feedback
                    v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        clickListener.onPhotoClick(adapterPosition, resourceId);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return photoResources != null ? photoResources.size() : 0;
    }

    /**
     * Update the photo list and refresh the adapter
     * @param newPhotoResources New list of photo resource IDs
     */
    public void updatePhotos(List<Integer> newPhotoResources) {
        this.photoResources = newPhotoResources;
        notifyDataSetChanged();
    }

    /**
     * Add a single photo to the list
     * @param resourceId Drawable resource ID to add
     */
    public void addPhoto(int resourceId) {
        if (photoResources != null) {
            photoResources.add(resourceId);
            notifyItemInserted(photoResources.size() - 1);
        }
    }

    /**
     * Remove a photo from the list
     * @param position Position to remove
     */
    public void removePhoto(int position) {
        if (photoResources != null && position >= 0 && position < photoResources.size()) {
            photoResources.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * ViewHolder class for photo items
     */
    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }
    }
}
