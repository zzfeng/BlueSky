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

import com.squareup.picasso.Picasso;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.NewDetailActivity;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.NewsListBean;
import com.sundyn.bluesky.bean.NewsListBean.News;
import com.sundyn.bluesky.utils.CommonUtil;
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

public class NewsFra extends BaseFragment {

    private View view;
    private NormalTopBar mTopBar;

    @ViewInject(R.id.chat_list_view)
    private PullToRefreshListView ptrLv;
    @ViewInject(R.id.loading_view)
    protected View loadingView;

    private ArrayList<News> allDataNews;
    public ArrayList<News> data_news;
    private QuickAdapter<News> newsAdapter;
    private static int pageSize = 15;
    private static int pageStart = 0;
    private boolean hasMore = false;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fra_news, null);
        x.view().inject(this, view); // 注入view和事件
        mTopBar = (NormalTopBar) view.findViewById(R.id.chat_top_bar);
        mTopBar.setBackVisibility(false);
        mTopBar.setTitle("绿城新闻");
        mTopBar.setActionTextVisibility(false);
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
        allDataNews = new ArrayList<News>();
        data_news = new ArrayList<News>();
        newsAdapter = new QuickAdapter<News>(mContext,
                R.layout.lv_news_item) {

            @Override
            protected void convert(BaseAdapterHelper helper, News item) {
                helper.setText(R.id.new_title, item.title);
                helper.setText(R.id.new_time, item.create_time);
                if (item.img != null && !"".endsWith(item.img)) {
                    helper.getView(R.id.iv_news_pic)
                            .setVisibility(View.VISIBLE);
                    helper.setImageBuilder(
                            R.id.iv_news_pic,
                            Picasso.with(mContext)
                                    .load(item.img)
                                    .placeholder(R.mipmap.pictures_no)
                                    .error(R.mipmap.pictures_no)
                                    .resize(CommonUtil.dip2px(mContext, 105),
                                            CommonUtil.dip2px(mContext, 70))
                                    .centerCrop());
                } else {
                    helper.getView(R.id.iv_news_pic).setVisibility(View.GONE);
                }

            }
        };
        ptrLv.getRefreshableView().setAdapter(newsAdapter);// 后面addData的时候会刷新
        getNewsList(false);
    }

    // 获取新闻数据
    private void getNewsList(final boolean isRefresh) {
        if (!isRefresh) {
            pageStart = 0;
        }
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_NEWLIST)
                .addParams("first", pageStart + "")
                .addParams("size", pageSize + "").build()
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
        NewsListBean newsListObject = GsonTools.changeGsonToBean(response,
                NewsListBean.class);
        if (newsListObject.success) {
            data_news = newsListObject.data;
            if (!isRefresh) {
                allDataNews.clear();
                newsAdapter.clear();
                newsAdapter.addAll(data_news);
            } else {
                newsAdapter.addAll(data_news);
            }
            allDataNews.addAll(data_news);
            onLoaded();// 该方法要在setHasMoreData方法前使用
            if (data_news.size() < pageSize) {
                ptrLv.setHasMoreData(false);
                hasMore = false;
            } else {
                ptrLv.setHasMoreData(true);
                hasMore = true;
            }
            setLastUpdateTime();
        } else {
            showToast(newsListObject.message);
            onLoaded();
        }
    }

    private void initEvent() {
        ptrLv.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                getNewsList(false);
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                if (hasMore) {
                    pageStart = pageStart + pageSize;
                    getNewsList(true);
                }
            }
        });
        ptrLv.getRefreshableView().setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {
                        Intent intent = new Intent(mContext,
                                NewDetailActivity.class);
                        String notice_id = "";
                        String title;
                        String time;
                        News newsItem;
                        if (ptrLv.getRefreshableView().getHeaderViewsCount() > 0) {
                            newsItem = allDataNews.get(position - 1);
                        } else {
                            newsItem = allDataNews.get(position);
                        }
                        notice_id = newsItem.notice_id;
                        title = newsItem.title;
                        time = newsItem.create_time;
                        intent.putExtra("notice_id", notice_id);
                        intent.putExtra("title", title);
                        intent.putExtra("time", time);
                        intent.putExtra("content", newsItem.content);
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
