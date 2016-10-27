package com.sundyn.bluesky.bean;

import com.sundyn.bluesky.view.pickerview.model.IPickerViewData;

import java.util.ArrayList;

/**
 * @author yangjl
 * @时间 2016-8-12上午11:47:32
 * @版本 1.0
 * @描述 获取办事处的实体类
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
