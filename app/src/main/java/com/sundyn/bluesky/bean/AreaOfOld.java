package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 * @ʱ�� 2016-8-26����10:09:31
 * @�汾 1.0
 * @���� ���ﳾ�������(����֣��)
 */
public class AreaOfOld extends BaseBean {
    public ArrayList<Region> data;

    public static class Region {
        public String argCode;
        public String argName;
    }
}
