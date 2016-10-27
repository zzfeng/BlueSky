package com.sundyn.bluesky.bean;

import java.util.ArrayList;

/**
 * @author yangjl
 *         描述：获取新闻列表bean
 *         2016-6-28下午5:56:51
 */
public class NewsListBean extends BaseBean {
    public ArrayList<News> data;

    public static class News {
        public String create_time;
        public String notice_id;
        public int notice_type;
        public String title;
        public String img;
        public String content;
    }
}
