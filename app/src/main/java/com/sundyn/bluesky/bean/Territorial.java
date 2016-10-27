package com.sundyn.bluesky.bean;

import com.sundyn.bluesky.view.pickerview.model.IPickerViewData;

import java.util.ArrayList;

/**
 * @author yangjl
 * @时间 2016-8-12上午11:47:32
 * @版本 1.0
 * @描述 督办催办通知区域选择
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
