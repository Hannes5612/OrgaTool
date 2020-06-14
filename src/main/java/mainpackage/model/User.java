package mainpackage.model;

public class User {
    private String userName, password;
    private int userid;

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User() {

    }


    public int getUserid(){return this.userid;}

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
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
