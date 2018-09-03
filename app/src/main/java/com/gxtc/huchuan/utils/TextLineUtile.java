package com.gxtc.huchuan.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * 利用反射修复TextLine.sCached 造成的内存泄露
 * Created by zzg on 2018/1/5.
 */

public class TextLineUtile {

    public static void clearTextLineCache(){
        Field textLineCached = null;
        try {
            textLineCached = Class.forName("android.text.TextLine").getDeclaredField("sCached");
            textLineCached.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (textLineCached == null) return;
        Object cached = null;
        try {
            // Get reference to the TextLine sCached array.
            cached = textLineCached.get(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (cached != null) {
            // Clear the array.
            for (int i = 0, size = Array.getLength(cached); i < size; i ++) {
                Array.set(cached, i, null);
            }
        }
    }
}
