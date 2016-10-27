package com.sundyn.bluesky.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sundyn.bluesky.bean.InstallPct;
import com.sundyn.bluesky.bean.LineChartBean;
import com.sundyn.bluesky.bean.PMBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GsonTools {

    public GsonTools() {
        // TODO Auto-generated constructor stub
    }

    public static String createGsonString(Object object) {
        Gson gson = new Gson();
        String gsonString = gson.toJson(object);
        return gsonString;
    }

    public static <T> T changeGsonToBean(String gsonString, Class<T> cls) {
        Gson gson = new Gson();
        T t = gson.fromJson(gsonString, cls);
        return t;
    }

    public static <T> List<T> changeGsonToList(String gsonString, Class<T> cls) {
        Gson gson = new Gson();
        List<T> list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
        }.getType());
        return list;
    }

    public static List<PMBean> changeGsonToPMList(String gsonString,
                                                  Class<PMBean> cls) {
        Gson gson = new Gson();
        List<PMBean> list = gson.fromJson(gsonString,
                new TypeToken<List<PMBean>>() {
                }.getType());
        return list;
    }

    /*获取数据中心折线图*/
    public static List<LineChartBean> changeGsonToDataCenterChartList(
            String gsonString, Class<LineChartBean> cls) {
        Gson gson = new Gson();
        List<LineChartBean> list = gson.fromJson(gsonString,
                new TypeToken<List<LineChartBean>>() {
                }.getType());
        return list;
    }

    /*首页工地占比*/
    public static List<InstallPct> changeGsonToInstallPctList(
            String gsonString, Class<InstallPct> cls) {
        Gson gson = new Gson();
        List<InstallPct> list = gson.fromJson(gsonString,
                new TypeToken<List<InstallPct>>() {
                }.getType());
        return list;
    }

    public static <T> List<Map<String, T>> changeGsonToListMaps(
            String gsonString) {
        List<Map<String, T>> list = null;
        Gson gson = new Gson();
        list = gson.fromJson(gsonString, new TypeToken<List<Map<String, T>>>() {
        }.getType());
        return list;
    }

    public static <T> Map<String, T> changeGsonToMaps(String gsonString) {
        Map<String, T> map = null;
        Gson gson = new Gson();
        map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
        }.getType());
        return map;
    }

    public ArrayList<HashMap<String, Object>> paseJSON(String jsonStr) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hashmap = null;

        return null;
    }

}
