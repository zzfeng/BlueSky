package com.sundyn.bluesky.hik.live;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;

import org.MediaPlayer.PlayM4.Player;
import org.MediaPlayer.PlayM4.Player.MPSystemTime;
import org.MediaPlayer.PlayM4.PlayerCallBack.PlayerDisplayCB;

import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hik.mcrsdk.rtsp.RtspClient;
import com.hik.mcrsdk.rtsp.RtspClientCallback;
import com.mobile.exception.MediaPlayerException;
import com.mobile.exception.PlayerException;
import com.sundyn.bluesky.hik.util.DebugLog;
import com.sundyn.bluesky.hik.util.UtilSDCard;

/**
 * Ԥ�����Ʋ�
 *
 * @author huangweifeng
 * @Data 2013-10-21
 */
public class LiveControl implements RtspClientCallback, PlayerDisplayCB {
    private final String TAG = this.getClass().getSimpleName();
    /**
     * �������ſ�������
     */
    private Player mPlayerHandler;
    /**
     * ����RTSPȡ����������
     */
    private RtspClient mRtspHandler;
    /**
     * ���ſⲥ�Ŷ˿�
     */
    private int mPlayerPort = -1;
    /**
     * ��ʼ���׶�
     */
    public final int LIVE_INIT = 0;
    /**
     * ȡ���׶�
     */
    public final int LIVE_STREAM = 1;
    /**
     * ���Ž׶�
     */
    public final int LIVE_PLAY = 2;
    /**
     * �ͷ���Դ�׶�
     */
    public final int LIVE_RELEASE = 3;
    /**
     * Ԥ��״̬
     */
    private int mLiveState = LIVE_INIT;
    /**
     * ���ŵ�ַ��URL��֧��MAG������ý��
     */
    private String mUrl = "";
    /**
     * ����ʹ�õ�SurfaceView����
     */
    private SurfaceView mSurfaceView;
    /**
     * ����RTSP��������
     */
    private int mRtspEngineIndex = RtspClient.RTSPCLIENT_INVALIDATE_ENGINEID;
    private LiveCallBack mLiveCallBack = null;
    private int connectNum = 0;
    /**
     * ץ��ͼƬ�ļ�
     */
    private File mPictureFile = null;
    /**
     * ¼���ļ�
     */
    private File mRecordFile = null;
    /**
     * �Ƿ�����¼��
     */
    private boolean mIsRecord = true;
    /**
     * ������
     */
    private ByteBuffer mStreamHeadDataBuffer;
    /**
     * �ļ������
     */
    private FileOutputStream mRecordFileOutputStream;
    /**
     * ��������
     */
    private long mStreamRate = 0;
    /**
     * ����SD��ʹ���޶ȣ���С��256Mʱ����ʾSD���ڴ治�㣬���ݾ�����������޸�
     */
    private int mSDCardSize = 256 * 1024 * 1024;
    /**
     * ת��װ״̬
     */
    private int mTransState = -1;
    private String mDeviceUserName = "";
    private String mDevicePassword = "";

    /**
     * ���캯��
     *
     * @param context
     */
    public LiveControl() {
        init();
    }

    /**
     * ���Ʋ��ʼ������ void
     *
     * @since V1.0
     */
    public void init() {
        mPlayerHandler = Player.getInstance();
        mRtspHandler = RtspClient.getInstance();
        mLiveState = LIVE_INIT;
    }

    /**
     * ����Ԥ������
     *
     * @param url      ���ŵ�ַ(��MAG/��ý��)
     * @param name     ��¼�豸���û���
     * @param password ��¼�豸������ void
     * @since V1.0
     */
    public void setLiveParams(String url, String name, String password) {
        mUrl = url;
        mDeviceUserName = name;
        mDevicePassword = password;
    }

    /**
     * ���ÿ��Ʋ�ص��ӿ�
     *
     * @param liveCallBack
     * @since V1.0
     */
    public void setLiveCallBack(LiveCallBack liveCallBack) {
        mLiveCallBack = liveCallBack;
    }

