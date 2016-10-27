package com.sundyn.bluesky.bean;

import java.util.List;


/**
 * 数据中心基本环境数据实体类
 *
 * @author Administrator
 */
public class DataCenterEnv extends BaseBean {
    public List<Env> data;

    public static class Env {
        public int areaId;
        public int cityId;
        public double humidity;
        public double noise;
        public double pm10;
        public double pm25;
        public String siteNo;
        public double tempera;
        public double windDict;
        public double windPower;
    }
}
