package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 * @ʱ�� 2016-8-19����9:43:13
 * @�汾 1.0
 * @���� ����߰�bean
 */
public class SuperviseHandle extends BaseBean {
    public ArrayList<Supervise> data;

    public static class Supervise {
        public int ID;
        public String NoticeNo;
        public String PrjAddress;
        public String PrjName;
        public String PublishDate;
        public String RectificationDate;
        public String StatusName;
        public String Territorial;
        public String isBack;
    }
}
