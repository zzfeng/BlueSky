package com.sundyn.bluesky.bean;

import java.util.ArrayList;

public class CheckProblem extends BaseBean {
    public ArrayList<ProblemItem> data;

    public static class ProblemItem {

        public String Item;
        public String ID;
    }
}
