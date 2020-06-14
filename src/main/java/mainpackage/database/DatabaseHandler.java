package mainpackage.database;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import mainpackage.model.Task;
import mainpackage.model.User;

import java.sql.*;
import java.util.concurrent.Executors;

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
        }catch(Exception e){
            e.printStackTrace();
        }
        return dbConnection;
    }

    public void signupUser(User user) {

        String insert = "INSERT INTO " + USER_TABLE + "("
                + USER_USERNAME + "," + USER_PASSWORD + ") VALUES(?,?)";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPassword());

            preparedStatement.executeUpdate();


        } catch (SQLIntegrityConstraintViolationException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Username already in Database!", ButtonType.OK);
            alert.showAndWait();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Connection failed", ButtonType.OK);
            alert.showAndWait();
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

    public void createTask(Task task, User user, String type){

        String insert = "INSERT INTO " + TASK_TABLE + "("
                + TASK_USER + "," + TASK_TYPE + "," + TASK_TITLE + "," + TASK_CONTENT + "," + TASK_PRIO + "," +
                TASK_COLOR + "," + TASK_DUEDATE + "," + TASK_CREATIONDATE + "," + TASK_STATE + ") VALUES(?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setInt(1, user.getUserid());
            preparedStatement.setString(2, type);
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
}