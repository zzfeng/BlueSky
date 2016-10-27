package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.bean.Area;
import com.sundyn.bluesky.bean.Area.AreaItem;
import com.sundyn.bluesky.fragment.ItemFragment;
import com.sundyn.bluesky.fragment.SiteCheckFragment;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.view.NormalTopBar;
import com.sundyn.bluesky.view.pagerindicator.TabPageIndicator;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import okhttp3.Call;

public class VideoCheckActivity extends BaseActivity {
    @ViewInject(R.id.loading_view)
    protected View loadingView;
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.indicator)
    private TabPageIndicator indicator;
    @ViewInject(R.id.pager)
    private ViewPager pager;

    private ArrayList<AreaItem> allAreaList;// 指示器的所有数据
    private ArrayList<BaseFragment> pages = new ArrayList<BaseFragment>();
    private FragmentPagerAdapter adapter;
    public static final String MODELID = "modleid";
    private int model;
    private int curIndex = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_videocheck);
        x.view().inject(this);
        initTitleBar();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            model = intent.getIntExtra(MODELID, WorkBenchFra.MODEL_VIDEO);
        }
        getAllArea();
    }

    private void getAllArea() {
        OkHttpUtils
                .post()
                .url(Constant.BASE_URL + Constant.URL_GETALLAREA)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("roleKeys", mApplication.getUser().getRolestring())
                .addParams("regions",
                        mApplication.getUser().getRegionidstring())
                .addParams("token", mApplication.getUser().getToken()).build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        if (!TextUtils.isEmpty(response)) {
                            Area allArea = GsonTools.changeGsonToBean(response,
                                    Area.class);
                            allAreaList = allArea.data;
                            pages.clear();
                            if (model == WorkBenchFra.MODEL_VIDEO) {
                                for (AreaItem areaItem : allAreaList) {
                                    BaseFragment itemFragment = new ItemFragment();
                                    pages.add(itemFragment);

                                }
                            } else if (model == WorkBenchFra.MODEL_SITETBJ) {
                                for (AreaItem areaItem : allAreaList) {
                                    BaseFragment itemFragment = new SiteCheckFragment();
                                    pages.add(itemFragment);
                                }

                            }
                            adapter = new TabPageIndicatorAdapter(
                                    getSupportFragmentManager(), pages);
                            pager.removeAllViews();
                            pager.setAdapter(adapter);
                            indicator.setViewPager(pager);
                            indicator.setVisibility(View.VISIBLE);
                            indicator.setCurrentItem(curIndex);

                            dismissLoadingView();
                        }
                        LogUtil.i(response);
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("获取数据失败");
                        dismissLoadingView();
                    }
                });
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                VideoCheckActivity.this.finish();
            }
        });
        mTopBar.setTitle("选择您要巡查的工地");
        mTopBar.setActionTextVisibility(false);
    }

    class TabPageIndicatorAdapter extends FragmentPagerAdapter {

        private ArrayList<BaseFragment> pages;

        public TabPageIndicatorAdapter(FragmentManager fm,
                                       ArrayList<BaseFragment> pages) {
            super(fm);
            this.pages = pages;
        }

        @Override
        public Fragment getItem(int position) {
            // 新建一个Fragment来展示ViewPager item的内容，并传递参数
            Bundle bundle = new Bundle();
            bundle.putInt("areaCode", allAreaList.get(position).key);
            pages.get(position).setArguments(bundle);

            return pages.get(position);
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return allAreaList.get(position).value;
        }

    }

    /**
     * 加载成功之后去除进度框
     */
    public void dismissLoadingView() {
        if (loadingView != null)
            loadingView.setVisibility(View.INVISIBLE);
    }
}
