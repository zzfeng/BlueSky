package com.sundyn.bluesky.bean;

import java.util.List;

public class PlusDay {
    public int allDays;
    public int plus_pm10;
    public int plus_pm25;
    public List<PlusItem> plus_pm10_time;
    public List<PlusItem> plus_pm25_time;

    public static class PlusItem {
        public int count;
        public int day;
        public int month;
    }

}
