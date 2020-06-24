package mainpackage.model;

import java.sql.Date;

/**
 * The Task object contains variables with user input
 * which will be displayed at the ToDoList and can also be accessed through the calendar.
 * The user can create as many tasks as he wants.
 * The task data will be stored in the database.
 */
public class Task extends Entry {

    /** ToDo:
     * priority -
     * color -
     * dueDate -
     */
    private String priority;
    private String color;
    private Date dueDate;

    /**
     * ToDo: Description
     */
    public Task(String title, String content, String priority, String color, Date dueDate, Date creationDate) {
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.color = color;
        this.creationDate = creationDate;
        this.dueDate = dueDate;
    }

    /**
     * ToDo: Description
     */
    public Task(int id, String title, String content, String priority, String color, java.sql.Date dueDate, java.sql.Date creationDate, int state) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.color = color;
        this.creationDate = creationDate;
        this.dueDate = dueDate;
        if (state == 0) { this.state = State.ACTIVE;   }
        if (state == 1) { this.state = State.FINISHED; }
        if (state == 2) { this.state = State.ARCHIVED; }
    }

    public String getPriority() {
        return priority;
    }

    public String getColor() {
        return color;
    }

    public Date getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return "Task {" +
                " title='"         + title        + '\'' +
                ", content='"      + content      + '\'' +
                ", priority='"     + priority     + '\'' +
                ", color='"        + color        + '\'' +
                ", dueDate='"      + dueDate      + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", state='"        + state        + '\'' +
                " }";
    }
}
