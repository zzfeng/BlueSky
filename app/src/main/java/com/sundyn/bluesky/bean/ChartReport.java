package com.sundyn.bluesky.bean;

import java.util.List;

/**
 * @author yangjl
 * @ʱ�� 2016-9-1����4:32:22
 * @�汾 1.0
 * @���� �ٱ�������ͼ
 */
public class ChartReport extends BaseBean {
    public List<ReportItem> data;

    public static class ReportItem {
        public String Cnt;
        public String Territorial;
        public String CreateDate;
    }

}
