package com.sundyn.bluesky.view;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.sundyn.bluesky.R;

public class MapAroundDialogFra extends DialogFragment implements
		OnClickListener {

	@ViewInject(R.id.tv_dis)
	private TextView tv_dis;
	@ViewInject(R.id.seekBar)
	private SeekBar seekBar;
	@ViewInject(R.id.pmSearch)
	private TextView pmSearch;
	@ViewInject(R.id.bulidSearch)
	private TextView bulidSearch;
	@ViewInject(R.id.id_tv_cancle)
	private TextView tv_cancle;
	private View view;
	private MapAroundListener mListener;
	public double dis = 5;

	private final int SEARCH_PM_ID = 0;
	private final int SEARCH_SITE_ID = 1;

	public interface MapAroundListener {
		void onCompleteSearch(int searcheID, double dis, boolean isSearch);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.dialogWindowAnim;
		view = inflater.inflate(R.layout.near, container);
		x.view().inject(this, view); // 注入view和事件
		initEvent();
		return view;

	}

	private void initEvent() {
		pmSearch.setOnClickListener(this);
		bulidSearch.setOnClickListener(this);
		tv_cancle.setOnClickListener(this);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				tv_dis.setText("周边范围 " + progress + " 公里");
			}
		});
		mListener = (MapAroundListener) getActivity();
	}

	@Override
	public void onClick(View view) {
		dis = seekBar.getProgress();
		switch (view.getId()) {
		case R.id.pmSearch:
			mListener.onCompleteSearch(SEARCH_PM_ID, dis, true);
			this.dismiss();
			break;
		case R.id.bulidSearch:
			mListener.onCompleteSearch(SEARCH_SITE_ID, dis, true);
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
