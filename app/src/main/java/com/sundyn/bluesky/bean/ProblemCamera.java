package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * ��ȡ������ͷ�����б�
 *
 * @author 08
 */
public class ProblemCamera {
    public int currentPage;
    public int firstResult;
    public int maxResults;
    public int records;
    public int total;
    public ArrayList<ProCamera> rows;

    public static class ProCamera {
        public String cameraCode;
        public String cameraName;
        public int id;
        public String siteName;
        public String siteNo;
    }

}
