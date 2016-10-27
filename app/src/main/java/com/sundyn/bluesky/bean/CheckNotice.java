package com.sundyn.bluesky.bean;

import java.util.List;

/**
 * @author yangjl
 * @时间 2016-8-23下午1:55:27
 * @版本 1.0
 * @描述 日常巡查列表
 */
public class CheckNotice extends BaseBean {
    public List<Notices> data;

    public static class Notices {
        public String CheckDate;
        public String CreateBy;
        public String CreateDate;
        public String Descrip;
        public int ID;
        public String PrjAddress;
        public int PrjID;
        public String PrjName;
        public String Territorial;
        public String UserName;
    }

}
