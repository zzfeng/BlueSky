package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.sundyn.bluesky.model.AllSiteMapModel;
import com.sundyn.bluesky.utils.DrivingRouteOverlay;
import com.sundyn.bluesky.utils.OverlayManager;
import com.sundyn.bluesky.utils.ReportUtils;
import com.sundyn.bluesky.view.MapAroundDialogFra;
import com.sundyn.bluesky.view.MapAroundDialogFra.MapAroundListener;
import com.sundyn.bluesky.view.NormalTopBar;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class AllSiteMapActivity extends BaseActivity implements
        OnMarkerClickListener, OnGetRoutePlanResultListener, MapAroundListener {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.bmapView)
    private MapView mMapView;
    @ViewInject(R.id.rl_info)
    private RelativeLayout rl_info; // �����ע��������ϸ��Ϣ
    @ViewInject(R.id.rl_content)
    private RelativeLayout rl_content;

    private BaiduMap mMap;// �൱�ڿ�����

    private AllSiteMapModel allSiteMapModel;// ����ҵ����

    private RoutePlanSearch mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
    private InfoWindow mInfoWindow;// ���ݵ�����

    // Ĭ��֣�ݾ�γ��
    private double lat = 34.7568711; // ά��
    private double lon = 113.663221; // ����

    /**
     * �������
     */
    private boolean isSearch = false;// �Ƿ������ܱ�
    private int searchID = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_allsitemap);
        x.view().inject(this);
        initTitleBar();

        mMapView.showScaleControl(true);
        mMap = mMapView.getMap();
        mMap.setMaxAndMinZoomLevel(8, 15);
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setOverlookingGesturesEnabled(false);

        // ��ʼ������ģ�飬ע���¼�����
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        MapStatus.Builder builder = new MapStatus.Builder();
        LatLng ll = new LatLng(lat, lon);
        builder.target(ll).zoom(12.0f);
        mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder
                .build()));

        initMapData();
    }

    private void initMapData() {
        if (allSiteMapModel == null)
            allSiteMapModel = new AllSiteMapModel(mContext, mMap);
        allSiteMapModel.setShow(true);
        allSiteMapModel.showOverlay();
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AllSiteMapActivity.this.finish();
            }
        });
        mTopBar.setTitle("ȫ������");
        mTopBar.setActionTextVisibility(true);
        mTopBar.setActionText("�ܱ�");
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
        // �رն�λͼ��
        mMap.setMyLocationEnabled(false);
        mMapView.onDestroy();

        allSiteMapModel.destory();
        allSiteMapModel = null;

    }

    /**
     * ��ע��ĵ���¼�
     */

    private LatLng nowLableLocation;
    private String bsiteNo;// ����Ĺ��ر��
    private String bsiteName;// ����Ĺ�������

    @Override
    public boolean onMarkerClick(final Marker marker) {
        TextView textView = new TextView(mContext);
        textView.setBackgroundResource(R.mipmap.tip_pointer_button_normal);
        textView.setPadding(30, 20, 30, 50);
        OnInfoWindowClickListener listener = null;
        listener = new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick() {
                mMap.hideInfoWindow();// ���ص�ǰ��ʾ������
                rl_info.setVisibility(View.GONE);
            }
        };
        LatLng ll = marker.getPosition();
        nowLableLocation = ll;
        Bundle bundle = marker.getExtraInfo();
        if (allSiteMapModel.isShow()) {
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
     * չʾ��ע��ϸ��Ϣ
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
        if (allSiteMapModel.isShow()) {
            viewHolder.pm.setText("Υ�´�����" + pm);
        }
    }

    static class ViewHolder {
        TextView siteName;
        TextView pm;
    }

    /**
     * �����ܱ�
     *
     * @param dis
     * ��λ����
     */
    public double dis;

    private void searchNear(int index) {
        switch (index) {
            case 1:
                allSiteMapModel.setShow(true);
                allSiteMapModel.showSearchView(myLocation, dis);
                break;
        }
        isSearch = false;
    }

    /**
     * �鿴��Ƶ
     *
     * @param view
     */
    public void checkVideo(View view) {
        Intent intent = new Intent(mContext, CameraListActivity.class);

		/* �����ύ����ʱ�Ĳ��� */
        ReportUtils.getReportUtils().clear();
        ReportUtils.getReportUtils().setBsiteName(bsiteName);
        ReportUtils.getReportUtils().setBsiteNo(bsiteNo);

        intent.putExtra("bsiteNo", bsiteNo);
        startActivity(intent);

    }

    /**
     * ȥ����
     *
     * @param view
     */
    boolean isGoWhere = false; // �Ƿ�����ȥ����

    public void goWhere(View view) {
        if (routeOverlay != null) {
            routeOverlay.removeFromMap();
        }
        isGoWhere = true;
        location();
    }

    // ��λ���
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    boolean isFirstLoc = true; // �Ƿ��״ζ�λ
    public LatLng myLocation;

    private void location() {
        mCurrentMode = LocationMode.NORMAL;
        mMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));
        // ������λͼ��
        mMap.setMyLocationEnabled(true);
        // ��λ��ʼ��
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // ��gps
        option.setCoorType("bd09ll"); // ������������
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * ��λSDK��������
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view ���ٺ��ڴ����½��յ�λ��
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mMap.setMyLocationData(locData);
            isFirstLoc = false;
            myLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());// ��λ����ǰλ�õ�����
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(myLocation).zoom(15.0f);
            mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder
                    .build()));
            mLocClient.stop();
            if (isGoWhere) {
                PlanNode stNode = PlanNode.withLocation(myLocation); // ��λ������
                PlanNode enNode = PlanNode.withLocation(nowLableLocation); // Ҫȥ������
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
            Toast.makeText(AllSiteMapActivity.this, "��Ǹ��δ�ҵ����",
                    Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // ���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
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

    /* �ܱ����� */
    @Override
    public void onCompleteSearch(int id, double distance, boolean search) {
        searchID = id;
        dis = distance;
        isSearch = search;
        location();
    }
}
