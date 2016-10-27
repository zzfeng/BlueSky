package com.sundyn.bluesky.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.fragment.CountCheckFra;
import com.sundyn.bluesky.fragment.ReformNoticeFra;
import com.sundyn.bluesky.fragment.SuperviseHandleFra;
import com.sundyn.bluesky.view.CustomViewPager;
import com.sundyn.bluesky.view.NormalTopBar;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class CountCheckActivity extends BaseActivity {
    @ViewInject(R.id.chat_top_bar)
    public NormalTopBar mTopBar;
    @ViewInject(R.id.viewpager)
    private CustomViewPager viewpager;
    @ViewInject(R.id.main_radio)
    private RadioGroup main_radio;
    @ViewInject(R.id.rb_countCheck)
    private RadioButton rb_countCheck;
    private List<Fragment> list;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_countcheck);
        x.view().inject(this);
        initTitleBar();
        initData();
    }

    private void initData() {
        list = new ArrayList<Fragment>();
        list.add(new CountCheckFra());
        list.add(new SuperviseHandleFra());
        list.add(new ReformNoticeFra());

        FragmentPagerAdapter adapter = new TabPageIndicatorAdapter(
                getSupportFragmentManager(), list);
        viewpager.setAdapter(adapter);

        main_radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_countCheck:
                        viewpager.setCurrentItem(0, false);
                        break;
                    case R.id.rb_supervisehandle:
                        viewpager.setCurrentItem(1, false);
                        break;
                    case R.id.rb_reformnotice:
                        viewpager.setCurrentItem(2, false);
                        break;
                }

            }
        });

        rb_countCheck.setChecked(true);
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CountCheckActivity.this.finish();
            }
        });
        mTopBar.setTitle("统计查询");
        mTopBar.setActionTextVisibility(false);
        // mTopBar.setActionText("筛选");
    }

    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        private List<Fragment> list;

        public TabPageIndicatorAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

    }
}
