package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.PMPlus;
import com.sundyn.bluesky.bean.PMPlus.PMItem;
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

import okhttp3.Call;

public class PMOverForDataCenterActivity extends BaseActivity implements
        OnItemClickListener {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.chat_list_view)
    private PullToRefreshListView ptrLv;
    @ViewInject(R.id.loading_view)
    protected View loadingView;

    private ArrayList<PMItem> allDataSites;
    private QuickAdapter<PMItem> adapter;
    private static int pageSize = 15;
    private static int pageStart = 1;
    private ArrayList<PMItem> pmList;// ���깤������
    private boolean hasMore = false;
    private int areaCode;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.fra_news);
        x.view().inject(this);
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                PMOverForDataCenterActivity.this.finish();
            }
        });
        mTopBar.setTitle("PM10���ؼ�¼");
        mTopBar.setActionTextVisibility(false);
        ptrLv.setPullRefreshEnabled(false);
        // �������ز�����
        ptrLv.setPullLoadEnabled(false);
        // ���������Զ����ؿ���
        ptrLv.setScrollLoadEnabled(true);
        setLastUpdateTime(); // �����ϴ�ˢ��ʱ��
        initEvent();
        initData();

    }

    private void initData() {
        pageStart = 1;

        Intent intent = getIntent();
        areaCode = intent.getIntExtra("areaCode", 0);

        allDataSites = new ArrayList<PMItem>();
        pmList = new ArrayList<PMItem>();
        adapter = new QuickAdapter<PMItem>(mContext,
                R.layout.lv_pmplus_item) {

            @Override
            protected void convert(BaseAdapterHelper helper, PMItem item) {
                helper.setText(R.id.tv_sitename, item.siteName);
                helper.setText(R.id.tv_pm, item.pm10 + "");
            }
        };
        ptrLv.getRefreshableView().setAdapter(adapter);// ����addData��ʱ���ˢ��
        getPmPlusSite(areaCode, false);
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
                    getPmPlusSite(areaCode, true);
                }
            }
        });
    }

    /**
     * ��ȡ���깤��
     *
     * @param areaCode
     */
    private void getPmPlusSite(int areaCode, final boolean isRefresh) {
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_GETPM_PLUS)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("regionType", "AREA")
                .addParams("regionId", areaCode + "")
                .addParams("page", pageStart + "")
                .addParams("size", pageSize + "").build()
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
        PMPlus pmPlus = GsonTools.changeGsonToBean(response, PMPlus.class);
        if (pmPlus.success) {
            pmList = pmPlus.data;
            if (!isRefresh) {
                allDataSites.clear();
                adapter.clear();
                adapter.addAll(pmList);
            } else {
                adapter.addAll(pmList);
            }
            allDataSites.addAll(pmList);
            onLoaded();// �÷���Ҫ��setHasMoreData����ǰʹ��
            if (pmList.size() < pageSize) {
                ptrLv.setHasMoreData(false);
                hasMore = false;
            } else {
                ptrLv.setHasMoreData(true);
                hasMore = true;
            }
            setLastUpdateTime();
        } else {
            showToast(pmPlus.message);
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

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int positon,
                            long arg3) {
        PMItem pmItem = allDataSites.get(positon);
        Intent intent = new Intent(mContext, SiteDataCenterActivity.class);
        intent.putExtra("siteNo", pmItem.siteNo);
        intent.putExtra("siteName", pmItem.siteName);
        startActivity(intent);
    }
}
