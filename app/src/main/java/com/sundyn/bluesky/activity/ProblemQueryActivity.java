package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.ProblemCamera;
import com.sundyn.bluesky.bean.ProblemCamera.ProCamera;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.view.NormalTopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * @author yangjl
 *         描述：故障查询activity
 *         2016-6-22上午10:40:14
 */
public class ProblemQueryActivity extends BaseActivity {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.lv_problemquery)
    private ListView lv_problemquery;
    @ViewInject(R.id.loading_view)
    protected View loadingView;

    private QuickAdapter<ProCamera> problemCameraAdapter;
    private ArrayList<ProCamera> problemList;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_problemquery);
        x.view().inject(this);
        initTitleBar();
        initData();
    }

    private void initData() {
        problemList = new ArrayList<ProCamera>();
        problemCameraAdapter = new QuickAdapter<ProCamera>(mContext, R.layout.lv_problemquery_item) {

            @Override
            protected void convert(BaseAdapterHelper helper, final ProCamera item) {
                helper.setText(R.id.tv_taskName, item.siteName);
                helper.setText(R.id.tv_camerasite, item.cameraName);
                helper.setText(R.id.tv_camerano, item.cameraCode);
                helper.getView(R.id.checkVideo).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(mContext, ProblemVideoDetailActivity.class);
                        intent.putExtra("cameraCode", item.cameraCode);
                        intent.putExtra("id", item.id);
                        startActivity(intent);
                    }
                });
            }
        };
        lv_problemquery.setAdapter(problemCameraAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoadingView();
        getCameraList();
    }

    private void getCameraList() {
        OkHttpUtils.post().url(Constant.BASE_URL + Constant.URL_GETERRORCAMERA)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        if (!TextUtils.isEmpty(response)) {
                            ProblemCamera problemCamera = GsonTools.changeGsonToBean(response, ProblemCamera.class);
                            problemList = problemCamera.rows;
                            problemCameraAdapter.clear();
                            problemCameraAdapter.addAll(problemList);
                        }
                        dismissLoadingView();
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        dismissLoadingView();
                        showToast("获取数据失败");
                    }
                });
    }


    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ProblemQueryActivity.this.finish();
            }
        });
        mTopBar.setTitle("故障列表");
        mTopBar.setActionTextVisibility(false);
    }

    /**
     * 加载成功之后去除进度框
     */
    public void dismissLoadingView() {
        if (loadingView != null)
            loadingView.setVisibility(View.INVISIBLE);
    }

    public void showLoadingView() {
        if (loadingView != null)
            loadingView.setVisibility(View.VISIBLE);
    }
}
