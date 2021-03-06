package database;


import mainpackage.ListManager;
import mainpackage.controller.TaskCell;
import mainpackage.database.DatabaseHandler;
import mainpackage.exceptions.IllegalIdentification;
import mainpackage.exceptions.UnsupportedStateType;
import mainpackage.model.Note;
import mainpackage.model.Task;
import mainpackage.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Testing of the fundamental DB features: Inserting and fetching exemplary with Task and Note(Same goes for
 * User)
 */
public class DatabaseHandlerTest {

    private static final Logger logger = LogManager.getLogger(TaskCell.class);

    DatabaseHandler databaseHandler = new DatabaseHandler();

    /**
     * Basic test, whether the connection gets created
     *
     * @throws SQLException - SQLException
     * @throws ClassNotFoundException - class not found
     */
    @Test
    public void connectionTest() throws SQLException, ClassNotFoundException {
        assertNotNull(databaseHandler.getDbConnection() != null);
    }

    /**
     * Insert method passes if no Exceptions are thrown. Id is irrelevant since it is not set by the program but rather
     * auto generated by mySQL database
     *
     * @throws SQLException - SQLException
     * @throws ClassNotFoundException - class not found
     */
    @Test
    public void insertNoteTest() throws SQLException, ClassNotFoundException {
        try {
            databaseHandler.createNote(new Note(911, "2", "3"));
            databaseHandler.createNote(new Note(112, "Note", "This is a Note"));
        } catch (UnsupportedStateType unsupportedStateType) {
            unsupportedStateType.printStackTrace();
            logger.error("Unsupported state type!");
        }
    }

    /**
     * Testing the SQL Databases constraints, asserting exception
     *
     * @throws SQLException - SQLException
     * @throws ClassNotFoundException - class not found
     */
    @Test(expected = SQLException.class)
    public void insertNoteTestFalse() throws SQLException, ClassNotFoundException {
        try {
            databaseHandler.createNote(new Note(911, "I should point out that the database under test is just a testDB " +
                    "created by other code and not any real commercial DB!", "3"));
        } catch (UnsupportedStateType unsupportedStateType) {
            unsupportedStateType.printStackTrace();
            logger.error("Unsupported state type!");
        }
    }

    /**
     * Insert method passes if no Exceptions are thrown. Id is irrelevant since it is not set by the program but rather
     * auto generated by mySQL database
     *
     * @throws SQLException - SQLException
     * @throws ClassNotFoundException - class not found
     */
    @Test
    public void insertTaskTest() throws SQLException, ClassNotFoundException {
        try {
            databaseHandler.createTask(new Task(911, "Title", "content", "H", "0x00000ff", new Date(321554543), new Date(321554543), 2));
            databaseHandler.createTask(new Task(112, "Title", "content", "H", "0x00000ff", new Date(321554543), new Date(321554543), 1));
        } catch (UnsupportedStateType unsupportedStateType) {
            unsupportedStateType.printStackTrace();
            logger.error("Unsupported state type!");
        }
    }

    /**
     * Testing the SQL Databases constraints, asserting exception
     *
     * @throws SQLException - SQLException
     * @throws ClassNotFoundException - class not found
     */
    @Test(expected = SQLException.class)
    public void insertTaskTestFalse() throws SQLException, ClassNotFoundException {
        try {
            databaseHandler.createTask(new Task(112, "TitleTitleTitleTitleTitleTitleTitleTitleTitleTitleTitleTitleTitleTitle",
                    "content", "H", "0x00000ff", new Date(321554543), new Date(321554543), 1));
        } catch (UnsupportedStateType unsupportedStateType) {
            unsupportedStateType.printStackTrace();
            logger.error("Unsupported state type!");
        }
    }

    /**
     * Testing of fetching data
     *
     * @throws SQLException - SQLException
     * @throws ClassNotFoundException - class not found
     */
    @Test
    public void fetchTaskTest() throws SQLException, ClassNotFoundException, IllegalIdentification {
        User user = new User();
        user.setUserid(1);
        ListManager.setUser(user);
        //A next row exist
        assertTrue(databaseHandler.getTasks().next());
        ResultSet resultSet = databaseHandler.getTasks();
        resultSet.next();

        //Expected TaskId(Column 1) of 11
        assertEquals(11, resultSet.getInt(1));
        //Not expected task title
        assertNotEquals(null, resultSet.getInt(2));
    }

    /**
     * Testing of fetching data
     *
     * @throws SQLException - SQLException
     * @throws ClassNotFoundException - class not found
     */
    @Test
    public void fetchNotesTest() throws SQLException, ClassNotFoundException, IllegalIdentification {
        User user = new User();
        user.setUserid(99999999);
        ListManager.setUser(user);
        //A next row does not exist(No created notes)
        assertFalse(databaseHandler.getNotes().next());

        user.setUserid(22);
        ListManager.setUser(user);
        ResultSet resultSet = databaseHandler.getNotes();
        resultSet.next();

        //Expected NoteId(Column 1) of 7
        assertEquals(7, resultSet.getInt(1));
        //Not expected title
        assertNotEquals(null, resultSet.getInt(2));
    }

}
