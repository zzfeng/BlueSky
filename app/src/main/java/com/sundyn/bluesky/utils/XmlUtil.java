package com.sundyn.bluesky.utils;


import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/29.
 */
public class XmlUtil {

    private static XmlUtil xmlUtil = new XmlUtil();

    private XmlUtil() {
    }

    public static XmlUtil getInstance() {
        return xmlUtil;
    }

    /**
     * ��ȡxml�ļ�ת���ɼ���
     */

    public List<Map<String, Object>> xmlToList(InputStream is) {

        try {
            List<Map<String, Object>> xmlList = null;
            Map<String, Object> map = null;
            // ���pull����������
            XmlPullParser parser = Xml.newPullParser();
            // ָ���������ļ��ͱ����ʽ
            parser.setInput(is, "utf-8");
            int eventType = parser.getEventType(); // ����¼�����
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // ��õ�ǰ�ڵ������

                switch (eventType) {
                    case XmlPullParser.START_TAG: // ��ǰ���ڿ�ʼ�ڵ� <person>
                        if ("list".equals(tagName)) { // <persons>
                            xmlList = new ArrayList<Map<String, Object>>();
                        } else if ("item".equals(tagName)) { // <person id="1">
                            map = new HashMap<String, Object>();
                        } else {
                            map.put(tagName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG: // </persons>
                        if ("item".equals(tagName)) {
                            xmlList.add(map);
                        }
                        break;
                    default:
                        break;
                }

                eventType = parser.next(); // �����һ���¼�����
            }
            return xmlList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
