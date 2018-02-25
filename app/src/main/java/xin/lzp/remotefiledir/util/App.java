package xin.lzp.remotefiledir.util;

import android.app.Application;

import java.util.HashMap;

/**
 * Created by lzp on 2017/10/10.
 */

public class App extends Application {

    //使用这个工具对象为某些不支持序列化的对象提供应用内运输功能
    public static HashMap<String, Object> dataProvider;

    @Override
    public void onCreate() {
        super.onCreate();


        //初始化相关数据
        FTPClientConfig.init(this);
        dataProvider = new HashMap<>();
    }
}
