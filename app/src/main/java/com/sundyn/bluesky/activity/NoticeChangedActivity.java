package com.sundyn.bluesky.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.ControlManBean;
import com.sundyn.bluesky.bean.GovernmentBean.Government;
import com.sundyn.bluesky.utils.CommonRq;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.utils.ImageUtils;
import com.sundyn.bluesky.utils.OptionsPickerViewUtils;
import com.sundyn.bluesky.utils.ReportUtils;
import com.sundyn.bluesky.view.FlowLayout;
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

public class NoticeChangedActivity extends BaseActivity implements
        OnClickListener {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.bt_commit)
    private Button mCommit;
    @ViewInject(R.id.id_tv_slectGovernment)
    private TextView tv_slectGovernment;
    @ViewInject(R.id.id_time)
    private TextView tv_time;
    @ViewInject(R.id.id_endtime)
    private TextView tv_endtime;
    @ViewInject(R.id.tv_SMan)
    private TextView tv_SMan;
    @ViewInject(R.id.id_tv_projectname)
    private TextView tv_projectname;
    @ViewInject(R.id.id_iv_search)
    private ImageView iv_search;
    @ViewInject(R.id.id_et_sman)
    private EditText et_sman;
    @ViewInject(R.id.id_et_No1)
    private EditText et_No1;
    @ViewInject(R.id.id_et_No2)
    private EditText et_No2;
    @ViewInject(R.id.tag_container)
    private FlowLayout tagContainer;// 巡查问题
    @ViewInject(R.id.imageGridView)
    private GridView imageGridView;
    private QuickAdapter<String> mImageAdapter;
    private TimePickerView pvTime;

    private ArrayList<ControlManBean.ControlMan> mControlManList;// 巡查人员

    private ArrayList<String> problemList;// 巡查问题
    private ArrayList<String> imageList;// 图片集合

    private String territorial;
    private CommonRq mConRq;// 共同请求体
    private SVProgressHUD mSVProgressHUD;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_noticechanged);
        x.view().inject(this);
        initTitleBar();
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        mCommit.setOnClickListener(this);
        tv_slectGovernment.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        tv_endtime.setOnClickListener(this);
    }

    private void initData() {
        pvTime = new TimePickerView(mContext,
                TimePickerView.Type.YEAR_MONTH_DAY);
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);

        problemList = ReportUtils.getReportUtils().getProblemCheckList();

        imageList = ReportUtils.getReportUtils().getImageList();
        mImageAdapter = new QuickAdapter<String>(mContext, R.layout.grid_item) {

            @Override
            protected void convert(BaseAdapterHelper helper, String item) {
                try {
                    helper.setImageBitmap(R.id.id_item_image,
                            ImageUtils.revitionImageSize(item));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        imageGridView.setAdapter(mImageAdapter);
        if (imageList != null)
            mImageAdapter.addAll(imageList);
    }

    private void initView() {
        tv_SMan.setText(mApplication.getUser().getUsername());
        tv_projectname.setText(ReportUtils.getReportUtils().getBsiteName());

        LayoutInflater mLayoutInflater = getLayoutInflater();
        if (problemList != null) {
            for (final String tag : problemList) {
                final TextView tagView;
                tagView = (TextView) mLayoutInflater.inflate(R.layout.tag,
                        tagContainer, false);
                tagView.setText(tag);
                tagView.setTextColor(mContext.getResources().getColor(
                        R.color.tag_font_unselect));
                tagView.setBackgroundDrawable(mContext.getResources()
                        .getDrawable(R.drawable.shape_tag));
                tagContainer.addView(tagView);
            }
        }
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                NoticeChangedActivity.this.finish();
            }
        });
        mTopBar.setTitle("整改通知");
        mTopBar.setActionTextVisibility(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_commit:
                commitNoticeChanged();
                break;
            case R.id.id_time:
                pvTime.setTime(new Date());
                pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

                    @Override
                    public void onTimeSelect(Date date) {
                        tv_time.setText(CommonUtil.formatDateOnlyMD(date, false));
                    }
                });
                pvTime.show();
                break;
            case R.id.id_endtime:
                pvTime.setTime(new Date());
                pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

                    @Override
                    public void onTimeSelect(Date date) {
                        tv_endtime.setText(CommonUtil.formatDateOnlyMD(date, true));
                    }
                });
                pvTime.show();
                break;
            case R.id.id_tv_slectGovernment:
                if (mConRq == null) {
                    mConRq = new CommonRq(mContext, mApplication);
                }
                mConRq.showSelectGovernment(new CommonRq.RqCallBack<Government>() {
                    @Override
                    public void initSelectData(Government data) {
                        tv_slectGovernment.setText(data.getPickerViewText());
                        territorial = data.Territorial;
                    }
                });
                break;
            case R.id.id_iv_search:
                if (mControlManList != null) {
                    showSelectMan(mControlManList);
                } else {
                    getSman();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 提交整改通知
     */
    private void commitNoticeChanged() {
        String checkTime = tv_time.getText().toString();
        String deadLineTime = tv_endtime.getText().toString();
        if (TextUtils.isEmpty(checkTime) || TextUtils.isEmpty(deadLineTime)
                || TextUtils.isEmpty(et_No1.getText().toString())
                || TextUtils.isEmpty(et_No2.getText().toString())) {
            showToast("请补全信息！");
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
                        NoticeChangedActivity.this.finish();
                        break;
                    case Faile:

                        break;
                    default:
                        break;
                }
            }
        });

        String checkMonth = checkTime.substring(0, checkTime.indexOf("月"));
        String checkDate = checkTime.substring(checkTime.indexOf("月") + 1,
                checkTime.indexOf("日"));
        String deadineYear = deadLineTime.substring(0,
                deadLineTime.indexOf("年"));
        String deadlineMonth = deadLineTime.substring(
                deadLineTime.indexOf("年") + 1, deadLineTime.indexOf("月"));
        String deadlineDate = deadLineTime.substring(
                deadLineTime.indexOf("月") + 1, deadLineTime.indexOf("日"));

        StringBuffer buffer = new StringBuffer();
        ArrayList<String> problemCheckList = ReportUtils.getReportUtils()
                .getProblemCheckList();
        if (problemCheckList != null) {
            for (int i = 0; i < problemCheckList.size(); i++) {
                buffer.append(problemCheckList.get(i));
                if (i != problemCheckList.size() - 1) {
                    buffer.append(",");
                }
            }
        }

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
                .url(Constant.BASE_URL + Constant.URL_ADDZGTZ)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("CheckDate", checkDate)
                .addParams("CheckMonth", checkMonth)
                .addParams("DeadineYear", deadineYear)
                .addParams("DeadlineDate", deadlineDate)
                .addParams("DeadlineMonth", deadlineMonth)
                .addParams("CheckTeam", mApplication.getUser().getUserNo())
                .addParams("CreateBy", mApplication.getUser().getUserNo())
                .addParams("GovernmentName", territorial)
                .addParams("No1", et_No1.getText().toString())
                .addParams("No2", et_No2.getText().toString())
                .addParams("PrjID", ReportUtils.getReportUtils().getBsiteNo())
                .addParams("PrjName",
                        ReportUtils.getReportUtils().getBsiteName())
                .addParams("SMan", et_sman.getText().toString())
                .addParams("fkID", 0 + "")
                .addParams("iType", 0 + "")
                .addParams("MDzgtzItem", buffer.toString())
                // 整改通知存在问题
                .addParams("UserName", mApplication.getUser().getUsername())
                .addParams("UserNo", mApplication.getUser().getUserNo())
                .addParams("MDzgtzImgs", imageBuffer.toString()).build()
                .execute(new StringCallback() {

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
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        disDIalog();
                        mSVProgressHUD.showErrorWithStatus("抱歉，提交失败!");
                    }
                });
    }

    /**
     * 选择巡检人员
     *
     * @param //mGovernmentList
     */
    private void showSelectMan(
            final ArrayList<ControlManBean.ControlMan> mControlManList) {

        OptionsPickerViewUtils.getOptionsPickerViewUtils().showSelectOptions(
                mContext, mControlManList, "请选择巡检人员",
                new OptionsPickerViewUtils.OptionsCallBack() {
                    @Override
                    public void onOptionsSelect(int options1, int option2,
                                                int options3) {
                        et_sman.setText(mControlManList.get(options1)
                                .getPickerViewText());
                    }
                });
    }

    /**
     * 获取巡检员名字
     */
    public void getSman() {
        OkHttpUtils.post().url(Constant.BASE_URL + Constant.URL_SELECTMAN)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken()).build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        if (!TextUtils.isEmpty(response)) {
                            ControlManBean controlManBean = GsonTools
                                    .changeGsonToBean(response,
                                            ControlManBean.class);
                            if (controlManBean.success) {
                                mControlManList = controlManBean.data;
                                showSelectMan(mControlManList);
                            }
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("获取数据失败");
                    }
                });
    }
}
