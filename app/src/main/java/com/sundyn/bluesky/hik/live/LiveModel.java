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
 * ���Ʋ��ŵ�ҵ���ֻ࣬��Ҫ����ID���ɲ���
 *
 * @author 08
 */
public class LiveModel implements LiveCallBack, Callback {

    private static LiveModel liveModel = new LiveModel();
    private Context mContext;
    private View writView;//���Ż��������
    private SurfaceView mSurfaceView;


    private VMSNetSDK mVmsNetSDK = null;
    private ServInfo mServInfo;// ��¼��Ϣ
    /* ͨ��VMSNetSDK���ص�Ԥ����ַ���� */
    private RealPlayURL mRealPlayURL;
    /* ���Ʋ���� */
    private LiveControl mLiveControl;
    private String mCameraID = null;// ����ͷID
    private String camera_name;
    private CameraInfoEx cameraInfoEx;

    /**
     * RTSP sdk���
     */
    private RtspClient mRtspHandle = null;
    /**
     * ��ȡ��ص�������
     */
    private boolean getCameraDetailInfoResult = false;
    /**
     * ��ȡ�豸������
     */
    private boolean getDeviceInfoResult = false;
    private String mDeviceID = "";
    private DeviceInfo deviceInfo;
    /**
     * ��¼�豸���û���
     */
    private String mName;

    /**
     * ��¼�豸������
     */
    private String mPassword;

    /**
     * ��������,���������ȣ����塢����
     */
    private int mStreamType = -1;

    /**
     * ������У��ʱ��token
     */
    private String mToken = null;

    /**
     * ������Ϣ����
     */
    private Handler mMessageHandler = new MyHandler();


    private LiveModel() {
    }

    public static LiveModel getLiveModel() {
        return liveModel;
    }

