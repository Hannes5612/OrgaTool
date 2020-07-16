package mainpackage.model;

import java.sql.Date;

/**
 * The Task object contains variables with user input
 * which will be displayed at the ToDoList and can also be accessed through the calendar.
 * The user can create as many tasks as he wants.
 * The task data will be stored in the database.
 */
public class Task extends Entry {

    /**
     * ToDo:
     * priority -
     * color -
     * dueDate -
     */
    // ToDo: priority to char
    private String priority;
    private String color;
    private Date dueDate;

    /**
     * ToDo: Description
     */
    public Task(int taskId, String title, String content, String priority, String color, Date dueDate, Date creationDate) {
        this.id = taskId;
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
        if (state == 0) {
            this.state = State.ACTIVE;
        }
        if (state == 1) {
            this.state = State.FINISHED;
        }
        if (state == 2) {
            this.state = State.ARCHIVED;
        }
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

    public String getDueMonth() {
        String month = "";
        switch (dueDate.toLocalDate().getMonthValue()) {
            case 1:
                month = "January";
                break;
            case 2:
                month = "February";
                break;
            case 3:
                month = "March";
                break;
            case 4:
                month = "April";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "June";
                break;
            case 7:
                month = "July";
                break;
            case 8:
                month = "August";
                break;
            case 9:
                month = "September";
                break;
            case 10:
                month = "October";
                break;
            case 11:
                month = "November";
                break;
            case 12:
                month = "December";
                break;
        }
        return month;
    }

    @Override
    public String toString() {
        return "{" +
                "priority='" + priority + '\'' +
                ", color='" + color + '\'' +
                ", dueDate=" + dueDate +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", creationDate=" + creationDate +
                ", state=" + state +
                '}';
    }
}
