package com.sundyn.bluesky.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.CheckNotice;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
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

public class CheckOfMyFra extends BaseFragment implements OnItemClickListener {
    private View view;

    @ViewInject(R.id.chat_list_view)
    private PullToRefreshListView ptrLv;
    @ViewInject(R.id.loading_view)
    protected View loadingView;

    private List<CheckNotice.Notices> allData;
    private QuickAdapter<CheckNotice.Notices> adapter;
    private static int pageSize = 15;
    private static int pageStart = 0;
    private List<CheckNotice.Notices> noticeList;// 超标工地数据
    private boolean hasMore = false;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fra_checkofmy, null);
        x.view().inject(this, view); // 注入view和事件
        ptrLv.setPullRefreshEnabled(false);
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
        allData = new ArrayList<CheckNotice.Notices>();
        noticeList = new ArrayList<CheckNotice.Notices>();
        adapter = new QuickAdapter<CheckNotice.Notices>(mContext,
                R.layout.lv_checkofmy_item) {

            @Override
            protected void convert(BaseAdapterHelper helper,
                                   final CheckNotice.Notices item) {
                helper.setText(R.id.tv_sitename, item.PrjName);
                helper.setText(R.id.tv_checkTime, item.CheckDate);
                helper.setText(R.id.id_tv_createby, item.CreateBy);
                helper.setText(R.id.id_tv_checkconent, item.Descrip);
            }
        };

        ptrLv.getRefreshableView().setAdapter(adapter);// 后面addData的时候会刷新
        getCheckNotice(false);

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
                    getCheckNotice(true);
                }
            }
        });
    }

    /**
     * 获取超标工地
     *
     * @param //areaCode2
     */
    private void getCheckNotice(final boolean isRefresh) {
        if (!isRefresh) {
            pageStart = 0;
        }
        OkHttpUtils.get()
                .url(Constant.BASE_URL + Constant.URL_SELECTCHECKRECORDLIST)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("userNo", mApplication.getUser().getUserNo())
                .addParams("PageSize", pageSize + "")
                .addParams("CurrentPage", pageStart + "")
                .addParams("typeName", "")
                .addParams("searchText", "").build()
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
        CheckNotice mCheckNotice = GsonTools.changeGsonToBean(response,
                CheckNotice.class);
        if (mCheckNotice.success) {
            noticeList = mCheckNotice.data;
            if (!isRefresh) {
                allData.clear();
                adapter.clear();
                adapter.addAll(noticeList);
            } else {
                adapter.addAll(noticeList);
            }
            allData.addAll(noticeList);
            onLoaded();// 该方法要在setHasMoreData方法前使用
            if (noticeList.size() < pageSize) {
                ptrLv.setHasMoreData(false);
                hasMore = false;
            } else {
                ptrLv.setHasMoreData(true);
                hasMore = true;
            }
            setLastUpdateTime();
        } else {
            showToast(mCheckNotice.message);
            onLoaded();
        }
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

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int positon,
                            long arg3) {
    }

}
