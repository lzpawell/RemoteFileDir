package xin.lzp.remotefiledir.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import xin.lzp.remotefiledir.R;
import xin.lzp.remotefiledir.controller.FileTransController;
import xin.lzp.remotefiledir.model.FileTransStatus;
import xin.lzp.remotefiledir.util.FileHeaderMatcher;
import xin.lzp.remotefiledir.util.FileSizeFormat;

/**
 * Created by lzp on 2017/9/22.
 */

public class TransFileItemAdapter extends BaseAdapter {

    List<FileTransController> controllers;

    private LayoutInflater inflater;


    public TransFileItemAdapter(Activity activity, List<FileTransController> controllers){
        this.controllers = controllers;
        this.inflater = activity.getLayoutInflater();
    }

    private static class ViewHolder{
        private TextView tvFileName;
        private TextView tvFileTotalLength;
        private TextView tvFileCurrentLength;
        private TextView tvCurrentSpeed;
        private ProgressBar progressBar;
        private ImageView imgStatus;
        private ImageView imgFileType;

        public ViewHolder(View rootView){
            tvFileName = (TextView) rootView.findViewById(R.id.tv_file_name);
            tvFileCurrentLength = (TextView) rootView.findViewById(R.id.tv_current_length);
            tvFileTotalLength = (TextView) rootView.findViewById(R.id.tv_total_length);
            tvCurrentSpeed = (TextView) rootView.findViewById(R.id.tv_current_speed);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progress_trans);
            imgStatus = (ImageView) rootView.findViewById(R.id.v_status);
            imgFileType = (ImageView) rootView.findViewById(R.id.img_header);
        }


        public void setView(final FileTransController controller){
            final FileTransStatus status = controller.getInfo();
            tvFileName.setText(status.getFileName());
            tvFileCurrentLength.setText(FileSizeFormat.format(status.getCurrentLength()));
            tvFileTotalLength.setText(FileSizeFormat.format(status.getTotalLength()));
            tvCurrentSpeed.setText(status.getSpeed());
            progressBar.setProgress(status.getCurrentPercentage());


            int imgResource = 0;

            switch (status.getState()){
                case downloading:
                    imgResource = R.drawable.trans_stop;
                    break;

                case uploading:
                    imgResource = R.drawable.trans_stop;
                    break;

                case stop_download:
                    imgResource = R.drawable.trans_download;
                    break;

                case stop_upload:
                    imgResource = R.drawable.trans_upload;
                    break;
            }
            imgStatus.setImageResource(imgResource);


            imgFileType.setImageResource(FileHeaderMatcher.match(status.getFileName()));



            imgStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (status.getState()){
                        case downloading:
                        case uploading:
                            controller.stop();
                            break;

                        case stop_download:
                        case stop_upload:
                            controller.start();
                            break;
                    }
                }
            });
        }
    }

    @Override
    public int getCount() {
        return controllers.size();
    }

    @Override
    public Object getItem(int position) {
        return controllers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item_trans_control, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.setView(controllers.get(position));

        return convertView;
    }
}
