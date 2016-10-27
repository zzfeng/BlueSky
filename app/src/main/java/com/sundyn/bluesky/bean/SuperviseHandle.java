package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 * @时间 2016-8-19上午9:43:13
 * @版本 1.0
 * @描述 督办催办bean
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
