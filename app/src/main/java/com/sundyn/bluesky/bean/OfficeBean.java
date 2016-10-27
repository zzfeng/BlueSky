package com.sundyn.bluesky.bean;

import com.sundyn.bluesky.view.pickerview.model.IPickerViewData;

import java.util.ArrayList;

/**
 * @author yangjl
 * @ʱ�� 2016-8-12����11:47:32
 * @�汾 1.0
 * @���� ��ȡ���´���ʵ����
 */
public class OfficeBean extends BaseBean {
    public ArrayList<Office> data;

    public static class Office implements IPickerViewData {
        public String UserName;
        public String UserNo;

        @Override
        public String getPickerViewText() {
            return UserName;
        }
    }

}
