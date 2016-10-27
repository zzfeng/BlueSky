package com.sundyn.bluesky.bean;

import java.util.List;

/**
 * 我的工地接口中json解析实体类
 *
 * @author Administrator
 */
public class MySiteBean extends BaseBean {
    public List<MySite> data;

    public static class MySite {
        public String bsiteNo;
        public String bsiteName;
        public int siteJudge;
        public String bsiteLoc;
    }

}
