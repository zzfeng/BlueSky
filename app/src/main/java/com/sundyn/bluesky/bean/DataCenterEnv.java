package com.sundyn.bluesky.bean;

import java.util.List;


/**
 * �������Ļ�����������ʵ����
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
