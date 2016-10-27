package com.sundyn.bluesky.hik.live;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import com.hik.mcrsdk.rtsp.LiveInfo;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.vmsnetsdk.CameraInfoEx;
import com.hikvision.vmsnetsdk.RealPlayURL;
import com.hikvision.vmsnetsdk.ServInfo;
import com.hikvision.vmsnetsdk.VMSNetSDK;
import com.hikvision.vmsnetsdk.netLayer.msp.deviceInfo.DeviceInfo;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.hik.TempData;
import com.sundyn.bluesky.hik.util.UtilAudioPlay;
import com.sundyn.bluesky.hik.util.UtilFilePath;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.CustomToast;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.util.Random;

/**
 * 控制播放的业务类，只需要传入ID即可播放
 *
 * @author 08
 */
public class LiveModel implements LiveCallBack, Callback {

    private static LiveModel liveModel = new LiveModel();
    private Context mContext;
    private View writView;//播放缓冲进度条
    private SurfaceView mSurfaceView;


    private VMSNetSDK mVmsNetSDK = null;
    private ServInfo mServInfo;// 登录信息
    /* 通过VMSNetSDK返回的预览地址对象 */
    private RealPlayURL mRealPlayURL;
    /* 控制层对象 */
    private LiveControl mLiveControl;
    private String mCameraID = null;// 摄像头ID
    private String camera_name;
    private CameraInfoEx cameraInfoEx;

    /**
     * RTSP sdk句柄
     */
    private RtspClient mRtspHandle = null;
    /**
     * 获取监控点详情结果
     */
    private boolean getCameraDetailInfoResult = false;
    /**
     * 获取设备详情结果
     */
    private boolean getDeviceInfoResult = false;
    private String mDeviceID = "";
    private DeviceInfo deviceInfo;
    /**
     * 登录设备的用户名
     */
    private String mName;

    /**
     * 登录设备的密码
     */
    private String mPassword;

    /**
     * 码流类型,控制清晰度，高清、标清
     */
    private int mStreamType = -1;

    /**
     * 服务器校验时的token
     */
    private String mToken = null;

    /**
     * 创建消息对象
     */
    private Handler mMessageHandler = new MyHandler();


    private LiveModel() {
    }

    public static LiveModel getLiveModel() {
        return liveModel;
    }

    /**
     * 播放视频
     *
     * @param camrarID
     * @param context
     */
    public void play(String camrarID, Context context, SurfaceView mSurfaceView, View writView) {
        this.mCameraID = camrarID;
        this.mContext = context;
        this.mSurfaceView = mSurfaceView;
        this.writView = writView;

        mStreamType = ConstantLive.SUB_STREAM;
        mSurfaceView.getHolder().addCallback(this);

        mServInfo = TempData.getIns().getLoginData(); // 登录数据
        if (mServInfo == null) {
            showToast("海康sessionID获取失败");
            return;
        }
        mRealPlayURL = new RealPlayURL(); // 播放地址
        mLiveControl = new LiveControl();
        mLiveControl.setLiveCallBack(this);

        // cameraInfo = TempData.getIns().getCameraInfo();
        // //获取保存的camerainfo,这里无保存
        cameraInfoEx = new CameraInfoEx();
        cameraInfoEx.setId(mCameraID);
        mVmsNetSDK = VMSNetSDK.getInstance();
        if (mVmsNetSDK == null) {
            return;
        }
        String serAddr = Constant.Hik.DEF_SERVER;
        String sessionid = mServInfo.getSessionID();

        getCameraDetailInfo(serAddr, sessionid);

        mRtspHandle = RtspClient.getInstance();
        if (null == mRtspHandle) {
            LogUtil.i("initialize:" + "RealPlay mRtspHandle is null!");
            return;
        }
    }

