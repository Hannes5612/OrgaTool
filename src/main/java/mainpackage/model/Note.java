package mainpackage.model;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Note {
    private String title;
    private String content;
    private String date;
    private State state = State.ACTIVE;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public String toString() {
        return "Title: " + title + "\nContent: " + content + "\nDate: " + date;
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.date = java.time.LocalDateTime.now().format(formatter);
    }

    public String getDate() {
        return date;
    }

    public State getState() {
        return state;
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
