package com.sundyn.bluesky.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.PMOverForDataCenterActivity;
import com.sundyn.bluesky.activity.SiteDataCenterActivity;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.AreaReport;
import com.sundyn.bluesky.bean.ChartBean;
import com.sundyn.bluesky.bean.ChartBean.ChartItemData;
import com.sundyn.bluesky.bean.ChartReport;
import com.sundyn.bluesky.bean.ChartReport.ReportItem;
import com.sundyn.bluesky.bean.DataCenterEnv;
import com.sundyn.bluesky.bean.PMPlus;
import com.sundyn.bluesky.bean.PMPlus.PMItem;
import com.sundyn.bluesky.model.LineCharModel;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.view.ColorfulRingProgressView;
import com.sundyn.bluesky.view.LinearListView;
import com.sundyn.bluesky.view.LinearListView.OnItemClickListener;
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

public class DataCenterItemFragment extends BaseFragment implements
        OnClickListener {

    private View view;
    private int areaCode;
    @ViewInject(R.id.sv)
    protected ScrollView sv;
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
    @ViewInject(R.id.lv_site)
    protected LinearListView lv_site;
    private static int pageStart = 1;
    private final static int MAXSIZE = 5;
    private ArrayList<PMItem> pmList;// 超标工地数据
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
    @ViewInject(R.id.tv_more)
    protected TextView tv_more;
    @ViewInject(R.id.id_tv_data_write)
    protected TextView tv_data_write;

    private QuickAdapter<PMItem> adapter;

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

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fra_datacenteritem, null);
        x.view().inject(this, view); // 注入view和事件
        sv.smoothScrollTo(0, 0);
        initEvent();
        return view;
    }

    private void initEvent() {
        tv_more.setOnClickListener(this);
        lv_site.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(LinearListView parent, View view,
                                    int position, long id) {
                PMItem pmItem = pmList.get(position);
                Intent intent = new Intent(mContext,
                        SiteDataCenterActivity.class);
                intent.putExtra("siteNo", pmItem.siteNo);
                intent.putExtra("siteName", pmItem.siteName);
                startActivity(intent);
            }
        });
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

    @Override
    public void initData(Bundle savedInstanceState) {
        adapter = new QuickAdapter<PMItem>(mContext,
                R.layout.lv_pmplus_item) {

            @Override
            protected void convert(BaseAdapterHelper helper, PMItem item) {
                helper.setText(R.id.tv_sitename, item.siteName);
                helper.setText(R.id.tv_pm, item.pm10 + "");
            }
        };
        lv_site.setAdapter(adapter);

        Bundle mBundle = getArguments();
        areaCode = mBundle.getInt("areaCode");
        rb_pmChart.setChecked(true);
        getBasicEnvironment();
        getReportPct(areaCode);
        getPmPlusSite(areaCode);
    }

    /**
     * 获取折现图数据
     *
     * @param //areaCode2
     */
    private void getLineChartData(int areaCode, final String type) {
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_GETLINECHART)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("regionType", "AREA")
                .addParams("regionId", areaCode + "")
                .addParams("pastDay", pastDayCount + "").build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        if (!TextUtils.isEmpty(response)) {
                            ChartBean chartBean = GsonTools.changeGsonToBean(
                                    response, ChartBean.class);
                            if (chartBean.success) {
                                List<ChartItemData> dataAllChart = chartBean.data;
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

    /* 获取举报折线图 */
    private void getReportChartData(int areaCode, String type) {
        OkHttpUtils
                .get()
                .url(Constant.BASE_URL + Constant.URL_GETREPORTCHART)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("territorial", areaCode + "")
                .addParams(
                        "dpStartTime",
                        CommonUtil.formatDateNoMinute(CommonUtil
                                .getLastWeekTime()))
                .addParams("dpEndTime",
                        CommonUtil.formatDateNoMinute(new Date())).build()
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

    private void initChartData(List<ChartItemData> dataAllChart, String type) {
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
            yValues_tempera.add(new Entry((float) dataAllChart.get(i).tempera,
                    i));
            yValues_humidity.add(new Entry(
                    (float) dataAllChart.get(i).humidity, i));
            yValues_windPower.add(new Entry(
                    (float) dataAllChart.get(i).windPower, i));
        }
        showChart(chart, type);
    }

    private void showChart(LineChart lineChart, String type) {
        ArrayList<Entry> yValues_temp = new ArrayList<Entry>();
        if (xValues == null) {
            getLineChartData(areaCode, type);
            return;
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

    /* 获取超标工地 */
    private void getPmPlusSite(int areaCode) {
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_GETPM_PLUS)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("regionType", "AREA")
                .addParams("regionId", areaCode + "")
                .addParams("page", pageStart + "")
                .addParams("size", MAXSIZE + "").build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        if (!TextUtils.isEmpty(response)) {
                            PMPlus pmPlus = GsonTools.changeGsonToBean(
                                    response, PMPlus.class);
                            if (pmPlus.success) {
                                pmList = pmPlus.data;
                                adapter.clear();
                                adapter.addAll(pmList);
                            } else {
                                showToast(pmPlus.message);
                            }
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("获取PM10超标工地数据失败");
                    }
                });
    }

    /* 获取群众举报统计 */
    private void getReportPct(int areaCode) {
        OkHttpUtils
                .get()
                .url(Constant.BASE_URL
                        + Constant.URL_SELECTCOMPLAINTBYWEEKCOUNT)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams(
                        "dpStartTime",
                        CommonUtil.formatDateNoMinute(CommonUtil
                                .getLastWeekTime()))
                .addParams("dpEndTime",
                        CommonUtil.formatDateNoMinute(new Date()))
                .addParams("territorial", areaCode + "").build()
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

    /**
     * 获取基本环境数据
     */
    private void getBasicEnvironment() {
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_AREAENV)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("regionType", "AREA")
                .addParams("regionId", areaCode + "").build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        if (!TextUtils.isEmpty(response)) {
                            DataCenterEnv dataCenterEnv = GsonTools
                                    .changeGsonToBean(response,
                                            DataCenterEnv.class);//
                            if (dataCenterEnv.success) {
                                List<DataCenterEnv.Env> mData = dataCenterEnv.data;
                                if (mData != null && mData.size() > 0) {

                                    vt_pm10.updateText(String.format("%.2f",
                                            mData.get(0).pm10), "");
                                    vt_humidity.updateText(String.format(
                                            "%.2f", mData.get(0).humidity), "");
                                    vt_temp.updateText(
                                            String.format("%.2f",
                                                    mData.get(0).tempera) + "℃",
                                            "");
                                    tv_windDictAvgic
                                            .setText(getwindDictAvgic(mData
                                                    .get(0).windDict));
                                    tv_windPowerAvg.setText(String.format(
                                            "%.2f", mData.get(0).windPower)
                                            + "(m/s)");
                                }
                            }

                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("获取基本环境数据失败");
                    }
                });
    }

    /**
     * 根据数值判断风向
     *
     * @param windDictAvgictAvg
     * @return
     */
    private String getwindDictAvgic(double windDictAvgictAvg) {
        String windDictAvgic = "无风";
        if (windDictAvgictAvg >= 0 && windDictAvgictAvg < 0.5) {
            windDictAvgic = "偏北风";
        }
        if (windDictAvgictAvg >= 0.5 && windDictAvgictAvg < 3.5) {
            windDictAvgic = "东北风";
        }
        if (windDictAvgictAvg >= 3.5 && windDictAvgictAvg < 4.5) {
            windDictAvgic = "东风";
        }
        if (windDictAvgictAvg >= 4.5 && windDictAvgictAvg < 7.5) {
            windDictAvgic = "东南风";
        }
        if (windDictAvgictAvg >= 7.5 && windDictAvgictAvg < 8.5) {
            windDictAvgic = "偏南风";
        }
        if (windDictAvgictAvg >= 8.5 && windDictAvgictAvg < 11.5) {
            windDictAvgic = "西南风";
        }
        if (windDictAvgictAvg >= 11.5 && windDictAvgictAvg < 12.5) {
            windDictAvgic = "西风";
        }
        if (windDictAvgictAvg >= 12.5 && windDictAvgictAvg < 15.5) {
            windDictAvgic = "西北风";
        }
        if (windDictAvgictAvg >= 15.5 && windDictAvgictAvg <= 16) {
            windDictAvgic = "偏北风";
        }
        return windDictAvgic;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_more:
                if (pmList != null) {
                    if (pmList.size() < MAXSIZE) {
                        showToast("无更多数据");
                        return;
                    }
                    Intent intent = new Intent(mContext,
                            PMOverForDataCenterActivity.class);
                    intent.putExtra("areaCode", areaCode);
                    startActivity(intent);
                }
                break;

            default:
                break;
        }

    }
}
