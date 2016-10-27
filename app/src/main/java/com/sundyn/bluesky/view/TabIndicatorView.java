package com.sundyn.bluesky.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sundyn.bluesky.R;

import static com.baidu.location.b.g.R;

public class TabIndicatorView extends RelativeLayout {
    private ImageView ivTabIcon;
    private TextView tvTabHint;
    private TextView tvTabUnRead;

    private int normalIconId;
    private int focusIconId;

    public TabIndicatorView(Context context) {
        this(context, null);
    }

    public TabIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // �������ļ��� ������а�
        View.inflate(context, R.layout.tab_indicator, this);

        ivTabIcon = (ImageView) findViewById(R.id.tab_indicator_icon);
        tvTabHint = (TextView) findViewById(R.id.tab_indicator_hint);
        tvTabUnRead = (TextView) findViewById(R.id.tab_indicator_unread);

        setTabUnreadCount(0);
    }

    // ����tab��title
    public void setTabTitle(String title) {
        tvTabHint.setText(title);
    }

    public void setTabTitle(int titleId) {
        tvTabHint.setText(titleId);
    }

    // ��ʼ��ͼ��
    public void setTabIcon(int normalIconId, int focusIconId) {
        this.normalIconId = normalIconId;
        this.focusIconId = focusIconId;

        ivTabIcon.setImageResource(normalIconId);
    }

    // ����δ����
    public void setTabUnreadCount(int unreadCount) {
        if (unreadCount <= 0) {
            tvTabUnRead.setVisibility(View.GONE);
        } else {
            if (unreadCount <= 99) {
                tvTabUnRead.setText(unreadCount + "");
            } else {
                tvTabUnRead.setText("99+");
            }

            tvTabUnRead.setVisibility(View.VISIBLE);
        }
    }

    // ����ѡ��
    public void setTabSelected(boolean selected) {
        if (selected) {
            ivTabIcon.setImageResource(focusIconId);
            tvTabHint.setTextColor(getResources().getColor(
                    R.color.tab_text_focus));
        } else {
            ivTabIcon.setImageResource(normalIconId);
            tvTabHint.setTextColor(getResources().getColor(
                    R.color.tab_text_normal));
        }
    }
}