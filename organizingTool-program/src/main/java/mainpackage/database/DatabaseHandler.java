package mainpackage.database;

import mainpackage.model.User;

import java.sql.*;

public class DatabaseHandler extends Config{
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException{

        Class.forName("com.mysql.jdbc.Driver");

        String connectionString = "jdbc:mysql://"+dbHost+":"+dbPort+"/"+dbName;


        dbConnection = DriverManager.getConnection(connectionString,dbUser,dbPass);

        return dbConnection;
    }

    public void signupUser(User user){

        String insert = "INSERT INTO "+USER_TABLE+"("
                +USER_USERNAME+","+USER_PASSWORD+") VALUES(?,?)";

        try (PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert)) {
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPassword());

            preparedStatement.executeUpdate();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



    public ResultSet getUser(User user){

        ResultSet resultSet = null;

        if (!user.getUserName().equals("")||!user.getPassword().equals("")){
            String query = "SELECT * FROM " + USER_TABLE + " WHERE " + USER_USERNAME + "=?" + " AND "
                    + USER_PASSWORD + "=?";

            try {
                PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
                preparedStatement.setString(1,user.getUserName());
                preparedStatement.setString(2,user.getPassword());

                resultSet = preparedStatement.executeQuery();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Please enter your credentials ");
        }
        return resultSet;
    }
}