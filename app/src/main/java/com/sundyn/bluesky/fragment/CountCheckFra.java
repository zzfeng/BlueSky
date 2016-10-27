package com.sundyn.bluesky.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.CompatinCountDetailActivity;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.CompaintCount;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.view.NormalTopBar;
import com.sundyn.bluesky.view.pullrefreshview.PullToRefreshBase;
import com.sundyn.bluesky.view.pullrefreshview.PullToRefreshBase.OnRefreshListener;
import com.sundyn.bluesky.view.pullrefreshview.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import okhttp3.Call;

public class CountCheckFra extends BaseFragment {

    private View view;
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.chat_list_view)
    private PullToRefreshListView ptrLv;
    @ViewInject(R.id.loading_view)
    protected View loadingView;

    private String typeName;// 筛选的条件类型
    private String argCode;// 筛选已办结、已处理等
    private ArrayList<CompaintCount.CompaintCountItem> allData;
    public ArrayList<CompaintCount.CompaintCountItem> data_compaint;
    private QuickAdapter<CompaintCount.CompaintCountItem> compaintAdapter;
    private static int pageSize = 15;
    private static int pageStart = 0;
    private boolean hasMore = false;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fra_news, null);
        x.view().inject(this, view); // 注入view和事件
        mTopBar.setVisibility(View.GONE);

        // 上拉加载不可用
        ptrLv.setPullLoadEnabled(false);
        // 滚动到底自动加载可用
        ptrLv.setScrollLoadEnabled(true);
        setLastUpdateTime(); // 设置上次刷新时间
        initEvent();
        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        allData = new ArrayList<CompaintCount.CompaintCountItem>();
        data_compaint = new ArrayList<CompaintCount.CompaintCountItem>();
        compaintAdapter = new QuickAdapter<CompaintCount.CompaintCountItem>(
                mContext, R.layout.lv_countcheckfra_item) {

            @Override
            protected void convert(BaseAdapterHelper helper,
                                   CompaintCount.CompaintCountItem item) {
                helper.setText(R.id.tv_taskName, item.PrjName);
                helper.setText(R.id.tv_taskTopic, item.ComplaintType);
                helper.setText(R.id.tv_taskTime, item.ComplaintDate);
                TextView tv_taskStaus = helper.getView(R.id.tv_taskStaus);
                tv_taskStaus.setVisibility(View.VISIBLE);
                tv_taskStaus.setText(item.argName);
                if ("Ch01".equals(item.ComplaintStatus)) {
                    tv_taskStaus.setBackgroundDrawable(getResources()
                            .getDrawable(R.drawable.shape_circlerect_red));
                } else if ("Ch80".equals(item.ComplaintStatus)) {
                    tv_taskStaus.setBackgroundDrawable(getResources()
                            .getDrawable(R.drawable.shape_circlerect_normal));
                } else {
                    tv_taskStaus.setBackgroundDrawable(getResources()
                            .getDrawable(R.drawable.shape_circlerect_blue));
                }
            }
        };
        ptrLv.getRefreshableView().setAdapter(compaintAdapter);// 后面addData的时候会刷新
        getCountCheck(false);
    }

    private void getCountCheck(final boolean isRefresh) {
        if (!isRefresh) {
            pageStart = 0;
        }

        OkHttpUtils.get()
                .url(Constant.BASE_URL + Constant.URL_SELECTCOMPLAINTLIST)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("currentPage", pageStart + "")
                .addParams("pageSize", pageSize + "")
                .addParams("typeName", typeName).addParams("searchText", "")
                .addParams("userNo", mApplication.getUser().getUserNo())
                .addParams("argCode", argCode).build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int arg1) {
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
        CompaintCount mCompaintCount = GsonTools.changeGsonToBean(response,
                CompaintCount.class);
        if (mCompaintCount.success) {
            data_compaint = mCompaintCount.data;
            if (!isRefresh) {
                allData.clear();
                compaintAdapter.clear();
                compaintAdapter.addAll(data_compaint);
            } else {
                compaintAdapter.addAll(data_compaint);
            }
            allData.addAll(data_compaint);
            onLoaded();// 该方法要在setHasMoreData方法前使用
            if (data_compaint.size() < pageSize) {
                ptrLv.setHasMoreData(false);
                hasMore = false;
            } else {
                ptrLv.setHasMoreData(true);
                hasMore = true;
            }
            setLastUpdateTime();
        } else {
            showToast(mCompaintCount.message);
            onLoaded();
        }
    }

    private void initEvent() {
        ptrLv.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                getCountCheck(false);
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                if (hasMore) {
                    pageStart = pageStart + 1;
                    getCountCheck(true);
                }
            }
        });
        ptrLv.getRefreshableView().setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {
                        Intent intent = new Intent(mContext,
                                CompatinCountDetailActivity.class);
                        CompaintCount.CompaintCountItem noticeItem;
                        if (ptrLv.getRefreshableView().getHeaderViewsCount() > 0) {
                            noticeItem = allData.get(position - 1);
                        } else {
                            noticeItem = allData.get(position);
                        }
                        intent.putExtra(CompatinCountDetailActivity.ID,
                                noticeItem.ID);
                        intent.putExtra(
                                CompatinCountDetailActivity.COMPLAINTSTATUS,
                                noticeItem.ComplaintStatus);
                        intent.putExtra(CompatinCountDetailActivity.ARGNAME,
                                noticeItem.argName);
                        startActivity(intent);

                    }
                });

    }

    private void setLastUpdateTime() {
        String label = DateUtils.formatDateTime(mContext,
                System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL);
        ptrLv.setLastUpdatedLabel(label);
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

}
