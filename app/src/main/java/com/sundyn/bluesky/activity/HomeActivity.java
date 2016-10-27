package com.sundyn.bluesky.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.fragment.HomeFra;
import com.sundyn.bluesky.fragment.MyFra;
import com.sundyn.bluesky.fragment.NewsFra;
import com.sundyn.bluesky.fragment.NoticeFra;
import com.sundyn.bluesky.receiver.PushReceiver;
import com.sundyn.bluesky.utils.AppManager;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.view.TabIndicatorView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class HomeActivity extends BaseActivity implements OnTabChangeListener {

    private FragmentTabHost tabhost;

    private TabIndicatorView homeIndicator;
    private TabIndicatorView newIndicator;
    private TabIndicatorView workbenchIndicator;
    private TabIndicatorView noticeIndicator;
    private TabIndicatorView meIndicator;

    private static final String TAB_HOME = "home";
    private static final String TAB_NEW = "new";
    private static final String TAB_WORKBENCH = "workbench";
    private static final String TAB_NOTICE = "notice";
    private static final String TAB_ME = "me";

    private PushReceiver pushReceiver = new PushReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            loadTabData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        IntentFilter filter = new IntentFilter();
        filter.addAction(PushReceiver.ACTION_TEXT);
        registerReceiver(pushReceiver, filter);

        // 1. 初始化TabHost
        tabhost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabhost.setup(this, getSupportFragmentManager(),
                R.id.activity_home_container);

        // 2. 新建TabSpec
        TabSpec spec = tabhost.newTabSpec(TAB_HOME);
        homeIndicator = new TabIndicatorView(this);
        homeIndicator.setTabTitle("首 页");
        homeIndicator.setTabIcon(R.mipmap.tab_icon_home_normal,
                R.mipmap.tab_icon_home_focus);
        spec.setIndicator(homeIndicator);

        // 3. 添加TabSpec
        tabhost.addTab(spec, HomeFra.class, null);

        // 2. 新建TabSpec
        spec = tabhost.newTabSpec(TAB_WORKBENCH);
        workbenchIndicator = new TabIndicatorView(this);
        workbenchIndicator.setTabIcon(R.mipmap.tab_icon_workbench_normal,
                R.mipmap.tab_icon_workbench_focus);
        workbenchIndicator.setTabTitle("工作台");
        spec.setIndicator(workbenchIndicator);
        // 3. 添加TabSpec
        tabhost.addTab(spec, WorkBenchFra.class, null);

        // 2. 新建TabSpec
        spec = tabhost.newTabSpec(TAB_NOTICE);
        noticeIndicator = new TabIndicatorView(this);
        noticeIndicator.setTabIcon(R.mipmap.tab_icon_notice_normal,
                R.mipmap.tab_icon_notice_focus);
        noticeIndicator.setTabTitle("消 息");
        spec.setIndicator(noticeIndicator);
        // 3. 添加TabSpec
        tabhost.addTab(spec, NoticeFra.class, null);

        // 2. 新建TabSpec
        spec = tabhost.newTabSpec(TAB_NEW);
        newIndicator = new TabIndicatorView(this);
        newIndicator.setTabTitle("通 知");
        newIndicator.setTabIcon(R.mipmap.tab_icon_new_normal,
                R.mipmap.tab_icon_new_focus);
        spec.setIndicator(newIndicator);

        // 3. 添加TabSpec
        tabhost.addTab(spec, NewsFra.class, null);

        // 2. 新建TabSpec
        spec = tabhost.newTabSpec(TAB_ME);
        meIndicator = new TabIndicatorView(this);
        meIndicator.setTabIcon(R.mipmap.tab_icon_me_normal,
                R.mipmap.tab_icon_me_focus);
        meIndicator.setTabTitle("我 的");
        spec.setIndicator(meIndicator);
        // 3. 添加TabSpec
        tabhost.addTab(spec, MyFra.class, null);

        // 去掉分割线
        tabhost.getTabWidget().setDividerDrawable(android.R.color.white);

        // 初始化 tab选中
        tabhost.setCurrentTabByTag(TAB_HOME);
        homeIndicator.setTabSelected(true);

        // 设置tab切换的监听
        tabhost.setOnTabChangedListener(this);
    }

    @Override
    public void onTabChanged(String tag) {
        homeIndicator.setTabSelected(false);
        newIndicator.setTabSelected(false);
        workbenchIndicator.setTabSelected(false);
        noticeIndicator.setTabSelected(false);
        meIndicator.setTabSelected(false);

        if (TAB_NEW.equals(tag)) {
            newIndicator.setTabSelected(true);
        } else if (TAB_WORKBENCH.equals(tag)) {
            workbenchIndicator.setTabSelected(true);
        } else if (TAB_NOTICE.equals(tag)) {
            noticeIndicator.setTabSelected(true);
        } else if (TAB_ME.equals(tag)) {
            meIndicator.setTabSelected(true);
        } else if (TAB_HOME.equals(tag)) {
            homeIndicator.setTabSelected(true);
        }
    }

    private void loadTabData() {
        noticeIndicator.setTabUnreadCount(1);
        getPushCount();
    }

    private int count;// 未读消息个数

    private void getPushCount() {
        OkHttpUtils
                .get()
                .url(Constant.BASE_URL + Constant.URL_GETPUSHCOUNT)
                .addParams("userNo", mApplication.getUser().getUserNo())
                .addParams("state", "false")
                .addParams("roleKey", mApplication.getUser().getRolestring())
                .addParams("areaId", mApplication.getUser().getRegionidstring())
                .build().execute(new StringCallback() {

            @Override
            public void onResponse(String response, int arg1) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        count = jsonObject.getInt("count");
                    }
                } catch (JSONException e) {
                    count = 0;
                } finally {
                    noticeIndicator.setTabUnreadCount(count);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
            }
        });
    }

    private long exitTime = 0;

    /* 再按一次退出程序 onkeydown在集成Tabactivity的activity中失效，事件冲突 */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showToast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                AppManager.getAppManager().AppExit(mContext);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(pushReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTabData();
    }

}
