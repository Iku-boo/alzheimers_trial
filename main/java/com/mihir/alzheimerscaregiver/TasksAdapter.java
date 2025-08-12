package com.mihir.alzheimerscaregiver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * RecyclerView Adapter for displaying tasks in the TasksActivity
 */
public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskInteractionListener listener;

    /**
     * Interface for handling task interactions
     */
    public interface OnTaskInteractionListener {
        void onTaskCompleted(Task task, int position);
        void onTaskClicked(Task task, int position);
    }

    /**
     * Constructor
     * @param taskList List of tasks to display
     */
    public TasksAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    /**
     * Constructor with interaction listener
     * @param taskList List of tasks to display
     * @param listener Listener for task interactions
     */
    public TasksAdapter(List<Task> taskList, OnTaskInteractionListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    /**
     * Set the interaction listener
     * @param listener The listener to set
     */
    public void setOnTaskInteractionListener(OnTaskInteractionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = taskList.get(position);
        holder.bind(currentTask, position);
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    /**
     * Update the task list and refresh the adapter
     * @param newTaskList The new list of tasks
     */
    public void updateTasks(List<Task> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged();
    }

    /**
     * Add a new task to the list
     * @param task The task to add
     */
    public void addTask(Task task) {
        if (taskList != null) {
            taskList.add(task);
            notifyItemInserted(taskList.size() - 1);
        }
    }

    /**
     * Remove a task from the list
     * @param position The position of the task to remove
     */
    public void removeTask(int position) {
        if (taskList != null && position >= 0 && position < taskList.size()) {
            taskList.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * ViewHolder class for task items
     */
    public class TaskViewHolder extends RecyclerView.ViewHolder {

        private CheckBox taskCheckBox;
        private TextView taskStatusText;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            taskStatusText = itemView.findViewById(R.id.taskStatusText);
        }

        /**
         * Bind task data to the views
         * @param task The task to bind
         * @param position The position of the task in the list
         */
        public void bind(Task task, int position) {
            // Set task name in checkbox
            taskCheckBox.setText(task.getTaskName());
            taskCheckBox.setChecked(task.isCompleted());

            // Set status text
            updateStatusText(task.isCompleted());

            // Set checkbox click listener
            taskCheckBox.setOnCheckedChangeListener(null); // Clear previous listener to avoid conflicts
            taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Provide haptic feedback
                buttonView.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                // Update task completion status
                task.setCompleted(isChecked);

                // Update status text
                updateStatusText(isChecked);

                // Notify listener if available
                if (listener != null) {
                    listener.onTaskCompleted(task, position);
                }
            });

            // Set item click listener (for the entire row)
            itemView.setOnClickListener(v -> {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                if (listener != null) {
                    listener.onTaskClicked(task, position);
                }
            });

            // Apply visual styling based on completion status
            applyCompletionStyling(task.isCompleted());
        }

        /**
         * Update the status text based on completion
         * @param isCompleted Whether the task is completed
         */
        private void updateStatusText(boolean isCompleted) {
            if (isCompleted) {
                taskStatusText.setText("✓ Completed");
                taskStatusText.setTextColor(itemView.getContext().getResources().getColor(R.color.success));
            } else {
                taskStatusText.setText("Pending");
                taskStatusText.setTextColor(itemView.getContext().getResources().getColor(R.color.text_secondary));
            }
        }

        /**
         * Apply styling based on task completion status
         * @param isCompleted Whether the task is completed
         */
        private void applyCompletionStyling(boolean isCompleted) {
            if (isCompleted) {
                // Slightly fade completed tasks
                itemView.setAlpha(0.7f);
                taskCheckBox.setAlpha(0.8f);
            } else {
                // Normal opacity for pending tasks
                itemView.setAlpha(1.0f);
                taskCheckBox.setAlpha(1.0f);
            }
        }
    }

    /**
     * Get the number of completed tasks
     * @return Count of completed tasks
     */
    public int getCompletedTasksCount() {
        if (taskList == null) return 0;

        int count = 0;
        for (Task task : taskList) {
            if (task.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get the number of pending tasks
     * @return Count of pending tasks
     */
    public int getPendingTasksCount() {
        return getItemCount() - getCompletedTasksCount();
    }
}
