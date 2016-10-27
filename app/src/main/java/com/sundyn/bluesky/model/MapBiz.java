package com.sundyn.bluesky.model;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.MyApplication;
import com.sundyn.bluesky.utils.CustomProgressDialog;

public abstract class MapBiz {
    public Context mContext;
    protected MyApplication mApplication;
    protected BaiduMap mBaiduMap;

    public MapBiz(Context mContext, MyApplication mApplication,
                  BaiduMap mBaiduMap) {
        this.mContext = mContext;
        this.mApplication = mApplication;
        this.mBaiduMap = mBaiduMap;
    }

    public abstract void getMapData();

    public abstract void showOfMap();

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
        showDialog(mContext.getString(R.string.dialogProgess));
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
