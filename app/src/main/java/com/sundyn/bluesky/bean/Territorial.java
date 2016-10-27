package com.sundyn.bluesky.bean;

import com.sundyn.bluesky.view.pickerview.model.IPickerViewData;

import java.util.ArrayList;

/**
 * @author yangjl
 * @ʱ�� 2016-8-12����11:47:32
 * @�汾 1.0
 * @���� ����߰�֪ͨ����ѡ��
 */
public class Territorial extends BaseBean {
    public ArrayList<TerritorialItem> data;

    public static class TerritorialItem implements IPickerViewData {
        public String argCode;
        public String argName;

        @Override
        public String getPickerViewText() {
            return argName;
        }
    }

}
