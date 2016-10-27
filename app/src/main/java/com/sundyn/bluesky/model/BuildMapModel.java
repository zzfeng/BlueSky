package com.sundyn.bluesky.model;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.model.LatLng;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseModel;
import com.sundyn.bluesky.bean.BrokeBuildBean;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

public class BuildMapModel extends BaseModel {
    private BaiduMap mBaiduMap;
    private LatLng myLocation;
    private double dis;

    private boolean isShow = false;
    private ArrayList<BrokeBuildBean.Build> bulidList;
    public boolean isSearch = false;// 周边查询
    private BitmapDescriptor bd_site = BitmapDescriptorFactory
            .fromResource(R.mipmap.sitecopy);
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            isShow = true;
            disDIalog();
        }

        ;
    };

    public BuildMapModel(Context ctx, BaiduMap mBaiduMap) {
        super(ctx);
        this.mBaiduMap = mBaiduMap;
    }

    /* 显示标注图 */
    public void showOverlay() {
        showDialog();

        if (bulidList != null) {
            addInfosOverlay(bulidList);
        } else {
            getBrokeBuild();
        }
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

    /**
     * 获取违章工地数据
     */
    public void getBrokeBuild() {
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_BUILD)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken()).build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int arg1) {
                        BrokeBuildBean brokeBuild = GsonTools.changeGsonToBean(
                                response, BrokeBuildBean.class);
                        if (brokeBuild.success) {
                            bulidList = brokeBuild.data;
                            if (bulidList != null && bulidList.size() > 0) {
                                if (isSearch) {
                                    addInfosOverlay(getSearchData(myLocation,
                                            dis));
                                    isSearch = false;
                                } else {
                                    addInfosOverlay(bulidList);
                                }
                            }
                        } else {
                            showToast(brokeBuild.message);
                        }

                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("获取违章工地数据失败");
                    }
                });
    }

    /* 展示搜索 */
    public void showSearchView(LatLng myLocation, double dis) {
        isSearch = true;
        this.myLocation = myLocation;
        this.dis = dis;
        if (bulidList != null) {
            addInfosOverlay(getSearchData(myLocation, dis));
        } else {
            getBrokeBuild();
        }
    }

    public ArrayList<BrokeBuildBean.Build> getSearchData(LatLng myLocation,
                                                         double dis) {
        Double radius = 6371.004;// 地球半径KM
        ArrayList<BrokeBuildBean.Build> list_search = new ArrayList<BrokeBuildBean.Build>();
        final Double PI = Math.PI;
        Double JD = myLocation.longitude;
        Double WD = myLocation.latitude;
        Double MaxJD = JD - dis / ((Math.cos(WD) * radius * PI) / 180);// 经度最大值
        Double MinJD = JD + dis / ((Math.cos(WD) * radius * PI) / 180);// 经度最小值
        Double MaxWD = WD + dis / (radius * PI / 180);
        Double MinWD = WD - dis / (radius * PI / 180);
        for (BrokeBuildBean.Build build : bulidList) {
            if (Double.valueOf(build.latitude) > MinWD && Double.valueOf(build.latitude) < MaxWD
                    && Double.valueOf(build.longitude) > MinJD && Double.valueOf(build.longitude) < MaxJD) {
                list_search.add(build);
            }
        }
        return list_search;
    }

    /* 将标注点到地图上 */
    public void addInfosOverlay(final ArrayList<BrokeBuildBean.Build> bulidList) {
        mBaiduMap.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (BrokeBuildBean.Build build : bulidList) {
                    if (TextUtils.isEmpty(build.latitude)
                            || TextUtils.isEmpty(build.longitude))
                        continue;
                    try {
                        LatLng llA = new LatLng(Double.valueOf(build.latitude),
                                Double.valueOf(build.longitude));
                        MarkerOptions ooA = new MarkerOptions().position(llA)
                                .icon(bd_site).zIndex(9).draggable(false);
                        ooA.animateType(MarkerAnimateType.grow);//
                        Marker marker = (Marker) mBaiduMap.addOverlay(ooA);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("buildInfo", build);
                        marker.setExtraInfo(bundle);
                    } catch (Exception e) {
                        continue;
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    /**
     * 销毁资源
     */
    public void destory() {
        bd_site.recycle();
    }

}
