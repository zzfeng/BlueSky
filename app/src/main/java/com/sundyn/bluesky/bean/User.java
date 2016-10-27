package com.sundyn.bluesky.bean;

import java.util.List;

/**
 * @author yangjl 描述：用户实体类 2016-6-24上午11:09:18
 */
public class User {
    private String username;
    private String rolestring;
    private String regionidstring;

    public String getRegionidstring() {
        return regionidstring;
    }

    public void setRegionidstring(String regionidstring) {
        this.regionidstring = regionidstring;
    }

    public String getRolestring() {
        return rolestring;
    }

    public void setRolestring(String rolestring) {
        this.rolestring = rolestring;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    private String userNo;
    private List<String> roles;

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public User(String username, String userNo, String token,
                List<String> roles, List<String> regionIds) {
        super();
        this.username = username;
        this.token = token;
        this.userNo = userNo;
        this.roles = roles;
        rolestring = changRoles2String(roles);
        regionidstring = changRoles2String(regionIds);
    }

    private String changRoles2String(List<String> list) {
        StringBuffer roleBuffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            roleBuffer.append(list.get(i));
            if (i != list.size() - 1) {
                roleBuffer.append(",");
            }
        }
        return roleBuffer.toString();
    }

    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
