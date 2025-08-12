package com.mihir.alzheimerscaregiver;



/**
 * Model class representing a task in the daily tasks list
 */
public class Task {

    // Private fields
    private String taskName;
    private boolean isCompleted;

    /**
     * Default constructor
     */
    public Task() {
        this.taskName = "";
        this.isCompleted = false;
    }

    /**
     * Constructor with task name
     * @param taskName The name/description of the task
     */
    public Task(String taskName) {
        this.taskName = taskName;
        this.isCompleted = false;
    }

    /**
     * Full constructor
     * @param taskName The name/description of the task
     * @param isCompleted Whether the task is completed or not
     */
    public Task(String taskName, boolean isCompleted) {
        this.taskName = taskName;
        this.isCompleted = isCompleted;
    }

    // Getter methods

    /**
     * Get the task name
     * @return The task name/description
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Check if the task is completed
     * @return true if completed, false otherwise
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    // Setter methods

    /**
     * Set the task name
     * @param taskName The new task name/description
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Set the completion status of the task
     * @param completed Whether the task is completed or not
     */
    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    /**
     * Toggle the completion status of the task
     */
    public void toggleCompleted() {
        this.isCompleted = !this.isCompleted;
    }

    /**
     * Get a string representation of the task
     * @return String representation showing task name and completion status
     */
    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", isCompleted=" + isCompleted +
                '}';
    }

    /**
     * Check equality with another Task object
     * @param obj The object to compare with
     * @return true if tasks are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Task task = (Task) obj;
        return isCompleted == task.isCompleted &&
                (taskName != null ? taskName.equals(task.taskName) : task.taskName == null);
    }

    /**
     * Generate hash code for the Task object
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = taskName != null ? taskName.hashCode() : 0;
        result = 31 * result + (isCompleted ? 1 : 0);
        return result;
    }
}
