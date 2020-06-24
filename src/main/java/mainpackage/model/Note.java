package mainpackage.model;

import java.sql.Date;

/**
 * The Note object contains variables with user input
 * which will be displayed at the NoteList.
 * The user can create as many notes as he wants.
 * The note data will be stored in the database.
 */
public class Note extends Entry {

    /**
     * ToDo: Description
     */
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * ToDo: Description
     */
    public Note(int id, String title, String content, Date creationDate, int state) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
        if (state == 0) { this.state = State.ACTIVE;   }
        if (state == 1) { this.state = State.FINISHED; }
        if (state == 2) { this.state = State.ARCHIVED; }
    }

    @Override
    public String toString() {
        return "Note {" +
                " title='"         + title        + '\'' +
                ", content='"      + content      + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", state='"        + state        + '\'' +
                " }";
    }
}
