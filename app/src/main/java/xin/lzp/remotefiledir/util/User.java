package xin.lzp.remotefiledir.util;

/**
 * Created by lzp on 2017/8/22.
 */

public class User {
    private static User currentUser;

    private String userId;
    private String password;

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    private String email;

    private User(String userId, String password, String email){

    }

    public static Result login(String user_id, String password){
        //login

        //if no error
        //new a User and set it as currentUser
        currentUser = new User("root", "Lzpchuyin12345", "12580");
        return Result.NO_ERROR;
    }

    public static Result register(String userId, String password, String confirmPassword, String email){
        //register

        //if no error
        //new a User and set it as currentUser

        return Result.NO_ERROR;
    }


    public static boolean logout(){
        currentUser = null;
        return true;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public enum Result{
        USER_NOT_FOUND,
        PASSWORD_ERROR,
        USER_IS_EXISTED,
        NO_ERROR
    }
}
