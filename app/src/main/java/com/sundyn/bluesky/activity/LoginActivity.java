package com.sundyn.bluesky.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hikvision.vmsnetsdk.ServInfo;
import com.hikvision.vmsnetsdk.VMSNetSDK;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.bean.User;
import com.sundyn.bluesky.bean.UserInfo;
import com.sundyn.bluesky.hik.TempData;
import com.sundyn.bluesky.utils.AppManager;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.utils.UpdateManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;

/**
 * Created by sundyn on 2016/10/25.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {


    @ViewInject(R.id.et_name)
    private EditText et_name;
    @ViewInject(R.id.et_password)
    private EditText et_password;
    @ViewInject(R.id.login)
    private Button login;

    private static ServInfo servInfo;
    private static String macAddress;

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj, null, mAliasCallback);
                    break;

			/*
             * case MSG_SET_TAGS:
			 * JPushInterface.setAliasAndTags(getApplicationContext(), null,
			 * (Set<String>) msg.obj, mTagsCallback); break;
			 */
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);
        x.view().inject(this);
        login.setOnClickListener(this);
        servInfo = new ServInfo();
        macAddress = getMacAddr();
        UpdateManager manager = new UpdateManager(mContext);
        manager.checkUpdate(false);
    }

    @Override
    public void onClick(View v) {
        hideKeyborad();
        final String username = et_name.getText().toString();
        String password = et_password.getText().toString();
        if (TextUtils.isEmpty(username)) {
            showToast("�û�������Ϊ��");
            return;
        } else if (TextUtils.isEmpty(password)) {
            showToast("���벻��Ϊ��");
            return;
        }
        showDialog("���ڵ�¼...");
        if (0 == CommonUtil.isNetworkAvailable(mContext)) {
            showToast("�����磬�����������ӣ�");
            disDIalog();
            return;
        }
        OkHttpUtils.post().url(Constant.BASE_URL + Constant.URL_LOGIN)
                .addParams("userName", username)
                .addParams("password", password).build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        try {
                            UserInfo userInfo = GsonTools.changeGsonToBean(
                                    response, UserInfo.class);
                            if (userInfo.success) {
                                LogUtil.e("��¼�ɹ�");
                                User user = new User(userInfo.user.userName,
                                        userInfo.user.userNo, userInfo.token,
                                        userInfo.user.roles,
                                        userInfo.user.regionIds);

                                mApplication.setUser(user);

                                setAlias(userInfo.user.userNo);
                                Intent intent = new Intent(mContext,
                                        HomeActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else {
                                showToast("�û������������");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast("��¼ʧ��");
                            disDIalog();
                        }
                        disDIalog();
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        LogUtil.e("��¼ʧ��" + arg1.getMessage());
                        showToast("����������ʧ��");
                        disDIalog();
                    }
                });

        // ��¼������Ƶ
        new MyThread().start();

    }

    private static class MyThread extends Thread {
        @Override
        public void run() {
            // ��¼����
            boolean ret = VMSNetSDK.getInstance().login(
                    Constant.Hik.DEF_SERVER, Constant.Hik.USERNAME,
                    Constant.Hik.PASSWORD, 1, macAddress, 4, servInfo);
            if (ret) {
                TempData.getInstance().setLoginData(servInfo);
            }
        }
    }

    /* ��ȡ��¼�豸mac��ַ */
    protected String getMacAddr() {
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String mac = wm.getConnectionInfo().getMacAddress();
        return mac == null ? "" : mac;
    }

    /* Ϊ�û��������ͱ��� */
    private void setAlias(String alias) {
        if (!CommonUtil.isValidTagAndAlias(alias)) {
            return;
        }
        // ����JPush API����Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    /* ���ñ����ص� */
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            switch (code) {
                case 0:
                    // ���óɹ�
                    break;
                case 6002:
                    if (0 != CommonUtil.isNetworkAvailable(mContext)) {
                        mHandler.sendMessageDelayed(
                                mHandler.obtainMessage(MSG_SET_ALIAS, alias),
                                1000 * 60);
                    }
                    break;

                default:
            }
        }

    };

    public static boolean isForeground = false;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        isForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        isForeground = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private long exitTime = 0;

    /* �ٰ�һ���˳����� onkeydown�ڼ���Tabactivity��activity��ʧЧ���¼���ͻ */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showToast("�ٰ�һ���˳�����");
                exitTime = System.currentTimeMillis();
            } else {
                AppManager.getAppManager().AppExit(mContext);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);

    }
}
