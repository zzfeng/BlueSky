package com.sundyn.bluesky.bean;

import java.util.List;

/**
 * @author yangjl
 * @ʱ�� 2016-8-17����5:46:56
 * @�汾 1.0
 * @���� ��������
 */
public class SuperviseDetail extends BaseBean {
    public List<ImageInfo> imginfo;
    public ZgtzInfo zgtzinfo;

    public static class ImageInfo {
        public String imgurl;
    }

    public static class ZgtzInfo {
        public String NoticeNo;
        public String Territorial;
        public String PrjName;
        public String PrjAddress;
        public String Item;
        public String isBack;
        public String ReContent;
        public String TContent;
        public String StatusName;
        public String PublishDate;
        public String RectificationDate;
        public String UserName;
        public String ReturnResult;
        public boolean IsReturn;
        public int ApproveStatus;
    }
}
