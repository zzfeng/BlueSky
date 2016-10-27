package com.sundyn.bluesky.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.SiteCheckDetailActivity;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.SiteBean;
import com.sundyn.bluesky.bean.SiteBean.Site;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.utils.IntentParam;
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

import okhttp3.Call;

public class SiteCheckFragment extends BaseFragment implements
        OnItemClickListener {

    private View view;
    private int areaCode;
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.chat_list_view)
    private PullToRefreshListView ptrLv;
    @ViewInject(R.id.loading_view)
    protected View loadingView;
    private ArrayList<Site> siteList;
    private ArrayList<Site> allSiteList;
    private QuickAdapter<Site> siteAdapter;

    private static int pageSize = 15;
    private static int pageStart = 0;
    private boolean hasMore = false;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fra_news, null);
        x.view().inject(this, view); // 注入view和事件
        mTopBar.setVisibility(View.GONE);
        ptrLv.setPullRefreshEnabled(false);
        // 上拉加载不可用
        ptrLv.setPullLoadEnabled(false);
        // 滚动到底自动加载可用
        ptrLv.setScrollLoadEnabled(true);
        initEvent();
        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        siteList = new ArrayList<Site>();
        allSiteList = new ArrayList<Site>();
        Bundle mBundle = getArguments();
        areaCode = mBundle.getInt("areaCode");
        siteAdapter = new QuickAdapter<Site>(mContext,
                R.layout.lv_sitecheck_item) {

            @Override
            protected void convert(BaseAdapterHelper helper, Site item) {
                helper.setText(R.id.tv_video_item, item.bsiteName);
                TextView tv_pm = helper.getView(R.id.id_iv_pm);
                TextView tv_video = helper.getView(R.id.id_iv_video);
                int siteJudge = item.siteJudge;
                int green = mContext.getResources().getColor(
                        R.color.bt_bg_login);
                int red = mContext.getResources().getColor(R.color.red);
                switch (siteJudge) {
                    case 1:
                        // 只有pm10
                        tv_video.setVisibility(View.GONE);
                        tv_pm.setVisibility(View.VISIBLE);
                        if (item.pmIsTbj) {
                            tv_pm.setTextColor(green);
                        } else {
                            tv_pm.setTextColor(red);
                        }
                        break;
                    case 2:
                        // 只有video
                        tv_pm.setVisibility(View.GONE);
                        tv_video.setVisibility(View.VISIBLE);
                        if (item.isTbj == 1) {
                            tv_video.setTextColor(green);
                        } else {
                            tv_video.setTextColor(red);
                        }
                        break;
                    case 3:
                        // 双接入
                        tv_pm.setVisibility(View.VISIBLE);
                        tv_video.setVisibility(View.VISIBLE);
                        if (item.pmIsTbj) {
                            tv_pm.setTextColor(green);
                        } else {
                            tv_pm.setTextColor(red);
                        }
                        if (item.isTbj == 1) {
                            tv_video.setTextColor(green);
                        } else {
                            tv_video.setTextColor(red);
                        }
                        break;

                    default:
                        // 无接入
                        tv_pm.setVisibility(View.GONE);
                        tv_video.setVisibility(View.GONE);
                        break;
                }
            }
        };
        ptrLv.getRefreshableView().setAdapter(siteAdapter);
        ptrLv.getRefreshableView().setOnItemClickListener(this);
        getSiteTbj(false);
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
                    pageStart = pageStart + 1;
                    getSiteTbj(true);
                }
            }
        });
    }

    private void getSiteTbj(final boolean isRefresh) {
        if (!isRefresh) {
            pageStart = 0;
        }
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_SITETBJ)
                .addParams("page", pageStart + "")
                .addParams("size", pageSize + "")
                .addParams("areaId", areaCode + "").build()
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
                        showToast("获取数据失败");
                        onLoaded();
                    }
                });
    }

    private void processData(String response, boolean isRefresh) {
        SiteBean siteBean = GsonTools
                .changeGsonToBean(response, SiteBean.class);
        if (siteBean.success) {
            siteList = siteBean.data;
            if (!isRefresh) {
                allSiteList.clear();
                siteAdapter.clear();
                siteAdapter.addAll(siteList);
            } else {
                siteAdapter.addAll(siteList);
            }
            allSiteList.addAll(siteList);
            onLoaded();// 该方法要在setHasMoreData方法前使用
            if (siteList.size() < pageSize) {
                ptrLv.setHasMoreData(false);
                hasMore = false;
            } else {
                ptrLv.setHasMoreData(true);
                hasMore = true;
            }
            setLastUpdateTime(ptrLv);
        } else {
            showToast(siteBean.message);
            onLoaded();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        Intent intent = new Intent(mContext, SiteCheckDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(IntentParam.PRJNAME,
                siteAdapter.getItem(position).bsiteName);
        bundle.putString(IntentParam.PRJADDRESS,
                siteAdapter.getItem(position).bsiteLoc);
        bundle.putString(IntentParam.AREA, siteAdapter.getItem(position).xzqhMc);
        bundle.putString(IntentParam.BUILDUNIT,
                siteAdapter.getItem(position).buildUnit);
        bundle.putString(IntentParam.MANAGER,
                siteAdapter.getItem(position).bsiteMgr);
        bundle.putString(IntentParam.PHONE,
                siteAdapter.getItem(position).linkphone);
        intent.putExtra(IntentParam.SITEBEAN, bundle);
        startActivity(intent);
    }

    private void onLoaded() {
        dismissLoadingView();
        ptrLv.onPullDownRefreshComplete();
        ptrLv.onPullUpRefreshComplete();
    }

    public void dismissLoadingView() {
        if (loadingView != null)
            loadingView.setVisibility(View.INVISIBLE);
    }
}
