package com.sundyn.bluesky.bean;

import java.util.List;

public class PlusPrecent {
    public int pm10_total;
    public int pm25_total;
    public List<PlusPrecentItem> pm10_precent;
    public List<PlusPrecentItem> pm25_precent;

    public static class PlusPrecentItem {
        public int count;
        public GeoData geo;
    }

    public static class GeoData {
        public int id;
        public double latitude;
        public double longitude;
        public String name;
        public int parentId;
        public int type;
        public String typeStrings;
    }

}
