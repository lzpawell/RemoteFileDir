package xin.lzp.remotefiledir.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import xin.lzp.remotefiledir.R;
import xin.lzp.remotefiledir.ui.popup.PopupWinFileOptMenu;
import xin.lzp.remotefiledir.ui.popup.PopupWinRename;
import xin.lzp.remotefiledir.util.FileSizeFormat;
import xin.lzp.remotefiledir.util.RootFileDirGetter;

public class ListFileActivity extends AppCompatActivity {

    private LinkedList<File> fileList;
    private ListView listView;
    private TextView tvCurrentPath;
    private List<File> rootFileList;

    private PopupWinFileOptMenu popupWinFileOptMenu;
    private File currentFile;

    private PopupWinRename popupWinRename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_file);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");


        initView();
    }

    private void initView() {
        popupWinFileOptMenu = new PopupWinFileOptMenu(this, this.findViewById(R.id.root_view), PopupWinFileOptMenu.TYPE_UPLOAD, new PopupWinFileOptMenu.OnGetOptSelectedCallback() {
            @Override
            public void getOptSelected(final PopupWinFileOptMenu.Option option) {
                Toast.makeText(ListFileActivity.this, "balala " + option.toString(), Toast.LENGTH_SHORT).show();
                if(currentFile != null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            switch (option){
                                case OPT_DELETE:
                                    currentFile.delete();
                                    refreshList();
                                    break;

                                case OPT_UPLOAD:
                                    Intent intent = new Intent(ListFileActivity.this, FTPUploadActivity.class);
                                    intent.setData(android.net.Uri.fromFile(currentFile));
                                    startActivity(intent);
                                    finish();
                                    break;

                                case OPT_RENAME:
                                    popupWinRename.show();
                                    break;
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
                            currentFile.renameTo(new File(currentFile.getParent(), name));
                            refreshList();
                        }
                    });
                }
            }
        });



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
                currentFile = file;
                if(file.isDirectory()){
                    fileList.push(file);
                    FileListAdapter adapter = new FileListAdapter(ListFileActivity.this, Arrays.asList(file.listFiles()));
                    listView.setAdapter(adapter);

                    if(fileList.size() == 1){
                        tvCurrentPath.setText("/" + file.getName());
                    }else{
                        tvCurrentPath.setText(tvCurrentPath.getText() + "/" + file.getName());
                    }
                }else{
                    popupWinFileOptMenu.show();
                }
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                File file = (File) parent.getItemAtPosition(position);
                currentFile = file;
                popupWinFileOptMenu.show();
                return true;
            }
        });

    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    private class FileListAdapter extends BaseAdapter{

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

                if(fileList.size() != 0)
                    fileList.pop();


                if(fileList.size() == 0){
                    this.finish();
                }else{
                    adapter = new FileListAdapter(ListFileActivity.this, Arrays.asList(fileList.getLast().listFiles()));
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
                FileListAdapter adapter = new FileListAdapter(ListFileActivity.this, Arrays.asList(fileList.getFirst().listFiles()));
                listView.setAdapter(adapter);
            }
        });
    }


    //为网络异步请求提供的handler
    private android.os.Handler handler;
    private HandlerThread handlerThread;
    {
        handlerThread = new HandlerThread("balalaThread");
        handlerThread.start();
        handler = new android.os.Handler(handlerThread.getLooper());
    }

}
