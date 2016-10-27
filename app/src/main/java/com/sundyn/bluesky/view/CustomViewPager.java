package com.sundyn.bluesky.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends LazyViewPager {

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CustomViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    /**
     * 不拦截事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 事件处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
