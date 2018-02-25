package xin.lzp.remotefiledir.ui.popup;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import xin.lzp.remotefiledir.R;

/**
 * Created by lzp on 2017/8/23.
 */

public class PopupWinMkDir implements View.OnClickListener{

    private PopupWindow popupWindow;
    private OnGetDirNameCallback callback;
    private View contentView;
    private View rootView;   //页面根视图， 用于作为popup上层
    private Activity activity;

    public PopupWinMkDir(@NonNull Activity activity, @NonNull View rootView, @NonNull OnGetDirNameCallback callback) {
        contentView = activity.getLayoutInflater().inflate(R.layout.popup_mk_dir, null);
        this.callback = callback;
        this.rootView = rootView;
        bindCallback();
        this.activity = activity;

        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), (Bitmap) null));
    }

    private EditText edtDirName;


    public void show(){
        edtDirName.setText("");
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private void bindCallback() {
        edtDirName = (EditText) contentView.findViewById(R.id.edt_mk_dir);
        Button btnCancel = (Button) contentView.findViewById(R.id.btn_cancel);
        Button btnConfirm = (Button) contentView.findViewById(R.id.btn_confirm);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_cancel && popupWindow.isShowing()){
            popupWindow.dismiss();
        }

        if(v.getId() == R.id.btn_confirm){
            popupWindow.dismiss();
            callback.getDirName(edtDirName.getText().toString().trim());
        }
    }

    public interface OnGetDirNameCallback{
        void getDirName(String dirName);
    }
}
