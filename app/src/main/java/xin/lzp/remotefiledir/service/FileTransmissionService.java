package xin.lzp.remotefiledir.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

import xin.lzp.remotefiledir.model.FileTransEntity;

public class FileTransmissionService extends Service {

    private MyBinder myBinder = new MyBinder();


    private List<FileTransEntity> entities;

    public FileTransmissionService() {
    }


    //在onCreate中初始化信息
    @Override
    public void onCreate() {


    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }


    public class MyBinder extends Binder{
        public FileTransmissionService getService(){
            return FileTransmissionService.this;
        }
    }


    /*
    * 从配置文件中加载当前的文件传输进度*/
    private boolean init(){
        SharedPreferences preferences = this.getSharedPreferences("transmissionInfo", MODE_PRIVATE);
        JSONArray datas = JSON.parseArray(preferences.getString("fileTransEntitys", "[]"));

        entities = new ArrayList<>();

        for (int i = 0; i < datas.size(); i++) {
            entities.add(datas.getObject(i, FileTransEntity.class));
        }

        return true;
    }
}
