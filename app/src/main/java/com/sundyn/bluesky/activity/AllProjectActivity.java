package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.ProjectInfo;
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

public class AllProjectActivity extends BaseActivity implements
        OnItemClickListener, OnClickListener {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.chat_list_view)
    private PullToRefreshListView ptrLv;
    @ViewInject(R.id.et_name)
    protected EditText et_name;
    @ViewInject(R.id.id_bt_search)
    protected Button bt_search;
    @ViewInject(R.id.id_rg_search)
    protected RadioGroup rg_search;

    private ArrayList<ProjectInfo.ProjectItem> allDataSites;
    private QuickAdapter<ProjectInfo.ProjectItem> adapter;
    private static int pageSize = 15;
    private static int pageStart = 0;
    private List<ProjectInfo.ProjectItem> pmList;// 所有项目信息
    private boolean hasMore = false;

    private final String BUILDUNIT = "BuildUnit";
    private final String WORKUNIT = "WorkUnit";
    private final String PRJNAME = "PrjName";
    private String typeName = PRJNAME;// 筛选的条件类型

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_allproject);
        x.view().inject(this);
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AllProjectActivity.this.finish();
            }
        });
        mTopBar.setTitle("项目查询");
        mTopBar.setActionTextVisibility(false);
        ptrLv.setPullRefreshEnabled(false);
        // 上拉加载不可用
        ptrLv.setPullLoadEnabled(false);
        // 滚动到底自动加载可用
        ptrLv.setScrollLoadEnabled(true);
        setLastUpdateTime(); // 设置上次刷新时间
        initEvent();
        initData();

    }

    private void initData() {
        allDataSites = new ArrayList<ProjectInfo.ProjectItem>();
        pmList = new ArrayList<ProjectInfo.ProjectItem>();
        adapter = new QuickAdapter<ProjectInfo.ProjectItem>(mContext,
                R.layout.lv_project_item) {

            @Override
            protected void convert(BaseAdapterHelper helper,
                                   final ProjectInfo.ProjectItem item) {
                helper.setText(R.id.tv_sitename, item.PrjName);
            }
        };
        ptrLv.getRefreshableView().setAdapter(adapter);// 后面addData的时候会刷新
        getProjectInfo(false, false);
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
                    getProjectInfo(true, false);
                }
            }
        });
        bt_search.setOnClickListener(this);
        rg_search.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.id_rb_priname:
                        typeName = PRJNAME;
                        break;
                    case R.id.id_rb_constrction:
                        typeName = BUILDUNIT;
                        break;
                    case R.id.id_rb_builder:
                        typeName = WORKUNIT;
                        break;
                }

            }
        });
    }

    private void getProjectInfo(final boolean isRefresh, boolean search) {
        if (!isRefresh)
            pageStart = 0;
        if (search)
            showDialog();
        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_ALLPROJECT)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("currentPage", pageStart + "")
                .addParams("pageSize", pageSize + "")
                .addParams("typeName", typeName)
                .addParams("searchText", et_name.getText().toString())
                .addParams("userNo", mApplication.getUser().getUserNo())
                .build().execute(new StringCallback() {

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
        ProjectInfo projectInfo = GsonTools.changeGsonToBean(response,
                ProjectInfo.class);
        if (projectInfo.success) {
            pmList = projectInfo.data;
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
            setLastUpdateTime();
        } else {
            showToast(projectInfo.message);
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
        disDIalog();
        ptrLv.onPullDownRefreshComplete();
        ptrLv.onPullUpRefreshComplete();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int positon,
                            long arg3) {
        Intent intent = new Intent();
        intent.putExtra("prjname", allDataSites.get(positon).PrjName);
        intent.putExtra("prjno", allDataSites.get(positon).ID);
        setResult(0x00, intent);
        this.finish();

    }

    @Override
    public void onClick(View view) {
        hideKeyborad();
        getProjectInfo(false, true);
    }
}
