package com.sundyn.bluesky.view;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.bean.InstallPct;
import com.sundyn.bluesky.utils.CommonUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class InstallPctDialogFra extends DialogFragment implements
		OnClickListener {

	@ViewInject(R.id.id_ll_parent)
	private LinearLayout ll_parent;
	@ViewInject(R.id.id_tv_areaName)
	private TextView tv_areaName;
	@ViewInject(R.id.id_tv_pmAllPct)
	private TextView tv_pmAllPct;
	@ViewInject(R.id.id_tv_videoAllPct)
	private TextView tv_videoAllPct;
	@ViewInject(R.id.id_tv_pmVideoAllPct)
	private TextView tv_pmVideoAllPct;
	private View view;

	private InstallPct mData;

	public InstallPctDialogFra(){};
	public void InstallPctDialogFra(InstallPct installPct) {
		this.mData = installPct;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.dialogWindowAnimScale;
		view = inflater.inflate(R.layout.custom_marker_view, container);
		x.view().inject(this, view); // 注入view和事件
		initEvent();
		fill2Data();
		return view;

	}

	private void fill2Data() {
		tv_areaName.setText(mData.areaName);
		tv_pmAllPct.setText("PM10接入比："
				+ CommonUtil.saveNumberPercent(mData.pmPct));
		tv_videoAllPct.setText("摄像接入比："
				+ CommonUtil.saveNumberPercent(mData.videoPct));
		tv_pmVideoAllPct.setText("PM10和摄像双接入比："
				+ CommonUtil.saveNumberPercent(mData.pmVideoAllPct));
	}

	private void initEvent() {
		ll_parent.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.id_ll_parent:
			this.dismiss();
			break;

		default:
			break;
		}

	}

}
