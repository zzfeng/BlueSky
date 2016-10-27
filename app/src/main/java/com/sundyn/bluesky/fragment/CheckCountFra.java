package com.sundyn.bluesky.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.CheckCountActivity;
import com.sundyn.bluesky.base.BaseFragment;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class CheckCountFra extends BaseFragment implements OnClickListener {
    private View view;
    @ViewInject(R.id.id_check)
    protected RelativeLayout check;
    @ViewInject(R.id.id_projectChart)
    protected RelativeLayout projectChart;
    @ViewInject(R.id.id_complaintsCount)
    protected RelativeLayout complaintsCount;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fra_checkcount, null);
        x.view().inject(this, view); // 注入view和事件

        initEvent();
        return view;

    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    private void initEvent() {
        check.setOnClickListener(this);
        projectChart.setOnClickListener(this);
        complaintsCount.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        FragmentActivity activity = getActivity();

        switch (view.getId()) {
            case R.id.id_check:
                if (activity != null) {
                    ((CheckCountActivity) activity).go2Check();
                }
                break;
            case R.id.id_projectChart:
                if (activity != null) {
                    ((CheckCountActivity) activity).go2ProjectChart();
                }
                break;
            case R.id.id_complaintsCount:
                if (activity != null) {
                    ((CheckCountActivity) activity).go2CompaintCount();
                }
                break;
            default:
                break;
        }

    }
}
