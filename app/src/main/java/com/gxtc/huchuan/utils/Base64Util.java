package com.gxtc.huchuan.utils;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

/**
 * Created by Gubr on 2017/3/6.
 */

public class Base64Util {

    private static final String TAG = Base64Util.class.getSimpleName();

    public static void encode(Map<String,String> map){
        if (map == null) {
            return;
        }
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (RegexUtils.isZh(entry.getValue())) {
                String encode = Base64.encodeToString(entry.getValue().getBytes(), Base64.NO_PADDING);
                map.put(entry.getKey(), encode);
            }
        }
    }

    /**
     * 编码
     * @param s
     * @return
     */
    public static String encode(String s) {
        // 使用基本的Base64编码
        String base64encodedString = null;
        try {
            base64encodedString = Base64.encodeToString(s.getBytes("utf-8"),Base64.NO_PADDING);

            Log.e(TAG, "encode: "+base64encodedString );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return base64encodedString;
    }

    /**
     * 转码
     * @param s
     * @return
     */
    public static String decode(String s) {
        String decodeString = null;
        try {
            byte[] base64decodedBytes = Base64.decode(s, Base64.DEFAULT);
            decodeString = new String(base64decodedBytes,"utf-8");

            Log.e(TAG, "decode: "+decodeString);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decodeString;
    }

}
