package com.sundyn.bluesky.bean;

import java.util.List;

/**
 * @author yangjl
 * @ʱ�� 2016-8-17����5:46:56
 * @�汾 1.0
 * @���� ����֪ͨ����
 */
public class ReformDetail extends BaseBean {
    public List<ImageInfo> imginfo;
    public List<ItemInfo> iteminfo;
    public ZgtzInfo zgtzinfo;

    public static class ImageInfo {
        public String imgurl;
    }

    public static class ItemInfo {
        public String Item;
    }

    public static class ZgtzInfo {
        public String NoticeNo;
        public String GovernmentName;
        public String PrjName;
        public String PrjAddress;
        public String CheckTeam;
        public String isBack;
        public String ReContent;
        public String StatusName;
        public String PublishDate;
        public String RectificationDate;
        public int ApproveStatus;
        public String itype;
        public String IsReturn;
        public String ReturnResult;
    }
}
