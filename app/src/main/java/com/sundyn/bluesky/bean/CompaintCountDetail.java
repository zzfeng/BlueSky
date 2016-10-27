package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 * @ʱ�� 2016-8-17����5:46:56
 * @�汾 1.0
 * @���� Ͷ�߻�����Ŀ�б�
 */
public class CompaintCountDetail extends BaseBean {
    public ArrayList<CompaintDetailItem> zgtzinfo;

    public static class CompaintDetailItem {
        public int ID;
        public String PrjID;
        public String PrjName;
        public String PrjAddress;
        public String Contents;
        public String ComplaintMan;
        public String Mobile;
        public String ComplaintType;
        public String ComplaintDate;
        public String ComplaintStatus;
        public String RectificationDate;
        public int fkNoticeID;
        public int fkZgtzID;
        public String CreateDate;
        public String CardNo;
        public String Territorial;
        public int iCount;
        public String CreateBy;
        public String DeptID;
        public String zgRectificationDate;
        public String Reason;
        public String Office;
        public String argName;
    }
}
