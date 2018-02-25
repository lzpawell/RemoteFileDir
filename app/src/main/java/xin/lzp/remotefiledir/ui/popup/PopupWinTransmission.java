package xin.lzp.remotefiledir.ui.popup;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import xin.lzp.remotefiledir.R;

/**
 * Created by lzp on 2017/10/10.
 * 用于显示传输时进度信息
 */

public class PopupWinTransmission{
    private PopupWindow popupWindow;
    private View contentView;
    private View rootView;   //页面根视图， 用于作为popup上层
    private Activity activity;
    private String type;

    private TextView tvProgressInfo;
    private ProgressBar progressBar;


    public static final String TYPE_UPDATE = "上传";
    public static final String TYPE_DOWNLOAD = "下载";

    public PopupWinTransmission(@NonNull Activity activity, @NonNull String type, @NonNull View rootView) {
        contentView = activity.getLayoutInflater().inflate(R.layout.popup_transmission, null);
        this.rootView = rootView;
        this.type = type;
        this.activity = activity;

        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), (Bitmap) null));

        //没有焦点的popupWindow不响应事件
        popupWindow.setFocusable(false);
        initView();
    }

    private void initView() {
        tvProgressInfo = (TextView) contentView.findViewById(R.id.tv_trans);
        progressBar = (ProgressBar) contentView.findViewById(R.id.progress_trans);

        tvProgressInfo.setText("正在" + type + "..." + 0 +"%");
    }


    public void show(){
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }


    public void updateProgress(int percentage){
        if(popupWindow.isShowing()){
            progressBar.setProgress(percentage);
            tvProgressInfo.setText("正在" + type + "..." + percentage +"%");
        }
    }

    public boolean isShowing(){
        return popupWindow.isShowing();
    }

    public void dismiss(){
        popupWindow.dismiss();
    }
}
