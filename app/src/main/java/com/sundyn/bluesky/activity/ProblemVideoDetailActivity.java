package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.hik.live.LiveModel;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.view.AlertDialog;
import com.sundyn.bluesky.view.NormalTopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import okhttp3.Call;

/**
 * @author yangjl ÃèÊö£º¹ÊÕÏÏêÇéÒ³activity 2016-6-22ÉÏÎç10:40:14
 */
public class ProblemVideoDetailActivity extends BaseActivity {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.surfaceView)
    private SurfaceView mSurfaceView;
    @ViewInject(R.id.writView)
    private TextView writView;

    private String cameraCode;
    private int id;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_problemvideodetail);
        x.view().inject(this);
        initTitleBar();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            cameraCode = intent.getStringExtra("cameraCode");
            id = intent.getIntExtra("id", 0);
            LiveModel.getLiveModel().play(cameraCode, mContext, mSurfaceView,
                    writView);

        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        writView.setVisibility(View.VISIBLE);
        LiveModel.getLiveModel().play(cameraCode, this, mSurfaceView, writView);
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ProblemVideoDetailActivity.this.finish();
            }
        });
        mTopBar.setTitle("¹ÊÕÏ¼ì²â");
        mTopBar.setActionTextVisibility(false);
    }

    /**
     * ÒÆ³ý¹ÊÕÏ
     *
     * @param view
     */
    public void removeProblem(View view) {
        new AlertDialog(mContext).builder().setTitle("ÌáÊ¾").setMsg("È·¶¨ÒÆ³ýÂð£¿")
                .setPositiveButton("È·¶¨", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (id == 0) {
                            showToast("ÉãÏñÍ·ID¶ªÊ§ÁË...");
                            return;
                        }
                        showDialog("ÕýÔÚÒÆ³ý...");
                        OkHttpUtils.get()
                                .url(Constant.BASE_URL + Constant.URL_DELERR)
                                .addParams("id", id + "").build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onResponse(String response,
                                                           int arg1) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(
                                                    response);
                                            boolean success = jsonObject
                                                    .getBoolean("success");
                                            if (success) {
                                                showToast("ÒÆ³ý³É¹¦!");
                                                ProblemVideoDetailActivity.this
                                                        .finish();
                                            } else {
                                                showToast(jsonObject
                                                        .getString("message"));
                                            }
                                            disDIalog();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            disDIalog();
                                        }

                                        LogUtil.i(response);

                                    }

                                    @Override
                                    public void onError(Call arg0,
                                                        Exception arg1, int arg2) {
                                        showToast("ÒÆ³ýÊ§°Ü!");
                                        disDIalog();
                                    }
                                });

                    }
                }).setNegativeButton("È¡Ïû", new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();

    }

}
