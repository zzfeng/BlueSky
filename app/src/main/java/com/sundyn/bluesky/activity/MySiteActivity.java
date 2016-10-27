package com.sundyn.bluesky.activity;

import android.animation.ObjectAnimator;
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
import com.sundyn.bluesky.bean.MySiteBean;
import com.sundyn.bluesky.bean.MySiteBean.MySite;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.view.NormalTopBar;
import com.sundyn.bluesky.view.pullrefreshview.PullToRefreshBase;
import com.sundyn.bluesky.view.pullrefreshview.PullToRefreshBase.OnRefreshListener;
import com.sundyn.bluesky.view.pullrefreshview.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MySiteActivity extends BaseActivity implements OnItemClickListener {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.chat_list_view)
    private PullToRefreshListView ptrLv;
    @ViewInject(R.id.loading_view)
    protected View loadingView;

    private ArrayList<MySite> allDataSites;
    private QuickAdapter<MySite> adapter;
    private static int pageSize = 15;
    private static int pageStart = 1;
    private List<MySite> pmList;// 超标工地数据
    private boolean hasMore = false;

    private int visiableIndex = Integer.MAX_VALUE;
    private boolean isShowChoice = false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.fra_news);
        x.view().inject(this);
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MySiteActivity.this.finish();
            }
        });
        mTopBar.setTitle("我的工地");
        mTopBar.setActionTextVisibility(false);
        ptrLv.setPullRefreshEnabled(false);
        // 上拉加载不可用
        ptrLv.setPullLoadEnabled(false);
        // 滚动到底自动加载可用
        ptrLv.setScrollLoadEnabled(true);
        initEvent();
        initData();

    }

    private void initData() {
        allDataSites = new ArrayList<MySite>();
        pmList = new ArrayList<MySite>();
        adapter = new QuickAdapter<MySite>(mContext,
                R.layout.lv_mysite_item) {

            @Override
            protected void convert(BaseAdapterHelper helper,
                                   final MySite item) {
                helper.setText(R.id.tv_sitename, item.bsiteName);
                helper.getView(R.id.id_mysite_video).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext,
                                        CameraListActivity.class);
                                intent.putExtra("bsiteNo", item.bsiteNo);
                                startActivity(intent);
                            }
                        });
                helper.getView(R.id.id_mysite_datacenter).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext,
                                        SiteDataCenterActivity.class);
                                intent.putExtra("siteNo", item.bsiteNo);
                                intent.putExtra("siteName", item.bsiteName);
                                startActivity(intent);

                            }
                        });

                MySite mySite = adapter.getItem(visiableIndex);
                if (mySite != null) {
                    if (item.bsiteNo.equals(mySite.bsiteNo)) {
                        if (!isShowChoice) {
                            ObjectAnimator
                                    .ofFloat(helper.getView(R.id.id_arrow),
                                            "rotation", 0.0F, 180.0F)
                                    .setDuration(500).start();
                            helper.getView(R.id.id_rt_mySiteOfMore)
                                    .setVisibility(View.VISIBLE);
                            ObjectAnimator
                                    .ofFloat(
                                            helper.getView(R.id.id_rt_mySiteOfMore),
                                            "alpha", 0, 0.25f, 1)
                                    .setDuration(500).start();
                            isShowChoice = true;

                        } else {
                            ObjectAnimator
                                    .ofFloat(helper.getView(R.id.id_arrow),
                                            "rotation", 180.0F, 360.0F)
                                    .setDuration(500).start();
                            helper.getView(R.id.id_rt_mySiteOfMore)
                                    .setVisibility(View.GONE);

                            isShowChoice = false;

                        }

                    }

                }

            }
        };
        ptrLv.getRefreshableView().setAdapter(adapter);// 后面addData的时候会刷新
        getPmPlusSite(false);
    }

    private void initEvent() {
        ptrLv.getRefreshableView().setOnItemClickListener(this);
        ptrLv.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                if (hasMore) {
                    pageStart = pageStart + pageSize;
                    getPmPlusSite(true);
                }
            }
        });
    }

    /**
     * 获取超标工地
     *
     * @param //areaCode2
     */
    private void getPmPlusSite(final boolean isRefresh) {
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_MYSITE)
                .addParams("userNo", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken()).build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        if (!TextUtils.isEmpty(response)) {
                            processData(response, isRefresh);
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("加载失败，请检查服务器连接");
                        onLoaded();
                    }
                });
    }

    /**
     * @param response
     * @param isRefresh 是否加载更多
     */
    private void processData(String response, boolean isRefresh) {
        MySiteBean mySiteBean = GsonTools.changeGsonToBean(response,
                MySiteBean.class);
        if (mySiteBean.success) {
            pmList = mySiteBean.data;
            if (!isRefresh) {
                allDataSites.clear();
                adapter.clear();
                adapter.addAll(pmList);
            } else {
                adapter.addAll(pmList);
            }
            allDataSites.addAll(pmList);
            onLoaded();// 该方法要在setHasMoreData方法前使用
            if (pmList.size() < pageSize) {
                ptrLv.setHasMoreData(false);
                hasMore = false;
            } else {
                ptrLv.setHasMoreData(true);
                hasMore = true;
            }
        } else {
            showToast(mySiteBean.message);
            onLoaded();
        }
    }

    /**
     * 数据处理完成
     */
    private void onLoaded() {
        dismissLoadingView();
        ptrLv.onPullDownRefreshComplete();
        ptrLv.onPullUpRefreshComplete();
    }

    /**
     * 加载成功之后去除进度框
     */
    public void dismissLoadingView() {
        if (loadingView != null)
            loadingView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int positon,
                            long arg3) {
        visiableIndex = positon;
        adapter.notifyDataSetChanged();
    }
}
