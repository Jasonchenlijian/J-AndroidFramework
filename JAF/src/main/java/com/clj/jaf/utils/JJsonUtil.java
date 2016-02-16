package com.clj.jaf.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JJsonUtil {
    public JJsonUtil() {
    }

    public static String getString(JSONObject jsonObject, String key) {
        String ret = "";
        if (jsonObject.has(key) && !jsonObject.isNull(key)) {
            try {
                ret = jsonObject.getString(key);
            } catch (JSONException var4) {
                var4.printStackTrace();
            }
        }

        return ret;
    }

    public static int getInt(JSONObject jsonObject, String key) {
        int ret = 0;
        if (jsonObject.has(key) && !jsonObject.isNull(key)) {
            try {
                ret = jsonObject.getInt(key);
            } catch (JSONException var4) {
                ;
            }
        }

        return ret;
    }

    public static long getLong(JSONObject jsonObject, String key) {
        long ret = 0L;
        if (jsonObject.has(key) && !jsonObject.isNull(key)) {
            try {
                ret = jsonObject.getLong(key);
            } catch (JSONException var5) {
                ;
            }
        }

        return ret;
    }

    public static double getDouble(JSONObject jsonObject, String key) {
        double ret = 0.0D;
        if (jsonObject.has(key) && !jsonObject.isNull(key)) {
            try {
                ret = jsonObject.getDouble(key);
            } catch (JSONException var5) {
                ;
            }
        }

        return ret;
    }
}
