package xin.lzp.remotefiledir.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPFile;
import xin.lzp.remotefiledir.R;
import xin.lzp.remotefiledir.util.FileSizeFormat;

/**
 * Created by lzp on 2017/9/3.
 */

public class FTPFileListAdapter extends BaseAdapter {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");


    private List<FTPFile> fileList;
    private LayoutInflater inflater;
    public FTPFileListAdapter(Activity activity, List<FTPFile> fileList){
        this.fileList = fileList;
        this.inflater = activity.getLayoutInflater();

        //对fileList进行重排序， 按照字母序
        Collections.sort(fileList, new Comparator<FTPFile>() {
            @Override
            public int compare(FTPFile o1, FTPFile o2) {
                return o1.getName().compareTo(o2.getName());
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
        private View vLine;

        public ViewHolder(View rootView){
            imageView = (ImageView) rootView.findViewById(R.id.img_header);
            textView = (TextView) rootView.findViewById(R.id.tv_file_name);
            tvTime = (TextView) rootView.findViewById(R.id.tv_time);
            tvSize = (TextView) rootView.findViewById(R.id.tv_file_size);
            vLine = rootView.findViewById(R.id.v_line);
        }

        public void setView(FTPFile file){
            if(file.getType() == FTPFile.TYPE_DIRECTORY){
                imageView.setImageResource(R.drawable.folder);
                tvSize.setText("");
                vLine.setVisibility(View.GONE);
            }else{
                imageView.setImageResource(R.drawable.file);
                tvSize.setText(FileSizeFormat.format(file.getSize()));
            }

            textView.setText(file.getName());
            tvTime.setText(dateFormat.format(file.getModifiedDate()));
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
