package com.sundyn.bluesky.bean;

import java.util.List;

/**
 * @author yangjl
 * @时间 2016-9-1下午4:32:22
 * @版本 1.0
 * @描述 举报数折线图
 */
public class ChartReport extends BaseBean {
    public List<ReportItem> data;

    public static class ReportItem {
        public String Cnt;
        public String Territorial;
        public String CreateDate;
    }

}
