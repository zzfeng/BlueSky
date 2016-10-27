package com.sundyn.bluesky.utils;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;


/**
 * @author yangjl
 * @ʱ�� 2016��10��19������9:57:53
 * @�汾 1.0
 * @���� ����������
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
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);//����ѭ��
        alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);//
        alphaAnimator.start();
    }

}
