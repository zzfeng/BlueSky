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
     * 读取xml文件转换成集合
     */

    public List<Map<String, Object>> xmlToList(InputStream is) {

        try {
            List<Map<String, Object>> xmlList = null;
            Map<String, Object> map = null;
            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(is, "utf-8");
            int eventType = parser.getEventType(); // 获得事件类型
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // 获得当前节点的名称

                switch (eventType) {
                    case XmlPullParser.START_TAG: // 当前等于开始节点 <person>
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

                eventType = parser.next(); // 获得下一个事件类型
            }
            return xmlList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
