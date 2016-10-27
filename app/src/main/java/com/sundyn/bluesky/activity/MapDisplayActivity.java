package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.bean.BrokeBuildBean.Build;
import com.sundyn.bluesky.bean.PMForMap.PMBean;
import com.sundyn.bluesky.model.BuildMapModel;
import com.sundyn.bluesky.model.PmMapModel;
import com.sundyn.bluesky.utils.DrivingRouteOverlay;
import com.sundyn.bluesky.utils.OverlayManager;
import com.sundyn.bluesky.utils.ReportUtils;
import com.sundyn.bluesky.view.MapAroundDialogFra;
import com.sundyn.bluesky.view.MapAroundDialogFra.MapAroundListener;
import com.sundyn.bluesky.view.NormalTopBar;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class MapDisplayActivity extends BaseActivity implements
        OnMarkerClickListener, OnGetRoutePlanResultListener, MapAroundListener {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.bmapView)
    private MapView mMapView;
    @ViewInject(R.id.main_radio)
    private RadioGroup main_radio;
    @ViewInject(R.id.rb_lable)
    private RadioButton rb_lable;
    @ViewInject(R.id.rl_info)
    private RelativeLayout rl_info; // 点击标注弹出的详细信息
    @ViewInject(R.id.rl_content)
    private RelativeLayout rl_content;

    private BaiduMap mMap;// 相当于控制器

    private PmMapModel pmMapModel;// pm10业务类
    private BuildMapModel buildMapmodel;// 工地业务类

    private RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private InfoWindow mInfoWindow;// 气泡弹出框

    // 默认郑州经纬度
    private double lat = 34.7568711; // 维度
    private double lon = 113.663221; // 经度

    /**
     * 搜索相关
     */
    private boolean isSearch = false;// 是否搜索周边
    private int searchID = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_mapdiaplay);
        x.view().inject(this);
        initTitleBar();

        mMapView.showScaleControl(true);
        mMap = mMapView.getMap();
        mMap.setMaxAndMinZoomLevel(8, 15);
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setOverlookingGesturesEnabled(false);

        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        MapStatus.Builder builder = new MapStatus.Builder();
        LatLng ll = new LatLng(lat, lon);
        builder.target(ll).zoom(12.0f);
        mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder
                .build()));

        initModel();
        initEnent();
        rb_lable.setChecked(true);
    }

    private void initEnent() {
        main_radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mMap.clear();
                rl_info.setVisibility(View.GONE);
                mMap.setMyLocationEnabled(false);

                switch (checkedId) {
                    case R.id.rb_lable:
                        pmMapModel.setShow(true);
                        pmMapModel.showPMInMap();
                        buildMapmodel.setShow(false);
                        if (pmMapModel.isShowHotMap()) {
                            pmMapModel.removeHeatMap();
                        }
                        break;
                    case R.id.rb_colorration:
                        buildMapmodel.setShow(true);
                        buildMapmodel.showOverlay();
                        pmMapModel.setShow(false);
                        if (pmMapModel.isShowHotMap()) {
                            pmMapModel.removeHeatMap();
                        }
                        break;
                    case R.id.rb_hot:
                        pmMapModel.setShow(false);
                        pmMapModel.showHotMap();
                        buildMapmodel.setShow(false);
                        break;
                }
            }
        });
    }

    /**
     * 初始化业务类
     */
    private void initModel() {
        pmMapModel = new PmMapModel(this, mMap);
        buildMapmodel = new BuildMapModel(mContext, mMap);
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MapDisplayActivity.this.finish();
            }
        });
        mTopBar.setTitle("地图展示");
        mTopBar.setActionTextVisibility(true);
        mTopBar.setActionText("周边");
        mTopBar.setOnActionListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MapAroundDialogFra mapDialog = new MapAroundDialogFra();
                mapDialog.show(getSupportFragmentManager(), "MAPAROUND");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocClient != null) {
            mLocClient.stop();
        }
        // 关闭定位图层
        mMap.setMyLocationEnabled(false);
        mMapView.onDestroy();

        pmMapModel.destory();
        buildMapmodel.destory();
        pmMapModel = null;
        buildMapmodel = null;
    }

    /**
     * 标注物的点击事件
     */

    private LatLng nowLableLocation;
    private String bsiteNo;// 点击的工地编号
    private String bsiteName;// 点击的工地名称

    @Override
    public boolean onMarkerClick(final Marker marker) {
        TextView textView = new TextView(mContext);
        textView.setBackgroundResource(R.mipmap.tip_pointer_button_normal);
        textView.setPadding(30, 20, 30, 50);
        OnInfoWindowClickListener listener = null;
        listener = new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick() {
                mMap.hideInfoWindow();// 隐藏当前显示的气泡
                rl_info.setVisibility(View.GONE);
            }
        };
        LatLng ll = marker.getPosition();
        nowLableLocation = ll;
        Bundle bundle = marker.getExtraInfo();
        if (pmMapModel.isShow()) {
            if (bundle != null) {
                PMBean pmBean = (PMBean) bundle.getSerializable("info");
                if (pmBean != null) {
                    textView.setText("PM10:" + pmBean.pm10);
                    bsiteNo = pmBean.siteNo;
                    bsiteName = pmBean.siteName;

                    mInfoWindow = new InfoWindow(
                            BitmapDescriptorFactory.fromView(textView), ll,
                            -47, listener);
                    mMap.showInfoWindow(mInfoWindow);
                    // 设置详细信息布局为可见
                    rl_info.setVisibility(View.VISIBLE);
                    popupInfo(rl_info, pmBean.siteName, pmBean.pm10 + "");
                }
            }

        } else if (buildMapmodel.isShow()) {
            if (bundle != null) {
                Build build = (Build) bundle
                        .getSerializable("buildInfo");
                if (build != null) {
                    bsiteNo = build.siteNo;
                    bsiteName = build.siteName;

                    textView.setText(build.siteName);
                    mInfoWindow = new InfoWindow(
                            BitmapDescriptorFactory.fromView(textView), ll,
                            -47, listener);
                    mMap.showInfoWindow(mInfoWindow);
                    rl_info.setVisibility(View.VISIBLE);
                    popupInfo(rl_info, build.siteName, build.count + "");
                }
            }
        }

        return true;
    }

    /**
     * 展示标注详细信息
     *
     * @param rl_info
     * @param pm
     */
    private void popupInfo(RelativeLayout rl_info, String siteName, String pm) {
        ViewHolder viewHolder = null;
        if (rl_info.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.siteName = (TextView) rl_info
                    .findViewById(R.id.siteName);
            viewHolder.pm = (TextView) rl_info.findViewById(R.id.pm);

            rl_info.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) rl_info.getTag();
        viewHolder.siteName.setText(siteName);
        if (pmMapModel.isShow()) {
            viewHolder.pm.setText("PM10:" + pm);
        } else if (buildMapmodel.isShow()) {
            viewHolder.pm.setText("违章次数：" + pm);
        }
    }

    static class ViewHolder {
        TextView siteName;
        TextView pm;
    }

    /* 搜索周边 */
    public double dis;

    private void searchNear(int index) {
        switch (index) {
            case 0:
                if (pmMapModel.isShowHotMap()) {
                    pmMapModel.removeHeatMap();
                }
                pmMapModel.setShow(true);
                buildMapmodel.setShow(false);
                pmMapModel.showSearchView(myLocation, dis);
                break;

            case 1:
                if (pmMapModel.isShowHotMap()) {
                    pmMapModel.removeHeatMap();
                }
                buildMapmodel.setShow(true);
                pmMapModel.setShow(false);
                buildMapmodel.showSearchView(myLocation, dis);
                break;
        }
        isSearch = false;
    }

    /* 查看视频 */
    public void checkVideo(View view) {
        Intent intent = new Intent(mContext, CameraListActivity.class);

		/* 用于提交报告时的参数 */
        ReportUtils.getReportUtils().clear();
        ReportUtils.getReportUtils().setBsiteName(bsiteName);
        ReportUtils.getReportUtils().setBsiteNo(bsiteNo);

        intent.putExtra("bsiteNo", bsiteNo);
        startActivity(intent);

    }

    /* 去这里 */
    boolean isGoWhere = false; // 是否点击了去这里

    public void goWhere(View view) {
        if (routeOverlay != null) {
            routeOverlay.removeFromMap();
        }
        isGoWhere = true;
        location();
    }

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    boolean isFirstLoc = true; // 是否首次定位
    public LatLng myLocation;

    private void location() {
        mCurrentMode = LocationMode.NORMAL;
        mMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));
        // 开启定位图层
        mMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mMap.setMyLocationData(locData);
            isFirstLoc = false;
            myLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());// 定位到当前位置的坐标
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(myLocation).zoom(15.0f);
            mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder
                    .build()));
            mLocClient.stop();
            if (isGoWhere) {
                PlanNode stNode = PlanNode.withLocation(myLocation); // 定位的坐标
                PlanNode enNode = PlanNode.withLocation(nowLableLocation); // 要去的坐标
                mSearch.drivingSearch((new DrivingRoutePlanOption()).from(
                        stNode).to(enNode));
            }
            if (isSearch) {
                searchNear(searchID);
            }

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult arg0) {

    }

    RouteLine route = null;
    OverlayManager routeOverlay = null;

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MapDisplayActivity.this, "抱歉，未找到结果",
                    Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            List<DrivingRouteLine> list = result.getRouteLines();
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new DrivingRouteOverlay(mMap);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
        isGoWhere = false;
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult arg0) {

    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult arg0) {

    }

    /* 周边搜索 */
    @Override
    public void onCompleteSearch(int id, double distance, boolean search) {
        searchID = id;
        dis = distance;
        isSearch = search;
        location();
    }
}
