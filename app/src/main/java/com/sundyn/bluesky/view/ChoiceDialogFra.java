package com.sundyn.bluesky.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.NoticeChangedActivity;
import com.sundyn.bluesky.activity.NoticeHandleActivity;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class ChoiceDialogFra extends DialogFragment implements OnClickListener {

	@ViewInject(R.id.notice_changed)
	private TextView notice_changed;
	@ViewInject(R.id.notice_handle)
	private TextView notice_handle;
	@ViewInject(R.id.id_tv_cancle)
	private TextView tv_cancle;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.dialogWindowAnim;
		view = inflater.inflate(R.layout.alertview, container);
		x.view().inject(this,view);
		initEvent();

		return view;

	}

	private void initEvent() {
		notice_changed.setOnClickListener(this);
		notice_handle.setOnClickListener(this);
		tv_cancle.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.notice_changed:
			// 整改通知
			Intent intent = new Intent(getActivity(),
					NoticeChangedActivity.class);
			startActivity(intent);
			this.dismiss();
			break;
		case R.id.notice_handle:
			Intent noticeHandleIntent = new Intent(getActivity(),
					NoticeHandleActivity.class);
			startActivity(noticeHandleIntent);
			this.dismiss();
			break;
		case R.id.id_tv_cancle:
			this.dismiss();
			break;

		default:
			break;
		}

	}

}
