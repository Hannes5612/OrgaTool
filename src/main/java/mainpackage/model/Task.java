package mainpackage.model;

import java.util.Date;

/**
 * The user can create as many tasks as he wants which will be displayed at the ToDoList.
 */
public class Task {

    private String name;
    private String content;
    private int priority;
    private String color;
    private Date dueDate;
    private Date reminder;
    private State state = State.ACTIVE;

    public Task(String name, String content, int priority, String color, Date dueDate, Date reminder) {
        this.name = name;
        this.content = content;
        this.priority = priority;
        this.color = color;
        this.dueDate = dueDate;
        this.reminder = reminder;
    }

    // Notiz: Überprüfen, ob Setter verwendet werden sollen oder ob in ToDoList ein neues Objekt erstellt werden soll, wenn es eine Änderung gibt.

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setReminder(Date reminder) {
        this.reminder = reminder;
    }

    public void setState(State state) {
        this.state = state;
    }

}
