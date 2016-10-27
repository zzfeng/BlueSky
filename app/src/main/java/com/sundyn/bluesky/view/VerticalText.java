package com.sundyn.bluesky.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sundyn.bluesky.R;

import static com.baidu.location.b.g.R;

public class VerticalText extends LinearLayout {

    private Context context;
    private View view;
    private TextView tv_up;
    private TextView tv_down;

    private String text_up;
    private String text_down;
    private int text_up_color;
    private int text_down_color;

    private int defaultColor = R.color.bg_login;

    public VerticalText(Context context) {
        this(context, null);
    }

    public VerticalText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        view = LayoutInflater.from(context)
                .inflate(R.layout.verticaltext, this);
        tv_up = (TextView) view.findViewById(R.id.tv_up);
        tv_down = (TextView) view.findViewById(R.id.tv_down);

        obtainStyledAttributes(attrs);
        fillData2View();

    }

    private void fillData2View() {
        tv_up.setText(text_up == null ? "..." : text_up);
        if (!TextUtils.isEmpty(text_down)) {
            tv_down.setText(text_down);
        }
    }

    private void obtainStyledAttributes(AttributeSet attrs) {
        final TypedArray attributes = getContext().obtainStyledAttributes(
                attrs, R.styleable.VerticalText);
        text_up = attributes.getString(R.styleable.VerticalText_text_up);
        text_down = attributes.getString(R.styleable.VerticalText_text_down);
        text_up_color = attributes.getColor(
                R.styleable.VerticalText_font_up_color, getResources().getColor(R.color.default_normal));
        text_down_color = attributes.getColor(
                R.styleable.VerticalText_font_down_color, getResources().getColor(R.color.default_pressed));
        int isBlod = attributes.getInt(R.styleable.VerticalText_text_bold,
                VISIBLE);
        if (isBlod != VISIBLE) {
            tv_up.getPaint().setFakeBoldText(true);// ¼Ó´Ö
        }
        attributes.recycle();

        tv_up.setTextColor(text_up_color);
        tv_down.setTextColor(text_down_color);

    }

    public void updateText(String text_up, String text_down) {
        this.text_up = text_up;
        this.text_down = text_down;
        fillData2View();
    }

}
