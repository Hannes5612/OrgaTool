package mainpackage.database;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import mainpackage.model.User;

import java.sql.*;
import java.util.concurrent.Executors;

/**
 * Database Handler class to process database requests
 */
public class DatabaseHandler extends Config {

    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {

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

        } catch (SQLException throwables) {
         throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

    public ResultSet getTasks(User user) {
        ResultSet tasksResulSet = null;

        return tasksResulSet;
    }
}