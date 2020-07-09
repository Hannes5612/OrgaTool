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

    public Connection getDbConnection() throws ClassNotFoundException {

        Class.forName("com.mysql.jdbc.Driver");
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

        try {
            dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbConnection;

    }

    public void signupUser(User user) throws SQLException {

        String insert = "INSERT INTO " + USER_TABLE + "("
                + USER_USERNAME + "," + USER_PASSWORD + ") VALUES(?,?)";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPassword());

            preparedStatement.executeUpdate();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();

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

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }

        return resultSet;

    }

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

    }

    public ResultSet getTasks(User user) throws SQLException, ClassNotFoundException {

        String query = "SELECT * FROM " + TASK_TABLE + " WHERE " + TASK_USER + "=?";
        PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
        preparedStatement.setInt(1, user.getUserid());

        ResultSet tasksResulSet = preparedStatement.executeQuery();

        return tasksResulSet;

    }

    public void deleteTask(Task task) throws SQLException, ClassNotFoundException {

        String insert = "DELETE FROM " + TASK_TABLE + " WHERE " + TASK_ID + "=?";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setInt(1, task.getId());

            preparedStatement.executeUpdate();

        }

    }

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

    }

    public void deleteNote(Note note) throws SQLException, ClassNotFoundException {

        String insert = "DELETE FROM " + NOTE_TABLE + " WHERE " + NOTE_ID + "=?";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setInt(1, note.getId());

            System.out.println("Gelöschte Note: " + note);
            preparedStatement.executeUpdate();

        }

    }

    public void editNote(int noteId, Note note, int state) throws ClassNotFoundException, SQLException {

        String insert = "UPDATE " + NOTE_TABLE + " SET " + NOTE_TITLE + "=?, " + NOTE_CONTENT + "=?, " + NOTE_STATE + "=" + state + " WHERE " + NOTE_ID + "=" + noteId;

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setString(1, note.getTitle());
        preparedStatement.setString(2, note.getContent());

        preparedStatement.executeUpdate();

    }

    public ResultSet getNotes() throws SQLException, ClassNotFoundException {

        String query = "SELECT * FROM " + NOTE_TABLE + " WHERE " + NOTE_USER + "=?";
        PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
        preparedStatement.setInt(1, ListManager.getUserId());

        ResultSet notesResultSet = preparedStatement.executeQuery();

        return notesResultSet;

    }

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

    }

    public void archiveTask(int taskId, Task task) throws ClassNotFoundException, SQLException {

        String insert = "UPDATE " + TASK_TABLE + " SET " + TASK_STATE + "=? WHERE " + TASK_ID + "=?";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setInt(1, 2);
        preparedStatement.setInt(2, taskId);

        preparedStatement.executeUpdate();

    }

    public void reactivateTask(int taskId, Task task) throws ClassNotFoundException, SQLException {

        String insert = "UPDATE " + TASK_TABLE + " SET " + TASK_STATE + "=? WHERE " + TASK_ID + "=?";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
        preparedStatement.setInt(1, 0);
        preparedStatement.setInt(2, taskId);

        preparedStatement.executeUpdate();

    }

    public ResultSet getTasks() throws SQLException, ClassNotFoundException {

        String query = "SELECT * FROM " + TASK_TABLE + " WHERE " + TASK_USER + "=?";
        PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
        preparedStatement.setInt(1, ListManager.getUserId());

        ResultSet tasksResultSet = preparedStatement.executeQuery();

        return tasksResultSet;

    }

}
