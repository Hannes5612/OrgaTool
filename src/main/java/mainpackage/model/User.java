package mainpackage.model;

/**
 * Used to create a user Objet with the important userID
 */
public class User {
    private String userName, password;
    private int userid;

    //constructors
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User() {}

    //getters
    public int getUserid(){return this.userid;}

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    //Set the userID of a User
    public void setUserid(int userid) {
        this.userid = userid;
    }
    @Override
    public java.lang.String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }
}
