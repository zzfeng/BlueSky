package com.sundyn.bluesky.view.svprogresshud;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.sundyn.bluesky.R;

import java.lang.ref.WeakReference;

public class SVProgressHUD {
	private WeakReference<Context> contextWeak;
	private static final long DISMISSDELAYED = 1000;
	private SVProgressHUDMaskType mSVProgressHUDMaskType;
	private boolean isShowing;
	private boolean isDismissing;

	public enum SVProgressHUDMaskType {
		None, // ������������ؼ����
		Clear, // ��������������ؼ����
		Black, // ��������������ؼ������������ɫ��͸��
		ClearCancel, // ��������������ؼ���������������ʧ
		BlackCancel, // ��������������ؼ������������ɫ��͸�������������ʧ
		;
	}

	private SVProgressHUDMessageType tag = SVProgressHUDMessageType.Progress;

	public enum SVProgressHUDMessageType {
		Success, Faile, Progress, Info;
	}

	private final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
	private ViewGroup decorView;// activity�ĸ�View
	private ViewGroup rootView;// mSharedView �� ��View
	private SVProgressDefaultView mSharedView;

	private Animation outAnim;
	private Animation inAnim;
	private int gravity = Gravity.CENTER;
	private OnDismissListener onDismissListener;

	public SVProgressHUD(Context context) {
		this.contextWeak = new WeakReference<Context>(context);
		gravity = Gravity.CENTER;
		initViews();
		initDefaultView();
		initAnimation();
	}

	protected void initViews() {
		Context context = contextWeak.get();
		if (context == null)
			return;

		LayoutInflater layoutInflater = LayoutInflater.from(context);
		decorView = (ViewGroup) ((Activity) context).getWindow().getDecorView()
				.findViewById(android.R.id.content);
		rootView = (ViewGroup) layoutInflater.inflate(
				R.layout.layout_svprogresshud, null, false);
		rootView.setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
	}

	protected void initDefaultView() {
		Context context = contextWeak.get();
		if (context == null)
			return;

		mSharedView = new SVProgressDefaultView(context);
		params.gravity = gravity;
		mSharedView.setLayoutParams(params);
	}

	protected void initAnimation() {
		if (inAnim == null)
			inAnim = getInAnimation();
		if (outAnim == null)
			outAnim = getOutAnimation();
	}

	/**
	 * show��ʱ�����
	 */
	private void onAttached() {
		isShowing = true;
		decorView.addView(rootView);
		if (mSharedView.getParent() != null)
			((ViewGroup) mSharedView.getParent()).removeView(mSharedView);
		rootView.addView(mSharedView);
	}

	/**
	 * ������View��Activity�ĸ���ͼ
	 */
	private void svShow() {
		mHandler.removeCallbacksAndMessages(null);
		onAttached();

		mSharedView.startAnimation(inAnim);

	}

	public void showInfoWithStatus(String string) {
		showInfoWithStatus(string, SVProgressHUDMaskType.Black);
	}

	public void showInfoWithStatus(String string, SVProgressHUDMaskType maskType) {
		tag = SVProgressHUDMessageType.Info;
		if (isShowing())
			return;
		setMaskType(maskType);
		mSharedView.showInfoWithStatus(string);
		svShow();
		scheduleDismiss();
	}

	public void showSuccessWithStatus(String string) {
		showSuccessWithStatus(string, SVProgressHUDMaskType.Black);
	}

	public void showSuccessWithStatus(String string,
									  SVProgressHUDMaskType maskType) {
		tag = SVProgressHUDMessageType.Success;

		if (isShowing())
			return;
		setMaskType(maskType);
		mSharedView.showSuccessWithStatus(string);
		svShow();
		scheduleDismiss();
	}

	public void showErrorWithStatus(String string) {
		showErrorWithStatus(string, SVProgressHUDMaskType.Black);
	}

	public void showErrorWithStatus(String string,
									SVProgressHUDMaskType maskType) {
		tag = SVProgressHUDMessageType.Faile;
		if (isShowing())
			return;
		setMaskType(maskType);
		mSharedView.showErrorWithStatus(string);
		svShow();
		scheduleDismiss();
	}

	public void setText(String string) {
		mSharedView.setText(string);
	}

	private void setMaskType(SVProgressHUDMaskType maskType) {
		mSVProgressHUDMaskType = maskType;
		switch (mSVProgressHUDMaskType) {
			case None:
				configMaskType(android.R.color.transparent, false, false);
				break;
			case Clear:
				configMaskType(android.R.color.transparent, true, false);
				break;
			case ClearCancel:
				configMaskType(android.R.color.transparent, true, true);
				break;
			case Black:
				configMaskType(R.color.bgColor_overlay, true, false);
				break;
			case BlackCancel:
				configMaskType(R.color.bgColor_overlay, true, true);
				break;
			default:
				break;
		}
	}

	private void configMaskType(int bg, boolean clickable, boolean cancelable) {
		rootView.setBackgroundResource(bg);
		rootView.setClickable(clickable);
		setCancelable(cancelable);
	}

	/**
	 * ����View�ǲ����Ѿ���ӵ�����ͼ
	 *
	 * @return �����ͼ�Ѿ����ڸ�View����true
	 */
	public boolean isShowing() {
		return rootView.getParent() != null || isShowing;
	}

	public void dismiss() {
		if (isDismissing)
			return;
		isDismissing = true;
		// ��ʧ����
		outAnim.setAnimationListener(outAnimListener);
		mSharedView.dismiss();
		mSharedView.startAnimation(outAnim);
	}

	public void dismissImmediately() {
		mSharedView.dismiss();
		rootView.removeView(mSharedView);
		decorView.removeView(rootView);
		isShowing = false;
		isDismissing = false;
		if (onDismissListener != null) {
			onDismissListener.onDismiss(tag);
		}

	}

	public Animation getInAnimation() {
		Context context = contextWeak.get();
		if (context == null)
			return null;

		int res = getAnimationResource(this.gravity, true);
		return AnimationUtils.loadAnimation(context, res);
	}

	private int getAnimationResource(int gravity, boolean isInAnimation) {
		return isInAnimation ? R.anim.svfade_in_center
				: R.anim.svfade_out_center;
	}

	public Animation getOutAnimation() {
		Context context = contextWeak.get();
		if (context == null)
			return null;

		int res = getAnimationResource(this.gravity, false);
		return AnimationUtils.loadAnimation(context, res);
	}

	private void setCancelable(boolean isCancelable) {
		View view = rootView.findViewById(R.id.sv_outmost_container);

		if (isCancelable) {
			view.setOnTouchListener(onCancelableTouchListener);
		} else {
			view.setOnTouchListener(null);
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismiss();
		}
	};

	private void scheduleDismiss() {
		mHandler.removeCallbacksAndMessages(null);
		mHandler.sendEmptyMessageDelayed(0, DISMISSDELAYED);
	}

	private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				dismiss();
				setCancelable(false);
			}
			return false;
		}
	};

	private Animation.AnimationListener outAnimListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			dismissImmediately();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	};

	public void setOnDismissListener(OnDismissListener listener) {
		this.onDismissListener = listener;
	}

	public OnDismissListener getOnDismissListener() {
		return onDismissListener;
	}

}
