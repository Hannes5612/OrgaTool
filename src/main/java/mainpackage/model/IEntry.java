package mainpackage.model;

import java.sql.Date;

public interface IEntry {

    /**
     * Method changes the state of the entry to FINISHED.
     */
    void finish();

    /**
     * Method changes the state of the entry to ARCHIVED.
     */
    void archive();

    int getId();

    String getTitle();
    String getContent();

    Date getCreationDate();

    int getState();
}
