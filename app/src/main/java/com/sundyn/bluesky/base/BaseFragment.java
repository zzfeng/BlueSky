package com.sundyn.bluesky.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.MyApplication;
import com.sundyn.bluesky.utils.CustomProgressDialog;
import com.sundyn.bluesky.utils.CustomToast;
import com.sundyn.bluesky.view.pullrefreshview.PullToRefreshListView;

public abstract class BaseFragment extends Fragment {

    public View view;
    public Context mContext;
    public MyApplication mApplication;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        mApplication = (MyApplication) this.getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = initView(inflater);
        return view;
    }

    public View getRootView() {
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        initData(savedInstanceState);
        super.onActivityCreated(savedInstanceState);
    }

    public abstract View initView(LayoutInflater inflater);

    public abstract void initData(Bundle savedInstanceState);

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
        showDialog(getString(R.string.dialogProgress));
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

    public void setLastUpdateTime(PullToRefreshListView ptrLv) {
        String label = DateUtils.formatDateTime(mContext,
                System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL);
        ptrLv.setLastUpdatedLabel(label);
    }

}