package mainpackage.database;

import mainpackage.ListManager;
import mainpackage.Main;
import mainpackage.model.Note;
import mainpackage.model.Task;
import mainpackage.model.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * Database Handler class to process database requests.
 */
public class DatabaseHandler extends Config {

    private final Logger logger = LogManager.getLogger(Main.class.getName());

    /**
     * Create the Connection object for a sql connection
     *
     * @return the created connection object
     * @throws ClassNotFoundException, SQLException
     */
    public Connection getDbConnection() throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.jdbc.Driver");
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

        Connection dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);


        logger.info("Database Connection created " + dbConnection);
        return dbConnection;

    }

    /**
     * Create a new db entry for a user who wants to register
     *
     * @param user to register
     * @throws SQLException - SQLException
     * @throws ClassNotFoundException - class not found
     */
    public void signupUser(User user) throws SQLException, ClassNotFoundException {

        String insert = "INSERT INTO " + USER_TABLE + "("
                + USER_USERNAME + "," + USER_PASSWORD + ") VALUES(?,?)";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setString(1, user.getUserName());
        preparedStatement.setString(2, user.getPassword());

        preparedStatement.executeUpdate();
        logger.info("User registered: " + user);


    }

    /**
     * Ceck wheter a user is in the database
     *
     * @param user to check
     */
    public ResultSet getUser(User user) throws SQLException, ClassNotFoundException {

        String query = "SELECT * FROM " + USER_TABLE + " WHERE " + USER_USERNAME + "=?" + " AND "
                + USER_PASSWORD + "=?";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
        preparedStatement.setString(1, user.getUserName());
        preparedStatement.setString(2, user.getPassword());


        ResultSet resultSet = preparedStatement.executeQuery();
        logger.info("User table with given credentials fetched: " + user);

        return resultSet;

    }

    /**
     * Create a database Entry for the current user with a new task
     *
     * @param task to check
     */
    public void createTask(Task task) throws ClassNotFoundException, SQLException {

        String insert = "INSERT INTO " + TASK_TABLE + "("
                + TASK_USER + "," + TASK_TYPE + "," + TASK_TITLE + "," + TASK_CONTENT + "," + TASK_PRIO + "," +
                TASK_COLOR + "," + TASK_DUEDATE + "," + TASK_CREATIONDATE + "," + TASK_STATE + ") VALUES(?,?,?,?,?,?,?,?,?)";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setInt(1, ListManager.getUserId());
        preparedStatement.setString(2, "Task");
        preparedStatement.setString(3, task.getTitle());
        preparedStatement.setString(4, task.getContent());
        preparedStatement.setString(5, task.getPriority());
        preparedStatement.setString(6, task.getColor());
        preparedStatement.setDate(7, task.getDueDate());
        preparedStatement.setDate(8, task.getCreationDate());
        preparedStatement.setInt(9, task.getState());

        preparedStatement.executeUpdate();
        logger.info("Task sent to database: " + task);
    }

    public ResultSet getTasks(User user) throws SQLException, ClassNotFoundException {

        String query = "SELECT * FROM " + TASK_TABLE + " WHERE " + TASK_USER + "=?";
        PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
        preparedStatement.setInt(1, user.getUserid());

        ResultSet tasksResulSet = preparedStatement.executeQuery();
        logger.info("Tasks fetched from server: " + tasksResulSet.getFetchSize() + " Rows");
        return tasksResulSet;

    }

    /**
     * Delete a task from the database
     *
     * @param task to delete
     */
    public void deleteTask(Task task) throws SQLException, ClassNotFoundException {

        String insert = "DELETE FROM " + TASK_TABLE + " WHERE " + TASK_ID + "=?";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setInt(1, task.getId());

            preparedStatement.executeUpdate();

            logger.info("Task deleted from server: " + task);
        }

    }

    /**
     * Send a note to the database
     *
     * @param note to send
     */
    public void createNote(Note note) throws ClassNotFoundException, SQLException {

        String insert = "INSERT INTO " + NOTE_TABLE + "("
                + NOTE_USER + "," + NOTE_TITLE + "," + NOTE_CONTENT + "," + NOTE_DATE + "," +
                NOTE_STATE + ") VALUES(?,?,?,?,?)";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setInt(1, ListManager.getUserId());
        preparedStatement.setString(2, note.getTitle());
        preparedStatement.setString(3, note.getContent());
        preparedStatement.setDate(4, note.getCreationDate());
        preparedStatement.setInt(5, note.getState());

        preparedStatement.executeUpdate();

        logger.info("Note was sent to the database: " + note);

    }

    /**
     * Delete a note from the database
     *
     * @param note to delete
     */
    public void deleteNote(Note note) throws SQLException, ClassNotFoundException {

        String insert = "DELETE FROM " + NOTE_TABLE + " WHERE " + NOTE_ID + "=?";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setInt(1, note.getId());

            preparedStatement.executeUpdate();

            logger.info("Note deleted from the database: " + note);

        }

    }

    /**
     * Edit a note in the database
     *
     * @param note to edit
     */
    public void editNote(int noteId, Note note, int state) throws ClassNotFoundException, SQLException {

        String insert = "UPDATE " + NOTE_TABLE + " SET " + NOTE_TITLE + "=?, " + NOTE_CONTENT + "=?, " + NOTE_STATE + "=" + state + " WHERE " + NOTE_ID + "=" + noteId;

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setString(1, note.getTitle());
        preparedStatement.setString(2, note.getContent());

        preparedStatement.executeUpdate();

        logger.info("Note edited in the database: " + note);

    }

    /**
     * Get notes from the database
     */
    public ResultSet getNotes() throws SQLException, ClassNotFoundException {

        String query = "SELECT * FROM " + NOTE_TABLE + " WHERE " + NOTE_USER + "=?";
        PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
        preparedStatement.setInt(1, ListManager.getUserId());

        ResultSet notesResultSet = preparedStatement.executeQuery();

        logger.info("Notes fetched from server: " + notesResultSet.getFetchSize() + " Rows");
        return notesResultSet;

    }

    /**
     * Edit a task in the database
     *
     * @param task to edit
     */
    public void editTask(int taskId, Task task) throws ClassNotFoundException, SQLException {

        String insert = "UPDATE " + TASK_TABLE +
                " SET " + TASK_TITLE + "=?, " + TASK_CONTENT + "=?, " + TASK_PRIO + "=?, " + TASK_COLOR + "=?, " +
                TASK_DUEDATE + "=?, " + TASK_CREATIONDATE + "=?, " + TASK_STATE + "=? " +
                "WHERE " + TASK_ID + "=" + taskId;

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setString(1, task.getTitle());
        preparedStatement.setString(2, task.getContent());
        preparedStatement.setString(3, task.getPriority());
        preparedStatement.setString(4, task.getColor());
        preparedStatement.setDate(5, task.getDueDate());
        preparedStatement.setDate(6, task.getCreationDate());
        preparedStatement.setInt(7, task.getState());

        preparedStatement.executeUpdate();

        logger.info("Task edited in the database: " + task);

    }

    public ResultSet getTasks() throws SQLException, ClassNotFoundException {

        String query = "SELECT * FROM " + TASK_TABLE + " WHERE " + TASK_USER + "=?";
        PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
        preparedStatement.setInt(1, ListManager.getUserId());

        ResultSet tasksResultSet = preparedStatement.executeQuery();

        logger.info("Tasks fetched from server: " + tasksResultSet.getFetchSize() + " Rows");
        return tasksResultSet;

    }


}
