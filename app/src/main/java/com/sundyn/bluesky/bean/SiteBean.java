package com.sundyn.bluesky.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author yangjl
 * @时间 2016年10月20日下午4:40:56
 * @版本 1.0
 * @描述 视频中心列表和工地维护列表(多pmIsTbj)
 */
public class SiteBean extends BaseBean {
    public ArrayList<Site> data;

    public static class Site implements Serializable {
        private static final long serialVersionUID = -1994113462479400500L;
        public String bsiteLoc;
        public String bsiteMgr;
        public String bsiteName;
        public String bsiteNo;// 工地编号
        public String bsiteProject;
        public int bsiteStruc;
        public int bsiteType;
        public String buildUnit;
        public int id;
        public int isSupervise;
        public int isTbj;
        public double latitude;
        public String linkphone;
        public double longitude;
        public int siteJudge;
        public String xzqhMc;
        public boolean pmIsTbj;

    }

}
