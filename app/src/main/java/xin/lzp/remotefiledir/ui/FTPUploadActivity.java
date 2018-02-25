package xin.lzp.remotefiledir.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;
import xin.lzp.remotefiledir.R;
import xin.lzp.remotefiledir.ui.adapter.FTPFileListAdapter;
import xin.lzp.remotefiledir.ui.popup.PopupWinConfirmCancel;
import xin.lzp.remotefiledir.ui.popup.PopupWinTransmission;
import xin.lzp.remotefiledir.util.FTPClientConfig;
import xin.lzp.remotefiledir.util.FileTool;
import xin.lzp.remotefiledir.util.User;

public class FTPUploadActivity extends AppCompatActivity
        implements View.OnClickListener,
        AdapterView.OnItemClickListener{

    private Button btnCancel;
    private Button btnConfirm;
    private TextView tvPath;
    private ListView listView;

    private FTPClient client;
    private HandlerThread handlerThread;
    private Handler asyncHandler;
    private Handler uiThreadHandler = new Handler();
    private File uploadFile;
    private User user;

    private boolean isOnRequestLogin;

    private boolean isOnTransferring = false;

    private LinkedList<FTPFile> fileList;

    private List<FTPFile> rootData;


    private PopupWinTransmission popupWinTransmission;
    private PopupWinConfirmCancel popupWinConfirmCancel;


    {
        handlerThread = new HandlerThread("asyncThreadForFTPUpload");
        handlerThread.start();
        asyncHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftpupload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setVisibility(View.GONE);


        Intent intent = getIntent();
        uploadFile = new File(FileTool.getRealFilePathFromUri(this, intent.getData()));


        initBaseView();
        initData();

        if(popupWinConfirmCancel == null){
            initPopupWindow();
        }
    }

    private void initData(){
        user = User.getCurrentUser();
        if(user == null){
            //用户未登录， 请求用户登录然后才能执行上传操作
            Intent intent = new Intent(FTPUploadActivity.this, LoginRegisterActivity.class);
            intent.putExtra(LoginRegisterActivity.ENTER_INTENT, LoginRegisterActivity.ENTER_REQUEST_LOGIN);

            isOnRequestLogin = true;
            startActivityForResult(intent, 1);
        }else{
            fileList = new LinkedList<>();

            client = new FTPClient();

            asyncHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        client.connect(FTPClientConfig.getAddress(), Integer.parseInt(FTPClientConfig.getPort()));

                        client.login(FTPClientConfig.getUsername(), FTPClientConfig.getPassword());
                        client.setPassive(true);

                        rootData = Arrays.asList(client.list());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initListView();
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

    private void initListView(){
        FTPFileListAdapter adapter = new FTPFileListAdapter(this, rootData);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
        tvPath.setText("/");
    }

    private void initBaseView() {
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnConfirm = (Button) findViewById(R.id.btn_submit);
        tvPath = (TextView) findViewById(R.id.tv_current_path);
        listView = (ListView) findViewById(R.id.lv_show_file);


        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == LoginRegisterActivity.EXIT_LOGIN_SUCCEED){
            //登录成功， init
        }else{
            //登录失败， 提示
            Toast.makeText(this, "登录失败！", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
        * for btn_cancel and btn_confirm*/
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_cancel){
            this.finish();
        }else{
            asyncHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        client.upload(uploadFile, new FTPDataTransferListener() {
                            long current = 0;

                            @Override
                            public void started() {
                                uiThreadHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        popupWinTransmission.show();
                                    }
                                });
                                updateProgress(0);

                                isOnTransferring = true;
                            }

                            @Override
                            public void transferred(int i) {
                                int currentPercentage = 0;
                                current += i;
                                Log.i("balala", "当前："  + current + " total：" + uploadFile.length());

                                if(uploadFile.length() != 0){
                                    currentPercentage = (int) (current * 100 / uploadFile.length());
                                }

                                updateProgress(currentPercentage);

                            }

                            @Override
                            public void completed() {
                                uiThreadHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(popupWinConfirmCancel.isShowing())
                                            popupWinConfirmCancel.dismiss();

                                        if(popupWinTransmission.isShowing())
                                            popupWinTransmission.dismiss();
                                    }
                                });

                                isOnTransferring = false;
                            }

                            @Override
                            public void aborted() {
                                uploadFailedMsg();
                                isOnTransferring = false;
                            }

                            @Override
                            public void failed() {
                                uploadFailedMsg();
                                isOnTransferring = false;
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
                    } catch (FTPAbortedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }



    /*
     *for ftp file list  */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final FTPFile file = (FTPFile) parent.getItemAtPosition(position);
        if(file.getType() == FTPFile.TYPE_DIRECTORY){
            new Thread(){
                @Override
                public void run() {
                    try {
                        client.changeDirectory(file.getName());
                        final List<FTPFile> ftpFiles = Arrays.asList(client.list());
                        final String currentPath = client.currentDirectory();

                        fileList.push(file);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FTPFileListAdapter adapter = new FTPFileListAdapter(FTPUploadActivity.this, ftpFiles);
                                listView.setAdapter(adapter);
                                tvPath.setText(currentPath);
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
            }.start();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { //表示按返回键 时的操作
                // 监听到返回按钮点击事件

                if(popupWinConfirmCancel.isShowing()){
                    popupWinConfirmCancel.dismiss();
                    return true;
                }


                if(popupWinTransmission.isShowing()){
                    popupWinConfirmCancel.show();
                    return true;
                }


                if(fileList.size() != 0){
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                client.changeDirectoryUp();
                                final List<FTPFile> files = Arrays.asList(client.list());
                                final String currentDir = client.currentDirectory();
                                fileList.pop();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        FTPFileListAdapter adapter = null;
                                        adapter = new FTPFileListAdapter(FTPUploadActivity.this, files);

                                        tvPath.setText(currentDir);
                                        listView.setAdapter(adapter);
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
                    }.start();
                    return true;
                }
                else{
                    //询问是否退出
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void initPopupWindow(){
        popupWinTransmission = new PopupWinTransmission(this, PopupWinTransmission.TYPE_UPDATE, findViewById(R.id.root_view));

        popupWinConfirmCancel = new PopupWinConfirmCancel(this, findViewById(R.id.root_view), new PopupWinConfirmCancel.OnCancelCallback() {
            @Override
            public void cancel() {
                asyncHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isOnTransferring && client != null && client.isConnected()){
                            try {
                                isOnTransferring = false;
                                client.abortCurrentDataTransfer(true);

                                client.deleteFile(client.currentDirectory() + "/" + uploadFile.getName());

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (FTPIllegalReplyException e) {
                                e.printStackTrace();
                            } catch (FTPException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        if(isOnRequestLogin == true){
            isOnRequestLogin = false;
        }

        initData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        asyncHandler.post(new Runnable() {
            @Override
            public void run() {
                if (client != null && client.isConnected()){
                    try {
                        client.logout();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (FTPIllegalReplyException e) {
                        e.printStackTrace();
                    } catch (FTPException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void updateProgress(final int progress){
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("balala", progress + "");
                popupWinTransmission.updateProgress(progress);
            }
        });
    }

    private void uploadFailedMsg(){
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if(popupWinConfirmCancel.isShowing())
                    popupWinConfirmCancel.dismiss();

                if(popupWinTransmission.isShowing())
                    popupWinTransmission.dismiss();
                Toast.makeText(FTPUploadActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
