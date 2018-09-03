package com.gxtc.huchuan.utils;

import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/22.
 */

public class StringUtil {

    /**
     * 格式化价格显示多少位小数
     * @param decimal 要显示的位数
     * @return
     */
    public static String formatMoney(int decimal, double money){
        String temp = String.format(Locale.CHINA, "%."+ decimal +"f", money);
        return temp;
    }

    /**
     * 格式化价格显示多少位小数
     * @param decimal 要显示的位数
     * @return
     */
    public static String formatMoney(int decimal, String money){
        if(TextUtils.isEmpty(money)) return "";
        try {
            double p = Double.valueOf(money);
            return formatMoney(decimal,p);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "0";
    }

    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception var3) {
            return defValue;
        }
    }

    public static int toInt(Object obj) {
        return obj == null?0:toInt(obj.toString(), 0);
    }

    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception var2) {
            return 0L;
        }
    }

    public static double toDouble(String obj) {
        try {
            return Double.parseDouble(obj);
        } catch (Exception var2) {
            return 0.0D;
        }
    }

    public static float toFloat(String obj) {
        try {
            return Float.parseFloat(obj);
        } catch (Exception var2) {
            return 0.0f;
        }
    }

    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception var2) {
            return false;
        }
    }


    //将大于10000的转换成 万为单位 例如 1.2万
    public static String Format10000(String num){
        double n = toDouble(num);
        if(n < 10000){
            return String.valueOf(n);
        }else {
            return (double) n / 10000 + "";
        }
    }


    /**
     * 计算出该TextView中文字的长度(像素)
     */
    public static int getTextViewLength(TextView textView, String text) {
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候,像素为多少
        return (int) paint.measureText(text);
    }
}
