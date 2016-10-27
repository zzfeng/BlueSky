package com.sundyn.bluesky.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * �ܹ���Ӧscrollview��listview
 * ������������ͷ���1��һ��ͬ����ë��������Ĭ����ʾ��������ListView����Ҫ�ֶ���ScrollView��������ˡ� sv =
 * (ScrollView) findViewById(R.id.act_solution_4_sv); sv.smoothScrollTo(0, 0);
 * 
 * @author 08
 * 
 */
public class ListViewForScrollView extends ListView {

	public ListViewForScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ListViewForScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ListViewForScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	/**
	 * ��д�÷������ﵽʹListView��ӦScrollView��Ч��
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
