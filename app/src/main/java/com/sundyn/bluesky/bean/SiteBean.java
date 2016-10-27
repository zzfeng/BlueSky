package com.sundyn.bluesky.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author yangjl
 * @ʱ�� 2016��10��20������4:40:56
 * @�汾 1.0
 * @���� ��Ƶ�����б�͹���ά���б�(��pmIsTbj)
 */
public class SiteBean extends BaseBean {
    public ArrayList<Site> data;

    public static class Site implements Serializable {
        private static final long serialVersionUID = -1994113462479400500L;
        public String bsiteLoc;
        public String bsiteMgr;
        public String bsiteName;
        public String bsiteNo;// ���ر��
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
