package com.sundyn.bluesky.model;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Gradient;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.model.LatLng;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseModel;
import com.sundyn.bluesky.bean.PMForMap;
import com.sundyn.bluesky.bean.PMForMap.PMBean;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * @author yangjl
 * @ʱ�� 2016��10��21������3:01:56
 * @�汾 1.0
 * @����
 */
public class PmMapModel extends BaseModel {
    private BaiduMap mBaiduMap;
    private LatLng myLocation;
    private double dis;

    private final static int SHOWPM = 0;
    private final static int SHOWPMSEARCH = 1;
    private final static int SHOWHOTMAP = 2;
    private int tempId;
    private boolean isShow;// �Ƿ���ʾpm10
    private boolean isShowHotMap;// �Ƿ���ʾ����ͼ
    private List<PMForMap.PMBean> pmBeanList;// ��ע����
    private BitmapDescriptor bd_red = BitmapDescriptorFactory
            .fromResource(R.mipmap.qq_red);
    private BitmapDescriptor bd_green = BitmapDescriptorFactory
            .fromResource(R.mipmap.qq_green);
    private BitmapDescriptor bd_yellow = BitmapDescriptorFactory
            .fromResource(R.mipmap.qq_yellow);
    private HeatMap heatmap;// ����ͼ
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    isShow = true;
                    disDIalog();
                    break;
                case 1:
                    mBaiduMap.addHeatMap(heatmap);
                    disDIalog();
                    break;
                default:
                    break;
            }

        }

        ;
    };

    public PmMapModel(Context ctx, BaiduMap mBaiduMap) {
        super(ctx);
        this.mBaiduMap = mBaiduMap;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShowHotMap() {
        return isShowHotMap;
    }

    public void setShowHotMap(boolean isShowHotMap) {
        this.isShowHotMap = isShowHotMap;
    }

    /* ��ʾPM */
    public void showPMInMap() {
        showDialog();
        tempId = SHOWPM;
        if (pmBeanList == null)
            getPMData();
        else
            addInfosOverlay(pmBeanList);
    }

    public void showHotMap() {
        showDialog();
        tempId = SHOWHOTMAP;
        if (pmBeanList == null)
            getPMData();
        else
            addHot(pmBeanList);

    }

    public void addHot(final List<PMBean> pmBeanList) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                // ���ý�����ɫֵ
                int[] DEFAULT_GRADIENT_COLORS = {Color.rgb(102, 225, 0),
                        Color.rgb(255, 0, 0)};
                // ���ý�����ɫ��ʼֵ
                float[] DEFAULT_GRADIENT_START_POINTS = {0.2f, 1f};
                // ������ɫ�������
                Gradient gradient = new Gradient(DEFAULT_GRADIENT_COLORS,
                        DEFAULT_GRADIENT_START_POINTS);
                List<LatLng> data = new ArrayList<LatLng>();
                if (pmBeanList != null && pmBeanList.size() > 0) {
                    for (PMBean pmObject : pmBeanList) {
                        LatLng llA = new LatLng(pmObject.latitude,
                                pmObject.longitude);
                        data.add(llA);
                    }
                    heatmap = new HeatMap.Builder().data(data).radius(30)
                            .build();
                    isShowHotMap = true;
                }
                handler.sendEmptyMessage(1);
            }
        }.start();
    }

    /* ��ȡPM10���� */
    public void getPMData() {
        OkHttpUtils.post().url(Constant.BASE_URL + Constant.URL_PM)
                .addParams("userName", mApplication.getUser().getUserNo())
                //
                .addParams("token", mApplication.getUser().getToken())
                //
                .addParams("regionType", "CITY")
                //
                .addParams("regionId", "2")
                // ֣��Ϊ2
                .addHeader("Accept-Charset", "gbk").build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        if (!TextUtils.isEmpty(response)) {
                            PMForMap pmForMap = GsonTools.changeGsonToBean(
                                    response, PMForMap.class);
                            if (pmForMap != null && pmForMap.success) {
                                pmBeanList = pmForMap.data;
                                switch (tempId) {
                                    case SHOWPM:
                                        addInfosOverlay(pmBeanList);
                                        break;
                                    case SHOWPMSEARCH:
                                        addInfosOverlay(getSearchData(myLocation,
                                                dis));
                                        break;
                                    case SHOWHOTMAP:
                                        addHot(pmBeanList);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("��ȡ����ʧ��");
                    }
                });
    }

    /* չʾ���� */
    public void showSearchView(LatLng myLocation, double dis) {
        showDialog();
        tempId = SHOWPMSEARCH;
        this.myLocation = myLocation;
        this.dis = dis;
        if (pmBeanList != null) {
            addInfosOverlay(getSearchData(myLocation, dis));
        } else {
            getPMData();
        }
    }

    public List<PMBean> getSearchData(LatLng myLocation, double dis) {
        Double radius = 6371.004;// ����뾶KM
        ArrayList<PMBean> list_search = new ArrayList<PMBean>();
        final Double PI = Math.PI;
        Double JD = myLocation.longitude;
        Double WD = myLocation.latitude;
        Double MaxJD = JD - dis / ((Math.cos(WD) * radius * PI) / 180);// �������ֵ
        Double MinJD = JD + dis / ((Math.cos(WD) * radius * PI) / 180);// ������Сֵ
        Double MaxWD = WD + dis / (radius * PI / 180);
        Double MinWD = WD - dis / (radius * PI / 180);
        for (PMBean pmbean : pmBeanList) {
            if (pmbean.latitude > MinWD && pmbean.latitude < MaxWD
                    && pmbean.longitude > MinJD && pmbean.longitude < MaxJD) {
                list_search.add(pmbean);
            }
        }
        return list_search;
    }

    /* ����ע�㵽��ͼ�� */
    public void addInfosOverlay(final List<PMBean> pmBeanList) {

        mBaiduMap.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (PMBean pmObject : pmBeanList) {
                    MarkerOptions ooA = null;
                    LatLng llA = new LatLng(pmObject.latitude,
                            pmObject.longitude);
                    if (pmObject.pm10 <= 50) {
                        ooA = new MarkerOptions().position(llA).icon(bd_green)
                                .zIndex(9).draggable(false);
                    } else if (pmObject.pm10 > 50 && pmObject.pm10 <= 100) {
                        ooA = new MarkerOptions().position(llA).icon(bd_yellow)
                                .zIndex(9).draggable(false);
                    } else {
                        ooA = new MarkerOptions().position(llA).icon(bd_red)
                                .zIndex(9).draggable(false);
                    }
                    ooA.animateType(MarkerAnimateType.grow);// ���¶���
                    Marker marker = (Marker) mBaiduMap.addOverlay(ooA);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("info", pmObject);
                    marker.setExtraInfo(bundle);
                }
                handler.sendEmptyMessage(0);
            }
        }).start();

    }

    public void removeHeatMap() {
        if (heatmap != null) {
            heatmap.removeHeatMap();
            isShowHotMap = false;
        }
    }

    /* ������Դ */
    public void destory() {
        bd_red.recycle();
        bd_green.recycle();
        bd_yellow.recycle();
        heatmap = null;
    }

}
