package com.sundyn.bluesky.bean;


/**
 * @author yangjl
 * @ʱ�� 2016-9-2����1:54:58
 * @�汾 1.0
 * @���� ����ͼ�ٱ���
 */
public class AreaReport extends BaseBean {
    public AreaReportItem data;

    public static class AreaReportItem {
        public String territorial;
        public String tscount;
        public String yxtscount;
    }
}
