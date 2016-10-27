package com.sundyn.bluesky.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.bean.Area;
import com.sundyn.bluesky.bean.Area.AreaItem;
import com.sundyn.bluesky.fragment.DataCenterItemFragment;
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
import java.util.List;

import okhttp3.Call;

/**
 * 数据中心activity
 *
 * @author 08
 */
public class DataCenterActivity extends BaseActivity {
    @ViewInject(R.id.loading_view)
    protected View loadingView;
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.indicator)
    private TabPageIndicator indicator;
    @ViewInject(R.id.pager)
    private ViewPager pager;

    private ArrayList<AreaItem> allAreaList;// 指示器的所有数据
    private ArrayList<DataCenterItemFragment> pages = new ArrayList<DataCenterItemFragment>();
    private FragmentPagerAdapter adapter;

    private int curIndex = 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_datacenter);
        x.view().inject(this);
        pager.setOffscreenPageLimit(0);
        initTitleBar();
        initData();
    }

    private void initData() {
        getAllArea();
        indicator.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                curIndex = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    private List<String> getAllArea() {
        OkHttpUtils.post().url(Constant.BASE_URL + Constant.URL_GETALLAREA)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("roleKeys", mApplication.getUser().getRolestring())
                .addParams("regions", mApplication.getUser().getRegionidstring())
                .addParams("token", mApplication.getUser().getToken()).build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        if (!TextUtils.isEmpty(response)) {
                            Area allArea = GsonTools.changeGsonToBean(response,
                                    Area.class);
                            allAreaList = allArea.data;
                            pages.clear();
                            for (AreaItem areaItem : allAreaList) {
                                DataCenterItemFragment itemFragment = new DataCenterItemFragment();
                                pages.add(itemFragment);
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
        return null;
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DataCenterActivity.this.finish();
            }
        });
        mTopBar.setTitle("数据中心");
        mTopBar.setActionTextVisibility(false);
    }

    class TabPageIndicatorAdapter extends FragmentPagerAdapter {

        private ArrayList<DataCenterItemFragment> pages;

        public TabPageIndicatorAdapter(FragmentManager fm,
                                       ArrayList<DataCenterItemFragment> pages) {
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
