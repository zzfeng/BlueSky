package com.sundyn.bluesky.hik;


import com.hikvision.vmsnetsdk.CameraInfo;
import com.hikvision.vmsnetsdk.CameraInfoEx;
import com.hikvision.vmsnetsdk.ServInfo;

public final class TempData {
    private static TempData ins = new TempData();

    public static TempData getIns() {
        return ins;
    }

    /**
     * ��¼���ص�����
     */
    private ServInfo loginData;

    /**
     * ��ص���Ϣ��������ʱ����������
     */
    private CameraInfo cameraInfo;

    private CameraInfoEx cameraInfoEx;

    public static TempData getInstance() {
        return ins;
    }

    /**
     * ���õ�¼�ɹ����ص���Ϣ
     *
     * @param loginData
     * @since V1.0
     */
    public void setLoginData(ServInfo loginData) {
        this.loginData = loginData;
    }

    /**
     * ��ȡ��¼�ɹ����ص���Ϣ
     *
     * @return
     * @since V1.0
     */
    public ServInfo getLoginData() {
        return loginData;
    }

    /**
     * �����ص���Ϣ
     *
     * @param cameraInfo
     * @since V1.0
     */
    public void setCameraInfo(CameraInfo cameraInfo) {
        this.cameraInfo = cameraInfo;
    }

    /**
     * ��ȡ��ص���Ϣ
     *
     * @return
     * @since V1.0
     */
    public CameraInfo getCameraInfo() {
        return cameraInfo;
    }


    public void setCameraInfoEx(CameraInfoEx cameraInfoEx) {
        this.cameraInfoEx = cameraInfoEx;
    }

    public CameraInfoEx getCameraInfoEx() {
        return cameraInfoEx;
    }

}
