package xin.lzp.remotefiledir.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import xin.lzp.remotefiledir.R;
import xin.lzp.remotefiledir.ui.popup.PopupWinConfirmCancel;
import xin.lzp.remotefiledir.ui.popup.PopupWinTransmission;
import xin.lzp.remotefiledir.util.App;
import xin.lzp.remotefiledir.util.FTPClientConfig;
import xin.lzp.remotefiledir.util.FileSizeFormat;
import xin.lzp.remotefiledir.util.RootFileDirGetter;

public class FTPDownloadPosSetActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String FTPFILE = "FTPFILE";
    public static final String FTPFILE_PATH = "FTPFILE_PATH";

    private LinkedList<File> fileList;
    private ListView listView;
    private TextView tvCurrentPath;
    private List<File> rootFileList;
    private File currentFile;
    File locateFile;


    private Button btnCancel;
    private Button btnConfirm;


    private FTPClient client;
    private boolean isOnTransferring = false;
    private PopupWinTransmission popupWinTransmission;
    private PopupWinConfirmCancel popupWinConfirmCancel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftpdownload_pos_set);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setVisibility(View.GONE);


        initView();
        initPopupWindow();
    }

    private void initView() {
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnConfirm = (Button) findViewById(R.id.btn_submit);

        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


        listView = (ListView) findViewById(R.id.lv_show_file);
        tvCurrentPath = (TextView) findViewById(R.id.tv_current_path);

        tvCurrentPath.setText("/");

        fileList = new LinkedList<>();
        rootFileList = RootFileDirGetter.getAvaliableStorage(this);

        FileListAdapter adapter = new FileListAdapter(this, rootFileList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = (File) parent.getItemAtPosition(position);
                if(file.isDirectory()){
                    currentFile = file;
                    fileList.push(file);
                    FileListAdapter adapter = new FileListAdapter(FTPDownloadPosSetActivity.this, Arrays.asList(file.listFiles()));
                    listView.setAdapter(adapter);

                    if(fileList.size() == 1){
                        tvCurrentPath.setText("/" + file.getName());
                    }else{
                        tvCurrentPath.setText(tvCurrentPath.getText() + "/" + file.getName());
                    }
                }else{

                }
            }
        });

    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    @Override
    public void onClick(View v) {
        Log.i("balala", "click");


        if(v.getId() == R.id.btn_submit){
            Log.i("balala", "confirm click");
            //执行下载逻辑把ftpFile 下载到 currentFile中去
            Toast.makeText(this, "执行下载", Toast.LENGTH_SHORT).show();
            asyncHandler.post(new Runnable() {
                @Override
                public void run() {
                    client = new FTPClient();
                    try {
                        client.connect(FTPClientConfig.getAddress(), Integer.parseInt(FTPClientConfig.getPort()));
                        client.login(FTPClientConfig.getUsername(), FTPClientConfig.getPassword());
                        client.setPassive(true);

                        final FTPFile ftpFile = (FTPFile) App.dataProvider.get(FTPFILE);
                        String ftpFilePath = (String) App.dataProvider.get(FTPFILE_PATH);

                        Log.i("balala", "file path "  + ftpFilePath);

                        locateFile = new File(currentFile.getAbsolutePath(), ftpFile.getName());
                        client.download(ftpFilePath, locateFile, new FTPDataTransferListener() {

                            private long current = 0;
                            @Override
                            public void started() {
                                handler.post(new Runnable() {
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
                                Log.i("balala", "当前："  + current + " total：" + ftpFile.getSize());

                                if(ftpFile.getSize() != 0){
                                    currentPercentage = (int) (current * 100 / ftpFile.getSize());
                                }

                                updateProgress(currentPercentage);
                            }

                            @Override
                            public void completed() {
                                handler.post(new Runnable() {
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
                                downloadFailedMsg();
                                isOnTransferring = false;
                            }

                            @Override
                            public void failed() {
                                downloadFailedMsg();
                                isOnTransferring = false;
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (FTPIllegalReplyException e) {
                        e.printStackTrace();
                    } catch (FTPException e) {
                        e.printStackTrace();
                    } catch (FTPAbortedException e) {
                        e.printStackTrace();
                    } catch (FTPDataTransferException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else if(v.getId() == R.id.btn_cancel){
            this.finish();
        }
    }

    private class FileListAdapter extends BaseAdapter {

        private List<File> fileList;
        private LayoutInflater inflater;
        public FileListAdapter(Activity activity, List<File> fileList){
            this.fileList = fileList;
            this.inflater = activity.getLayoutInflater();

            //对fileList进行重排序， 按照字母序
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
        }

        @Override
        public int getCount() {
            return fileList.size();
        }

        @Override
        public Object getItem(int position) {
            return fileList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder{
            private ImageView imageView;
            private TextView textView;
            private TextView tvTime;
            private TextView tvSize;

            public ViewHolder(View rootView){
                imageView = (ImageView) rootView.findViewById(R.id.img_header);
                textView = (TextView) rootView.findViewById(R.id.tv_file_name);
                tvTime = (TextView) rootView.findViewById(R.id.tv_time);
                tvSize = (TextView) rootView.findViewById(R.id.tv_file_size);
            }

            public void setView(File file){
                if(file.isDirectory()){
                    imageView.setImageResource(R.drawable.folder);
                    tvSize.setText(file.listFiles().length + "项");
                }else{
                    imageView.setImageResource(R.drawable.file);
                    tvSize.setText(FileSizeFormat.format(file.length()));
                }

                textView.setText(file.getName());
                tvTime.setText(dateFormat.format(file.lastModified()));
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            if(convertView == null){
                convertView = inflater.inflate(R.layout.list_item_show_files, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.setView(fileList.get(position));

            return convertView;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { //表示按返回键 时的操作
                // 监听到返回按钮点击事件
                FileListAdapter adapter = null;

                if(popupWinConfirmCancel.isShowing()){
                    popupWinConfirmCancel.dismiss();
                    return true;
                }


                if(popupWinTransmission.isShowing()){
                    popupWinConfirmCancel.show();
                    return true;
                }



                if(fileList.size() != 0)
                    fileList.pop();


                if(fileList.size() == 0){
                    this.finish();
                }else{
                    adapter = new FileListAdapter(FTPDownloadPosSetActivity.this, Arrays.asList(fileList.getLast().listFiles()));
                    String lastPath = tvCurrentPath.getText().toString();
                    tvCurrentPath.setText(lastPath.subSequence(0, lastPath.lastIndexOf('/')));
                }
                listView.setAdapter(adapter);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    //刷新列表， 提供给新建文件夹或者删除文件（夹）后刷新文件列表
    private void refreshList(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FileListAdapter adapter = new FileListAdapter(FTPDownloadPosSetActivity.this, Arrays.asList(fileList.getFirst().listFiles()));
                listView.setAdapter(adapter);
            }
        });
    }


    //为网络异步请求提供的handler
    private android.os.Handler handler = new Handler();
    private HandlerThread handlerThread;
    private Handler asyncHandler;
    {
        handlerThread = new HandlerThread("balalaThread");
        handlerThread.start();
        asyncHandler = new android.os.Handler(handlerThread.getLooper());
    }


    private void initPopupWindow(){
        popupWinTransmission = new PopupWinTransmission(this, PopupWinTransmission.TYPE_DOWNLOAD, findViewById(R.id.root_view));

        popupWinConfirmCancel = new PopupWinConfirmCancel(this, findViewById(R.id.root_view), new PopupWinConfirmCancel.OnCancelCallback() {
            @Override
            public void cancel() {
                Log.i("cancel", "preview");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("cancel", "start");
                        Log.i("cancel", "is on transferring " + isOnTransferring);
                        Log.i("cancel", "is client null?  " + (client == null));
                        if(isOnTransferring && client != null){
                            isOnTransferring = false;
                            try {
                                client.abortCurrentDataTransfer(true);

                                Log.i("cancel", "success stop");
                                if(locateFile.exists()){
                                    locateFile.delete();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (FTPIllegalReplyException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }


    @Override
    protected void onRestart() {
        super.onRestart();

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
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("balala", progress + "");
                popupWinTransmission.updateProgress(progress);
            }
        });
    }

    private void downloadFailedMsg(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(popupWinConfirmCancel.isShowing())
                    popupWinConfirmCancel.dismiss();

                if(popupWinTransmission.isShowing())
                    popupWinTransmission.dismiss();
                Toast.makeText(FTPDownloadPosSetActivity.this, "下载失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
