package com.sundyn.bluesky.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.MyApplication;
import com.sundyn.bluesky.utils.AppManager;
import com.sundyn.bluesky.utils.CustomProgressDialog;
import com.sundyn.bluesky.utils.CustomToast;
import com.sundyn.bluesky.view.pullrefreshview.PullToRefreshListView;


public class BaseActivity extends FragmentActivity {
    public Context mContext;
    public MyApplication mApplication;

    @Override
    protected void onCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        mContext = this;
        mApplication = (MyApplication) getApplication();
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
		 * overridePendingTransition(android.R.anim.fade_in,
		 * android.R.anim.fade_out);
		 */
    }

    @Override
    protected void onDestroy() {
        AppManager.getAppManager().finishActivity(this);
        super.onDestroy();
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
        showDialog(getString(R.string.dialogProgess));
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

    public void hideKeyborad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive())
            imm.hideSoftInputFromWindow(getWindow().getDecorView()
                    .getWindowToken(), 0);

    }

    public void setLastUpdateTime(PullToRefreshListView ptrLv) {
        String label = DateUtils.formatDateTime(mContext,
                System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL);
        ptrLv.setLastUpdatedLabel(label);
    }

}
