package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 * @ʱ�� 2016��10��14������2:36:04
 * @�汾 1.0
 * @���� ��Ϣ����
 */
public class JpushMessage extends BaseBean {
    public ArrayList<MsgItem> data;

    public static class MsgItem {
        public int areaId;
        public String content;
        public int id;
        public String role_key;
        public boolean state;
        public String title;
        public String type;
        public String userNo;
        public String dateString;
    }
}
