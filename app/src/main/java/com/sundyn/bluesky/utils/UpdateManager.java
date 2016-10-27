package com.sundyn.bluesky.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.sundyn.bluesky.bean.UpdateBean;
import com.sundyn.bluesky.view.AlertDialog;
import com.sundyn.bluesky.view.svprogresshud.SVProgressHUD;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.util.LogUtil;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;

public class UpdateManager {
    private FragmentActivity mContext;
    private double versionCode;
    private String url;
    private String versionName;
    private CircleProgressDialog mCircleProgress;
    /* 下载保存路径 */
    private String mSavePath;
    private boolean showInfo;
    private final String appName = "BlueSky.apk";
    private SVProgressHUD mSVProgressHUD;

    public UpdateManager(Context context) {
        this.mContext = (FragmentActivity) context;
    }

    /**
     * 检测软件更新
     */
    public void checkUpdate(boolean showInfo) {
        this.showInfo = showInfo;
        getVersionFromServer();
    }

    private boolean isUpdate() {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(
                    mContext.getPackageName(), 0);
            int versionCodeNow = packageInfo.versionCode;
            if (versionCode > versionCodeNow) {
                return true;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private void getVersionFromServer() {
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_VALIDAPPVERSION)
                .build().execute(new StringCallback() {

            @Override
            public void onResponse(String response, int arg1) {
                LogUtil.i(response);
                UpdateBean mUpdateData = GsonTools.changeGsonToBean(
                        response, UpdateBean.class);
                if (mUpdateData.success) {
                    versionCode = Double.valueOf(mUpdateData.version);
                    url = mUpdateData.url;
                    versionName = mUpdateData.name;
                    if (isUpdate()) {
                        new AlertDialog(mContext)
                                .builder()
                                .setTitle("提示")
                                .setMsg("发现新版本")
                                .setPositiveButton("立即升级",
                                        new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDownloadDialog();
                                            }
                                        })
                                .setNegativeButton("取消",
                                        new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        }).show();
                    } else {
                        if (showInfo) {
                            if (mSVProgressHUD == null)
                                mSVProgressHUD = new SVProgressHUD(
                                        mContext);
                            if (mSVProgressHUD.isShowing()) {
                                return;
                            }
                            mSVProgressHUD
                                    .showInfoWithStatus(
                                            "当前已是最新版本",
                                            SVProgressHUD.SVProgressHUDMaskType.None);
                        }
                    }
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
            }
        });
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        mCircleProgress = new CircleProgressDialog(mContext);
        mCircleProgress.show();
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String sdpath = Environment.getExternalStorageDirectory()
                    + File.separator;
            mSavePath = sdpath + "BlueSky/app";
            OkHttpUtils.get().addHeader("Accept-Encoding", "identity").url(url)
                    .build().execute(new FileCallBack(mSavePath, appName) {

                @Override
                public void onBefore(Request request, int id) {
                }

                @Override
                public void inProgress(float progress, long total,
                                       int id) {
                    if (mCircleProgress != null)
                        mCircleProgress
                                .setProgressNum((int) (100 * progress));
                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    CustomToast customToast = new CustomToast(mContext,
                            "下载失败！", 0);
                    customToast.show();
                }

                @Override
                public void onResponse(File file, int id) {
                    if (mCircleProgress != null) {
                        mCircleProgress.dismiss();
                    }
                    installApk();
                }
            });
        }
    }

    /**
     * 安装APK文件
     */

    private void installApk() {
        File apkfile = new File(mSavePath, appName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }

}
