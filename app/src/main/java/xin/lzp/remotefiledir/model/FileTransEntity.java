package xin.lzp.remotefiledir.model;

import com.alibaba.fastjson.JSON;

/**
 * Created by lzp on 2017/9/22.
 * 文件传输实体
 * 标注一个正在传输任务中的实体
 */

public class FileTransEntity {
    public static final int TYPE_UPLOAD = 0;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static final int TYPE_DOWNLOAD = 1;

    private String fileName;
    private String remoteFilePath;
    private String localFilePath;
    private int type;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRemoteFilePath() {
        return remoteFilePath;
    }

    public void setRemoteFilePath(String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }



    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


}
