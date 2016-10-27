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

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.CountCheckActivity;
import com.sundyn.bluesky.activity.ReformNoticeDetailActivity;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.ReformNotice;
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

public class ReformNoticeFra extends BaseFragment {

    private View view;
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.chat_list_view)
    private PullToRefreshListView ptrLv;
    @ViewInject(R.id.loading_view)
    protected View loadingView;

    private ArrayList<ReformNotice.Notice> allDataNotices;
    public ArrayList<ReformNotice.Notice> data_notices;
    private QuickAdapter<ReformNotice.Notice> noticeAdapter;
    private static int pageSize = 15;
    private static int pageStart = 0;
    private boolean hasMore = false;

    private String typeName;// ɸѡ����������
    private CountCheckActivity activity;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fra_news, null);
        x.view().inject(this, view); // ע��view���¼�
        mTopBar.setVisibility(View.GONE);
        activity = (CountCheckActivity) getActivity();

        // �������ز�����
        ptrLv.setPullLoadEnabled(false);
        // ���������Զ����ؿ���
        ptrLv.setScrollLoadEnabled(true);
        setLastUpdateTime(); // �����ϴ�ˢ��ʱ��
        initEvent();
        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        allDataNotices = new ArrayList<ReformNotice.Notice>();
        data_notices = new ArrayList<ReformNotice.Notice>();
        noticeAdapter = new QuickAdapter<ReformNotice.Notice>(mContext,
                R.layout.lv_countcheckfra_item) {

            @Override
            protected void convert(BaseAdapterHelper helper,
                                   ReformNotice.Notice item) {
                helper.setText(R.id.tv_taskName, item.PrjName);
                if (TextUtils.isEmpty(item.StatusName)) {
                    helper.setText(R.id.tv_taskTopic, "δ�ظ�");
                } else {
                    helper.setText(R.id.tv_taskTopic, item.StatusName);
                }
                helper.setText(R.id.tv_taskTime, item.CreateDate);
            }
        };
        ptrLv.getRefreshableView().setAdapter(noticeAdapter);// ����addData��ʱ���ˢ��
        getReformNotice(false);
    }

    // ��ȡ��������
    private void getReformNotice(final boolean isRefresh) {
        if (!isRefresh) {
            pageStart = 0;
        }
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_ZGTZONELIST)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("currentPage", pageStart + "")
                .addParams("pageSize", pageSize + "")
                .addParams("typeName", typeName).addParams("searchText", "")
                .addParams("userNo", mApplication.getUser().getUserNo())
                .build().execute(new StringCallback() {
            @Override
            public void onResponse(String response, int arg1) {
                if (!TextUtils.isEmpty(response)) {
                    processData(response, isRefresh);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                showToast("����ʧ�ܣ��������������");
                onLoaded();
            }
        });
    }

    /**
     * @param response
     * @param isRefresh �Ƿ���ظ���
     */
    private void processData(String response, boolean isRefresh) {
        ReformNotice reformNotice = GsonTools.changeGsonToBean(response,
                ReformNotice.class);
        if (reformNotice.success) {
            data_notices = reformNotice.data;
            if (!isRefresh) {
                allDataNotices.clear();
                noticeAdapter.clear();
                noticeAdapter.addAll(data_notices);
            } else {
                noticeAdapter.addAll(data_notices);
            }
            allDataNotices.addAll(data_notices);
            onLoaded();// �÷���Ҫ��setHasMoreData����ǰʹ��
            if (data_notices.size() < pageSize) {
                ptrLv.setHasMoreData(false);
                hasMore = false;
            } else {
                ptrLv.setHasMoreData(true);
                hasMore = true;
            }
            setLastUpdateTime();
        } else {
            showToast(reformNotice.message);
            onLoaded();
        }
    }

    private void initEvent() {
        ptrLv.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                getReformNotice(false);
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                if (hasMore) {
                    pageStart = pageStart + 1;
                    getReformNotice(true);
                }
            }
        });
        ptrLv.getRefreshableView().setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {
                        Intent intent = new Intent(mContext,
                                ReformNoticeDetailActivity.class);
                        ReformNotice.Notice noticeItem;
                        if (ptrLv.getRefreshableView().getHeaderViewsCount() > 0) {
                            noticeItem = allDataNotices.get(position - 1);
                        } else {
                            noticeItem = allDataNotices.get(position);
                        }
                        intent.putExtra(ReformNoticeDetailActivity.NOTICEID,
                                noticeItem.ID);
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
     * ���ݴ������
     */
    private void onLoaded() {
        dismissLoadingView();
        ptrLv.onPullDownRefreshComplete();
        ptrLv.onPullUpRefreshComplete();
    }

    /**
     * ���سɹ�֮��ȥ�����ȿ�
     */
    public void dismissLoadingView() {
        if (loadingView != null)
            loadingView.setVisibility(View.INVISIBLE);
    }

}
