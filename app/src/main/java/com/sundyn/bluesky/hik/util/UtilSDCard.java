package com.sundyn.bluesky.hik.util;


import java.io.File;

import android.os.Environment;
import android.os.StatFs;

/**
 * SDCard�����
 * @author huangweifeng
 * @Data 2013-10-23
 */
public class UtilSDCard {

    /**
     * ��ȡSDCard ·��
     * 
     * @return SDCard ·��
     * @since V1.0
     */
    public static File getSDCardPath() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * ��ȡSDCardʣ�µĴ�С
     * 
     * @return SDCardʣ�µĴ�С
     * @since V1.0
     */
    public static long getSDCardRemainSize() {
        StatFs statfs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize = statfs.getBlockSize();
        long availableBlocks = statfs.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * ��ȡSDCard��״̬
     * 
     * @return SDCard ���õ�״̬
     */
    public static boolean isSDCardUsable() {
        boolean SDCardMounted = false;
        String sDStateString = Environment.getExternalStorageState();
        if (sDStateString.equals(Environment.MEDIA_MOUNTED)) {
            SDCardMounted = true;
        }

        // �Ƿ����ڼ��SD��
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_CHECKING)
                || Environment.getExternalStorageState().equals(Environment.MEDIA_NOFS)) {
            SDCardMounted = false;
        }

        // ����Ƿ����SD��
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)
                || Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
            SDCardMounted = false;
        }

        // ���SD���Ƿ����ӵ��Թ���
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_SHARED)) {
            SDCardMounted = false;
        }

        return SDCardMounted;
    }
}