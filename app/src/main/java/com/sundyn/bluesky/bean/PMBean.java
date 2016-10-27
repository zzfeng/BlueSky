package com.sundyn.bluesky.bean;

import java.io.Serializable;

/**
 * @author yangjl 描述：地图activity中获取pm10的实体类 2016-6-24下午3:25:40
 */
public class PMBean implements Serializable {
    public double humidity;
    public double latitude;
    public double longitude;
    public double pm10;
    public String siteName;
    public String siteNo;
    public double temperature;
    public String time;
    public double windDict;
    public String windPower;
}
