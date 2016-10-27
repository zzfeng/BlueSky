package com.sundyn.bluesky.view.svprogresshud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sundyn.bluesky.R;

public class SVProgressDefaultView extends LinearLayout {
	private int resInfo = R.mipmap.ic_svstatus_info;
	private int resSuccess = R.mipmap.ic_svstatus_success;
	private int resError = R.mipmap.ic_svstatus_error;
	private ImageView ivSmallLoading;
	private TextView tvMsg;

	public SVProgressDefaultView(Context context) {
		super(context);
		initViews();
	}

	private void initViews() {
		LayoutInflater.from(getContext()).inflate(
				R.layout.view_svprogressdefault, this, true);
		ivSmallLoading = (ImageView) findViewById(R.id.ivSmallLoading);
		tvMsg = (TextView) findViewById(R.id.tvMsg);
	}

	public void showInfoWithStatus(String string) {
		showBaseStatus(resInfo, string);
	}

	public void showSuccessWithStatus(String string) {
		showBaseStatus(resSuccess, string);
	}

	public void showErrorWithStatus(String string) {
		showBaseStatus(resError, string);
	}

	public void setText(String string) {
		tvMsg.setText(string);
	}

	public void showBaseStatus(int res, String string) {
		clearAnimations();
		ivSmallLoading.setImageResource(res);
		tvMsg.setText(string);
		ivSmallLoading.setVisibility(View.VISIBLE);
		tvMsg.setVisibility(View.VISIBLE);
	}

	public void dismiss() {
		clearAnimations();
	}

	private void clearAnimations() {
		ivSmallLoading.clearAnimation();
	}

}
