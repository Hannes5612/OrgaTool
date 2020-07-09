package mainpackage.database;

import mainpackage.ListManager;
import mainpackage.model.Note;
import mainpackage.model.Task;
import mainpackage.model.User;

import java.sql.*;

/**
 * Database Handler class to process database requests
 */
public class DatabaseHandler extends Config {

    Connection dbConnection;

    /**
     * Create the Connection object for a sql connection
     *
     * @return the created connection object
     */
    public Connection getDbConnection() {

        try {
            Class.forName("com.mysql.jdbc.Driver");

            String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

            dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dbConnection;

    }

    /**
     * Create a new db entry for a user who wants to register
     *
     * @param user to register
     */
    public void signupUser(User user) {

        String insert = "INSERT INTO " + USER_TABLE + "("
                + USER_USERNAME + "," + USER_PASSWORD + ") VALUES(?,?)";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPassword());

            preparedStatement.executeUpdate();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }


    public ResultSet getUser(User user) {

        ResultSet resultSet = null;

        String query = "SELECT * FROM " + USER_TABLE + " WHERE " + USER_USERNAME + "=?" + " AND "
                + USER_PASSWORD + "=?";

        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPassword());


            resultSet = preparedStatement.executeQuery();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return resultSet;

    }

    public void createTask(Task task) {

        String insert = "INSERT INTO " + TASK_TABLE + "("
                + TASK_USER + "," + TASK_TYPE + "," + TASK_TITLE + "," + TASK_CONTENT + "," + TASK_PRIO + "," +
                TASK_COLOR + "," + TASK_DUEDATE + "," + TASK_CREATIONDATE + "," + TASK_STATE + ") VALUES(?,?,?,?,?,?,?,?,?)";

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getDbConnection().prepareStatement(insert);

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
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public ResultSet getTasks(User user) {

        ResultSet tasksResulSet = null;
        String query = "SELECT * FROM " + TASK_TABLE + " WHERE " + TASK_USER + "=?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getDbConnection().prepareStatement(query);

            preparedStatement.setInt(1, user.getUserid());

            tasksResulSet = preparedStatement.executeQuery();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return tasksResulSet;

    }

    public void deleteTask(Task task) {

        String insert = "DELETE FROM " + TASK_TABLE + " WHERE " + TASK_ID + "=?";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setInt(1, task.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    public void createNote(Note note) {

        String insert = "INSERT INTO " + NOTE_TABLE + "("
                + NOTE_USER + "," + NOTE_TITLE + "," + NOTE_CONTENT + "," + NOTE_DATE + "," +
                NOTE_STATE + ") VALUES(?,?,?,?,?)";

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getDbConnection().prepareStatement(insert);

            preparedStatement.setInt(1, ListManager.getUserId());
            preparedStatement.setString(2, note.getTitle());
            preparedStatement.setString(3, note.getContent());
            preparedStatement.setDate(4, note.getCreationDate());
            preparedStatement.setInt(5, note.getState());
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }


    }

    public void deleteNote(Note note) {

        String insert = "DELETE FROM " + NOTE_TABLE + " WHERE " + NOTE_ID + "=?";

        try {
            try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
                preparedStatement.setInt(1, note.getId());

                System.out.println("Gelöschte Note: " + note);
                preparedStatement.executeUpdate();

            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    public void editNote(int noteId, Note note, int state) {

        String insert = "UPDATE " + NOTE_TABLE + " SET " + NOTE_TITLE + "=?, " + NOTE_CONTENT + "=?, " + NOTE_STATE + "=" + state + " WHERE " + NOTE_ID + "=" + noteId;

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getDbConnection().prepareStatement(insert);
            preparedStatement.setString(1, note.getTitle());
            preparedStatement.setString(2, note.getContent());

            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    public ResultSet getNotes() {

        String query = "SELECT * FROM " + NOTE_TABLE + " WHERE " + NOTE_USER + "=?";
        PreparedStatement preparedStatement = null;
        ResultSet notesResultSet = null;
        try {
            preparedStatement = getDbConnection().prepareStatement(query);

            preparedStatement.setInt(1, ListManager.getUserId());

            notesResultSet = preparedStatement.executeQuery();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return notesResultSet;

    }

    public void editTask(int taskId, Task task) {

        String insert = "UPDATE " + TASK_TABLE +
                " SET " + TASK_TITLE + "=?, " + TASK_CONTENT + "=?, " + TASK_PRIO + "=?, " + TASK_COLOR + "=?, " +
                TASK_DUEDATE + "=?, " + TASK_CREATIONDATE + "=?, " + TASK_STATE + "=? " +
                "WHERE " + TASK_ID + "=" + taskId;

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getDbConnection().prepareStatement(insert);

            preparedStatement.setString(1, task.getTitle());
            preparedStatement.setString(2, task.getContent());
            preparedStatement.setString(3, task.getPriority());
            preparedStatement.setString(4, task.getColor());
            preparedStatement.setDate(5, task.getDueDate());
            preparedStatement.setDate(6, task.getCreationDate());
            preparedStatement.setInt(7, task.getState());

            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public ResultSet getTasks(){

        ResultSet tasksResultSet = null;
        String query = "SELECT * FROM " + TASK_TABLE + " WHERE " + TASK_USER + "=?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getDbConnection().prepareStatement(query);

        preparedStatement.setInt(1, ListManager.getUserId());

        tasksResultSet = preparedStatement.executeQuery();
    } catch (SQLException sqlException) {
        sqlException.printStackTrace();
    }
        return tasksResultSet;

    }


}
