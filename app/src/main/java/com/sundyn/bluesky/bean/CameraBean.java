package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author 08
 *         根据工地ID获取摄像头列表实体类
 */
public class CameraBean extends BaseBean {
    public ArrayList<Camera> data;

    public static class Camera {
        public String name;
        public String value;
    }
}
