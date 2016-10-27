package com.sundyn.bluesky.base;

import android.app.Activity;
import android.content.Context;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.MyApplication;
import com.sundyn.bluesky.utils.CustomProgressDialog;
import com.sundyn.bluesky.utils.CustomToast;

/**
 * @author yangjl
 * @时间 2016-9-2下午5:38:07
 * @版本 1.0
 */
public class BaseModel {
    private Context mContext;
    public MyApplication mApplication;

    public BaseModel(Context ctx) {
        this.mContext = ctx;
        Activity act = (Activity) ctx;
        mApplication = (MyApplication) act.getApplication();
    }

    /**
     * 显示自定义吐司
     */
    public void showToast(String msg) {
        showToast(msg, 0);
    }

    public void showToast(String msg, int time) {
        CustomToast customToast = new CustomToast(mContext, msg, time);
        customToast.show();
    }

    /**
     * 显示自定义进度条
     *
     * @param content
     */
    private CustomProgressDialog dialog;

    public void showDialog(String content) {
        dialog = new CustomProgressDialog(mContext, content);
        dialog.show();
    }

    public void showDialog() {
        showDialog(mContext.getString(R.string.dialogProgress));
        dialog.show();
    }

    /**
     * 取消进度条
     */
    public void disDIalog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
