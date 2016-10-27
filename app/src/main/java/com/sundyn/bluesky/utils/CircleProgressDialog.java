package com.sundyn.bluesky.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.view.RoundProgressBarWidthNumber;

/**
 * @author yangjl
 * @ʱ�� 2016��9��29������4:16:20
 * @�汾 1.0
 * @���� �Զ���Բ��������
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
