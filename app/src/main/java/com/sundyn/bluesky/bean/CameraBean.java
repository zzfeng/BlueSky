package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author 08
 *         ���ݹ���ID��ȡ����ͷ�б�ʵ����
 */
public class CameraBean extends BaseBean {
    public ArrayList<Camera> data;

    public static class Camera {
        public String name;
        public String value;
    }
}
