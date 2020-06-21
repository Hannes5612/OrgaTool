package mainpackage.model;

import com.jfoenix.controls.base.IFXLabelFloatControl;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Note {
    private int noteid;
    private String title;
    private String content;
    private Date date = new java.sql.Date(System.currentTimeMillis());
    private State state = State.ACTIVE;


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public String toString() {
        return "Title: " + title + "\nContent: " + content + "\nDate: " + date + "\nState: " + state;
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Note(int noteid, String title, String content, Date date, int state) {
        this.noteid = noteid;
        this.title = title;
        this.content = content;
        this.date = date;
        if (state == 0) {this.state = State.ACTIVE;}
        if (state == 1) {this.state = State.FINISHED;}
        if (state == 2) {this.state = State.ARCHIVED;}
    }

    public int getNoteid() {
        return noteid;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
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

    public void createNote() {

    }

    public void deleteNote() {

    }

    public void editNote(Note note) {

    }

    public void archiveNote(Note note) {
        note.setState(State.ARCHIVED);
    }

}
