package com.sundyn.bluesky.activity;

import android.os.Bundle;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;

public class JpushMessageActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jpushmsg);
    /*	TextView tv = new TextView(this);
        Intent intent = getIntent();
		if (null != intent) {
			Bundle bundle = getIntent().getExtras();
			String title = bundle
					.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			String content = bundle.getString(JPushInterface.EXTRA_ALERT);
			tv.setText("Title : " + title + "  " + "Content : " + content);
		}
		addContentView(tv, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));*/
    }
}
