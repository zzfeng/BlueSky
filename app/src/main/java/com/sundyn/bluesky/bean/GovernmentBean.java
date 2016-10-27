package com.sundyn.bluesky.bean;

import com.sundyn.bluesky.view.pickerview.model.IPickerViewData;

import java.util.ArrayList;

/**
 * @author yangjl
 * @时间 2016-8-12上午11:47:32
 * @版本 1.0
 * @描述 获取区政府的实体类
 */
public class GovernmentBean extends BaseBean {
    public ArrayList<Government> data;

    public static class Government implements IPickerViewData {
        public String Territorial;
        public String UserName;
        public String UserNo;

        @Override
        public String getPickerViewText() {
            return UserName;
        }
    }

}
