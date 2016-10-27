package com.sundyn.bluesky.bean;

import java.util.List;

/**
 * 折线图数据 ,分为区域折线图和工地折线图 ，如果是工地则多个"siteNo": "175",字段，没有areaId;areaName;
 *
 * @author Administrator
 */
public class ChartBean extends BaseBean {

    public List<ChartItemData> data;

    public static class ChartItemData {
        public String areaId;
        public String areaName;
        public double humidity;
        public String id;
        public String obj_id;
        public double pm10;
        public double tempera;
        public String time;
        public double windDict;
        public double windPower;
    }
}
