package com.sundyn.bluesky.utils;

import android.content.Context;
import android.text.TextUtils;

import com.sundyn.bluesky.activity.MyApplication;
import com.sundyn.bluesky.bean.GovernmentBean;
import com.sundyn.bluesky.bean.GovernmentBean.Government;
import com.sundyn.bluesky.bean.OfficeBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * @author yangjl
 * @时间 2016-8-17下午3:13:27
 * @版本 1.0
 * @描述 共同请求的工具类 比如选择区政府等
 */
public class CommonRq {
    private RqCallBack callBack;

    private ArrayList<Government> mGovernmentList;// 区政府选择
    private ArrayList<OfficeBean.Office> mOfficeList;// 区政府选择
    private MyApplication mApplication;
    private Context mContext;

    public CommonRq(Context mContext, MyApplication mApplication) {
        this.mApplication = mApplication;
        this.mContext = mContext;
    }

    public interface RqCallBack<T> {
        void initSelectData(T data);
    }

    /**
     * 选择区政府界面
     *
     * @param //mGovernmentList
     */
    public void showSelectGovernment(RqCallBack rqCallBack) {
        this.callBack = rqCallBack;

        if (mGovernmentList != null) {
            OptionsPickerViewUtils.getOptionsPickerViewUtils()
                    .showSelectOptions(mContext, mGovernmentList, "请选择区政府",
                            new OptionsPickerViewUtils.OptionsCallBack() {
                                @Override
                                public void onOptionsSelect(int options1,
                                                            int option2, int options3) {
                                    if (callBack != null) {
                                        callBack.initSelectData(mGovernmentList
                                                .get(options1));
                                    }
                                }
                            });
        } else {
            getGovernment();
        }
    }

    /**
     * 获取区政府管委
     */
    private void getGovernment() {

        OkHttpUtils.post()
                .url(Constant.BASE_URL + Constant.URL_SELECTGOVERNMENT)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken()).build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        if (!TextUtils.isEmpty(response)) {
                            GovernmentBean governmentBean = GsonTools
                                    .changeGsonToBean(response,
                                            GovernmentBean.class);
                            if (governmentBean.success) {
                                mGovernmentList = governmentBean.data;
                                showSelectGovernment(callBack);
                            }
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                    }
                });
    }

    /* 选择办事处 */
    public void selectOffice(RqCallBack rqCallBack) {
        this.callBack = rqCallBack;
        if (mOfficeList != null) {
            OptionsPickerViewUtils.getOptionsPickerViewUtils()
                    .showSelectOptions(mContext, mOfficeList, "请选择办事处",
                            new OptionsPickerViewUtils.OptionsCallBack() {
                                @Override
                                public void onOptionsSelect(int options1,
                                                            int option2, int options3) {
                                    if (callBack != null) {
                                        callBack.initSelectData(mOfficeList
                                                .get(options1));
                                    }
                                }
                            });
        } else {
            getOffice();
        }
    }

    /* 获取办事处 */
    private void getOffice() {
        OkHttpUtils.post().url(Constant.BASE_URL + Constant.URL_SELECTOFFICE)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("userNo", mApplication.getUser().getUserNo())
                .build().execute(new StringCallback() {

            @Override
            public void onResponse(String response, int arg1) {
                LogUtil.i(response);
                if (!TextUtils.isEmpty(response)) {
                    OfficeBean mOfficeBean = GsonTools
                            .changeGsonToBean(response,
                                    OfficeBean.class);
                    if (mOfficeBean.success) {
                        mOfficeList = mOfficeBean.data;
                        selectOffice(callBack);
                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
            }
        });
    }

    /**
     * 显示自定义吐司
     */
    public void showToast(String msg) {
        CustomToast customToast = new CustomToast(mContext, msg, 0);
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

    /**
     * 取消进度条
     */
    public void disDIalog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
