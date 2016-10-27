package com.sundyn.bluesky.bean;

import java.util.List;

public class SiteLineChartBean {
    public String site;
    public String siteName;
    public String type;
    public List<SiteLineData> data;

    public static class SiteLineData {
        public String siteNo;
        public String time;
        public float value;
    }
}