    /**
     * �������Ʋ㲥��
     *
     * @param surfaceView
     * @since V1.0
     */
    public void startLive(SurfaceView surfaceView) {
        if (null == surfaceView) {
            DebugLog.error(TAG, "startLive():: surfaceView is null");
            return;
        }
        mSurfaceView = surfaceView;

        if (LIVE_STREAM == mLiveState) {
            DebugLog.error(TAG, "startLive():: is palying");
        }
        startRtsp();
    }

    /**
     * ��ȡ��ǰ����״̬
     *
     * @return LIVE_INIT��ʼ����LIVE_STREAMȡ����LIVE_PLAY���š�LIVE_RELEASE�ͷ���Դ
     * @since V1.0
     */
    public int getLiveState() {
        return mLiveState;
    }

    /**
     * ����RTSP��ʼȡ��
     *
     * @since V1.0
     */
    private void startRtsp() {
        if (null == mRtspHandler) {
            DebugLog.error(TAG, "startRtsp():: mRtspHandler is null");
            return;
        }
        mRtspEngineIndex = mRtspHandler.createRtspClientEngine(this, RtspClient.RTPRTSP_TRANSMODE);
        if (mRtspEngineIndex < 0) {
            int errorCode = mRtspHandler.getLastError();
            DebugLog.error("AAA", "startRtsp():: errorCode is R" + errorCode);
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLive.RTSP_FAIL);
            }
            return;
        }
        boolean ret = mRtspHandler.startRtspProc(mRtspEngineIndex, mUrl, mDeviceUserName, mDevicePassword);
        if (!ret) {
            int errorCode = mRtspHandler.getLastError();
            DebugLog.error(TAG, "startRtsp():: errorCode is R" + errorCode);
            mRtspHandler.releaseRtspClientEngineer(mRtspEngineIndex);
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLive.RTSP_FAIL);
            }
            return;
        }
        mLiveState = LIVE_STREAM;
        if (null != mLiveCallBack) {
            mLiveCallBack.onMessageCallback(ConstantLive.RTSP_SUCCESS);
        }
    }

    /**
     * ֹͣԤ������
     *
     * @since V1.0
     */
    public void stop() {
        if (LIVE_INIT == mLiveState) {
            return;
        }

        if (mIsRecord) {
            stopRecord();
            mIsRecord = false;
        }

        stopRtsp();
        closePlayer();
        if (null != mLiveCallBack) {
            mLiveCallBack.onMessageCallback(ConstantLive.STOP_SUCCESS);
        }

        mLiveState = LIVE_INIT;
    }

    /**
     * ֹͣRTSP
     *
     * @since V1.0
     */
    private void stopRtsp() {
        if (null != mRtspHandler) {
            if (RtspClient.RTSPCLIENT_INVALIDATE_ENGINEID != mRtspEngineIndex) {
                mRtspHandler.stopRtspProc(mRtspEngineIndex);
                mRtspHandler.releaseRtspClientEngineer(mRtspEngineIndex);
                mRtspEngineIndex = RtspClient.RTSPCLIENT_INVALIDATE_ENGINEID;
            }
        }
    }

    /**
     * �رղ��ſ� void
     *
     * @since V1.0
     */
    private void closePlayer() {
        if (null != mPlayerHandler) {
            if (-1 != mPlayerPort) {
                boolean ret = mPlayerHandler.stop(mPlayerPort);
                if (!ret) {
                    DebugLog.error(
                            TAG,
                            "closePlayer(): Player stop  failed!  errorCode is P"
                                    + mPlayerHandler.getLastError(mPlayerPort));
                }

                ret = mPlayerHandler.closeStream(mPlayerPort);
                if (!ret) {
                    DebugLog.error(TAG, "closePlayer(): Player closeStream  failed!");
                }

                ret = mPlayerHandler.freePort(mPlayerPort);
                if (!ret) {
                    DebugLog.error(TAG, "closePlayer(): Player freePort  failed!");
                }
                mPlayerPort = -1;
            }
        }
    }

    /*
     * handle - - ����id dataType - - �������ͣ�����data���ݵ�����,����DATATYPE_HEADER��DATATYPE_STREAM�������� data -
     * -�ص�����,��Ϊ��header���ݺ�stream���ݣ���datatype�����֣�header���ڳ�ʼ�����ſ� length - - data ���ݵĳ��� timeStamp - - ʱ����������� packetNo -
     * -rtp���ţ������� useId - - �û����ݣ�Ĭ�Ͼ�������id��handle��ͬ
     */

    @Override
    public void onDataCallBack(int handle, int dataType, byte[] data, int length, int timeStamp, int packetNo, int useId) {
        if (mStreamRate + length >= Long.MAX_VALUE) {
            mStreamRate = 0;
        }
        mStreamRate += length;

        switch (dataType) {
            case RtspClient.DATATYPE_HEADER:
                boolean ret = processStreamHeader(data, length);
                if (!ret) {
                    if (null != mLiveCallBack) {
                        mLiveCallBack.onMessageCallback(ConstantLive.START_OPEN_FAILED);
                        return;
                    } else {
                        DebugLog.error(TAG, "onDataCallBack():: mLiveCallBack is null");
                    }
                } else {
                    DebugLog.error(TAG, "MediaPlayer Header success!");
                }
                break;
            default:
                processStreamData(data, length);
                break;
        }
        processRecordData(dataType, data, length);
    }

    /**
     * ¼�����ݴ���
     *
     * @param dataType   ������
     * @param dataBuffer ���ݻ���
     * @param dataLength ���ݳ���
     */
    private void processRecordData(int dataType, byte[] dataBuffer, int dataLength) {
        if (null == dataBuffer || dataLength == 0) {
            return;
        }
        if (mIsRecord) {
            if (RtspClient.DATATYPE_HEADER == dataType) {
                mStreamHeadDataBuffer = ByteBuffer.allocate(dataLength);
                for (int i = 0; i < dataLength; i++) {
                    mStreamHeadDataBuffer.put(dataBuffer[i]);
                }
            } else if (RtspClient.DATATYPE_STREAM == dataType) {
                writeStreamData(dataBuffer, dataLength);
            }
        } else {
            if (-1 != mTransState) {
                mTransState = -1;
            }
        }
    }

    /**
     * ¼������д���ļ�
     *
     * @param recordData ¼������
     * @param length     ¼�����ݳ���
     * @since V1.0
     */
    private boolean writeStreamData(byte[] recordData, int length) {
        if (null == recordData || length <= 0) {
            return false;
        }

        if (null == mRecordFile) {
            return false;
        }

        try {
            if (null == mRecordFileOutputStream) {
                mRecordFileOutputStream = new FileOutputStream(mRecordFile);
            }
            mRecordFileOutputStream.write(recordData, 0, length);
            DebugLog.error(TAG, "writeStreamData() success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * ����������ͷ
     *
     * @param data
     * @param len
     * @return boolean
     * @since V1.0
     */
    private boolean processStreamHeader(byte[] data, int len) {
        if (-1 != mPlayerPort) {
            closePlayer();
        }

        boolean ret = startPlayer(data, len);
        return ret;
    }

    /**
     * �������ſⷽ��
     *
     * @param data
     * @param len
     * @return boolean
     * @since V1.0
     */
    private boolean startPlayer(byte[] data, int len) {
        if (null == data || 0 == len) {
            DebugLog.error(TAG, "startPlayer() Stream data error data is null or len is 0");
            return false;
        }

        if (null == mPlayerHandler) {
            DebugLog.error(TAG, "startPlayer(): mPlayerHandler is null!");
            return false;
        }

        mPlayerPort = mPlayerHandler.getPort();
        if (-1 == mPlayerPort) {
            DebugLog.error(TAG, "startPlayer(): mPlayerPort is -1");
            return false;
        }

        boolean ret = mPlayerHandler.setStreamOpenMode(mPlayerPort, Player.STREAM_REALTIME);
        if (!ret) {
            int tempErrorCode = mPlayerHandler.getLastError(mPlayerPort);
            mPlayerHandler.freePort(mPlayerPort);
            mPlayerPort = -1;
            DebugLog.error(TAG, "startPlayer(): Player setStreamOpenMode failed! errorCord is P" + tempErrorCode);
            return false;
        }

        ret = mPlayerHandler.openStream(mPlayerPort, data, len, 2 * 1024 * 1024);
        if (!ret) {
            DebugLog.error(TAG, "startPlayer() mPlayerHandle.openStream failed!" + "Port: " + mPlayerPort
                    + "ErrorCode is P " + mPlayerHandler.getLastError(mPlayerPort));
            return false;
        }

        ret = mPlayerHandler.setDisplayCB(mPlayerPort, this);
        if (!ret) {
            DebugLog.error(
                    TAG,
                    "startPlayer() mPlayerHandle.setDisplayCB() failed errorCode is P"
                            + mPlayerHandler.getLastError(mPlayerPort));
            return false;
        }

        if (null == mSurfaceView) {
            DebugLog.error(TAG, "startPlayer():: mSurfaceView is null");
            return false;
        }

        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (null == surfaceHolder) {
            DebugLog.error(TAG, "startPlayer() mPlayer mainSurface is null!");
            return false;
        }

        ret = mPlayerHandler.play(mPlayerPort, surfaceHolder);
        if (!ret) {
            DebugLog.error(TAG,
                    "startPlayer() mPlayerHandle.play failed!" + "Port: " + mPlayerPort + "PlayView Surface: "
                            + surfaceHolder + "errorCode is P" + mPlayerHandler.getLastError(mPlayerPort));
            return false;
        }

        return true;
    }

    /**
     * ץ�� void
     *
     * @param filePath ����ļ�·��
     * @param picName  ץ��ʱ�ļ�������
     * @return true-ץ�ĳɹ���false-ץ��ʧ��
     * @since V1.0
     */
    public boolean capture(String filePath, String picName) {
        if (!UtilSDCard.isSDCardUsable()) {
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLive.SD_CARD_UN_USEABLE);
            }
            return false;
        }

        if (UtilSDCard.getSDCardRemainSize() <= mSDCardSize) {
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLive.SD_CARD_SIZE_NOT_ENOUGH);
            }
            return false;
        }

        if (LIVE_PLAY != mLiveState) {
            mLiveCallBack.onMessageCallback(ConstantLive.CAPTURE_FAILED_NPLAY_STATE);
            return false;
        }

        byte[] pictureBuffer = getPictureOnJPEG();
        if (null == pictureBuffer || pictureBuffer.length == 0) {
            DebugLog.error(TAG, "capture():: pictureBuffer is null or length 0");
            return false;
        }

        boolean ret = createPictureFile(filePath, picName);
        if (!ret) {
            pictureBuffer = null;
            DebugLog.error(TAG, "capture():: createPictureFile() return false");
            return false;
        }

        ret = writePictureToFile(pictureBuffer, pictureBuffer.length);
        if (!ret) {
            pictureBuffer = null;
            removePictureFile();
            DebugLog.error(TAG, "capture():: writePictureToFile() return false");
            return false;
        }
        return true;
    }

    /**
     * ��ȡJPEGͼƬ����
     *
     * @return JPEGͼƬ������.
     * @since V1.0
     */
    private byte[] getPictureOnJPEG() {
        if (null == mPlayerHandler) {
            DebugLog.error(TAG, "getPictureOnJPEG():: mPlayerHandler is null");
            return null;
        }

        if (-1 == mPlayerPort) {
            DebugLog.error(TAG, "getPictureOnJPEG():: mPlayerPort is Unavailable");
            return null;
        }

        int picSize = getPictureSize();
        if (picSize <= 0) {
            return null;
        }

        byte[] pictureBuffer = null;
        try {
            pictureBuffer = new byte[picSize];
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            pictureBuffer = null;
            return null;
        }

        Player.MPInteger jpgSize = new Player.MPInteger();

        boolean ret = mPlayerHandler.getJPEG(mPlayerPort, pictureBuffer, picSize, jpgSize);
        if (!ret) {
            DebugLog.error(TAG, "getPictureOnJPEG():: mPlayerHandler.getJPEG() return false");
            return null;
        }

        int jpegSize = jpgSize.value;
        if (jpegSize <= 0) {
            pictureBuffer = null;
            return null;
        }

        ByteBuffer jpgBuffer = ByteBuffer.wrap(pictureBuffer, 0, jpegSize);
        if (null == jpgBuffer) {
            pictureBuffer = null;
            return null;
        }

        return jpgBuffer.array();
    }

    /**
     * ����ͼƬ�ļ�
     *
     * @param path     ͼƬ·��
     * @param fileName ͼƬ����
     * @return true - ͼƬ�����ɹ� or false - ͼƬ����ʧ��
     * @since V1.0
     */
    private boolean createPictureFile(String path, String fileName) {
        if (null == path || null == fileName || path.equals("") || fileName.equals("")) {
            return false;
        }

        String dirPath = createFileDir(path);
        if (null == dirPath || dirPath.equals("")) {
            return false;
        }

        try {
            mPictureFile = new File(dirPath + File.separator + fileName);
            if ((null != mPictureFile) && (!mPictureFile.exists())) {
                mPictureFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mPictureFile = null;
            return false;
        }
        return true;
    }

    /**
     * ץ��ͼƬд��SDCard
     *
     * @param picData ͼƬ����
     * @param length  ͼƬ���ݳ���
     * @since V1.0
     */
    private boolean writePictureToFile(byte[] picData, int length) {
        if (null == picData || length <= 0 || picData.length > length) {
            return false;
        }

        if (null == mPictureFile) {
            return false;
        }

        FileOutputStream fOut = null;
        try {
            if (!mPictureFile.exists()) {
                mPictureFile.createNewFile();
            }
            fOut = new FileOutputStream(mPictureFile);
            fOut.write(picData, 0, length);
            fOut.flush();
            fOut.close();
            fOut = null;
        } catch (Exception e) {
            e.printStackTrace();
            fOut = null;
            mPictureFile.delete();
            mPictureFile = null;
            return false;
        }
        return true;
    }

    /**
     * ɾ��ͼƬ�ļ�
     *
     * @since V1.0
     */
    private void removePictureFile() {
        try {
            if (null == mPictureFile) {
                return;
            }
            mPictureFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mPictureFile = null;
        }
    }

    /**
     * �����ļ���
     *
     * @param path �ļ�·��
     * @return �ļ���·��
     * @since V1.0
     */
    private String createFileDir(String path) {
        if (null == path || path.equals("")) {
            return "";
        }
        File tempFile = null;
        try {
            tempFile = new File(path);
            if ((null != tempFile) && (!tempFile.exists())) {
                tempFile.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
            tempFile = null;
            return "";
        }
        return tempFile.getAbsolutePath();
    }

    /**
     * ֹͣ¼�� void
     *
     * @since V1.0
     */
    public void stopRecord() {
        if (!mIsRecord) {
            return;
        }

        mIsRecord = false;

        stopWriteStreamData();
    }

    /**
     * ֹͣд��������
     *
     * @since V1.0
     */
    private void stopWriteStreamData() {
        if (null == mRecordFileOutputStream) {
            return;
        }

        try {
            mRecordFileOutputStream.flush();
            mRecordFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mRecordFileOutputStream = null;
            mRecordFile = null;
        }
    }

    /**
     * ����¼�񷽷�
     *
     * @param filePath     ¼���ļ�·��
     * @param fileName     ¼���ļ�����
     * @param isRpmPackage �Ƿ�����ת��װ
     * @return true-����¼��ɹ���false-����¼��ʧ��
     * @since V1.0
     */
    public boolean startRecord(String filePath, String fileName) {

        if (!UtilSDCard.isSDCardUsable()) {
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLive.SD_CARD_UN_USEABLE);
            }
            return false;
        }

        if (UtilSDCard.getSDCardRemainSize() <= mSDCardSize) {
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLive.SD_CARD_SIZE_NOT_ENOUGH);
            }
            return false;
        }

        if (LIVE_PLAY != mLiveState) {
            DebugLog.error(TAG, "�ǲ���״̬����¼��");
            mLiveCallBack.onMessageCallback(ConstantLive.RECORD_FAILED_NPLAY_STATE);
            return false;
        }

        boolean ret = createRecordFile(filePath, fileName);
        if (!ret) {
            DebugLog.error(TAG, "createRecordFile() fail ����¼���ļ�ʧ��");
            return false;
        }

        ret = writeStreamHead(mRecordFile);
        if (!ret) {
            DebugLog.error(TAG, "writeStreamHead() д�ļ�ʧ��");
            removeRecordFile();
            return false;
        }

        mIsRecord = true;
        DebugLog.error(TAG, "����¼��ɹ�");
        return true;
    }

    /**
     * ����¼���ļ�
     *
     * @param path     �ļ�·��
     * @param fileName �ļ���
     * @return true - �����ɹ� or false - ����ʧ��
     * @since V1.0
     */
    private boolean createRecordFile(String path, String fileName) {
        if (null == path || path.equals("") || null == fileName || fileName.equals("")) {
            return false;
        }

        try {
            mRecordFile = new File(path + File.separator + fileName);
            if ((null != mRecordFile) && (!mRecordFile.exists())) {
                mRecordFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();

            mRecordFile = null;
            return false;
        }

        return true;
    }

    /**
     * д��ͷ�ļ�
     *
     * @param file д����ļ�
     * @return true - д��ͷ�ļ��ɹ�. false - д��ͷ�ļ�ʧ��.
     * @since V1.0
     */
    private boolean writeStreamHead(File file) {
        if (null == file || null == mStreamHeadDataBuffer) {
            Log.e("AAA", "mStreamHeadDataBuffer is null!");
            return false;
        }

        byte[] tempByte = mStreamHeadDataBuffer.array();
        if (null == tempByte) {
            return false;
        }
        try {
            if (null == mRecordFileOutputStream) {
                mRecordFileOutputStream = new FileOutputStream(file);
            }
            mRecordFileOutputStream.write(tempByte, 0, tempByte.length);
        } catch (Exception e) {
            e.printStackTrace();
            if (mRecordFileOutputStream != null) {
                try {
                    mRecordFileOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            mRecordFileOutputStream = null;
            mStreamHeadDataBuffer = null;
            tempByte = null;
            return false;
        }

        return true;
    }

    /**
     * ɾ��¼���ļ�
     *
     * @since V1.0
     */
    private void removeRecordFile() {
        try {
            if (null == mRecordFile) {
                return;
            }
            mRecordFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRecordFile = null;
        }
    }

    /**
     * ������Ƶ
     *
     * @return boolean
     * @since V1.0
     */
    public boolean startAudio() {
        if (LIVE_PLAY != mLiveState) {
            DebugLog.error(TAG, "�ǲ���״̬���ܿ�����Ƶ");
            mLiveCallBack.onMessageCallback(ConstantLive.AUDIO_START_FAILED_NPLAY_STATE);
            return false;
        }

        if (null == mPlayerHandler) {
            return false;
        }

        boolean ret = mPlayerHandler.playSound(mPlayerPort);
        if (!ret) {
            return false;
        }
        return true;
    }

    /**
     * �ر���Ƶ
     *
     * @return boolean
     * @since V1.0
     */
    public boolean stopAudio() {
        if (LIVE_PLAY != mLiveState) {
            DebugLog.error(TAG, "�ǲ���״̬���ܹر���Ƶ");
            return false;
        }

        if (null == mPlayerHandler) {
            return false;
        }

        boolean ret = mPlayerHandler.stopSound();
        if (!ret) {
            return false;
        }
        return true;
    }

    /**
     * ��ȡJPEGͼƬ��С
     *
     * @return JPEGͼƬ�Ĵ�С.
     * @throws PlayerException
     * @throws MediaPlayerException MediaPlayer �쳣
     * @since V1.0
     */
    private int getPictureSize() {
        Player.MPInteger width = new Player.MPInteger();
        Player.MPInteger height = new Player.MPInteger();
        boolean ret = mPlayerHandler.getPictureSize(mPlayerPort, width, height);
        if (!ret) {
            DebugLog.error(TAG, "getPictureSize():: mPlayerHandler.getPictureSize() return false��errorCode is P"
                    + mPlayerHandler.getLastError(mPlayerPort));
            return 0;
        }
        int pictureSize = width.value * height.value * 3;
        return pictureSize;
    }

    /**
     * �򲥷ſ�������
     *
     * @param data
     * @param len  void
     * @since V1.0
     */
    private void processStreamData(byte[] data, int len) {
        if (null == data || 0 == len) {
            DebugLog.error(TAG, "processStreamData() Stream data is null or len is 0");
            return;
        }
        if (null != mPlayerHandler) {
            boolean ret = mPlayerHandler.inputData(mPlayerPort, data, len);
            if (!ret) {
                SystemClock.sleep(10);
            }
        }
    }

    /*
     * handle - - ����id opt - -�ص���Ϣ��������RTSPCLIENT_MSG_PLAYBACK_FINISH,RTSPCLIENT_MSG_BUFFER_OVERFLOW
     * ,RTSPCLIENT_MSG_CONNECTION_EXCEPTION ���� param1 - - �������� param2 - - �������� useId - - �û����ݣ�Ĭ�Ͼ�������id��handle��ͬ
     */

    @Override
    public void onMessageCallBack(int handle, int opt, int param1, int param2, int useId) {

        if (opt == RtspClient.RTSPCLIENT_MSG_CONNECTION_EXCEPTION) {
            stop();
            DebugLog.error(TAG, "onMessageCallBack():: rtsp connection exception");
            if (connectNum > 3) {
                DebugLog.error(TAG, "onMessageCallBack():: rtsp connection more than three times");
                connectNum = 0;
            } else {
                startLive(mSurfaceView);
                connectNum++;
            }
        }
    }

    /**
     * �����Ѿ����ŵ����� void
     *
     * @return long
     * @since V1.0
     */
    public long getStreamRate() {
        return mStreamRate;
    }

    /**
     * �������ͳ�� void
     *
     * @since V1.0
     */
    public void clearStreamRate() {
        mStreamRate = 0;
    }

    /**
     * ��ȡOSDʱ��
     *
     * @return Calendar
     * @since V1.0
     */
    public Calendar getOSDTime() {
        Calendar systemTime = Calendar.getInstance();
        if (null == mPlayerHandler) {
            DebugLog.error(TAG, "getOSDTime(): mPlayerHandler is null!");
            return null;
        }

        if (-1 == mPlayerPort) {
            DebugLog.error(TAG, "getOSDTime(): mPlayerPort is -1");
            return null;
        }

        MPSystemTime time = new MPSystemTime();
        boolean ret = mPlayerHandler.getSystemTime(mPlayerPort, time);
        if (!ret) {
            DebugLog.error(TAG,
                    "getOSDTime(): getSystemTime() fail errorCode is " + mPlayerHandler.getLastError(mPlayerPort));
            mLiveCallBack.onMessageCallback(ConstantLive.GET_OSD_TIME_FAIL);
            return systemTime;
        }

        systemTime.set(Calendar.YEAR, time.year);
        systemTime.set(Calendar.MONTH, time.month - 1);
        systemTime.set(Calendar.DAY_OF_MONTH, time.day);
        systemTime.set(Calendar.HOUR_OF_DAY, time.hour);
        systemTime.set(Calendar.MINUTE, time.min);
        systemTime.set(Calendar.SECOND, time.sec);
        systemTime.set(Calendar.MILLISECOND, time.ms);

        return systemTime;
    }

    @Override
    public void onDisplay(int arg0, byte[] arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) {
        if (LIVE_PLAY != mLiveState) {
            mLiveState = LIVE_PLAY;
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLive.PLAY_DISPLAY_SUCCESS);
            } else {
                DebugLog.error(TAG, "onDisplay():: mLiveCallBack is null");
            }

        }
    }

}
