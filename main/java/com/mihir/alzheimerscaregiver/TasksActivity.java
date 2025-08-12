package com.mihir.alzheimerscaregiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksActivity extends AppCompatActivity implements TasksAdapter.OnTaskInteractionListener {

    // UI Elements
    private ImageButton backButton;
    private TextView todayDateText, currentTimeText, tasksRemainingText;
    private RecyclerView tasksRecyclerView;
    private FloatingActionButton addTaskFab;

    // Adapter and data
    private TasksAdapter tasksAdapter;
    private List<Task> taskList;

    // Handler for updating time
    private Handler timeHandler;
    private Runnable timeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Initialize UI elements
        initializeViews();

        // Set up current date and time
        setupCurrentDateTime();

        // Start time updates
        startTimeUpdates();

        // Create sample task list
        createSampleTasks();

        // Initialize RecyclerView and adapter
        setupRecyclerView();

        // Set up click listeners
        setupClickListeners();

        // Update tasks remaining counter
        updateTasksRemainingCounter();
    }

    /**
     * Initialize all UI elements
     */
    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        todayDateText = findViewById(R.id.todayDateText);
        currentTimeText = findViewById(R.id.currentTimeText);
        tasksRemainingText = findViewById(R.id.tasksRemainingText);
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        addTaskFab = findViewById(R.id.addTaskFab);
    }

    /**
     * Set up current date and time display
     */
    private void setupCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        String todayDate = "Today, " + dateFormat.format(calendar.getTime());
        todayDateText.setText(todayDate);

        updateCurrentTime();
    }

    /**
     * Start updating time every minute
     */
    private void startTimeUpdates() {
        timeHandler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateCurrentTime();
                // Update every minute (60000 milliseconds)
                timeHandler.postDelayed(this, 60000);
            }
        };

        timeHandler.post(timeRunnable);
    }

    /**
     * Update current time display
     */
    private void updateCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());
        currentTimeText.setText(currentTime);
    }

    /**
     * Create sample tasks for demonstration
     */
    private void createSampleTasks() {
        taskList = new ArrayList<>();

        // Add sample tasks - some completed, some not
        taskList.add(new Task("Take morning walk", false));
        taskList.add(new Task("Read a book for 30 minutes", true));
        taskList.add(new Task("Call family member", false));
        taskList.add(new Task("Do gentle stretching exercises", false));
        taskList.add(new Task("Write in journal", true));
        taskList.add(new Task("Take afternoon medication", false));
        taskList.add(new Task("Water the plants", false));
        taskList.add(new Task("Listen to favorite music", true));
        taskList.add(new Task("Prepare and eat healthy lunch", false));
        taskList.add(new Task("Practice memory exercises", false));
    }

    /**
     * Set up RecyclerView with adapter
     */
    private void setupRecyclerView() {
        // Create and set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        tasksRecyclerView.setLayoutManager(layoutManager);

        // Create adapter with task list and interaction listener
        tasksAdapter = new TasksAdapter(taskList, this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        // Optional: Add item decoration for better spacing
        // You can uncomment this if you want additional spacing between items
        // tasksRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    /**
     * Set up click listeners for all interactive elements
     */
    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to main activity
                finish();
            }
        });

        // Add task FAB
        addTaskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Provide haptic feedback
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);

                // Show toast message for now
                showToast("Add task feature coming soon!");

                // TODO: Implement add task functionality
                // This could open a dialog or new activity to add tasks
            }
        });
    }

    /**
     * Update the tasks remaining counter
     */
    private void updateTasksRemainingCounter() {
        if (tasksAdapter != null) {
            int pendingTasks = tasksAdapter.getPendingTasksCount();
            int totalTasks = tasksAdapter.getItemCount();

            if (pendingTasks == 0) {
                tasksRemainingText.setText("All done! 🎉");
            } else if (pendingTasks == 1) {
                tasksRemainingText.setText("1 remaining");
            } else {
                tasksRemainingText.setText(pendingTasks + " remaining");
            }
        }
    }

    /**
     * Helper method to show toast messages
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // TasksAdapter.OnTaskInteractionListener implementation

    @Override
    public void onTaskCompleted(Task task, int position) {
        // Handle task completion
        String message = task.isCompleted() ?
                "Great job! Task completed: " + task.getTaskName() :
                "Task marked as pending: " + task.getTaskName();

        showToast(message);

        // Update tasks remaining counter
        updateTasksRemainingCounter();

        // You could also:
        // - Save task status to database
        // - Send notification to caregiver
        // - Update progress tracking
        // - Show encouraging messages for completing tasks

        // Simulate caregiver notification for completed tasks
        if (task.isCompleted()) {
            simulateCaregiverNotification(task);
        }
    }

    @Override
    public void onTaskClicked(Task task, int position) {
        // Handle task item click (not checkbox click)
        // This could be used for editing tasks, showing details, etc.
        showToast("Task: " + task.getTaskName());

        // TODO: Implement task detail view or edit functionality
    }

    /**
     * Simulate sending notification to caregiver when task is completed
     */
    private void simulateCaregiverNotification(Task task) {
        // In a real app, this would send a notification to the caregiver's device
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showToast("✓ Caregiver notified: " + task.getTaskName() + " completed");
            }
        }, 1500);
    }

    /**
     * Method to add a new task (for future use)
     */
    public void addNewTask(String taskName) {
        if (taskName != null && !taskName.trim().isEmpty()) {
            Task newTask = new Task(taskName.trim(), false);
            tasksAdapter.addTask(newTask);
            updateTasksRemainingCounter();

            // Scroll to the new task
            tasksRecyclerView.scrollToPosition(tasksAdapter.getItemCount() - 1);

            showToast("New task added: " + taskName);
        }
    }

    /**
     * Get greeting based on time of day
     */
    private String getTimeBasedGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour < 12) {
            return "Good Morning";
        } else if (hour < 17) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop time updates when activity is destroyed
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update time and task counter when returning to this activity
        updateCurrentTime();
        updateTasksRemainingCounter();
    }
}
