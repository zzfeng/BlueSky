package com.sundyn.bluesky.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.DataCenterEnv;
import com.sundyn.bluesky.bean.InstallPct;
import com.sundyn.bluesky.bean.MySiteState;
import com.sundyn.bluesky.bean.PlusDay;
import com.sundyn.bluesky.bean.PlusPrecent;
import com.sundyn.bluesky.bean.PlusPrecent.PlusPrecentItem;
import com.sundyn.bluesky.bean.User;
import com.sundyn.bluesky.model.LineCharModel;
import com.sundyn.bluesky.utils.AnimatorUtil;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.view.ColorfulRingProgressView;
import com.sundyn.bluesky.view.ImageGridView;
import com.sundyn.bluesky.view.InstallPctDialogFra;
import com.sundyn.bluesky.view.LinearListView;
import com.sundyn.bluesky.view.VerticalText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class HomeFra extends BaseFragment {

    private View view;
    @ViewInject(R.id.id_pie_chart_geo)
    private PieChart mChart_geo;
    @ViewInject(R.id.id_gv_geosite)
    private ImageGridView gv_geosite;
    @ViewInject(R.id.vt_now)
    private VerticalText vt_now;
    @ViewInject(R.id.vt_prcent)
    private VerticalText vt_prcent;
    @ViewInject(R.id.main_radio)
    private RadioGroup main_radio;
    @ViewInject(R.id.rb_pm10)
    private RadioButton rb_pm10;
    @ViewInject(R.id.tv_more)
    private TextView tv_more;
    @ViewInject(R.id.id_tv_write)
    private TextView tv_write;
    @ViewInject(R.id.id_tv_currentdata)
    private TextView tv_currentdata;
    @ViewInject(R.id.id_tv_pmtitle)
    private TextView tv_pmTitle;
    @ViewInject(R.id.tv_title_overdate)
    private TextView tv_title_overdate;
    @ViewInject(R.id.id_framel_installpct)
    private FrameLayout framel_installpct;
    @ViewInject(R.id.id_fl_overproof)
    private FrameLayout fl_overproof;// 超标天数占比
    @ViewInject(R.id.id_ll_plusprecent)
    private LinearLayout ll_plusprecent;

    private PlusDay plusDay;
    private boolean isPM10 = true;
    private int allDays;
    private int plus_pm10;
    private int plus_pm25;

    private PlusPrecent plusPrecent;
    private PieData pieDataGeo;
    private List<InstallPct> installPctList;// 系统接入工地占比
    private QuickAdapter<InstallPct> installAdapter;

    private boolean showAllData = false;// 是否展开全部
    private final static String ROLE_SITE_MANAGER = "ROLE_SITE_MANAGER";
    private MySiteState mySiteState;
    private LayoutInflater inflater;

    @Override
    public View initView(LayoutInflater inflater) {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.fra_home, null);
        x.view().inject(this, view); // 注入view和事件
        initEvent();
        return view;
    }

    private void initEvent() {
        main_radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_pm10:
                        isPM10 = true;
                        choiceRB();
                        break;
                    case R.id.rb_pm25:
                        isPM10 = false;
                        choiceRB();
                        break;
                }

            }
        });
        rb_pm10.setChecked(true);
        tv_more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (installAdapter != null) {
                    showAllData = !showAllData;
                    installAdapter.setShowAllData(showAllData);
                    installAdapter.notifyDataSetChanged();
                    if (showAllData) {
                        tv_more.setText("收起全部");
                    } else {
                        tv_more.setText("查看全部");
                    }
                }
            }
        });
        gv_geosite.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                InstallPctDialogFra iFra = new InstallPctDialogFra(installPctList.get(position));
                iFra.show(getFragmentManager(), "INSTALLPCT");
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (isSiteManager()) {
            tv_title_overdate.setText("PM超标数据记录");
            framel_installpct.setVisibility(View.GONE);
            showMySiteState();
        } else {
            tv_title_overdate.setText("超标天数占比");
            mChart_geo.setVisibility(View.VISIBLE);
            if (installPctList != null) {
                processData();
            } else {
                installPct();
            }
        }
    }

    private void showMySiteState() {
        if (mySiteState == null) {
            getMysiteState();
        } else {
            fillView2MySite();
        }
    }

    /**
     * 判断是否为工地管理者
     */
    private boolean isSiteManager() {
        User user = mApplication.getUser();
        if (user != null) {
            List<String> rolesList = user.getRoles();
            for (String role : rolesList) {
                if (ROLE_SITE_MANAGER.equalsIgnoreCase(role))
                    return true;
            }
        }
        return false;
    }

    private void fill2View() {
        if (allDays != 0) {
            float percent;
            int current;
            if (isPM10) {
                percent = (float) plus_pm10 / allDays;
                current = plus_pm10;
            } else {
                percent = (float) plus_pm25 / allDays;
                current = plus_pm25;
            }
            vt_prcent.updateText(CommonUtil.saveNumberPercent(percent), "");
            vt_now.updateText(current + "", "");
        }
    }

    /* 饼状图数据 */
    private void getplusDay() {
        OkHttpUtils
                .get()
                .url(Constant.BASE_URL + Constant.URL_GETPLUSDAY)
                .addParams("areaIds",
                        mApplication.getUser().getRegionidstring())
                .addParams("userNo", mApplication.getUser().getUserNo())
                .build().execute(new StringCallback() {
            @Override
            public void onResponse(String response, int arg1) {
                LogUtil.i(response);
                plusDay = GsonTools.changeGsonToBean(response,
                        PlusDay.class);
                if (plusDay != null) {
                    allDays = plusDay.allDays;
                    plus_pm10 = plusDay.plus_pm10;
                    plus_pm25 = plusDay.plus_pm25;
                    fill2View();
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                LogUtil.i(arg1.getMessage());
            }
        });
    }

    // 郑州市各县区超标天数占比
    private void getPlusPrecent() {
        OkHttpUtils
                .get()
                .url(Constant.BASE_URL + Constant.URL_GETPLUSPRECENT)
                .addParams("areaIds",
                        mApplication.getUser().getRegionidstring()).build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        plusPrecent = GsonTools.changeGsonToBean(response,
                                PlusPrecent.class);
                        fill2PlusPrecent();

                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                    }
                });
    }

    private void fill2PlusPrecent() {
        if (plusPrecent.pm10_precent.size() > 1
                && plusPrecent.pm25_precent.size() > 1) {
            LineCharModel.getLineCharModel().showPieChart(mChart_geo,
                    getPieDataOfGeo(), mContext, true, true, false);
            LineCharModel.getLineCharModel().refreashPieChart(mChart_geo,
                    pieDataGeo);
        } else {
            ll_plusprecent.setVisibility(View.GONE);
        }
    }

    // 郑州市各县区接入系统工地占比
    private void installPct() {
        OkHttpUtils
                .get()
                .url(Constant.BASE_URL + Constant.URL_INSTALLPCT)
                .addParams("areaIds",
                        mApplication.getUser().getRegionidstring()).build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        installPctList = GsonTools.changeGsonToInstallPctList(
                                response, InstallPct.class);
                        if (installPctList != null && installPctList.size() > 0) {
                            processData();
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                    }
                });
    }

    private void processData() {
        if (installAdapter == null)
            installAdapter = new QuickAdapter<InstallPct>(mContext,
                    R.layout.gv_installpct_item) {
                @Override
                protected void convert(BaseAdapterHelper helper, InstallPct item) {
                    float allPct = item.pmPct + item.videoPct - item.pmVideoPct;
                    ColorfulRingProgressView crpv = helper.getView(R.id.crpv);
                    crpv.setPercent(allPct * 100);
                    helper.setText(R.id.tvPercent,
                            CommonUtil.saveNumberPercent(allPct));
                    helper.setText(R.id.tv_area, item.areaName);
                }
            };
        gv_geosite.setAdapter(installAdapter);
        installAdapter.clear();
        if (installPctList.size() > 8) {
            installAdapter.setVisiableCount(8);
        }
        installAdapter.addAll(installPctList);

    }

    private PieData getPieDataOfGeo() {
        ArrayList<String> xValues = new ArrayList<String>();
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        List<PlusPrecentItem> tempData;
        int tempColor = 0;
        if (isPM10) {
            tempData = plusPrecent.pm10_precent;
            tempColor = R.color.yellow_datacenter;
            mChart_geo.setCenterText(getString(R.string.pm10overtime)); // 饼状图中间的文字
        } else {
            tempData = plusPrecent.pm25_precent;
            tempColor = R.color.red_dark;
            mChart_geo.setCenterText(getString(R.string.pm25overtime)); // 饼状图中间的文字
        }
        if (tempData != null) {

            int j = 0;// yValues若不连续，则点击时会出现模块消失现象
            for (int i = 0; i < tempData.size(); i++) {
                if (tempData.get(i).count == 0)
                    continue;
                xValues.add(tempData.get(i).geo.name);
                yValues.add(new Entry(tempData.get(i).count, j++));
            }
        }
        mChart_geo.setCenterTextColor(mContext.getResources().getColor(
                tempColor));
        mChart_geo.setCenterTextSize(12);
        // y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "");
        pieDataSet.setSliceSpace(2f); // 设置个饼状图之间的距离
        pieDataSet.setColors(CommonUtil.getColors());
        pieDataSet.setSelectionShift(5f); // 选中态多出的长度
        pieDataSet.setValueFormatter(new PercentFormatter(new DecimalFormat(
                "0.00")));
        pieDataGeo = new PieData(xValues, pieDataSet);
        return pieDataGeo;
    }

    private void choiceRB() {
        if (currentPM != null) {
            fill2CurrentPM();
        } else {
            getCurrentPMData();
        }
        if (plusDay == null) {
            getplusDay();
        } else {
            fill2View();
        }
        if (!isSiteManager()) {
            if (plusPrecent == null) {
                getPlusPrecent();
            } else {
                fill2PlusPrecent();
            }
        }
    }

    /* 获取实时PM10、PM2.5数据 */
    private DataCenterEnv.Env currentPM;

    private void getCurrentPMData() {
        OkHttpUtils
                .get()
                .url(Constant.BASE_URL + Constant.URL_CURRENTPMAVGDATABYNAME)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("areaIds",
                        mApplication.getUser().getRegionidstring()).build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        DataCenterEnv dataCenterEnv = GsonTools
                                .changeGsonToBean(response, DataCenterEnv.class);//
                        if (dataCenterEnv.success) {
                            List<DataCenterEnv.Env> mData = dataCenterEnv.data;
                            if (mData != null && mData.size() > 0) {
                                currentPM = mData.get(0);
                                fill2CurrentPM();
                            }
                        }

                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                    }
                });
    }

    private void fill2CurrentPM() {
        tv_write.setVisibility(view.GONE);
        DecimalFormat format = new DecimalFormat("0.00");
        double tempPM;
        if (isPM10) {
            tv_pmTitle.setText(getString(R.string.pm10current));
            tempPM = currentPM.pm10;
        } else {
            tv_pmTitle.setText(getString(R.string.pm25current));
            tempPM = currentPM.pm25;
        }
        AnimatorUtil.getInstance().reaptAlpha(tv_currentdata);
        tv_currentdata.setText(format.format(tempPM));
    }

    private void getMysiteState() {
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_GETMYSITESTATE)
                .addParams("userNo", mApplication.getUser().getUserNo())
                .build().execute(new StringCallback() {

            @Override
            public void onResponse(String response, int arg1) {
                LogUtil.i(response);
                mySiteState = GsonTools.changeGsonToBean(response,
                        MySiteState.class);
                fillView2MySite();
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
            }
        });
    }

    private View siteView;
    private TextView tv_staus;
    private LinearListView lv_sitestate;
    private QuickAdapter<MySiteState.SiteData> siteAdapter;

    private void fillView2MySite() {
        if (mySiteState.success) {
            if (siteView == null) {
                siteView = inflater.inflate(R.layout.view_home_sitedata, null);
                tv_staus = (TextView) siteView.findViewById(R.id.tv_staus);
                lv_sitestate = (LinearListView) siteView
                        .findViewById(R.id.lv_sitestate);
            }
            FrameLayout parent = (FrameLayout) siteView.getParent();
            if (parent != null) {
                parent.removeView(siteView);
            }
            fl_overproof.removeAllViews();
            fl_overproof.addView(siteView);
            tv_staus.setText(mySiteState.result);
            if (siteAdapter == null)
                siteAdapter = new QuickAdapter<MySiteState.SiteData>(mContext,
                        R.layout.lv_pmplus_item) {

                    @Override
                    protected void convert(BaseAdapterHelper helper,
                                           MySiteState.SiteData item) {
                        helper.setText(R.id.tv_sitename, item.time);
                        helper.setText(R.id.tv_pm, item.pm);
                    }
                };
            lv_sitestate.setAdapter(siteAdapter);
            siteAdapter.clear();
            siteAdapter.addAll(mySiteState.datas);
        }
    }

}
