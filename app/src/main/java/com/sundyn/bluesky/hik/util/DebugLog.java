package com.sundyn.bluesky.hik.util;


import android.util.Log;

/**
 * ��ӡ��־��
 * @author huangweifeng
 * @Data 2013-10-15
 */
public class DebugLog {
  
        /**
          * �����Ƿ��ӡ��־
          * @param isPrintLog
          * void
          * @since V1.0
          */
        public static void setLogOption(boolean isPrintLog) {
            DEBUG = isPrintLog;
        }
        
        private static  boolean DEBUG = true;
        
        private DebugLog() {
        }
        
        /**
          * ����Է���������
          * @param tag
          * @param desc
          * void
          * @since V1.0
          */
        public static void debug(String tag, String desc) {
            if (DEBUG) Log.d(tag, desc);
        }

        /**
          * ����Է���������
          * @param tag
          * @param desc
          * void
          * @since V1.0
          */
        public static void verbose(String tag, String desc) {
            if (DEBUG) Log.v(tag, desc);
        }

        /**
          * ����Է���������
          * @param tag
          * @param desc
          * void
          * @since V1.0
          */
        public static void warn(String tag, String desc) {
            if (DEBUG) Log.w(tag, desc);
        }

        /**
          * ����Է���������
          * @param tag
          * @param desc
          * void
          * @since V1.0
          */
        public static void info(String tag, String desc) {
            if (DEBUG) Log.i(tag, desc);
        }

        /**
          * ����Է���������
          * @param tag
          * @param desc
          * void
          * @since V1.0
          */
        public static void error(String tag, String desc) {
            if (DEBUG) Log.e(tag, desc);
        }
    
}
