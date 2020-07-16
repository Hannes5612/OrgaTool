package mainpackage.model;

import mainpackage.exceptions.IllegalIdentification;

/**
 * Class creates user objects with a unique id.
 */
public class User {
    private String userName, password;
    private int userid;

    /**
     * Creating user with name and password
     *
     * @param userName - name of user
     * @param password - password for account of user
     */
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Creating user without variables
     */
    public User() {}

    /**
     * getting ID of user
     *
     * @return user ID
     */
    public int getUserid() {
        return this.userid;
    }

    /**
     * getting user's name
     *
     * @return user's name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * getting user's password
     *
     * @return user's password
     */
    public String getPassword() {
        return password;
    }

    // setters

    /**
     * setting the ID of a user
     *
     * @param userid new user ID
     */
    public void setUserid(int userid) throws IllegalIdentification {
        if (userid >= 0) {
            this.userid = userid;
        } else {
            throw new IllegalIdentification("ID is invalid.");
        }
    }

    /**
     * method writes user object information into String
     *
     * @return String with all of the user's information
     */
    @Override
    public java.lang.String toString() {
        return "User{" +
                "userName='"   + userName + '\'' +
                ", password='" + password + '\'' +
                ", userid='"   + userid + '\'' +
                '}';
    }
}
