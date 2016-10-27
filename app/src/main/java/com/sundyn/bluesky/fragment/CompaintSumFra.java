package com.sundyn.bluesky.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.view.VerticalText;
import com.sundyn.bluesky.view.pickerview.TimePickerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Date;

import okhttp3.Call;

/**
 * @author yangjl
 * @date 2016-8-10上午10:15:41
 * @版本：1.0
 * @描述：投诉汇总数量查询
 */
public class CompaintSumFra extends BaseFragment implements OnClickListener {

    private View view;
    @ViewInject(R.id.id_bt_stardate)
    private Button bt_stardate;
    @ViewInject(R.id.id_bt_enddate)
    private Button bt_enddate;
    @ViewInject(R.id.id_tv_tscount)
    private TextView tv_tscount;
    @ViewInject(R.id.id_tv_tsch01)
    private VerticalText tv_tsch01;
    @ViewInject(R.id.id_tv_tsch02)
    private VerticalText tv_tsch02;
    @ViewInject(R.id.id_tv_tsch80)
    private VerticalText tv_tsch80;
    @ViewInject(R.id.id_tv_tskoukuan)
    private TextView tv_tskoukuan;

    private String areaCode;
    private TimePickerView pvTime;
    private String dpStartTime;
    private String dpEndTime;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fra_compaintsum, null);
        x.view().inject(this, view); // 注入view和事件
        initEvent();
        return view;
    }

    private void initEvent() {
        bt_stardate.setOnClickListener(this);
        bt_enddate.setOnClickListener(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        dpEndTime = CommonUtil.getStringDate();
        dpStartTime = CommonUtil.getMonthStart();
        bt_stardate.setText(dpStartTime);
        bt_enddate.setText(dpEndTime);

        Bundle bundle = getArguments();
        if (bundle != null) {
            areaCode = bundle.getString("areaCode");
            getCountData();
        }

        pvTime = new TimePickerView(mContext,
                TimePickerView.Type.YEAR_MONTH_DAY);
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
    }

    private void getCountData() {
        showDialog();

        OkHttpUtils.get()
                .url(Constant.BASE_URL + Constant.URL_SELECTCOMPLAINTSUMMARY)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("dpStartTime", dpStartTime)
                .addParams("dpEndTime", dpEndTime)
                .addParams("territorial", areaCode).build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int arg1) {
                        if (!TextUtils.isEmpty(response)) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject
                                        .getBoolean("success");
                                if (success) {
                                    tv_tscount.setText(jsonObject
                                            .getString("tscount"));
                                    tv_tsch01.updateText(jsonObject.getString("tsCh01"), "");
                                    tv_tsch02.updateText(jsonObject.getString("tsCh02"), "");
                                    tv_tsch80.updateText(jsonObject.getString("tsCh80"), "");
                                    tv_tskoukuan.setText(jsonObject
                                            .getString("tskoukuan"));
                                } else {
                                    showToast(jsonObject.getString("message"));
                                }
                                disDIalog();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                disDIalog();
                            }
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("获取数据失败");
                        disDIalog();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_bt_stardate:
                pvTime.setTime(new Date());
                pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

                    @Override
                    public void onTimeSelect(Date date) {
                        dpStartTime = CommonUtil.formatDateNoMinute(date);
                        bt_stardate.setText(dpStartTime);
                        getCountData();
                    }
                });
                pvTime.show();
                break;
            case R.id.id_bt_enddate:
                pvTime.setTime(new Date());
                pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

                    @Override
                    public void onTimeSelect(Date date) {
                        dpEndTime = CommonUtil.formatDateNoMinute(date);
                        bt_enddate.setText(dpEndTime);
                        getCountData();
                    }
                });
                pvTime.show();
                break;
            default:
                break;
        }
    }
}