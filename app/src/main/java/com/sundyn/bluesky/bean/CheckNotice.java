package com.sundyn.bluesky.bean;

import java.util.List;

/**
 * @author yangjl
 * @ʱ�� 2016-8-23����1:55:27
 * @�汾 1.0
 * @���� �ճ�Ѳ���б�
 */
public class CheckNotice extends BaseBean {
    public List<Notices> data;

    public static class Notices {
        public String CheckDate;
        public String CreateBy;
        public String CreateDate;
        public String Descrip;
        public int ID;
        public String PrjAddress;
        public int PrjID;
        public String PrjName;
        public String Territorial;
        public String UserName;
    }

}
