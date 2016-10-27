package com.sundyn.bluesky.bean;

import java.io.Serializable;
import java.util.List;

public class PMForMap extends BaseBean {
    public List<PMBean> data;

    public static class PMBean implements Serializable {
        private static final long serialVersionUID = -5678674277422229032L;
        public String areaId;
        public String areaName;
        public double humidity;
        public String id;
        public double latitude;
        public double longitude;
        public double pm10;
        public String siteName;
        public String siteNo;
        public double tempera;
        public String time;
        public String windDict;
        public String windPower;
    }
}
