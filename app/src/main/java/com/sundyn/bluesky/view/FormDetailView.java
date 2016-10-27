package com.sundyn.bluesky.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sundyn.bluesky.R;

public class FormDetailView extends LinearLayout {
	private Context context;
	private View view;
	private TextView tv_title;
	private TextView tv_prjName;
	private View lineView;

	private String title;
	private String prjName;

	public FormDetailView(Context context) {
		this(context, null);
	}

	public FormDetailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView(attrs);
	}

	private void initView(AttributeSet attrs) {
		view = LayoutInflater.from(context).inflate(R.layout.formdetailview,
				this);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_prjName = (TextView) view.findViewById(R.id.tv_prjname);
		lineView = view.findViewById(R.id.line);
		obtainStyledAttributes(attrs);
		fillData2View();

	}

	private void fillData2View() {
		tv_title.setText(title);
		tv_prjName.setText(prjName == null?"...":prjName);
	}

	/**
	 * get the styled attributes
	 * 
	 * @param attrs
	 */
	private void obtainStyledAttributes(AttributeSet attrs) {
		final TypedArray attributes = getContext().obtainStyledAttributes(
				attrs, R.styleable.Fra_my);
		title = attributes.getString(R.styleable.Fra_my_tv_name);
		prjName = attributes.getString(R.styleable.Fra_my_tv_dic);
		int lineVisible = attributes.getInt(
				R.styleable.Fra_my_bottomline_visibility, VISIBLE);
		if (lineVisible != VISIBLE) {
			lineView.setVisibility(View.INVISIBLE);
		}
		attributes.recycle();
	}

	public void updateText(String name) {
		this.prjName = name;
		tv_prjName.setText(prjName);
	}

	public String getText() {
		return prjName == null ? "" : prjName;
	}

}
