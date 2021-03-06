package mainpackage.model;

import mainpackage.exceptions.UnsupportedStateType;

import java.sql.Date;

/**
 * Interface for entries with basic methods.
 */
public interface IEntry {

    /**
     * Method changes the state of the entry to FINISHED.
     */
    void finish();

    /**
     * Method changes the state of the entry to ARCHIVED.
     */
    void archive();

    /**
     * Method changes the state of the entry to ACTIVE.
     */
    void reactivate();

    int getId();

    String getTitle();

    String getContent();

    Date getCreationDate();

    int getState() throws UnsupportedStateType;
}
