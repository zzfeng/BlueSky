package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 * @时间 2016-8-26上午10:09:31
 * @版本 1.0
 * @描述 老扬尘办的区域(不带郑州)
 */
public class AreaOfOld extends BaseBean {
    public ArrayList<Region> data;

    public static class Region {
        public String argCode;
        public String argName;
    }
}
