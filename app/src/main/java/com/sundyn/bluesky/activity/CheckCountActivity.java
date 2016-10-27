package com.sundyn.bluesky.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.fragment.AddCheckRecordFra;
import com.sundyn.bluesky.fragment.CheckCountFra;
import com.sundyn.bluesky.fragment.CheckOfMyFra;
import com.sundyn.bluesky.fragment.CompaintCountFra;
import com.sundyn.bluesky.fragment.CompaintDetailFra;
import com.sundyn.bluesky.fragment.CompaintSumFra;
import com.sundyn.bluesky.fragment.ProjectChartFra;
import com.sundyn.bluesky.view.NormalTopBar;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * @author yangjl
 * @date 2016年8月9日
 * @版本: 1.0
 * @描述: 工作台检查汇总
 */
public class CheckCountActivity extends BaseActivity implements OnClickListener {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;

    private Fragment currentFra;
    private String currentTag;
    private FragmentManager fm;

    public static final String TAG_MAIN = "main";
    public static final String TAG_CHECK = "check";
    public static final String TAG_CHART = "chart";
    public static final String TAG_COMPAINT = "compaint";
    public static final String TAG_COMPAINTDETAIL = "compaintdetail";
    public static final String TAG_ADDCHECKRECORD = "addcheckrecord";
    public static final String TAG_COMPAINTSUM = "compaintsum";

    public static final int ENTER_FIRST = 0;
    public static final int ENTER_CHECK = 1;
    public static final int ENTER_CHART = 2;
    public static final int ENTER_COMPAINT = 3;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_checkcount);
        x.view().inject(this);

        fm = getSupportFragmentManager();

        currentTag = TAG_MAIN;
        currentFra = new CheckCountFra();

        mTopBar.setTitle("巡查汇总");
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(this);

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contanier_checkcount, currentFra, currentTag);
        transaction.addToBackStack(currentTag);
        transaction.commit();

    }

    public void go2Check() {
        Fragment fragment = fm.findFragmentByTag(TAG_CHECK);
        if (fragment == null) {
            fragment = new CheckOfMyFra();
        }

        // 设置头
        mTopBar.setTitle("日常巡查");
        mTopBar.setBackVisibility(true);
        mTopBar.setActionTextVisibility(true);
        mTopBar.setActionText("添加");
        mTopBar.setOnActionListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                go2AddCheckRecord();
            }
        });

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contanier_checkcount, fragment, TAG_CHECK);
        ft.addToBackStack(TAG_CHECK);
        ft.commit();
    }

    public void go2ProjectChart() {
        Fragment fragment = fm.findFragmentByTag(TAG_CHART);
        if (fragment == null) {
            fragment = new ProjectChartFra();
        }

        // 设置头
        mTopBar.setTitle("项目图表");
        mTopBar.setBackVisibility(true);
        mTopBar.setActionTextVisibility(false);

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contanier_checkcount, fragment, TAG_CHART);
        ft.addToBackStack(TAG_CHART);
        ft.commit();
    }

    public void go2CompaintCount() {
        Fragment fragment = fm.findFragmentByTag(TAG_COMPAINT);
        if (fragment == null) {
            fragment = new CompaintCountFra();
        }

        // 设置头
        mTopBar.setTitle("投诉汇总");
        mTopBar.setBackVisibility(true);
        mTopBar.setActionTextVisibility(false);

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contanier_checkcount, fragment, TAG_COMPAINT);
        ft.addToBackStack(TAG_COMPAINT);
        ft.commit();
    }

    public void go2CompaintDetail(int areaCode) {
        Fragment fragment = fm.findFragmentByTag(TAG_COMPAINTDETAIL);
        if (fragment == null) {
            fragment = new CompaintDetailFra();
        }
        Bundle bundle = new Bundle();
        bundle.putInt("areaCode", areaCode);
        fragment.setArguments(bundle);
        // 设置头
        mTopBar.setTitle("投诉汇总");
        mTopBar.setBackVisibility(true);
        mTopBar.setActionTextVisibility(false);

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contanier_checkcount, fragment, TAG_COMPAINTDETAIL);
        ft.addToBackStack(TAG_COMPAINTDETAIL);
        ft.commit();
    }

    public void go2CompaintSum(String areaCode) {
        Fragment fragment = fm.findFragmentByTag(TAG_COMPAINTSUM);
        if (fragment == null) {
            fragment = new CompaintSumFra();
        }
        Bundle bundle = new Bundle();
        bundle.putString("areaCode", areaCode);
        fragment.setArguments(bundle);
        // 设置头
        mTopBar.setTitle("投诉汇总");
        mTopBar.setBackVisibility(true);
        mTopBar.setActionTextVisibility(false);

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contanier_checkcount, fragment, TAG_COMPAINTSUM);
        ft.addToBackStack(TAG_COMPAINTSUM);
        ft.commit();
    }

    public void go2AddCheckRecord() {
        Fragment fragment = fm.findFragmentByTag(TAG_ADDCHECKRECORD);
        if (fragment == null) {
            fragment = new AddCheckRecordFra();
        }
        // 设置头
        mTopBar.setTitle("日常巡查添加");
        mTopBar.setBackVisibility(true);
        mTopBar.setActionTextVisibility(false);

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contanier_checkcount, fragment, TAG_ADDCHECKRECORD);
        ft.addToBackStack(TAG_ADDCHECKRECORD);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        clickBack();
    }

    @Override
    public void onClick(View v) {
        if (v == mTopBar.getBackView()) {
            clickBack();
        }
    }

    public void clickBack() {
        int count = fm.getBackStackEntryCount();
        if (count <= 1) {
            finish();
        } else {
            fm.popBackStack();

            if (count == 2) {
                BackStackEntry entry = fm.getBackStackEntryAt(0);
                String name = entry.getName();
                if (TAG_MAIN.equals(name)) {
                    mTopBar.setTitle("巡查汇总");
                    mTopBar.setBackVisibility(true);
                    mTopBar.setActionTextVisibility(false);
                }
            }
            if (count == 3) {
                BackStackEntry entry = fm.getBackStackEntryAt(1);
                String name = entry.getName();
                if (TAG_CHECK.equals(name)) {
                    mTopBar.setTitle("日常巡查");
                    mTopBar.setBackVisibility(true);
                    mTopBar.setActionTextVisibility(true);
                    mTopBar.setActionText("添加");
                    mTopBar.setOnActionListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            go2AddCheckRecord();
                        }
                    });
                }
            }

        }
    }

}
