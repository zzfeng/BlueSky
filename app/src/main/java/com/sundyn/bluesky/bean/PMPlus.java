package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * 数据中心中PM10超标的工地
 *
 * @author Administrator
 */
public class PMPlus extends BaseBean {
    public ArrayList<PMItem> data;

    public static class PMItem {
        public String TIME;
        public String areaId;
        public String areaName;
        public String latitude;
        public String longitude;
        public String siteName;
        public String siteNo;
        public double pm10;

    }

}
