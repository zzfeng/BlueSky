package com.sundyn.bluesky.bean;

import com.sundyn.bluesky.view.pickerview.model.IPickerViewData;

import java.util.ArrayList;

/**
 * @author yangjl
 * @时间 2016-8-12上午11:47:32
 * @版本 1.0
 * @描述 获取巡检人员
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