    /**
     * ������Ƶ
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

        mServInfo = TempData.getIns().getLoginData(); // ��¼����
        if (mServInfo == null) {
            showToast("����sessionID��ȡʧ��");
            return;
        }
        mRealPlayURL = new RealPlayURL(); // ���ŵ�ַ
        mLiveControl = new LiveControl();
        mLiveControl.setLiveCallBack(this);

        // cameraInfo = TempData.getIns().getCameraInfo();
        // //��ȡ�����camerainfo,�����ޱ���
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
     * ��ȡ��ص����鷽��
     *
     * @param serAddr   ��������ַ
     * @param sessionid �ỰID
     */
    private void getCameraDetailInfo(final String serAddr,
                                     final String sessionid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCameraDetailInfoResult = mVmsNetSDK.getCameraInfoEx(serAddr,
                        sessionid, mCameraID, cameraInfoEx);// ��Ϣ������cameraInfoEx������
                mDeviceID = cameraInfoEx.getDeviceId();// �豸ID
                deviceInfo = new DeviceInfo();// �豸��Ϣ�������
                // ��ȡ�豸��Ϣ
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
     * �������� void
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
     * �÷����ǻ�ȡ���ŵ�ַ�ģ���mStreamType=2ʱ����ȡ����MAG����mStreamType =1ʱ��ȡ������������mStreamType =
     * 0ʱ��ȡ���������� ���ڸ÷����в��ֲ����Ǽ�ص�����ԣ�������Ҫ�Ȼ�ȡ��ص���Ϣ�������ȡ��ص���Ϣ�ķ�����resourceActivity��
     *
     * @param streamType 2����ʾMAGȡ����ʽ��1����ʾ������ȡ����ʽ��0����ʾ������ȡ����ʽ��
     * @return String ���ŵ�ַ ��2����ʾ���ص���MAG�Ĳ��ŵ�ַ;1����ʾ���ص����������Ĳ��ŵ�ַ��0����ʾ���ص����������Ĳ��ŵ�ַ��
     * @since V1.0
     */
    private String getPlayUrl(int streamType) {
        String url = "";

        if (mRealPlayURL == null) {
            return null;
        }

        // ��ȡ����Token
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
        // ת�벻������������
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
            // ��ȡ��ת���ַ
            liveInfo.setbTranscode(false);
            mRealPlayURL.setUrl1(mRtspHandle.generateLiveUrl(liveInfo));

            // ��ȡת���ַ
            // ʹ��Ĭ��ת�����cif 128 15 h264 ps
            liveInfo.setbTranscode(true);
            mRealPlayURL.setUrl2(mRtspHandle.generateLiveUrl(liveInfo));
        } else {
            liveInfo.setIsInternet(LiveInfo.NETWORK_TYPE_LOCAL);
            liveInfo.setbTranscode(false);
            // ������ת��
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
     * ������Ϣ
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
     * ��Ϣ��
     */
    @SuppressLint("HandlerLeak")
    private final class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case ConstantLive.RTSP_SUCCESS:
                    //showToast("����ȡ���ɹ�");
                    break;

                case ConstantLive.STOP_SUCCESS:
                    //showToast("ֹͣ�ɹ�");
                    break;

                case ConstantLive.START_OPEN_FAILED:
                    showToast("�������ſ�ʧ��");
                    if (null != writView) {
                        writView.setVisibility(View.GONE);
                    }
                    break;

                case ConstantLive.PLAY_DISPLAY_SUCCESS:
                    //showToast("���ųɹ�");
                    if (null != writView) {
                        writView.setVisibility(View.GONE);
                    }
                    break;

                case ConstantLive.RTSP_FAIL:
                    showToast("RTSP����ʧ��");
                    if (null != writView) {
                        writView.setVisibility(View.GONE);
                    }
                    if (null != mLiveControl) {
                        mLiveControl.stop();
                    }
                    break;

                case ConstantLive.GET_OSD_TIME_FAIL:
                    showToast("��ȡOSDʱ��ʧ��");
                    break;

                case ConstantLive.SD_CARD_UN_USEABLE:
                    showToast("SD��������");
                    break;

                case ConstantLive.SD_CARD_SIZE_NOT_ENOUGH:
                    showToast("SD���ռ䲻��");
                    break;
                case ConstantLive.CAPTURE_FAILED_NPLAY_STATE:
                    showToast("�ǲ���״̬����ץ��");
                    break;
                case ConstantLive.RECORD_FAILED_NPLAY_STATE:
                    showToast("�ǲ���״̬����¼��");
                    break;
                case ConstantLive.AUDIO_START_FAILED_NPLAY_STATE:
                    showToast("�ǲ���״̬���ܿ�����Ƶ");
                    break;
            }
        }
    }


    /**
     * ץ��
     *
     * @since V1.0
     */
    public String capture() {
        String path = null;
        if (null != mLiveControl) {
            if (mLiveControl.LIVE_PLAY != mLiveControl.getLiveState()) {
                showToast("��Ƶδ���ţ��޷���ͼ");
                return null;
            }
            // �漴����һ��1��10000�����֣�����ץ��ͼƬ���Ƶ�һ���֣�����ͼƬ�������߿��Ը���ʵ������޸�����ͼƬ���Ƶķ���
            int recordIndex = new Random().nextInt(10000);
            boolean ret = mLiveControl.capture(UtilFilePath.getPictureDirPath()
                    .getAbsolutePath(), "Picture" + recordIndex + ".jpg");
            if (ret) {
                UtilAudioPlay.playAudioFile(mContext, R.raw.paizhao);
                path = UtilFilePath.getPictureDirPath().getAbsolutePath() + File.separator + "Picture" + recordIndex + ".jpg";
            } else {
                showToast("ץ��ʧ��");
            }
        }
        return path;
    }


    /**
     * ��ʾ�Զ�����˾
     */
    public void showToast(String msg) {
        showToast(msg, 0);
    }

    public void showToast(String msg, int time) {
        CustomToast customToast = new CustomToast(mContext, msg, time);
        customToast.show();
    }

    /**
     * surface�Ļص�����
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
			 * if (mIsRecord) { mRecordBtn.setText("��ʼ¼��");
			 * mLiveControl.stopRecord(); mIsRecord = false; }
			 */
            mLiveControl.stop();
        }
    }


}
