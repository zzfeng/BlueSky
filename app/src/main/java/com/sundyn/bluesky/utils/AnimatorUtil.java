package com.sundyn.bluesky.utils;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;


/**
 * @author yangjl
 * @时间 2016年10月19日上午9:57:53
 * @版本 1.0
 * @描述 动画工具类
 */
public class AnimatorUtil {

    private static AnimatorUtil animUtil = new AnimatorUtil();

    private AnimatorUtil() {
    }

    public static AnimatorUtil getInstance() {
        return animUtil;
    }

    public void reaptAlpha(Object target) {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(target, "alpha", 1, 0.25f, 1);
        alphaAnimator.setDuration(2000);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);//
        alphaAnimator.start();
    }

}
