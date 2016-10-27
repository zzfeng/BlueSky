package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.utils.IntentParam;
import com.sundyn.bluesky.view.FormDetailView;
import com.sundyn.bluesky.view.NormalTopBar;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class SiteCheckDetailActivity extends BaseActivity {

    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.id_tv_prjname)
    private FormDetailView tv_prjname;
    @ViewInject(R.id.id_tv_prjaddress)
    private FormDetailView tv_prjaddress;
    @ViewInject(R.id.id_tv_area)
    private FormDetailView tv_area;
    @ViewInject(R.id.id_tv_buildunit)
    private FormDetailView tv_buildunit;
    @ViewInject(R.id.id_tv_people)
    private FormDetailView tv_people;
    @ViewInject(R.id.id_tv_phone)
    private FormDetailView tv_phone;

    private String prjName;
    private String prjAddress;
    private String area;
    private String buildUnit;
    private String people;
    private String phone;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_sitecheck);
        x.view().inject(this);
        initData();
        initTitleBar();
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(IntentParam.SITEBEAN);
        if (bundle != null) {
            prjName = bundle.getString(IntentParam.PRJNAME, "");
            prjAddress = bundle.getString(IntentParam.PRJADDRESS, "");
            area = bundle.getString(IntentParam.AREA, "");
            buildUnit = bundle.getString(IntentParam.BUILDUNIT, "");
            people = bundle.getString(IntentParam.MANAGER, "");
            phone = bundle.getString(IntentParam.PHONE, "");
        }
        tv_prjname.updateText(prjName);
        tv_prjaddress.updateText(prjAddress);
        tv_area.updateText(area);
        tv_buildunit.updateText(buildUnit);
        tv_people.updateText(people);
        tv_phone.updateText(phone);
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SiteCheckDetailActivity.this.finish();
            }
        });
        mTopBar.setTitle(prjName);
        mTopBar.setActionTextVisibility(false);
    }

}
