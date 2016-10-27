package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 * @时间 2016年10月14日下午2:36:04
 * @版本 1.0
 * @描述 消息界面
 */
public class JpushMessage extends BaseBean {
    public ArrayList<MsgItem> data;

    public static class MsgItem {
        public int areaId;
        public String content;
        public int id;
        public String role_key;
        public boolean state;
        public String title;
        public String type;
        public String userNo;
        public String dateString;
    }
}
