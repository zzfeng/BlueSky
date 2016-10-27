package com.sundyn.bluesky.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.MyImageActivity;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.utils.AppManager;
import com.sundyn.bluesky.utils.DataCleanManager;
import com.sundyn.bluesky.utils.UpdateManager;
import com.sundyn.bluesky.view.AlertDialog;
import com.sundyn.bluesky.view.MyFraOfView;
import com.sundyn.bluesky.view.NormalTopBar;
import com.sundyn.bluesky.view.svprogresshud.SVProgressHUD;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class MyFra extends BaseFragment implements OnClickListener {
    private View view;
    private NormalTopBar mTopBar;

    @ViewInject(R.id.cleanCache)
    protected MyFraOfView cleanCache;
    @ViewInject(R.id.update)
    protected MyFraOfView update;
    @ViewInject(R.id.commen)
    protected MyFraOfView commen;
    @ViewInject(R.id.myFile)
    protected MyFraOfView myFile;
    @ViewInject(R.id.bt_ext)
    protected Button bt_ext;
    @ViewInject(R.id.tv_userName)
    protected TextView tv_userName;
    @ViewInject(R.id.id_contain)
    protected LinearLayout contain;

    private SVProgressHUD mSVProgressHUD;
    private UpdateManager manager;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fra_my, null);
        x.view().inject(this, view); // 注入view和事件
        mTopBar = (NormalTopBar) view.findViewById(R.id.chat_top_bar);
        mTopBar.setBackVisibility(false);
        mTopBar.setTitle("我的");

        Animation animation = AnimationUtils.loadAnimation(mContext,
                R.anim.anim_newsitem);
        LayoutAnimationController controller = new LayoutAnimationController(
                animation);
        controller.setDelay(0.3f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        contain.setLayoutAnimation(controller);

        initEvent();
        return view;

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        tv_userName.setText(mApplication.getUser().getUsername());

    }

    private void initEvent() {
        cleanCache.setOnClickListener(this);
        update.setOnClickListener(this);
        commen.setOnClickListener(this);
        bt_ext.setOnClickListener(this);
        myFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cleanCache:
                String cacheSize = DataCleanManager.getCacheSize(mContext);
                if ("0.0Byte".equals(cacheSize)) {
                    new AlertDialog(mContext).builder().setMsg("该应用无缓存")
                            .setNegativeButton("确定", new OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                } else {
                    new AlertDialog(mContext).builder().setTitle("提示")
                            .setMsg("缓存大小:" + cacheSize)
                            .setPositiveButton("清理", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DataCleanManager
                                            .cleanApplicationCache(mContext);
                                }
                            }).setNegativeButton("取消", new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }
                break;
            case R.id.update:
                if (mSVProgressHUD == null)
                    mSVProgressHUD = new SVProgressHUD(mContext);
                if (mSVProgressHUD.isShowing()) {
                    return;
                }
                if (manager == null)
                    manager = new UpdateManager(mContext);
                manager.checkUpdate(true);
                break;
            case R.id.myFile:
                Intent myFileIntent = new Intent(mContext, MyImageActivity.class);
                startActivity(myFileIntent);
                break;
            case R.id.commen:

                break;
            case R.id.bt_ext:
                new AlertDialog(mContext).builder().setTitle("提示")
                        .setMsg("退出当前账号？")
                        .setPositiveButton("确定", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mApplication.setUser(null);
                                AppManager.getAppManager().AppExit(mContext);
                            }
                        }).setNegativeButton("取消", new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
                break;
            default:
                break;
        }
    }
}
