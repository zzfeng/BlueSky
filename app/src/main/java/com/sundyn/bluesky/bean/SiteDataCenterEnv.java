package com.sundyn.bluesky.bean;

import java.util.List;

/**
 * �����������Ļ�����������ʵ����
 *
 * @author Administrator
 */
public class SiteDataCenterEnv extends BaseBean {
    public List<SiteEnv> data;

    public static class SiteEnv {
        public String areaId;
        public String areaName;
        public float humidity;
        public String id;
        public String latitude;
        public String longitude;
        public float pm10;
        public String siteName;
        public String siteNo;
        public float tempera;
        public String time;
        public String windDict;
        public String windPower;
    }

}
