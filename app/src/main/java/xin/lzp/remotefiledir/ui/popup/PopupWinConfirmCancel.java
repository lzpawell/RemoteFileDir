package xin.lzp.remotefiledir.ui.popup;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import xin.lzp.remotefiledir.R;

/**
 * Created by lzp on 2017/10/10.
 * 确认取消对话框
 */

public class PopupWinConfirmCancel implements View.OnClickListener{
    private PopupWindow popupWindow;
    private OnCancelCallback callback;
    private View contentView;
    private View rootView;   //页面根视图， 用于作为popup上层
    private Activity activity;

    public PopupWinConfirmCancel(@NonNull Activity activity, @NonNull View rootView){
        this(activity, rootView, null);
    }

    public PopupWinConfirmCancel(@NonNull Activity activity, @NonNull View rootView, OnCancelCallback callback) {
        contentView = activity.getLayoutInflater().inflate(R.layout.popup_confirm_cancel, null);
        this.callback = callback;
        this.rootView = rootView;
        bindCallback();
        this.activity = activity;

        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), (Bitmap) null));
        popupWindow.setFocusable(false);
    }


    public void show(){
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }


    Button btnCancel;
    Button btnConfirm;
    private void bindCallback() {
        btnCancel = (Button) contentView.findViewById(R.id.btn_cancel);
        btnConfirm = (Button) contentView.findViewById(R.id.btn_confirm);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Log.i("balala", "click");

        if(v.getId() == R.id.btn_cancel && popupWindow.isShowing()){
            popupWindow.dismiss();
        }

        if(v.getId() == R.id.btn_confirm){
            popupWindow.dismiss();

            Log.i("balala", "confirm click");

            if(callback != null){
                callback.cancel();
                Log.i("balala", "callback is called");
            }
        }
    }

    public interface OnCancelCallback{
        void cancel();
    }


    public void setOnCancelCallback(OnCancelCallback callback){
        this.callback = callback;
    }

    public boolean isShowing(){
        return popupWindow.isShowing();
    }

    public void dismiss(){
        popupWindow.dismiss();
    }
}
