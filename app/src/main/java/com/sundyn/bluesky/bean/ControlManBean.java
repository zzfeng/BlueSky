package com.sundyn.bluesky.bean;

import com.sundyn.bluesky.view.pickerview.model.IPickerViewData;

import java.util.ArrayList;

/**
 * @author yangjl
 * @ʱ�� 2016-8-12����11:47:32
 * @�汾 1.0
 * @���� ��ȡѲ����Ա
 */
public class ControlManBean extends BaseBean {
    public ArrayList<ControlMan> data;

    public static class ControlMan implements IPickerViewData {
        public String UserName;
        public String id;

        @Override
        public String getPickerViewText() {
            return UserName;
        }
    }

}
