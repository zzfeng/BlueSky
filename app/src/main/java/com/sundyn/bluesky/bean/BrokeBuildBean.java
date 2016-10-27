package com.sundyn.bluesky.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author yangjl ÃèÊö£ºÎ¥ÕÂ¹¤µØ
 */
public class BrokeBuildBean extends BaseBean implements Serializable {
    private static final long serialVersionUID = -758459502806858414L;
    public ArrayList<Build> data;

    public static class Build implements Serializable {
        public int count;
        public String latitude;
        public String longitude;
        public String siteName;
        public String siteNo;
    }
}
