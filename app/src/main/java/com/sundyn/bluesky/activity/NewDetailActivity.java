package com.sundyn.bluesky.activity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.view.NormalTopBar;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.io.InputStream;

public class NewDetailActivity extends BaseActivity {

    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.news_detail_wv)
    private WebView mWebView;
    private WebSettings settings;
    @ViewInject(R.id.loading_view)
    protected View loadingView;
    @ViewInject(R.id.tv_publishtime)
    protected TextView tv_publishtime;
    @ViewInject(R.id.tv_new_title)
    protected TextView tv_new_title;
    private String url;
    private String notice_id;
    private String title;
    private String time;
    private String content;
    private boolean jpushmsg;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_newdetail);
        x.view().inject(this);
        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                NewDetailActivity.this.finish();
            }
        });
        mTopBar.setTitle("详情");
        mTopBar.setActionTextVisibility(false);
    }

    private void initData() {
        notice_id = getIntent().getStringExtra("notice_id");
        title = getIntent().getStringExtra("title");
        time = getIntent().getStringExtra("time");
        jpushmsg = getIntent().getBooleanExtra("jpushmsg", false);
        if (jpushmsg)
            url = Constant.BASE_URL + Constant.URL_PUSHMSG + notice_id;
        else
            url = Constant.BASE_URL + Constant.URL_NEWCONTENT + notice_id;
        content = getIntent().getStringExtra("content");
        dealNewsDetail();
    }

    private void dealNewsDetail() {
        tv_publishtime.setText(time);
        tv_new_title.setText(title);

        settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true); // 支持通过JS打开新窗口
        // settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        // settings.setTextSize(TextSize.LARGEST);
        mWebView.setWebViewClient(new WebViewClient() {
            // 所有链接跳转会走此方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadurl(view, url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e("onPageStarted", "");
                showLoadingView();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                Log.e("onPageFinished", "");
                dismissLoadingView();
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                Toast.makeText(mContext, "加载失败，请检查网络", Toast.LENGTH_LONG).show();
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        loadurl(mWebView, url);
    }

    public void loadurl(final WebView view, final String url) {
        // view.loadUrl(url);
        addContentToHtml();
    }

    private void addContentToHtml() {
        content = content.replace("\\\"", "'");
        execute("file:///android_asset/",
                getStringFromAssets("content_news.html"));
    }

    public void execute(String loadHtmlModelName_url, String html) {
        mWebView.loadDataWithBaseURL(loadHtmlModelName_url,
                html.replace("{#content}", content), "text/html",
                "charset=UTF-8", null);
    }

    private String getStringFromAssets(String path) {
        AssetManager assetManager = getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream2String(inputStream);
    }

    private String inputStream2String(InputStream in) {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        try {
            for (int n; (n = in.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    public void showLoadingView() {
        if (loadingView != null)
            loadingView.setVisibility(View.VISIBLE);
    }

    public void dismissLoadingView() {
        if (loadingView != null)
            loadingView.setVisibility(View.INVISIBLE);
    }

}
