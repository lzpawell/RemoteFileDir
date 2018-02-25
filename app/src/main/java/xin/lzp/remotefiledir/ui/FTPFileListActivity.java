package xin.lzp.remotefiledir.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;
import xin.lzp.remotefiledir.R;
import xin.lzp.remotefiledir.ui.adapter.FTPFileListAdapter;
import xin.lzp.remotefiledir.ui.popup.PopupWinFileOptMenu;
import xin.lzp.remotefiledir.ui.popup.PopupWinMkDir;
import xin.lzp.remotefiledir.ui.popup.PopupWinQuit;
import xin.lzp.remotefiledir.ui.popup.PopupWinRename;
import xin.lzp.remotefiledir.util.App;
import xin.lzp.remotefiledir.util.FTPClientConfig;
import xin.lzp.remotefiledir.util.User;

public class FTPFileListActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private FTPClient client;
    private User user;

    private LinkedList<FTPFile> fileList = new LinkedList<>();
    private ListView listView;
    private TextView tvCurrentPath;
    private List<FTPFile> rootFileList;

    private PopupWinFileOptMenu popupWinFileOptMenu;
    private FTPFile currentFile;


    private PopupWinRename popupWinRename;
    private PopupWinQuit popupWinQuit;


    private boolean isOnRequestLogin;

    /*
    * init方法用于加载ftp用户的文件目录*/
    private void initData(){
        user = User.getCurrentUser();
        client = new FTPClient();

        new Thread(){
            @Override
            public void run() {
                try {
                    client.connect(FTPClientConfig.getAddress(), Integer.parseInt(FTPClientConfig.getPort()));

                    client.login(FTPClientConfig.getUsername(), FTPClientConfig.getPassword());
                    client.setPassive(true);

                    rootFileList = Arrays.asList(client.list());


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftpfile_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FTPFileListActivity.this, ListFileActivity.class);
                startActivity(intent);
            }
        });



        checkAndRequestLoginThenLoadData();
    }

    private void checkAndRequestLoginThenLoadData() {
        user = User.getCurrentUser();
        if (user == null){
            isOnRequestLogin = true;
            Intent intent = new Intent(FTPFileListActivity.this, LoginRegisterActivity.class);
            intent.putExtra(LoginRegisterActivity.ENTER_INTENT, LoginRegisterActivity.ENTER_REQUEST_LOGIN);
            startActivityForResult(intent, 1);
        }else{
            initData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == LoginRegisterActivity.EXIT_LOGIN_SUCCEED){

        }
        else{
            Log.i("balala","login failed");
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        popupWinMkDir = new PopupWinMkDir[1];
        getMenuInflater().inflate(R.menu.ftp_main_menu, menu);//加载menu文件到布局
        return true;
    }


    private void initView() {
        popupWinFileOptMenu = new PopupWinFileOptMenu(this, this.findViewById(R.id.root_view), PopupWinFileOptMenu.TYPE_DOWNLOAD, new PopupWinFileOptMenu.OnGetOptSelectedCallback() {
            @Override
            public void getOptSelected(final PopupWinFileOptMenu.Option option) {
                Toast.makeText(FTPFileListActivity.this, "balala " + option.toString(), Toast.LENGTH_SHORT).show();
                if(currentFile != null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                switch (option){
                                    case OPT_DELETE:
                                        if(currentFile.getType() == FTPFile.TYPE_DIRECTORY){
                                            client.deleteDirectory(currentFile.getName());
                                        }else{
                                            client.deleteFile(currentFile.getName());
                                        }
                                        refreshList();
                                        break;

                                    case OPT_DOWNLOAD:
                                        App.dataProvider.put(FTPDownloadPosSetActivity.FTPFILE_PATH, tvCurrentPath.getText().toString().trim() + "/" + currentFile.getName());
                                        App.dataProvider.put(FTPDownloadPosSetActivity.FTPFILE, currentFile);
                                        Intent intent = new Intent(FTPFileListActivity.this, FTPDownloadPosSetActivity.class);
                                        startActivity(intent);


                                        break;

                                    case OPT_RENAME:
                                        popupWinRename.show();
                                        break;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (FTPIllegalReplyException e) {
                                e.printStackTrace();
                            } catch (FTPException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
        });


        popupWinRename = new PopupWinRename(this, findViewById(R.id.root_view), new PopupWinRename.OnGetNameCallback() {
            @Override
            public void getName(final String name) {
                //rename the current file
                if(name != null && name.equals("") == false){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                client.rename(currentFile.getName(), name);
                                refreshList();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (FTPIllegalReplyException e) {
                                e.printStackTrace();
                            } catch (FTPException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        listView = (ListView) findViewById(R.id.lv_show_file);
        tvCurrentPath = (TextView) findViewById(R.id.tv_current_path);

        tvCurrentPath.setText("/");


        FTPFileListAdapter adapter = new FTPFileListAdapter(this, rootFileList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(itemClickListener);
        listView.setOnItemLongClickListener(itemLongClickListener);


    }

    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            currentFile = (FTPFile) parent.getItemAtPosition(position);
            popupWinFileOptMenu.show();
            return true;
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
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
                                    FTPFileListAdapter adapter = new FTPFileListAdapter(FTPFileListActivity.this, ftpFiles);
                                    listView.setAdapter(adapter);
                                    tvCurrentPath.setText(currentPath);
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
            }else{
                currentFile = file;
                popupWinFileOptMenu.show();
            }
        }
    };



    private PopupWinMkDir[] popupWinMkDir;


    //为网络异步请求提供的handler
    private android.os.Handler handler;
    private HandlerThread handlerThread;
    {
        handlerThread = new HandlerThread("balalaThread");
        handlerThread.start();
        handler = new android.os.Handler(handlerThread.getLooper());
    }

    //刷新列表， 提供给新建文件夹或者删除文件（夹）后刷新文件列表
    private void refreshList(){

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<FTPFile> files = Arrays.asList(client.list());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FTPFileListAdapter adapter = new FTPFileListAdapter(FTPFileListActivity.this, files);
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
                } catch (FTPAbortedException e) {
                    e.printStackTrace();
                } catch (FTPListParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //delete file
    private void removeFile(final FTPFile ftpFile){
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if(ftpFile.getType() == FTPFile.TYPE_DIRECTORY){
                        client.deleteDirectory(ftpFile.getName());
                    }else{
                        client.deleteFile(ftpFile.getName());
                    }

                    refreshList();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FTPIllegalReplyException e) {
                    e.printStackTrace();
                } catch (FTPException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //rename


    //toolbar menu item click
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_mk_dir:
                if(popupWinMkDir[0] == null){
                    popupWinMkDir[0] = new PopupWinMkDir(this, this.findViewById(R.id.root_view), new PopupWinMkDir.OnGetDirNameCallback() {
                        @Override
                        public void getDirName(final String dirName) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        client.createDirectory(dirName);
                                        refreshList();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (FTPIllegalReplyException e) {
                                        e.printStackTrace();
                                    } catch (FTPException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                }

                popupWinMkDir[0].show();
                break;
            case R.id.menu_settings:
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_quit:
                User.logout();
                Intent myIntent = new Intent(FTPFileListActivity.this, LoginRegisterActivity.class);
                myIntent.putExtra(LoginRegisterActivity.ENTER_INTENT, LoginRegisterActivity.ENTER_REQUEST_LOGIN);
                isOnRequestLogin = true;
                startActivityForResult(myIntent, 1);
                break;

            case R.id.menu_transmission:
                Toast.makeText(this, "传输列表", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FTPFileListActivity.this, TransControlActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_refresh:
                refreshList();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { //表示按返回键 时的操作
                // 监听到返回按钮点击事件

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
                                        adapter = new FTPFileListAdapter(FTPFileListActivity.this, files);

                                        tvCurrentPath.setText(currentDir);
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
                    if(popupWinQuit == null){
                        popupWinQuit = new PopupWinQuit(FTPFileListActivity.this, findViewById(R.id.root_view), new PopupWinQuit.OnQuitCallback() {
                            @Override
                            public void quit() {
                                FTPFileListActivity.this.finish();
                            }
                        });
                    }

                    popupWinQuit.show();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        if(isOnRequestLogin == true){
            isOnRequestLogin = false;
        }

        checkAndRequestLoginThenLoadData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.post(new Runnable() {
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
}
