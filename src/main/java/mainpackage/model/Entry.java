package mainpackage.model;

import java.sql.Date;

/**
 * Dummy class of the class Note and Task.
 * The class defines the structure of an entry which is used in Note and Task.
 */
public abstract class Entry implements IEntry {

    /**
     * id - unique id of an entry object
     * title - titel of an entry
     * content - description of an entry
     * creationDate - timestamp when an entry object is created
     * state - current state of an entry.
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

    /**
     * Method returns the state of the object.
     *
     * @return integer 0, 1 or 2
     */
    public int getState() {
        switch (state) {
            case ACTIVE:
                return 0;
            case FINISHED:
                return 1;
            case ARCHIVED:
                return 2;
            default:
                throw new RuntimeException("Illegal type State!"); //TODO new Exception?
        }
    }

}
