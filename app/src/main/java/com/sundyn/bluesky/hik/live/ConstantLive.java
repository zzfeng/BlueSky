package com.sundyn.bluesky.hik.live;


/**
 * Ԥ��ģ���õ��ĳ���
 *
 * @author huangweifeng
 * @Data 2013-10-21
 */
public class ConstantLive {

    private static final int ERR_BASE = 10000;
    /**
     * ȡ���ɹ�
     */
    public static final int RTSP_SUCCESS = ERR_BASE;
    /**
     * ��������ʧ��
     **/
    public static final int START_OPEN_FAILED = ERR_BASE + 1;
    /**
     * ���ųɹ�
     */
    public static final int PLAY_DISPLAY_SUCCESS = ERR_BASE + 2;
    /**
     * ֹͣ�ɹ�
     */
    public static final int STOP_SUCCESS = ERR_BASE + 3;
    /**
     * ���ſ���������
     */
    public static final int PLAYER_HANDLE_NULL = ERR_BASE + 4;
    /**
     * ���ſ�˿ڲ�����
     */
    public static final int PLAYER_PORT_UNAVAILABLE = ERR_BASE + 5;
    /**
     * RTSP����ʧ��
     */
    public static final int RTSP_FAIL = ERR_BASE + 6;
    /**
     * ��ȡOSDʱ��ʧ��
     */
    public static final int GET_OSD_TIME_FAIL = ERR_BASE + 7;
    /**
     * SD��������
     */
    public static final int SD_CARD_UN_USEABLE = ERR_BASE + 8;
    /**
     * SD���ռ䲻��
     */
    public static final int SD_CARD_SIZE_NOT_ENOUGH = ERR_BASE + 9;
    /**
     * �ǲ���״̬����ץ��
     */
    public static final int CAPTURE_FAILED_NPLAY_STATE = ERR_BASE + 10;
    /**
     * �ǲ���״̬����¼��
     */
    public static final int RECORD_FAILED_NPLAY_STATE = ERR_BASE + 11;
    /**
     * �ǲ���״̬���ܿ�����Ƶ
     */
    public static final int AUDIO_START_FAILED_NPLAY_STATE = ERR_BASE + 12;
    /**
     * �ǲ���״̬���ܹر���Ƶ
     */
    public static final int AUDIO_STOP_FAILED_NPLAY_STATE = ERR_BASE + 13;
    /**
     * ��MAGȡ����ǩ
     */
    public static final int MAG = 2;
    /**
     * ��������ǩ
     */
    public static final int MAIN_STREAM = 0;
    /**
     * ��������ǩ
     */
    public static final int SUB_STREAM = 1;

}
