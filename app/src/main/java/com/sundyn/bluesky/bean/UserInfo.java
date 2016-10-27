package com.sundyn.bluesky.bean;

import java.util.List;

public class UserInfo extends BaseBean {
    public String token;
    public Person user;

    public static class Person {
        public String userName;
        public String userNo;
        public List<String> roles;
        public List<String> regionIds;
    }

}
