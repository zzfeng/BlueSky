package com.sundyn.bluesky.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sundyn.bluesky.R;

public class MyFraOfView extends LinearLayout {
	private static final int imageID = R.mipmap.fra_my_file;
	
	private Context context;
	private View view;
	private TextView tv_name;
	private TextView tv_desc;
	private ImageView iv_icon;
	private View lineView;

	private int iv_imageID;
	private String name;
	private String desc;

	

	public MyFraOfView(Context context) {
		this(context,null);
	}

	public MyFraOfView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView(attrs);
	}

	private void initView(AttributeSet attrs) {
		view = LayoutInflater.from(context).inflate(R.layout.fra_my_item, this);
		tv_name = (TextView) view.findViewById(R.id.tv_name);
		tv_desc = (TextView) view.findViewById(R.id.tv_desc);
		iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
		lineView = view.findViewById(R.id.line);
		
		obtainStyledAttributes(attrs);
		fillData2View();
		
	}
	
	private void fillData2View() {
		tv_name.setText(name);
		tv_desc.setText(desc);
		iv_icon.setImageResource(iv_imageID);
	}

	/**
	 * get the styled attributes
	 * 
	 * @param attrs
	 */
	private void obtainStyledAttributes(AttributeSet attrs) {
		final TypedArray attributes = getContext().obtainStyledAttributes(
				attrs, R.styleable.Fra_my);
		iv_imageID = attributes.getResourceId(R.styleable.Fra_my_iv_icon, imageID);
		name = attributes.getString(R.styleable.Fra_my_tv_name);
		desc = attributes.getString(R.styleable.Fra_my_tv_dic);
		int lineVisible = attributes
				.getInt(R.styleable.Fra_my_bottomline_visibility,
						VISIBLE);
		if (lineVisible != VISIBLE) {
			lineView.setVisibility(View.INVISIBLE);
		}
		attributes.recycle();
	}

}
