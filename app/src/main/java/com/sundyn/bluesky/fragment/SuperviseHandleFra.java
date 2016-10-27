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
import com.sundyn.bluesky.activity.SuperviseHandleDetailActivity;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.SuperviseHandle;
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

/**
 * @author yangjl
 * @date 2016-8-10����4:37:35
 * @�汾��1.0
 * @����������߰�ҳ��
 */
public class SuperviseHandleFra extends BaseFragment {

    private View view;
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.chat_list_view)
    private PullToRefreshListView ptrLv;
    @ViewInject(R.id.loading_view)
    protected View loadingView;

    private ArrayList<SuperviseHandle.Supervise> allDataSupervise;
    public ArrayList<SuperviseHandle.Supervise> data_supervise;
    private QuickAdapter<SuperviseHandle.Supervise> superviseAdapter;
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
        allDataSupervise = new ArrayList<SuperviseHandle.Supervise>();
        data_supervise = new ArrayList<SuperviseHandle.Supervise>();
        superviseAdapter = new QuickAdapter<SuperviseHandle.Supervise>(
                mContext, R.layout.lv_countcheckfra_item) {

            @Override
            protected void convert(BaseAdapterHelper helper,
                                   SuperviseHandle.Supervise item) {
                helper.setText(R.id.tv_taskName, item.PrjName);
                if (TextUtils.isEmpty(item.StatusName)) {
                    helper.setText(R.id.tv_taskTopic, "δ�ظ�");
                } else {
                    helper.setText(R.id.tv_taskTopic, item.StatusName);
                }
                helper.setText(R.id.tv_taskTime, item.PublishDate);
            }
        };
        ptrLv.getRefreshableView().setAdapter(superviseAdapter);// ����addData��ʱ���ˢ��
        getSuperviseHandle(false);
    }

    // ��ȡ��������
    private void getSuperviseHandle(final boolean isRefresh) {
        if (!isRefresh) {
            pageStart = 0;
        }
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_DBCBLIST)
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
        SuperviseHandle superviseHandle = GsonTools.changeGsonToBean(response,
                SuperviseHandle.class);
        if (superviseHandle.success) {
            data_supervise = superviseHandle.data;
            if (!isRefresh) {
                allDataSupervise.clear();
                superviseAdapter.clear();
                superviseAdapter.addAll(data_supervise);
            } else {
                superviseAdapter.addAll(data_supervise);
            }
            allDataSupervise.addAll(data_supervise);
            onLoaded();// �÷���Ҫ��setHasMoreData����ǰʹ��
            if (data_supervise.size() < pageSize) {
                ptrLv.setHasMoreData(false);
                hasMore = false;
            } else {
                ptrLv.setHasMoreData(true);
                hasMore = true;
            }
            setLastUpdateTime();
        } else {
            showToast(superviseHandle.message);
            onLoaded();
        }
    }

    private void initEvent() {
        ptrLv.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                getSuperviseHandle(false);
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                if (hasMore) {
                    pageStart = pageStart + 1;
                    getSuperviseHandle(true);
                }
            }
        });
        ptrLv.getRefreshableView().setOnItemClickListener(
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {
                        Intent intent = new Intent(mContext,
                                SuperviseHandleDetailActivity.class);
                        SuperviseHandle.Supervise superviseItem;
                        if (ptrLv.getRefreshableView().getHeaderViewsCount() > 0) {
                            superviseItem = allDataSupervise.get(position - 1);
                        } else {
                            superviseItem = allDataSupervise.get(position);
                        }
                        intent.putExtra(SuperviseHandleDetailActivity.NOTICEID,
                                superviseItem.ID);
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
