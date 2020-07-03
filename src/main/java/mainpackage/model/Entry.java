package mainpackage.model;

import java.sql.Date;

/**
 * ToDo: Description
 */
public abstract class Entry implements IEntry {

    /** ToDo:
     * id -
     * title -
     * content -
     * creationDate -
     * state -
     */
    protected int id;
    protected String title;
    protected String content;
    protected Date creationDate = new java.sql.Date(System.currentTimeMillis());
    protected State state = State.ACTIVE;

    /**
     * Method changes the state of the entry to FINISHED.
     */
    public void finish() {
        state = State.FINISHED;
    }

    /**
     * Method changes the state of the entry to ARCHIVED.
     */
    public void archive() {
        state = State.ARCHIVED;
    }

    /**
     * Method changes the state of the entry to ACTIVE.
     */
    public void reactivate() {
        state = State.ACTIVE;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getState() {
        switch(state) {
            case ACTIVE:   return 0;
            case FINISHED: return 1;
            case ARCHIVED: return 2;
            default: throw new RuntimeException("Illegal type State!"); //TODO new Exception?
        }
    }

}
