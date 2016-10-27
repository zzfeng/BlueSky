package com.sundyn.bluesky.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 能够适应scrollview的listview
 * 不过这个方法和方法1有一个同样的毛病，就是默认显示的首项是ListView，需要手动把ScrollView滚动至最顶端。 sv =
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
	 * 重写该方法，达到使ListView适应ScrollView的效果
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
