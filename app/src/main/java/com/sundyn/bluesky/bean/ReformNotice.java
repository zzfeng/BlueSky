package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 * @ʱ�� 2016-8-17����5:46:56
 * @�汾 1.0
 * @���� ����֪ͨitem
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
