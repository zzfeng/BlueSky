package com.sundyn.bluesky.activity;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.vmsnetsdk.VMSNetSDK;
import com.sundyn.bluesky.BuildConfig;
import com.sundyn.bluesky.bean.AuthModle.ModleItem;
import com.sundyn.bluesky.bean.CheckProblem.ProblemItem;
import com.sundyn.bluesky.bean.User;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {

    public User user;
    /* ����̨Ȩ��ģ�� */
    public List<ModleItem> controlModleData;
    public ArrayList<ProblemItem> problemList;

    public ArrayList<ProblemItem> getProblemList() {
        return problemList;
    }

    public void setProblemList(ArrayList<ProblemItem> problemList) {
        this.problemList = problemList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        // ��ʹ�� SDK �����֮ǰ��ʼ�� context ��Ϣ������ ApplicationContext
        SDKInitializer.initialize(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // ����debug��Ӱ������

        JPushInterface.setDebugMode(true); // ���ÿ�����־,����ʱ��ر���־
        JPushInterface.init(this); // ��ʼ�� JPush
        // ������Ƶ��ʼ��
        System.loadLibrary("gnustl_shared");
        MCRSDK.init();
        RtspClient.initLib();
        MCRSDK.setPrint(1, null);
        VMSNetSDK.getInstance().openLog(true);
    }

    public List<ModleItem> getControlModleData() {
        return controlModleData;
    }

    public void setControlModleData(List<ModleItem> controlModleData) {
        this.controlModleData = controlModleData;
    }

    /**
     * �˳�application
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
