package com.sundyn.bluesky.bean;

import java.util.List;

/**
 * ����ͼ���� ,��Ϊ��������ͼ�͹�������ͼ ������ǹ�������"siteNo": "175",�ֶΣ�û��areaId;areaName;
 *
 * @author Administrator
 */
public class ChartBean extends BaseBean {

    public List<ChartItemData> data;

    public static class ChartItemData {
        public String areaId;
        public String areaName;
        public double humidity;
        public String id;
        public String obj_id;
        public double pm10;
        public double tempera;
        public String time;
        public double windDict;
        public double windPower;
    }
}
