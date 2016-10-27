package com.sundyn.bluesky.bean;

import java.util.List;

public class ProjectChart extends BaseBean {
    public List<BarChartItem> data;

    public static class BarChartItem {
        public int PrjCount;
        public String argName;
        public float sArea;
        public float sMoney;
    }
}
