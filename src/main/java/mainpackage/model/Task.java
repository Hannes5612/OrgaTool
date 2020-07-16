package mainpackage.model;

import java.sql.Date;

/**
 * The class customizes and extends the structure of the class Entry.
 * The Task object contains variables with user input
 * which will be displayed at the TaskList and can also be accessed through the calendar.
 * The user can create as many tasks as he wants.
 * The task data will be stored in the database.
 */
public class Task extends Entry {

    /**
     * priority - priority of a task
     * color - color of a task
     * dueDate - date when a task should be finished
     */
    // ToDo: priority to char
    private String priority;
    private String color;
    private Date dueDate;

    /**
     * Creating task.
     *
     * @param id - unique id of task
     * @param title - title of task
     * @param content - description of task
     * @param priority - priority of task
     * @param color - color of task
     * @param dueDate - date when a task should be finished
     * @param creationDate - timestamp when an entry object is created
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

    /**
     * Method converts the dueDate of the object into a month.
     *
     * @return String - month
     */
    public String getDueMonth(){
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
