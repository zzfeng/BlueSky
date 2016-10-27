package com.sundyn.bluesky.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.AllProjectActivity;
import com.sundyn.bluesky.activity.CheckCountActivity;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.utils.Constant;
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

import java.util.Date;

import okhttp3.Call;

public class AddCheckRecordFra extends BaseFragment implements OnClickListener {
    private View view;
    @ViewInject(R.id.id_timepicker)
    private RelativeLayout timepicker;
    @ViewInject(R.id.id_tv_time)
    private TextView tv_time;
    @ViewInject(R.id.id_tv_projectname)
    private TextView tv_projectname;
    @ViewInject(R.id.id_et_dec)
    private EditText et_dec;
    @ViewInject(R.id.id_bt_save)
    private Button bt_save;

    private TimePickerView pvTime;

    private static final int SELECT_PRJ = 0x000002;
    private int prjno;// 工地id
    private String prjname;// 工地名称
    private SVProgressHUD mSVProgressHUD;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fra_addcheckrecord, null);
        x.view().inject(this, view); // 注入view和事件
        initEvent();
        return view;
    }

    private void initEvent() {
        timepicker.setOnClickListener(this);
        tv_projectname.setOnClickListener(this);
        bt_save.setOnClickListener(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_timepicker:
                if (pvTime == null) {
                    pvTime = new TimePickerView(mContext,
                            TimePickerView.Type.YEAR_MONTH_DAY);
                }
                pvTime.setTime(new Date());
                pvTime.setCyclic(false);
                pvTime.setCancelable(true);
                // 时间选择后回调
                pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

                    @Override
                    public void onTimeSelect(Date date) {
                        tv_time.setText(CommonUtil.formatDateNoMinute(date));
                    }
                });
                pvTime.show();
                break;
            case R.id.id_tv_projectname:
                Intent intent = new Intent(mContext, AllProjectActivity.class);
                AddCheckRecordFra.this.startActivityForResult(intent, SELECT_PRJ);
                break;
            case R.id.id_bt_save:
                addCheckRecord();
                break;
            default:
                break;
        }
    }

    private void addCheckRecord() {
        String checkTime = tv_time.getText().toString();
        String prjname = tv_projectname.getText().toString();
        if (TextUtils.isEmpty(checkTime) || TextUtils.isEmpty(prjname)) {
            showToast("请补全信息之后提交！");
            return;
        }

        showDialog("正在提交...");
        if (mSVProgressHUD == null)
            mSVProgressHUD = new SVProgressHUD(mContext);
        mSVProgressHUD.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(SVProgressHUD.SVProgressHUDMessageType tag) {
                switch (tag) {
                    case Success:
                        ((CheckCountActivity) getActivity()).clickBack();
                        break;
                    case Faile:

                        break;
                    default:
                        break;
                }
            }
        });

        OkHttpUtils.post().url(Constant.BASE_URL + Constant.URL_ADDCHECKRECORD)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("CheckDate", checkTime)
                .addParams("PrjID", prjno + "").addParams("PrjName", prjname)
                .addParams("Descrip", et_dec.getText().toString())
                .addParams("UserNo", mApplication.getUser().getUserNo())
                .addParams("MDnoticeImgs", "").build()
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
                        disDIalog();
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        disDIalog();
                        mSVProgressHUD.showErrorWithStatus("抱歉，提交失败!");
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PRJ:
                if (data != null) {
                    prjname = data.getStringExtra("prjname");
                    prjno = data.getIntExtra("prjno", 0);
                    tv_projectname.setText(prjname);
                }
        }
    }

}
