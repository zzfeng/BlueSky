package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.CameraBean;
import com.sundyn.bluesky.bean.CameraBean.Camera;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.view.NormalTopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Random;

import okhttp3.Call;

public class CameraListActivity extends BaseActivity implements
        OnItemClickListener {

    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.lv_item_camear)
    private ListView lv_camera;
    @ViewInject(R.id.loading_view)
    protected View loadingView;
    private String bsiteNo;// 工地编号
    private Random random = new Random();

    private ArrayList<Camera> cameraList;
    private QuickAdapter<Camera> cameraAdapter;
    private int[] colors = {R.color.bg_camearlist_1, R.color.bg_camearlist_2,
            R.color.bg_camearlist_3, R.color.bg_camearlist_4,
            R.color.bg_camearlist_5, R.color.bg_camearlist_6};

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_cameralist);
        x.view().inject(this);
        initTitleBar();
        initView();
        initData();
    }

    private void initView() {

    }

    private void initData() {
        cameraList = new ArrayList<Camera>();

        Intent intent = getIntent();
        if (intent != null) {
            bsiteNo = intent.getStringExtra("bsiteNo");
        }

        cameraAdapter = new QuickAdapter<Camera>(mContext,
                R.layout.gv_cameralist_item) {

            @Override
            protected void convert(BaseAdapterHelper helper, Camera item) {
                helper.setText(R.id.item_name, item.name);
                int index_color = random.nextInt(colors.length);
                helper.getView(R.id.rt_cameralist).setBackgroundColor(getResources().getColor(colors[index_color]));
            }

        };
        OkHttpUtils.post().url(Constant.BASE_URL + Constant.URL_GETCAMERALIST)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("siteNo", bsiteNo).build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        if (!TextUtils.isEmpty(response)) {
                            CameraBean cameraBean = GsonTools.changeGsonToBean(
                                    response, CameraBean.class);
                            if (cameraBean.success) {
                                cameraList = cameraBean.data;
                                cameraAdapter.addAll(cameraList);
                            } else {
                                showToast(cameraBean.message);
                            }
                        }
                        dismissLoadingView();
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("获取数据失败");
                        dismissLoadingView();
                    }
                });

        lv_camera.setAdapter(cameraAdapter);
        lv_camera.setOnItemClickListener(this);
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CameraListActivity.this.finish();
            }
        });
        mTopBar.setTitle("选择您要查看的摄像头");
        mTopBar.setActionTextVisibility(false);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        Intent intent = new Intent(mContext, CameraDetailActivity.class);
        intent.putExtra(Constant.Hik.CAMERA_ID, cameraList.get(position).value);
        intent.putExtra("camera_name", cameraList.get(position).name);
        startActivity(intent);
    }

    public void dismissLoadingView() {
        if (loadingView != null)
            loadingView.setVisibility(View.INVISIBLE);
    }

}
