package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 * @时间 2016年10月13日下午4:27:58
 * @版本 1.0
 * @描述 首页工地状态
 */
public class MySiteState extends BaseBean {
    public String result;
    public ArrayList<SiteData> datas;

    public static class SiteData {
        public String pm;
        public String time;
    }
}
