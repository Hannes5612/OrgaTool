package mainpackage.database;

import mainpackage.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHandler extends Config{
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException{

        Class.forName("com.mysql.jdbc.Driver");

        String connectionString = "jdbc:mysql://"+dbHost+":"+dbPort+"/"+dbName;


        dbConnection = DriverManager.getConnection(connectionString,dbUser,dbPass);

        return dbConnection;
    }

    public void signupUser(User user){

        String insert = "INSERT INTO "+Const.USER_TABLE+"("+Const.USER_FIRSTNAME+","+Const.USER_LASTNAME+","
                +Const.USER_USERNAME+","+Const.USER_PASSWORD+","+Const.USER_GENDER+") VALUES(?,?,?,?,?)";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getUserName());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getGender());

            preparedStatement.executeUpdate();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}