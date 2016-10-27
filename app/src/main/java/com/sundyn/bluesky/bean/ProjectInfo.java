package com.sundyn.bluesky.bean;

import java.util.ArrayList;

public class ProjectInfo extends BaseBean {
    public ArrayList<ProjectItem> data;

    public static class ProjectItem {
        public int ID;
        public String PrjName;
    }
}
