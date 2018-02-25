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
 * Created by lzp on 2017/8/24.
 */

public class PopupWinRename implements View.OnClickListener{
    private PopupWindow popupWindow;
    private OnGetNameCallback callback;
    private View contentView;
    private View rootView;   //页面根视图， 用于作为popup上层
    private Activity activity;

    public PopupWinRename(@NonNull Activity activity, @NonNull View rootView, @NonNull OnGetNameCallback callback) {
        contentView = activity.getLayoutInflater().inflate(R.layout.popup_rename, null);
        this.callback = callback;
        this.rootView = rootView;
        bindCallback();
        this.activity = activity;

        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), (Bitmap) null));
    }

    private EditText edtName;


    public void show(){
        edtName.setText("");
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private void bindCallback() {
        edtName = (EditText) contentView.findViewById(R.id.edt_mk_dir);
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
            callback.getName(edtName.getText().toString().trim());
        }
    }

    public interface OnGetNameCallback{
        void getName(String name);
    }
}
