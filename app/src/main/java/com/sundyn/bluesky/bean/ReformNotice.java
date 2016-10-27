package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 * @时间 2016-8-17下午5:46:56
 * @版本 1.0
 * @描述 整改通知item
 */
public class ReformNotice extends BaseBean {
    public ArrayList<Notice> data;

    public static class Notice {
        public String CheckTeam;
        public String CreateDate;
        public int ID;
        public String GovernmentName;
        public String NoticeNo;
        public String PrjName;
        public String isBack;
        public String StatusName;
    }
}
