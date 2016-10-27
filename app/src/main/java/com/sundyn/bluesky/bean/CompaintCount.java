package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 * @时间 2016-8-17下午5:46:56
 * @版本 1.0
 * @描述 投诉汇总项目列表
 */
public class CompaintCount extends BaseBean {
    public ArrayList<CompaintCountItem> data;

    public static class CompaintCountItem {
        public int ID;
        public String PrjName;
        public String ComplaintDate;
        public String ComplaintMan;
        public String ComplaintType;
        public String ComplaintStatus;
        public String argName;
    }
}
