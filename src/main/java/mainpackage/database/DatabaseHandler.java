package mainpackage.database;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import mainpackage.model.EntryLists;
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

    public void createTask(Task task) {

        String insert = "INSERT INTO " + TASK_TABLE + "("
                + TASK_USER + "," + TASK_TYPE + "," + TASK_TITLE + "," + TASK_CONTENT + "," + TASK_PRIO + "," +
                TASK_COLOR + "," + TASK_DUEDATE + "," + TASK_CREATIONDATE + "," + TASK_STATE + ") VALUES(?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setInt(1, EntryLists.getUserId());
            preparedStatement.setString(2, "Task");
            preparedStatement.setString(3, task.getName());
            preparedStatement.setString(4, task.getContent());
            preparedStatement.setString(5, task.getPriority());
            preparedStatement.setString(6, task.getColor());
            preparedStatement.setDate(7, task.getDueDate());
            preparedStatement.setDate(8, task.getCreationDate());
            preparedStatement.setInt(9, task.getState());

            preparedStatement.executeUpdate();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection failed", ButtonType.OK);
            alert.showAndWait();
        }

    }

    public ResultSet getTasks(User user) {
        ResultSet tasksResulSet = null;

        String query = "SELECT * FROM " + TASK_TABLE + " WHERE " + TASK_USER + "=?";
        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
            preparedStatement.setInt(1, user.getUserid());

            tasksResulSet = preparedStatement.executeQuery();

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return tasksResulSet;
    }

    public void deleteTask(Task task) {
        String insert = "DELETE FROM " + TASK_TABLE + " WHERE " + TASK_ID + "=?";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setInt(1, task.getTaskid());

            preparedStatement.executeUpdate();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection failed", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void createNote(Note note) {

        String insert = "INSERT INTO " + NOTE_TABLE + "("
                + NOTE_USER + "," + NOTE_TITLE + "," + NOTE_CONTENT + "," + NOTE_DATE + "," +
                NOTE_STATE + ") VALUES(?,?,?,?,?)";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setInt(1, EntryLists.getUserId());
            preparedStatement.setString(2, note.getTitle());
            preparedStatement.setString(3, note.getContent());
            preparedStatement.setDate(4, note.getDate());
            preparedStatement.setInt(5, note.getState());


            preparedStatement.executeUpdate();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection failed", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public ResultSet getNotes() {
        ResultSet notesResultSet = null;

        String query = "SELECT * FROM " + NOTE_TABLE + " WHERE " + NOTE_USER + "=?";
        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
            preparedStatement.setInt(1, EntryLists.getUserId());

            notesResultSet = preparedStatement.executeQuery();

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return notesResultSet;
    }
}