package com.sundyn.bluesky.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.sundyn.bluesky.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    /* 判断当前是否有可用的网络以及网络类型 0：无网络 1：WIFI 2：CMWAP 3：CMNET */
    public static int isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return 0;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        NetworkInfo netWorkInfo = info[i];
                        if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            return 1;
                        } else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            String extraInfo = netWorkInfo.getExtraInfo();
                            if ("cmwap".equalsIgnoreCase(extraInfo)
                                    || "cmwap:gsm".equalsIgnoreCase(extraInfo)) {
                                return 2;
                            }
                            return 3;
                        }
                    }
                }
            }
        }
        return 0;
    }

    /* 获取现在时间 */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String getMonthStart() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        return formatter.format(c.getTime());
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String formatDateOnlyMD(Date date, boolean hasYear) {
        SimpleDateFormat format = null;
        if (hasYear) {
            format = new SimpleDateFormat("yyyy年MM月dd日");
        } else {
            format = new SimpleDateFormat("MM月dd日");
        }
        return format.format(date);
    }

    public static String formatDateNoMinute(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String formatDateNoMinute(String dateStr, boolean onlyMouth) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        if (onlyMouth) {
            sdf = new SimpleDateFormat("dd");
            return sdf.format(date);
        }
        return formatDateOnlyMD(date, false);
    }

    /* 获取上周时间 */
    public static Date getLastWeekTime() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -6);
        return c.getTime();
    }

    /* 判断风向 */
    public static String getWindDic(String dic) {
        char[] obj = new char[dic.length()];
        obj = dic.toCharArray();
        String windHtm = "";
        switch (obj.length) {
            case 1:
                if (containsOBJ(obj, "E")) {
                    windHtm = "东";
                }
                if (containsOBJ(obj, "W")) {
                    windHtm = "西";
                }
                if (containsOBJ(obj, "S")) {
                    windHtm = "南";
                }
                if (containsOBJ(obj, "N")) {
                    windHtm = "北";
                }
                break;
            case 2:
                if (containsOBJ(obj, "E") && !containsOBJ(obj, "W")) {
                    windHtm = "东";
                }
                if (containsOBJ(obj, "W") && !containsOBJ(obj, "E")) {
                    windHtm = "西";
                }
                if (containsOBJ(obj, "S") && !containsOBJ(obj, "N")) {
                    windHtm += "南";
                }
                if (containsOBJ(obj, "N") && !containsOBJ(obj, "S")) {
                    windHtm += "北";
                }
            case 3:
                if ((containsOBJ(obj, "E") && containsOBJ(obj, "W"))
                        || (containsOBJ(obj, "S") && containsOBJ(obj, "N"))) {
                    windHtm = "旋风";
                } else {
                    if (containsOBJ(obj, "E") && !containsOBJ(obj, "W")) {
                        windHtm = "东";
                    }
                    if (containsOBJ(obj, "W") && !containsOBJ(obj, "E")) {
                        windHtm = "西";
                    }
                    if (containsOBJ(obj, "S") && !containsOBJ(obj, "N")) {
                        windHtm += "南";
                    }
                    if (containsOBJ(obj, "N") && !containsOBJ(obj, "S")) {
                        windHtm += "北";
                    }
                }

                break;
            case 4:
                if ((containsOBJ(obj, "E") && containsOBJ(obj, "W"))
                        || (containsOBJ(obj, "S") && containsOBJ(obj, "N"))) {
                    windHtm = "旋风";
                } else {
                    if (containsOBJ(obj, "E") && !containsOBJ(obj, "W")) {
                        windHtm = "东";
                    }
                    if (containsOBJ(obj, "W") && !containsOBJ(obj, "E")) {
                        windHtm = "西";
                    }
                    if (containsOBJ(obj, "S") && !containsOBJ(obj, "N")) {
                        windHtm += "南";
                    }
                    if (containsOBJ(obj, "N") && !containsOBJ(obj, "S")) {
                        windHtm += "北";
                    }
                }
                break;

        }
        return windHtm + "风";
    }

    private static boolean containsOBJ(char[] arr, String obj) {
        for (char str : arr) {
            if (obj.equals(String.valueOf(str))) {
                return true;
            }
        }
        return false;
    }

    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_!@#$&*+=.|￥¥]+$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    /* 为GridView设置动画 */
    public static void setLayoutAnimationController(ViewGroup mView,
                                                    Context mContext) {
        Animation animation = AnimationUtils.loadAnimation(mContext,
                R.anim.anim_workbenchitem);
        LayoutAnimationController controller = new LayoutAnimationController(
                animation);
        controller.setDelay(0.3f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        mView.setLayoutAnimation(controller);

    }

    /* 百分比保留两位小数 */
    public static String saveNumberPercent(float number) {
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(2);
        return nt.format(number);
    }

    public static ArrayList<Integer> getColors() {
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        return colors;
    }

}
