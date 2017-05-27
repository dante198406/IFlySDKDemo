/*
 * NaviSelectJsonHelper.java<br/>
 * 包含类名列表<br/>
 * 版本信息<br/>
 * date 2015-11-17 上午10:37:35<br/>
 * Copy Right
 */
package com.erobbing.iflysdkdemo.ui;

import java.lang.reflect.Type;

import org.json.JSONArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * JsonHelper
 *
 * @author admin<br/>
 *         description: TODO<br/>
 *         create: 2015-11-17 上午10:37:35<br/>
 *         <p>
 *         change by admin, 2015-11-17 上午10:37:35, reason: TODO
 */
public class NaviJsonParser {
    //private static String TAG = "NaviSelectJsonHelper";

    /**
     * convertToObject:(将poilist由json转为object). <br/>
     * (方法详述) <br/>
     *
     * @param jsonstr
     * @param typeToken
     * @return Object
     */
    @SuppressWarnings("rawtypes")
    public static Object convertToObject(String jsonstr, TypeToken typeToken) {
        Object obj = null;
        Gson gson;
        Type type;
        if (jsonstr == null || jsonstr.trim().length() == 0
                || typeToken == null)
            return null;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        type = typeToken.getType();

        try {
            obj = gson.fromJson(jsonstr, type);
        } catch (JsonSyntaxException e) {
            return null;
        }
        return obj;
    }


    public static String convertToJsonString(Object ojbect) {
        Gson gson;
        String jsonStr = "";
        if (ojbect == null) {
            return jsonStr;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        try {
            jsonStr = "[" + gson.toJson(ojbect) + "]";
        } catch (JsonSyntaxException e) {
            return jsonStr;
        }
        return jsonStr;
    }

    public static String reduceJson(String jsonPOI, int length) {
        JSONArray jsonArray = null;
        try {
            if (jsonPOI != null) {
                jsonArray = new JSONArray(jsonPOI);
            }
            /** 导航到A点，切目的地唯一 */
            if (jsonArray != null && jsonArray.length() > length) {
                JSONArray list = new JSONArray();
                for (int i = 0; i < length; i++) {
                    list.put(jsonArray.get(i));
                }
                jsonPOI = list.toString();
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonPOI;
    }

    public static boolean isJsonArrayEmpty(String jsonPOI) {
        JSONArray jsonArray = null;
        if (jsonPOI == null || jsonPOI.length() == 0) {
            return true;
        }
        try {
            jsonArray = new JSONArray(jsonPOI);
            if (jsonArray.length() > 0) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
