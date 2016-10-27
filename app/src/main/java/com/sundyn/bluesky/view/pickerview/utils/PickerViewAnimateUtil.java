package com.sundyn.bluesky.view.pickerview.utils;

import android.view.Gravity;

import com.sundyn.bluesky.R;

public class PickerViewAnimateUtil {
	private static final int INVALID = -1;

	public static int getAnimationResource(int gravity, boolean isInAnimation) {
		switch (gravity) {
		case Gravity.BOTTOM:
			return isInAnimation ? R.anim.slide_in : R.anim.slide_out;
		}
		return INVALID;
	}
}
