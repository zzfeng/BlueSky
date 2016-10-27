package com.sundyn.bluesky.bean;


/**
 * @author yangjl
 * @时间 2016-9-2下午1:54:58
 * @版本 1.0
 * @描述 环形图举报数
 */
public class AreaReport extends BaseBean {
    public AreaReportItem data;

    public static class AreaReportItem {
        public String territorial;
        public String tscount;
        public String yxtscount;
    }
}
