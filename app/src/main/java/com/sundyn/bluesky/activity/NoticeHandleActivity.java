package com.sundyn.bluesky.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.bean.Territorial;
import com.sundyn.bluesky.bean.Territorial.TerritorialItem;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.utils.ImageUtils;
import com.sundyn.bluesky.utils.OptionsPickerViewUtils;
import com.sundyn.bluesky.utils.ReportUtils;
import com.sundyn.bluesky.view.NormalTopBar;
import com.sundyn.bluesky.view.pickerview.TimePickerView;
import com.sundyn.bluesky.view.svprogresshud.OnDismissListener;
import com.sundyn.bluesky.view.svprogresshud.SVProgressHUD;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;

public class NoticeHandleActivity extends BaseActivity implements
        OnClickListener {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.id_tv_publishtime)
    private TextView tv_publishtime;
    @ViewInject(R.id.id_tv_endtime)
    private TextView tv_endtime;
    @ViewInject(R.id.id_tv_prj)
    private TextView tv_prj;
    @ViewInject(R.id.id_tv_territorial)
    private TextView tv_territorial;
    @ViewInject(R.id.id_et_tcontent)
    private EditText et_tcontent;
    @ViewInject(R.id.extraMessage)
    private EditText extraMessage;
    @ViewInject(R.id.bt_commit)
    private Button bt_commit;

    private TimePickerView pvTime;
    private String territorial;
    private SVProgressHUD mSVProgressHUD;

    private ArrayList<TerritorialItem> mtTerritorialList;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_noticehandle);
        x.view().inject(this);
        initTitleBar();
        initEvent();
        initData();
    }

    private void initData() {
        pvTime = new TimePickerView(mContext,
                TimePickerView.Type.YEAR_MONTH_DAY);
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);

        tv_prj.setText(ReportUtils.getReportUtils().getBsiteName());
    }

    private void initEvent() {
        tv_publishtime.setOnClickListener(this);
        tv_endtime.setOnClickListener(this);
        tv_territorial.setOnClickListener(this);
        bt_commit.setOnClickListener(this);
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                NoticeHandleActivity.this.finish();
            }
        });
        mTopBar.setTitle("督办催办通知");
        mTopBar.setActionTextVisibility(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tv_publishtime:
                pvTime.setTime(new Date());
                pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

                    @Override
                    public void onTimeSelect(Date date) {
                        tv_publishtime.setText(CommonUtil.formatDateNoMinute(date));
                    }
                });
                pvTime.show();
                break;
            case R.id.id_tv_endtime:
                pvTime.setTime(new Date());
                pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

                    @Override
                    public void onTimeSelect(Date date) {
                        tv_endtime.setText(CommonUtil.formatDateNoMinute(date));
                    }
                });
                pvTime.show();
                break;
            case R.id.id_tv_territorial:
                if (mtTerritorialList == null) {
                    selectTerritorial();
                } else {
                    showSelectGovernment();
                }
                break;
            case R.id.bt_commit:
                commitNoticeChanged();
                break;

            default:
                break;
        }
    }

    private void selectTerritorial() {
        OkHttpUtils.post()
                .url(Constant.BASE_URL + Constant.URL_SELECTTERRITORIAL)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken()).build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        if (!TextUtils.isEmpty(response)) {
                            Territorial mtTerritorial = GsonTools
                                    .changeGsonToBean(response,
                                            Territorial.class);
                            if (mtTerritorial.success) {
                                mtTerritorialList = mtTerritorial.data;
                                showSelectGovernment();
                            }
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                    }
                });
    }

    protected void showSelectGovernment() {
        OptionsPickerViewUtils.getOptionsPickerViewUtils().showSelectOptions(
                mContext, mtTerritorialList, "请选择区域",
                new OptionsPickerViewUtils.OptionsCallBack() {
                    @Override
                    public void onOptionsSelect(int options1, int option2,
                                                int options3) {
                        territorial = mtTerritorialList.get(options1).argName;
                        tv_territorial.setText(territorial);
                    }
                });
    }

    private void commitNoticeChanged() {
        String publishDate = tv_publishtime.getText().toString();
        String rectificationDate = tv_endtime.getText().toString();
        String prjName = tv_prj.getText().toString();
        String area = tv_territorial.getText().toString();
        String tContent = "";
        String tContentHint = et_tcontent.getHint().toString();
        if (!TextUtils.isEmpty(tContentHint)) {
            tContent = tContentHint;
        } else {
            tContent = et_tcontent.getText().toString();
        }
        if (TextUtils.isEmpty(publishDate)
                || TextUtils.isEmpty(rectificationDate)
                || TextUtils.isEmpty(prjName) || TextUtils.isEmpty(area)
                || TextUtils.isEmpty(tContent)) {
            showToast("请补全信息进行提交!");
            return;
        }

        showDialog("正在提交...");
        mSVProgressHUD = new SVProgressHUD(this);
        mSVProgressHUD.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(SVProgressHUD.SVProgressHUDMessageType tag) {
                switch (tag) {
                    case Success:
                        ReportUtils.getReportUtils().clear();
                        NoticeHandleActivity.this.finish();
                        break;
                    case Faile:

                        break;
                    default:
                        break;
                }
            }
        });

        StringBuffer imageBuffer = new StringBuffer();
        ArrayList<String> imageList = ReportUtils.getReportUtils()
                .getImageList();
        try {
            if (imageList != null) {
                for (int i = 0; i < imageList.size(); i++) {
                    imageBuffer.append(ImageUtils.getImageUtils()
                            .Bitmap2StrByBase64(
                                    ImageUtils.revitionImageSize(imageList
                                            .get(i))));
                    if (i != imageList.size() - 1) {
                        imageBuffer.append(",");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        OkHttpUtils
                .post()
                .url(Constant.BASE_URL + Constant.URL_ADDSUPERVISIONNOTICE)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("CreateBy", mApplication.getUser().getUserNo())
                .addParams("Item", extraMessage.getText().toString())
                //
                .addParams("PrjID", ReportUtils.getReportUtils().getBsiteNo())
                //
                .addParams("PrjName",
                        ReportUtils.getReportUtils().getBsiteName())//
                .addParams("PublishDate", publishDate)//
                .addParams("RectificationDate", rectificationDate)//
                .addParams("TContent", tContent)//
                .addParams("Territorial", territorial)//
                .addParams("MDnoticeImgs", imageBuffer.toString())//
                .build().execute(new StringCallback() {

            @Override
            public void onResponse(String response, int arg1) {
                LogUtil.i(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        disDIalog();
                        mSVProgressHUD
                                .showSuccessWithStatus("恭喜，提交成功！");
                    } else {
                        disDIalog();
                        mSVProgressHUD.showErrorWithStatus("抱歉，提交失败!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    disDIalog();
                    mSVProgressHUD.showErrorWithStatus("抱歉，提交失败!");
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                disDIalog();
                mSVProgressHUD.showErrorWithStatus("抱歉，提交失败!");
            }
        });
    }
}
