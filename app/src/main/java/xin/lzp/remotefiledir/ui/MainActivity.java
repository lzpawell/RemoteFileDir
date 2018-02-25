package xin.lzp.remotefiledir.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;
import xin.lzp.remotefiledir.R;
import xin.lzp.remotefiledir.util.RootFileDirGetter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListFileActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        Log.i("balala","a");

        for(File file : RootFileDirGetter.getAvaliableStorage(this)){
            Log.i("filename: ", file.getName());
            Log.i("filepath: ", file.getAbsolutePath());
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("balala","b");
                FTPClient client = new FTPClient();

                try {
                    client.connect("39.108.65.230");
                    Log.i("balala","c");
                    client.login("root", "Lzpchuyin12345");
                    Log.i("balala","d");


                    for(FTPFile file : client.list()){
                        Log.i("ftp::", file.getName());
                    }


                    //设置ftp被动模式
                    client.setPassive(true);



                    Log.i("ftpD","" + Environment.getExternalStorageState());
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                    File root = new File(path + "/" + "LZPFtp");
                    if(!root.exists())
                        Log.i("ftpd", "mkdir: " + root.mkdirs());

                    File newFile = new File(root, "set_ftp_port.sh");
                    if(!newFile.exists())
                        newFile.createNewFile();

                    client.download("set_ftp_port.sh", newFile, new FTPDataTransferListener() {
                        @Override
                        public void started() {
                            Log.i("ftpD","ftp download started!");
                        }

                        @Override
                        public void transferred(int i) {
                            Log.i("ftpD","ftp download " + i);
                        }

                        @Override
                        public void completed() {
                            Log.i("ftpD","ftp download finished!");
                        }

                        @Override
                        public void aborted() {
                            Log.i("ftpD","ftp download aborted!");
                        }

                        @Override
                        public void failed() {
                            Log.i("ftpD","ftp download failed!");
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FTPIllegalReplyException e) {
                    e.printStackTrace();
                } catch (FTPException e) {
                    e.printStackTrace();
                } catch (FTPDataTransferException e) {
                    e.printStackTrace();
                } catch (FTPListParseException e) {
                    e.printStackTrace();
                } catch (FTPAbortedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
