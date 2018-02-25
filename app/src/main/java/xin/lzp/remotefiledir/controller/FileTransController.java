package xin.lzp.remotefiledir.controller;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import xin.lzp.remotefiledir.model.FileTransEntity;
import xin.lzp.remotefiledir.model.FileTransStatus;
import xin.lzp.remotefiledir.util.FTPClientConfig;

/**
 * Created by lzp on 2017/9/22.
 * 文件传输控制器
 * 用于在程序运行中控制文件传输的细节
 * （进度， 开始， 暂停， 取消， 获取相关信息）
 *
 *
 * 每个文件传输单独由一个控制器控制，使用独立的FTPClient
 */

public class FileTransController {
    private FileTransEntity entity;

    private FileTransStatus status;

    private boolean hasError;

    private FTPClient client;

    private FTPDataTransferListener listener;

    public FileTransController(FileTransEntity entity){
        this.entity = entity;



        //加载ftpClient
        try {
            initFTPClient();
            hasError = true;
        } catch (Exception e) {
            e.printStackTrace();
            hasError = false;
        }

        //初始化本地环境
        hasError = ! initLocal();
    }

    private void initFTPClient() throws Exception{
        String address = FTPClientConfig.getAddress();
        int port = Integer.parseInt(FTPClientConfig.getPort());
        String userName = FTPClientConfig.getUsername();
        String password = FTPClientConfig.getPassword();

        client.connect(address, port);
        client.login(userName, password);
    }


    /*
    * 从本地文件中初始化 状态
    * return true for init succeed false for file not found or file has been variation*/
    private boolean initLocal(){
        status = new FileTransStatus();
        status.setFileName(entity.getFileName());

        File file;

        try{
            file = new File(entity.getLocalFilePath());

            if(file.exists()){
                status.setCurrentLength(file.length());
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean start(){
        try{
            if(!hasError){
                if(entity.getType() == FileTransEntity.TYPE_DOWNLOAD){
                    client.download(entity.getRemoteFilePath(), new File(entity.getLocalFilePath()), status.getCurrentLength(), listener);
                }else{

                }
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean stop(){
        return true;
    }

    public boolean terminated(){
        return true;
    }

    public FileTransStatus getInfo(){
        return status;
    }


    private void initListener(){
        listener = new FTPDataTransferListener() {
            @Override
            public void started() {

            }

            @Override
            public void transferred(int i) {

            }

            @Override
            public void completed() {

            }

            @Override
            public void aborted() {

            }

            @Override
            public void failed() {
                hasError = true;
            }
        };
    }
}
