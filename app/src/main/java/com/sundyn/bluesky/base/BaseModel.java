package com.sundyn.bluesky.base;

import android.app.Activity;
import android.content.Context;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.MyApplication;
import com.sundyn.bluesky.utils.CustomProgressDialog;
import com.sundyn.bluesky.utils.CustomToast;

/**
 * @author yangjl
 * @ʱ�� 2016-9-2����5:38:07
 * @�汾 1.0
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
     * ��ʾ�Զ�����˾
     */
    public void showToast(String msg) {
        showToast(msg, 0);
    }

    public void showToast(String msg, int time) {
        CustomToast customToast = new CustomToast(mContext, msg, time);
        customToast.show();
    }

    /**
     * ��ʾ�Զ��������
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
     * ȡ��������
     */
    public void disDIalog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
