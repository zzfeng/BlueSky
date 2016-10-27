package com.sundyn.bluesky.hik.util;


import java.io.File;
import java.io.IOException;
/**
 * ��ȡץ�ġ�¼��·����
 * 
 * @author huangweifeng
 * @Data 2013-10-23
 */
public class UtilFilePath {

    /**
     * ��ȡͼƬĿ¼
     * 
     * @return Pictrue dir path.
     * @since V1.0
     */
    public static File getPictureDirPath() {
        File SDFile = null;
        File mIVMSFolder = null;
        try {
            SDFile = android.os.Environment.getExternalStorageDirectory();
            String path = SDFile.getAbsolutePath() + File.separator + "BlueSky";
            mIVMSFolder = new File(path);
            if ((null != mIVMSFolder) && (!mIVMSFolder.exists())) {
                mIVMSFolder.mkdir();
                mIVMSFolder.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIVMSFolder;
    }

    /**
     * ��ȡ¼��Ŀ¼
     * 
     * @return Video dir path.
     * @since V1.0
     */
    public static File getVideoDirPath() {
        File SDFile = null;
        File mIVMSFolder = null;
        try {
            SDFile = android.os.Environment.getExternalStorageDirectory();
            mIVMSFolder = new File(SDFile.getAbsolutePath() + File.separator + "BlueSky");
            if ((null != mIVMSFolder) && (!mIVMSFolder.exists())) {
                mIVMSFolder.mkdir();
                mIVMSFolder.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mIVMSFolder;
    }
}
