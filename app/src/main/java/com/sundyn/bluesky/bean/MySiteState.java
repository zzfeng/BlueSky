package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 * @ʱ�� 2016��10��13������4:27:58
 * @�汾 1.0
 * @���� ��ҳ����״̬
 */
public class MySiteState extends BaseBean {
    public String result;
    public ArrayList<SiteData> datas;

    public static class SiteData {
        public String pm;
        public String time;
    }
}
