package com.sundyn.bluesky.bean;

import java.util.List;

/**
 * Ȩ��ģ��
 *
 * @author 08
 */
public class AuthModle extends BaseBean {
    public List<ModleItem> data;

    public static class ModleItem {
        public int id;
        public String model_name;
    }

}
