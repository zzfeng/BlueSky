package com.sundyn.bluesky.bean;

import java.util.ArrayList;

public class Area extends BaseBean {
    public ArrayList<AreaItem> data;

    public static class AreaItem {
        public int key;
        public String value;
    }
}
