package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 * @ʱ�� 2016-8-17����5:46:56
 * @�汾 1.0
 * @���� Ͷ�߻�����Ŀ�б�
 */
public class CompaintCount extends BaseBean {
    public ArrayList<CompaintCountItem> data;

    public static class CompaintCountItem {
        public int ID;
        public String PrjName;
        public String ComplaintDate;
        public String ComplaintMan;
        public String ComplaintType;
        public String ComplaintStatus;
        public String argName;
    }
}
