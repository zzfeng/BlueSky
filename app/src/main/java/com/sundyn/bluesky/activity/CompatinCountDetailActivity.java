package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.bean.AreaOfOld;
import com.sundyn.bluesky.bean.AreaOfOld.Region;
import com.sundyn.bluesky.bean.CompaintCountDetail;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.utils.ReportUtils;
import com.sundyn.bluesky.view.AlertDialog;
import com.sundyn.bluesky.view.FormDetailView;
import com.sundyn.bluesky.view.ListProblemPopupWindow;
import com.sundyn.bluesky.view.ListProblemPopupWindow.OnProblemSelected;
import com.sundyn.bluesky.view.NormalTopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class CompatinCountDetailActivity extends BaseActivity implements
        OnClickListener, OnProblemSelected {

    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.id_tv_beingtime)
    private FormDetailView tv_beingtime;
    @ViewInject(R.id.id_tv_endtime)
    private FormDetailView tv_endtime;//
    @ViewInject(R.id.id_tv_prjname)
    private FormDetailView tv_prjname;
    @ViewInject(R.id.id_tv_prjaddress)
    private FormDetailView tv_prjaddress;
    @ViewInject(R.id.id_tv_area)
    private FormDetailView tv_area;
    @ViewInject(R.id.id_tv_street)
    private FormDetailView tv_street;
    @ViewInject(R.id.id_tv_reportperople)
    private FormDetailView tv_reportperople;
    @ViewInject(R.id.id_tv_phone)
    private FormDetailView tv_phone;
    @ViewInject(R.id.id_tv_reportstatue)
    private FormDetailView tv_reportstatue;
    @ViewInject(R.id.id_tv_problem)
    private TextView tv_problem;
    @ViewInject(R.id.id_ll_publish)
    private LinearLayout ll_publish;
    @ViewInject(R.id.id_bt_reformnotice)
    private Button bt_reformnotice;
    @ViewInject(R.id.id_bt_supervisehandle)
    private Button bt_supervisehandle;
    @ViewInject(R.id.id_ll_pending)
    private LinearLayout ll_pending;
    @ViewInject(R.id.id_tv_prostatus)
    private TextView tv_prostatus;
    @ViewInject(R.id.id_bt_save)
    private TextView bt_save;
    private ListProblemPopupWindow mListProblemPopupWindow;

    public final static String ID = "id";
    public final static String COMPLAINTSTATUS = "complaintstatus";
    public final static String ARGNAME = "argname";

    private int prjID;
    private String mStatus;
    private String mArgname;
    private String prjno;
    private ArrayList<String> problemCheckList;

    private List<Region> mData;

    private String prjName;
    private String reason = "恶意投诉";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_compaintcount);
        x.view().inject(this);
        initData();
        initTitleBar();
        initEvent();
    }

    private void initEvent() {
        tv_prostatus.setOnClickListener(this);
        bt_save.setOnClickListener(this);
        bt_reformnotice.setOnClickListener(this);
        bt_supervisehandle.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            prjID = intent.getIntExtra(ID, 0);
            mStatus = intent.getStringExtra(COMPLAINTSTATUS);
            mArgname = intent.getStringExtra(ARGNAME);
        }

        if ("Ch01".equals(mStatus)) {
            ll_pending.setVisibility(View.VISIBLE);
        }
        if ("Ch80".equals(mStatus)) {
            ll_publish.setVisibility(View.GONE);
        }

        OkHttpUtils.get()
                .url(Constant.BASE_URL + Constant.URL_SELECTSINGLECOMPLAINT)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("id", prjID + "").build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int arg1) {
                        if (!TextUtils.isEmpty(response)) {
                            processData(response);
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("加载失败，请检查服务器连接");
                    }
                });

    }

    protected void processData(String response) {
        CompaintCountDetail mCompaintCountDetail = GsonTools.changeGsonToBean(
                response, CompaintCountDetail.class);
        if (mCompaintCountDetail.success) {
            if (mCompaintCountDetail.zgtzinfo.size() > 0) {
                CompaintCountDetail.CompaintDetailItem mCompaintDetailItem = mCompaintCountDetail.zgtzinfo
                        .get(0);
                tv_beingtime.updateText(mCompaintDetailItem.RectificationDate);
                tv_endtime.updateText(mCompaintDetailItem.zgRectificationDate);
                prjName = mCompaintDetailItem.PrjName;
                tv_prjname.updateText(prjName);
                tv_prjaddress.updateText(mCompaintDetailItem.PrjAddress);
                tv_area.updateText(mCompaintDetailItem.Territorial);
                tv_street.updateText(mCompaintDetailItem.Office);
                tv_reportperople.updateText(mCompaintDetailItem.ComplaintMan);
                tv_phone.updateText(mCompaintDetailItem.Mobile);
                tv_reportstatue.updateText(mCompaintDetailItem.ComplaintType);
                tv_problem.setText(mCompaintDetailItem.Contents);
                prjno = mCompaintDetailItem.PrjID;

            }
        } else {
            showToast(mCompaintCountDetail.message);
        }

    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CompatinCountDetailActivity.this.finish();
            }
        });
        mTopBar.setTitle(mArgname);
        mTopBar.setActionTextVisibility(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tv_prostatus:
                if (mData != null) {
                    showPopView(mData);
                } else {
                    getProblemStatus();
                }
                break;
            case R.id.id_bt_save:
                new AlertDialog(mContext).builder().setTitle("提示").setMsg("确定保存吗？")
                        .setPositiveButton("确定", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateComplaintSt();
                            }
                        }).setNegativeButton("取消", new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
                break;
            case R.id.id_bt_reformnotice:
                Intent intent = new Intent(mContext, NoticeHandleActivity.class);
                if (problemCheckList == null) {
                    problemCheckList = new ArrayList<String>();
                } else {
                    problemCheckList.clear();
                }
                problemCheckList.add(tv_problem.getText().toString());
                ReportUtils.getReportUtils().clear();
                ReportUtils.getReportUtils().setProblemCheckList(problemCheckList);
                ReportUtils.getReportUtils().setBsiteName(
                        tv_prjname.getText().toString());
                ReportUtils.getReportUtils().setBsiteNo(prjno + "");
                ReportUtils.getReportUtils().setDescribe(
                        tv_problem.getText().toString());

                startActivity(intent);

                break;
            case R.id.id_bt_supervisehandle:

                Intent noticeChangedIntent = new Intent(mContext,
                        NoticeChangedActivity.class);
                if (problemCheckList == null) {
                    problemCheckList = new ArrayList<String>();
                } else {
                    problemCheckList.clear();
                }
                problemCheckList.add(tv_problem.getText().toString());
                ReportUtils.getReportUtils().clear();
                ReportUtils.getReportUtils().setProblemCheckList(problemCheckList);
                ReportUtils.getReportUtils().setBsiteName(
                        tv_prjname.getText().toString());
                ReportUtils.getReportUtils().setBsiteNo(prjno + "");
                ReportUtils.getReportUtils().setDescribe(
                        tv_problem.getText().toString());

                startActivity(noticeChangedIntent);

                break;

            default:
                break;
        }
    }

    private void showPopView(List<Region> data) {
        mListProblemPopupWindow = new ListProblemPopupWindow(
                tv_prostatus.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT,
                data, LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.compaintreson, null));

        mListProblemPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListProblemPopupWindow.setOnProblemSelected(this);
        mListProblemPopupWindow.setAnimationStyle(R.style.dialogWindowAnimScale);
        mListProblemPopupWindow.showAsDropDown(tv_prostatus, 0, 0);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = .3f;
        getWindow().setAttributes(lp);
    }

    private void getProblemStatus() {
        OkHttpUtils.get()
                .url(Constant.BASE_URL + Constant.URL_SELECTCOMPLAINTSTATUS)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken()).build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int arg1) {
                        if (!TextUtils.isEmpty(response)) {
                            AreaOfOld allArea = GsonTools.changeGsonToBean(
                                    response, AreaOfOld.class);
                            mData = allArea.data;
                            showPopView(mData);
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("加载失败，请检查服务器连接");
                    }
                });
    }

    private void updateComplaintSt() {
        OkHttpUtils.get()
                .url(Constant.BASE_URL + Constant.URL_UPDATECOMPLAINTST)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("prjName", prjName)
                .addParams("complaintStatus", mStatus)
                .addParams("reason", reason).build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int arg1) {
                        if (!TextUtils.isEmpty(response)) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject
                                        .getBoolean("success");
                                if (success) {
                                    showToast("保存成功！");
                                    CompatinCountDetailActivity.this.finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("保存失败！");
                    }
                });
    }

    @Override
    public void selected(Region region) {
        reason = region.argName;
        tv_prostatus.setText(reason);
        mListProblemPopupWindow.dismiss();
    }
}
