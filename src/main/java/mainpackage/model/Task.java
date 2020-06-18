package mainpackage.model;


import java.sql.Date;

/**
 * The user can create as many tasks as he wants which will be displayed at the ToDoList.
 */
public class Task{

    private String name;
    private String content;
    private String priority;
    private String color;
    private Date dueDate;
    private Date creationDate;
    private State state = State.ACTIVE;

    public Task(String name, String content, String priority, String color, Date dueDate, Date creationDate) {
        this.name = name;
        this.content = content;
        this.priority = priority;
        this.color = color;
        this.dueDate = dueDate;
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public int getState() {
        switch (state) {
            case ACTIVE:   return 0;
            case FINISHED: return 1;
            case ARCHIVED: return 2;
            // default: return eigeneException?
        }
        return 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setReminder(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setState(State state) {
        this.state = state;
    }

}
