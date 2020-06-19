package mainpackage.model;


import mainpackage.controller.Entry;

import java.sql.Date;

/**
 * The user can create as many tasks as he wants which will be displayed at the ToDoList.
 */
public class Task implements Entry {

    private int taskid;
    private String name;
    private String content;
    private String priority;
    private String color;
    private Date dueDate;
    private Date creationDate;
    private State state = State.ACTIVE;

    public Task(int taskid, String name, String content, String priority, String color, java.sql.Date dueDate, java.sql.Date creationDate, int state) {
        this.taskid= taskid;
        this.name = name;
        this.content = content;
        this.priority = priority;
        this.color = color;
        this.dueDate = dueDate;
        this.creationDate = creationDate;
        if(state==1) this.state = State.FINISHED;
        if(state==2) this.state = State.ARCHIVED;
    }
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

    public void setTaskid(int taskid) {
        this.taskid = taskid;
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
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", priority='" + priority + '\'' +
                ", color='" + color + '\'' +
                ", dueDate=" + dueDate +
                ", creationDate=" + creationDate +
                ", state=" + state +
                '}';
    }
}
