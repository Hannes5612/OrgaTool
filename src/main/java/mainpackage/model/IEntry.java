package mainpackage.model;

import java.sql.Date;

public interface IEntry {


    /**
     * Method changes the state of the entry to FINISHED.
     */
    public void finish();

    /**
     * Method changes the state of the entry to ARCHIVED.
     */
    public void archive();

    public int getId();

    public String getTitle();
    public String getContent();

    public Date getCreationDate();

    public int getState();
}