    /**
     * 获取监控点详情方法
     *
     * @param serAddr   服务器地址
     * @param sessionid 会话ID
     */
    private void getCameraDetailInfo(final String serAddr,
                                     final String sessionid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCameraDetailInfoResult = mVmsNetSDK.getCameraInfoEx(serAddr,
                        sessionid, mCameraID, cameraInfoEx);// 信息保存在cameraInfoEx对象中
                mDeviceID = cameraInfoEx.getDeviceId();// 设备ID
                deviceInfo = new DeviceInfo();// 设备信息保存对象
                // 获取设备信息
                getDeviceInfoResult = mVmsNetSDK.getDeviceInfo(serAddr,
                        sessionid, mDeviceID, deviceInfo);
                if (!getDeviceInfoResult || null == deviceInfo
                        || deviceInfo.getLoginName().equals("")
                        || deviceInfo.getLoginPsw().equals("")) {
                    deviceInfo.setLoginName(Constant.Hik.USERNAME);
                    deviceInfo.setLoginPsw(Constant.Hik.PASSWORD);
                }
                mName = deviceInfo.getLoginName();
                mPassword = deviceInfo.getLoginPsw();
                startBtnOnClick();

                LogUtil.i("ret is :" + getDeviceInfoResult + "----------------"
                        + deviceInfo.getDeviceName() + "--------"
                        + "deviceLoginName is " + mName + "---"
                        + "deviceLoginPassword is " + mPassword + "-----"
                        + "deviceID is " + mDeviceID);
            }

        }).start();

    }

    /**
     * 启动播放 void
     *
     * @since V1.0
     */
    private void startBtnOnClick() {
        writView.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                super.run();
                mLiveControl.setLiveParams(getPlayUrl(mStreamType), mName,
                        mPassword);
                if (mLiveControl.LIVE_PLAY == mLiveControl.getLiveState()) {
                    mLiveControl.stop();
                }

                if (mLiveControl.LIVE_INIT == mLiveControl.getLiveState()) {
                    mLiveControl.startLive(mSurfaceView);
                }
            }
        }.start();
    }

    /**
     * 该方法是获取播放地址的，当mStreamType=2时，获取的是MAG，当mStreamType =1时获取的子码流，当mStreamType =
     * 0时获取的是主码流 由于该方法中部分参数是监控点的属性，所以需要先获取监控点信息，具体获取监控点信息的方法见resourceActivity。
     *
     * @param streamType 2、表示MAG取流方式；1、表示子码流取流方式；0、表示主码流取流方式；
     * @return String 播放地址 ：2、表示返回的是MAG的播放地址;1、表示返回的是子码流的播放地址；0、表示返回的是主码流的播放地址。
     * @since V1.0
     */
    private String getPlayUrl(int streamType) {
        String url = "";

        if (mRealPlayURL == null) {
            return null;
        }

        // 获取播放Token
        mToken = mVmsNetSDK.getPlayToken(mServInfo.getSessionID());
        LogUtil.i("mToken is :" + mToken);
        LogUtil.d("generateLiveUrl MagStreamSerAddr:"
                + mServInfo.getMagServer().getMagStreamSerAddr());
        LogUtil.d("generateLiveUrl MagStreamSerPort:"
                + mServInfo.getMagServer().getMagStreamSerPort());
        LogUtil.d("generateLiveUrl cameraId:" + cameraInfoEx.getId());
        LogUtil.d("generateLiveUrl token:" + mToken);
        LogUtil.d("generateLiveUrl streamType:" + streamType);
        LogUtil.d("generateLiveUrl appNetId:" + mServInfo.getAppNetId());
        LogUtil.d("generateLiveUrl deviceNetID:"
                + cameraInfoEx.getDeviceNetId());
        LogUtil.d("generateLiveUrl userAuthority:"
                + mServInfo.getUserAuthority());
        LogUtil.d("generateLiveUrl cascadeFlag:"
                + cameraInfoEx.getCascadeFlag());
        LogUtil.d("generateLiveUrl internet:" + mServInfo.isInternet());

        LiveInfo liveInfo = new LiveInfo();
        liveInfo.setMagIp(mServInfo.getMagServer().getMagStreamSerAddr());
        liveInfo.setMagPort(mServInfo.getMagServer().getMagStreamSerPort());
        liveInfo.setCameraIndexCode(cameraInfoEx.getId());
        liveInfo.setToken(mToken);
        // 转码不区分主子码流
        liveInfo.setStreamType(streamType);
        liveInfo.setMcuNetID(mServInfo.getAppNetId());
        liveInfo.setDeviceNetID(cameraInfoEx.getDeviceNetId());
        liveInfo.setiPriority(mServInfo.getUserAuthority());
        liveInfo.setCascadeFlag(cameraInfoEx.getCascadeFlag());

        if (deviceInfo != null) {
            if (cameraInfoEx.getCascadeFlag() == LiveInfo.CASCADE_TYPE_YES) {
                deviceInfo.setLoginName(Constant.Hik.USERNAME);
                deviceInfo.setLoginPsw(Constant.Hik.PASSWORD);
            }
        }

        if (mServInfo.isInternet()) {
            liveInfo.setIsInternet(LiveInfo.NETWORK_TYPE_INTERNET);
            // 获取不转码地址
            liveInfo.setbTranscode(false);
            mRealPlayURL.setUrl1(mRtspHandle.generateLiveUrl(liveInfo));

            // 获取转码地址
            // 使用默认转码参数cif 128 15 h264 ps
            liveInfo.setbTranscode(true);
            mRealPlayURL.setUrl2(mRtspHandle.generateLiveUrl(liveInfo));
        } else {
            liveInfo.setIsInternet(LiveInfo.NETWORK_TYPE_LOCAL);
            liveInfo.setbTranscode(false);
            // 内网不转码
            mRealPlayURL.setUrl1(mRtspHandle.generateLiveUrl(liveInfo));
            mRealPlayURL.setUrl2("");
        }

        LogUtil.d("url1:" + mRealPlayURL.getUrl1());
        LogUtil.d("url2:" + mRealPlayURL.getUrl2());

        url = mRealPlayURL.getUrl1();
        if (streamType == 2 && mRealPlayURL.getUrl2() != null
                && mRealPlayURL.getUrl2().length() > 0) {
            url = mRealPlayURL.getUrl2();
        }

        LogUtil.d("mRTSPUrl" + url);

        return url;
    }


    @Override
    public void onMessageCallback(int messageID) {
        sendMessageCase(messageID);
    }

    /**
     * 发送消息
     *
     * @param i void
     * @since V1.0
     */
    private void sendMessageCase(int i) {
        if (null != mMessageHandler) {
            Message msg = Message.obtain();
            msg.arg1 = i;
            mMessageHandler.sendMessage(msg);
        }
    }

    /**
     * 消息类
     */
    @SuppressLint("HandlerLeak")
    private final class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case ConstantLive.RTSP_SUCCESS:
                    //showToast("启动取流成功");
                    break;

                case ConstantLive.STOP_SUCCESS:
                    //showToast("停止成功");
                    break;

                case ConstantLive.START_OPEN_FAILED:
                    showToast("开启播放库失败");
                    if (null != writView) {
                        writView.setVisibility(View.GONE);
                    }
                    break;

                case ConstantLive.PLAY_DISPLAY_SUCCESS:
                    //showToast("播放成功");
                    if (null != writView) {
                        writView.setVisibility(View.GONE);
                    }
                    break;

                case ConstantLive.RTSP_FAIL:
                    showToast("RTSP链接失败");
                    if (null != writView) {
                        writView.setVisibility(View.GONE);
                    }
                    if (null != mLiveControl) {
                        mLiveControl.stop();
                    }
                    break;

                case ConstantLive.GET_OSD_TIME_FAIL:
                    showToast("获取OSD时间失败");
                    break;

                case ConstantLive.SD_CARD_UN_USEABLE:
                    showToast("SD卡不可用");
                    break;

                case ConstantLive.SD_CARD_SIZE_NOT_ENOUGH:
                    showToast("SD卡空间不足");
                    break;
                case ConstantLive.CAPTURE_FAILED_NPLAY_STATE:
                    showToast("非播放状态不能抓拍");
                    break;
                case ConstantLive.RECORD_FAILED_NPLAY_STATE:
                    showToast("非播放状态不能录像");
                    break;
                case ConstantLive.AUDIO_START_FAILED_NPLAY_STATE:
                    showToast("非播放状态不能开启音频");
                    break;
            }
        }
    }


    /**
     * 抓拍
     *
     * @since V1.0
     */
    public String capture() {
        String path = null;
        if (null != mLiveControl) {
            if (mLiveControl.LIVE_PLAY != mLiveControl.getLiveState()) {
                showToast("视频未播放，无法截图");
                return null;
            }
            // 随即生成一个1到10000的数字，用于抓拍图片名称的一部分，区分图片，开发者可以根据实际情况修改区分图片名称的方法
            int recordIndex = new Random().nextInt(10000);
            boolean ret = mLiveControl.capture(UtilFilePath.getPictureDirPath()
                    .getAbsolutePath(), "Picture" + recordIndex + ".jpg");
            if (ret) {
                UtilAudioPlay.playAudioFile(mContext, R.raw.paizhao);
                path = UtilFilePath.getPictureDirPath().getAbsolutePath() + File.separator + "Picture" + recordIndex + ".jpg";
            } else {
                showToast("抓拍失败");
            }
        }
        return path;
    }


    /**
     * 显示自定义吐司
     */
    public void showToast(String msg) {
        showToast(msg, 0);
    }

    public void showToast(String msg, int time) {
        CustomToast customToast = new CustomToast(mContext, msg, time);
        customToast.show();
    }

    /**
     * surface的回调函数
     */
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        if (null != mLiveControl) {
            /*
			 * if (mIsRecord) { mRecordBtn.setText("开始录像");
			 * mLiveControl.stopRecord(); mIsRecord = false; }
			 */
            mLiveControl.stop();
        }
    }


}
