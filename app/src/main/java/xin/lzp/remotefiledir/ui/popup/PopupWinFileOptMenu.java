package xin.lzp.remotefiledir.ui.popup;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import xin.lzp.remotefiledir.R;

import static xin.lzp.remotefiledir.ui.popup.PopupWinFileOptMenu.Option.OPT_DELETE;
import static xin.lzp.remotefiledir.ui.popup.PopupWinFileOptMenu.Option.OPT_RENAME;
import static xin.lzp.remotefiledir.ui.popup.PopupWinFileOptMenu.Option.OPT_UPLOAD;

/**
 * Created by lzp on 2017/8/24.
 */

public class PopupWinFileOptMenu implements View.OnClickListener{
    private PopupWindow popupWindow;
    private OnGetOptSelectedCallback callback;
    private View contentView;
    private View rootView;   //页面根视图， 用于作为popup上层
    private Activity activity;

    public static final int TYPE_UPLOAD = 0;
    public static final int TYPE_DOWNLOAD = 1;

    private int type;

    public PopupWinFileOptMenu(@NonNull Activity activity, @NonNull View rootView, int type, @NonNull OnGetOptSelectedCallback callback) {
        contentView = activity.getLayoutInflater().inflate(R.layout.popup_file_opt_menu, null);
        this.callback = callback;
        this.rootView = rootView;
        this.activity = activity;
        this.type = type;

        bindCallback();

        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), (Bitmap) null));
    }



    public void show(){
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private void bindCallback() {
        Button btnLoad;
        if (type == TYPE_DOWNLOAD){
            btnLoad = (Button) contentView.findViewById(R.id.btn_upload);
            btnLoad.setVisibility(View.GONE);
            btnLoad = (Button) contentView.findViewById(R.id.btn_download);
        }else{
            btnLoad = (Button) contentView.findViewById(R.id.btn_download);
            btnLoad.setVisibility(View.GONE);
            btnLoad = (Button) contentView.findViewById(R.id.btn_upload);
        }

        Button btnDelete = (Button) contentView.findViewById(R.id.btn_delete);
        Button btnRename = (Button) contentView.findViewById(R.id.btn_rename);

        btnLoad.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnRename.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Option option = null;
        switch (v.getId()){
            case R.id.btn_download:
                option = Option.OPT_DOWNLOAD;
                break;
            case R.id.btn_upload:
                option = OPT_UPLOAD;
                break;
            case R.id.btn_delete:
                option = OPT_DELETE;
                break;
            case R.id.btn_rename:
                option = OPT_RENAME;
                break;
            default:
                break;
        }

        if(option != null){
            popupWindow.dismiss();
            callback.getOptSelected(option);
        }
    }

    public interface OnGetOptSelectedCallback{
        void getOptSelected(Option option);
    }

    public enum Option{
        OPT_RENAME,
        OPT_DELETE,
        OPT_DOWNLOAD,
        OPT_UPLOAD
    }
}
