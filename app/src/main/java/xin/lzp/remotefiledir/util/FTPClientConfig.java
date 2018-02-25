package xin.lzp.remotefiledir.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lzp on 2017/9/22.
 * 在这里获取一些FTPClient的相关配置
 */

public class FTPClientConfig {


    private static String username;
    private static String password;
    private static String port;
    private static String address;
    private static SharedPreferences preferences;


    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        FTPClientConfig.username = username;
        save();
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        FTPClientConfig.password = password;
        save();
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        FTPClientConfig.port = port;
        save();
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        FTPClientConfig.address = address;
        save();
    }

    private static void save(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("address", address);
        editor.putString("port", port);

        editor.apply();
    }


    public static void init(Context context){
        preferences = context.getSharedPreferences("ftpClientConfig", Context.MODE_PRIVATE);
        username = preferences.getString("username", null);
        password = preferences.getString("password", null);
        address = preferences.getString("address", "awell.xin");
        port =  preferences.getString("port", "21");
    }
}
