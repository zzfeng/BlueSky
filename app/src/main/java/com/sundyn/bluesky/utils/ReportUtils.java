package com.sundyn.bluesky.utils;

import java.util.ArrayList;

/* 提交报告因为参数较多，不在同一页面，创建对象，保存参数 */
public class ReportUtils {

    private static ReportUtils instance = new ReportUtils();

    private String bsiteNo;// 工地编号
    private String bsiteName;// 工地名称
    private String describe;// 问题描述
    private ArrayList<String> problemCheckList;
    private ArrayList<String> imageList;

    private ReportUtils() {
    }

    /**
     * 单一实例
     */
    public static ReportUtils getReportUtils() {
        return instance;
    }

    public String getBsiteNo() {
        return bsiteNo;
    }

    public void setBsiteNo(String bsiteNo) {
        this.bsiteNo = bsiteNo;
    }

    public String getBsiteName() {
        return bsiteName;
    }

    public void setBsiteName(String bsiteName) {
        this.bsiteName = bsiteName;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public ArrayList<String> getProblemCheckList() {
        return problemCheckList;
    }

    public void setProblemCheckList(ArrayList<String> problemCheckList) {
        this.problemCheckList = problemCheckList;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    public void clear() {
        this.bsiteNo = null;
        this.bsiteName = null;
        this.describe = null;
        this.problemCheckList = null;
        this.imageList = null;
    }

}
