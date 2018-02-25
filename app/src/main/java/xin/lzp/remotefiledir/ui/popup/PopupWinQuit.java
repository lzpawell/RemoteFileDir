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

/**
 * Created by lzp on 2017/9/3.
 */

public class PopupWinQuit implements View.OnClickListener{
    private PopupWindow popupWindow;
    private OnQuitCallback callback;
    private View contentView;
    private View rootView;   //页面根视图， 用于作为popup上层
    private Activity activity;

    public PopupWinQuit(@NonNull Activity activity, @NonNull View rootView, @NonNull OnQuitCallback callback) {
        contentView = activity.getLayoutInflater().inflate(R.layout.popup_quit, null);
        this.callback = callback;
        this.rootView = rootView;
        bindCallback();
        this.activity = activity;

        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), (Bitmap) null));
    }


    public void show(){
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private void bindCallback() {
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
            callback.quit();
        }
    }

    public interface OnQuitCallback{
        void quit();
    }
}
