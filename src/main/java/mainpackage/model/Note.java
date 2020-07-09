package mainpackage.model;

import mainpackage.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;

/**
 * The Note object contains variables with user input
 * which will be displayed in the NoteList.
 * The user can create as many notes as he wants.
 * The note data will be stored in the database.
 */
public class Note extends Entry {

    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    /**
     * Creating note with only title and content
     * The rest is set automatically
     * @param title title of note
     * @param content description of note
     */
    public Note(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        logger.info("Note created: " + this);
    }

    /**
     * Creating note
     * @param id note ID, unique
     * @param title title of note
     * @param content description of note
     * @param creationDate date when note was created
     * @param state state of note (ACTIVE/ARCHIVED/FINISHED)
     */
    public Note(int id, String title, String content, Date creationDate, int state) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
        if (state == 0) { this.state = State.ACTIVE;   }
        if (state == 1) { this.state = State.FINISHED; }
        if (state == 2) { this.state = State.ARCHIVED; }
        logger.info("Note loaded: " + this);
    }

    @Override
    public String toString() {
        return "{" + "id=" + id +
                ", title='"         + title        + '\'' +
                ", content='"      + content      + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", state='"        + state        + '\'' +
                " }";
    }
}
