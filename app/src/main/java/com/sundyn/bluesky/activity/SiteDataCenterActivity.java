package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.lidroid.xutils.util.LogUtils;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.bean.AreaReport;
import com.sundyn.bluesky.bean.ChartBean;
import com.sundyn.bluesky.bean.ChartBean.ChartItemData;
import com.sundyn.bluesky.bean.ChartReport;
import com.sundyn.bluesky.bean.ChartReport.ReportItem;
import com.sundyn.bluesky.bean.SiteDataCenterEnv;
import com.sundyn.bluesky.model.LineCharModel;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.view.ColorfulRingProgressView;
import com.sundyn.bluesky.view.NormalTopBar;
import com.sundyn.bluesky.view.VerticalText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class SiteDataCenterActivity extends BaseActivity {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.vt_pm10)
    protected VerticalText vt_pm10;
    @ViewInject(R.id.vt_humidity)
    protected VerticalText vt_humidity;
    @ViewInject(R.id.vt_temp)
    protected VerticalText vt_temp;
    @ViewInject(R.id.tv_windDic)
    protected TextView tv_windDictAvgic;
    @ViewInject(R.id.tv_windPowerAvg)
    protected TextView tv_windPowerAvg;
    @ViewInject(R.id.tvPercent)
    protected TextView tvPercent;
    @ViewInject(R.id.crpv)
    protected ColorfulRingProgressView crpv;// 统计的百分比
    @ViewInject(R.id.ll_report)
    protected LinearLayout ll_report;
    @ViewInject(R.id.lineChart)
    protected LineChart chart;
    @ViewInject(R.id.id_radiogroup)
    private RadioGroup mRadioGroup;
    @ViewInject(R.id.rb_pmChart)
    private RadioButton rb_pmChart;

    @ViewInject(R.id.reproView)
    protected VerticalText reproView;
    @ViewInject(R.id.reproView_userfull)
    protected VerticalText reproView_userfull;
    @ViewInject(R.id.id_tv_data_write)
    protected TextView tv_data_write;

    private String areaCode;
    private String siteName;

    private final static String PM10 = "PM10";
    private final static String TEMPERATEUR = "TEMPERATEUR";
    private final static String HUMIDTY = "HUMIDITY";
    private final static String WINDPD = "WINDPD";
    private final static String AREA = "AREA";
    private final static int pastDayCount = 7;

    private ArrayList<String> xValues;
    private ArrayList<Entry> yValues_pm;
    private ArrayList<Entry> yValues_tempera;
    private ArrayList<Entry> yValues_humidity;
    private ArrayList<Entry> yValues_windPower;
    private List<ReportItem> chartList;// 举报数折线图集合
    private List<ChartItemData> dataAllChart;// 除举报数折线图数据

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_sitedatacenter);
        x.view().inject(this);

        Intent intent = getIntent();
        areaCode = intent.getStringExtra("siteNo");
        siteName = intent.getStringExtra("siteName");

        initTitleBar();
        initEvent();
        initData();
    }

    private void initEvent() {
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_pmChart:
                        showChart(chart, PM10);
                        break;
                    case R.id.rb_temChart:
                        showChart(chart, TEMPERATEUR);
                        break;
                    case R.id.rb_humidtyChart:
                        showChart(chart, HUMIDTY);
                        break;
                    case R.id.rb_windChart:
                        showChart(chart, WINDPD);
                        break;
                    case R.id.rb_reportChart:
                        if (chartList != null) {
                            showReportChart();
                        } else {
                            getReportChartData(areaCode, AREA);
                        }
                        break;
                }
            }
        });
    }

    private void initData() {
        // mRadioGroup.check(checkedId);
        rb_pmChart.setChecked(true);// 用上面的方法则执行两遍监听
        getSiteData(areaCode);
        getReportPct(siteName);
    }

    /**
     * 获取基本环境数据
     *
     * @param siteNo
     */
    private void getSiteData(String siteNo) {
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_GETSITEDATA)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("regionType", "BUILD_SITE")
                .addParams("regionId", siteNo).build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        if (!TextUtils.isEmpty(response)) {
                            SiteDataCenterEnv dataCenter = GsonTools
                                    .changeGsonToBean(response,
                                            SiteDataCenterEnv.class);
                            if (dataCenter.success) {
                                List<SiteDataCenterEnv.SiteEnv> data = dataCenter.data;
                                if (data.size() > 0) {
                                    SiteDataCenterEnv.SiteEnv env = data.get(0);
                                    vt_pm10.updateText(
                                            String.format("%.2f", env.pm10), "");
                                    vt_humidity.updateText(
                                            String.format("%.2f", env.humidity),
                                            "");
                                    vt_temp.updateText(
                                            String.format("%.2f", env.tempera)
                                                    + "℃", "");
                                    tv_windDictAvgic.setText(CommonUtil
                                            .getWindDic(env.windDict));
                                    tv_windPowerAvg.setText(String.format(
                                            "%.2f",
                                            Float.valueOf(env.windPower))
                                            + "(m/s)");
                                }
                            } else {
                                showToast(dataCenter.message);
                            }
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception e, int arg2) {
                        LogUtils.i(e.getMessage());
                    }
                });
    }

    /* 获取群众举报统计 */
    private void getReportPct(String siteName) {
        OkHttpUtils
                .get()
                .url(Constant.BASE_URL
                        + Constant.URL_SELECTCOMPLAINTGDBYWEEKCOUNT)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams(
                        "dpStartTime",
                        CommonUtil.formatDateNoMinute(CommonUtil
                                .getLastWeekTime()))
                .addParams("dpEndTime",
                        CommonUtil.formatDateNoMinute(new Date()))
                .addParams("prjName", siteName).build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        AreaReport areaReport = GsonTools.changeGsonToBean(
                                response, AreaReport.class);
                        if (areaReport.success) {
                            String tscount = areaReport.data.tscount;
                            String yxtscount = areaReport.data.yxtscount;
                            int passCount = Integer.valueOf(yxtscount);
                            int count = Integer.valueOf(tscount);
                            if (count != 0) {
                                int roundProgress = passCount * 100 / count;
                                crpv.setPercent(roundProgress);
                                tvPercent.setText(roundProgress + "%");
                            } else {
                                crpv.setPercent(0);
                                tvPercent.setText(0 + "%");
                            }
                            reproView.updateText(count + "", "");
                            reproView_userfull.updateText(passCount + "", "");
                        } else {
                            showToast(areaReport.message);
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("获取群众举报数据失败");
                    }
                });
    }

    /* 获取折现图数据 */
    private void getLineChartData(String areaCode, final String type) {
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_GETLINECHART)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("regionType", "BUILD_SITE")
                .addParams("regionId", areaCode)
                .addParams("pastDay", pastDayCount + "").build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        if (!TextUtils.isEmpty(response)) {
                            ChartBean chartBean = GsonTools.changeGsonToBean(
                                    response, ChartBean.class);
                            if (chartBean.success) {
                                dataAllChart = chartBean.data;
                                if (dataAllChart != null
                                        && dataAllChart.size() > 0) {
                                    initChartData(dataAllChart, type);
                                } else {
                                    tv_data_write
                                            .setText(getString(R.string.nodata));
                                }
                            } else {
                                showToast(chartBean.message);
                            }
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("获取折线图数据失败");
                    }
                });
    }

    /* 获取举报折线图数据 */
    private void getReportChartData(String siteNo, String type) {
        OkHttpUtils
                .get()
                .url(Constant.BASE_URL + Constant.URL_SELECTCOMPLAINTGDBYWEEK)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams(
                        "dpStartTime",
                        CommonUtil.formatDateNoMinute(CommonUtil
                                .getLastWeekTime()))
                .addParams("dpEndTime",
                        CommonUtil.formatDateNoMinute(new Date()))
                .addParams("prjName", siteName).build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        if (!TextUtils.isEmpty(response)) {
                            ChartReport chartData = GsonTools.changeGsonToBean(
                                    response, ChartReport.class);
                            if (chartData.success) {
                                chartList = chartData.data;
                                showReportChart();
                            }
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("获取举报折线图数据失败");
                    }
                });
    }

    private void initChartData(List<ChartItemData> dataAllChart, String type) {
        ArrayList<Entry> yValues_temp = null;
        if (xValues == null) {
            xValues = new ArrayList<String>();
            yValues_pm = new ArrayList<Entry>();
            yValues_tempera = new ArrayList<Entry>();
            yValues_humidity = new ArrayList<Entry>();
            yValues_windPower = new ArrayList<Entry>();
            for (int i = 0; i < dataAllChart.size(); i++) {
                String timeStr = "";
                if (i == 0) {
                    timeStr = CommonUtil.formatDateNoMinute(
                            dataAllChart.get(i).time.trim(), false);
                } else {
                    timeStr = CommonUtil.formatDateNoMinute(
                            dataAllChart.get(i).time.trim(), true);
                }
                xValues.add(timeStr);
                yValues_pm.add(new Entry((float) dataAllChart.get(i).pm10, i));
                yValues_tempera.add(new Entry(
                        (float) dataAllChart.get(i).tempera, i));
                yValues_humidity.add(new Entry(
                        (float) dataAllChart.get(i).humidity, i));
                yValues_windPower.add(new Entry(
                        (float) dataAllChart.get(i).windPower, i));
            }
        }
        if (type == PM10) {
            yValues_temp = yValues_pm;
        } else if (type == TEMPERATEUR) {
            yValues_temp = yValues_tempera;
        } else if (type == HUMIDTY) {
            yValues_temp = yValues_humidity;
        } else if (type == WINDPD) {
            yValues_temp = yValues_windPower;
        }
        tv_data_write.setVisibility(View.GONE);
        LineCharModel.getLineCharModel().showChart(chart, xValues,
                yValues_temp, mContext);
    }

    private void showChart(LineChart lineChart, String type) {
        if (dataAllChart != null) {
            initChartData(dataAllChart, type);
        } else {
            getLineChartData(areaCode, type);
        }
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SiteDataCenterActivity.this.finish();
            }
        });
        mTopBar.setTitle(siteName != null ? siteName : "暂无工地名称");
        mTopBar.setActionTextVisibility(false);
    }

    private void showReportChart() {
        ArrayList<String> xValues = new ArrayList<String>();// x轴的数据
        ArrayList<Entry> yValues = new ArrayList<Entry>();// y轴的数据
        for (int i = 0; i < chartList.size(); i++) {
            String timeStr = "";
            if (i == 0) {
                timeStr = CommonUtil.formatDateNoMinute(
                        chartList.get(i).CreateDate.trim(), false);
            } else {
                timeStr = CommonUtil.formatDateNoMinute(
                        chartList.get(i).CreateDate.trim(), true);
            }
            xValues.add(timeStr);
            String count = chartList.get(i).Cnt;
            if (count == null) {
                count = "0";
            }
            yValues.add(new Entry(Float.parseFloat(count), i));
        }
        LineCharModel.getLineCharModel().showChart(chart, xValues, yValues,
                mContext);
    }

}
