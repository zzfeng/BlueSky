package com.sundyn.bluesky.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.view.RoundProgressBarWidthNumber;

/**
 * @author yangjl
 * @时间 2016年9月29日下午4:16:20
 * @版本 1.0
 * @描述 自定义圆环进度条
 */
public class CircleProgressDialog extends ProgressDialog {

    private RoundProgressBarWidthNumber roundPro;

    public CircleProgressDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        roundPro.setProgress(0);
    }

    public void setProgressNum(int progress) {
        roundPro.setProgress(progress);
    }

    private void initView() {
        setContentView(R.layout.updateview);
        roundPro = (RoundProgressBarWidthNumber) findViewById(R.id.id_circlepro);
    }

}
