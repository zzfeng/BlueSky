package com.sundyn.bluesky.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.view.ChoiceDialogFra;
import com.sundyn.bluesky.view.NormalTopBar;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class EnforcementTaskDetailActivity extends BaseActivity {

    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_enforcementtaskdetail);
        x.view().inject(this);
        initTitleBar();
        initView();
        initData();
    }

    private void initView() {

    }

    private void initData() {
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                EnforcementTaskDetailActivity.this.finish();
            }
        });
        mTopBar.setTitle("WE-一期项目");
        mTopBar.setActionTextVisibility(true);
        mTopBar.setActionText("创建");
        mTopBar.setOnActionListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ChoiceDialogFra cFra = new ChoiceDialogFra();
                cFra.show(getSupportFragmentManager(), "CHOICE");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("--------enforcementtaskdetailactivity-------");
    }

}
