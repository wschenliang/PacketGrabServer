package com.jiangnan.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.TypeUtils;

import java.io.Serializable;
import java.util.List;

public class FastjsonUtils {

    private static final String indexRegex = "\\[(\\d+)\\]";

    public static boolean isEmpty(Object json) {
        if (json == null) {
            return true;
        } else if (json instanceof JSONObject) {
            return ((JSONObject) json).size() < 1;
        } else if (json instanceof JSONArray) {
            return ((JSONArray) json).size() < 1;
        } else {
            return true;
        }
    }

    private static JSONArray get(JSONArray array, int i) {
        if(CollectionUtils.isEmpty(array)) {
            return null;
        }
        return array.size() > i ? array.getJSONArray(i) : null;
    }

    private static <T> T getBySingleKey(Object json, String key, Class<T> clazz) {
        if (StringUtils.isBlank(key)) {
            return null;
        }

        if (!(json instanceof JSON)) {
            json = JSON.toJSON(json);
        }

        if (isEmpty(json)) {
            return null;
        }

        List<String> indexs = RegexUtils.findMatches(key, indexRegex);
        if (CollectionUtils.isEmpty(indexs)) {
            return ((JSONObject) json).getObject(key, clazz);
        }

        int start = key.indexOf('[');
        JSONArray ja;
        if (start < 1) {
            ja = (JSONArray) json;
        } else {
            ja = ((JSONObject) json).getJSONArray(key.substring(0, start));
            if (CollectionUtils.isEmpty(ja)) {
                return null;
            }
        }
        int last = indexs.size() - 1;
        for (int j = 0; j < last; ++j) {
            ja = get(ja, Integer.parseInt(indexs.get(j)));
            if (CollectionUtils.isEmpty(ja)) {
                return null;
            }
        }
        return ja.getObject(last, clazz);
    }

    private static JSONObject getJSONObjectBySingleKey(JSON json, String key) {
        if (StringUtils.isBlank(key) || isEmpty(json)) {
            return null;
        }

        List<String> indexs = RegexUtils.findMatches(key, indexRegex);
        if (CollectionUtils.isEmpty(indexs)) {
            return ((JSONObject) json).getJSONObject(key);
        }

        int start = key.indexOf('[');
        JSONArray ja;
        if (start < 1) {
            ja = (JSONArray) json;
        } else {
            ja = ((JSONObject) json).getJSONArray(key.substring(0, start));
            if (CollectionUtils.isEmpty(ja)) {
                return null;
            }
        }
        int last = indexs.size() - 1;
        for (int j = 0; j < last; ++j) {
            ja = get(ja, Integer.parseInt(indexs.get(j)));
            if (CollectionUtils.isEmpty(ja)) {
                return null;
            }
        }
        return ja.getJSONObject(last);
    }

    private static Object getBySingleKey(Object json, String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }

        if (!(json instanceof JSON)) {
            json = JSON.toJSON(json);
        }

        if (isEmpty(json)) {
            return null;
        }

        List<String> indexs = RegexUtils.findMatches(key, indexRegex);
        if (CollectionUtils.isEmpty(indexs)) {
            return ((JSONObject) json).get(key);
        }

        int start = key.indexOf('[');
        JSONArray ja;
        if (start < 1) {
            ja = (JSONArray) json;
        } else {
            ja = ((JSONObject) json).getJSONArray(key.substring(0, start));
            if (CollectionUtils.isEmpty(ja)) {
                return null;
            }
        }
        int last = indexs.size() - 1;
        for (int j = 0; j < last; ++j) {
            ja = get(ja, Integer.parseInt(indexs.get(j)));
            if (CollectionUtils.isEmpty(ja)) {
                return null;
            }
        }
        return ja.get(last);
    }

    private static Object getPreJSON(Object json, String[] keyArray, int last) {
        for (int i = 0; i < last; ++i) {
            json = getBySingleKey(json, keyArray[i]);
        }
        return json;
    }

    public static String getStringByKeys(JSONObject json, String keys) {
        if (keys == null) {
            return null;
        }
        String[] keyArray = keys.split("\\.");
        return getString(json, keyArray);
    }

    public static String getString(JSON json, String... keys) {
        Object obj = get(json, keys);
        return TypeUtils.castToString(obj);
    }

    public static Object getByKeys(JSON json, String keys) {
        if (keys == null) {
            return null;
        }
        String[] keyArray = keys.split("\\.");
        return get(json, keyArray);
    }

    public static Object get(JSON json, String... keys) {
        if (ArrayUtils.isEmpty(keys)) {
            return null;
        }
        int ilast = keys.length - 1;
        Object rs = json;
        if (ilast > 0) {
            rs = getPreJSON(json, keys, ilast);
        }
        return getBySingleKey(rs, keys[ilast]);
    }

    public static <T> T getByKeys(JSONObject json, String keys, Class<T> clazz) {
        if (keys == null) {
            return null;
        }
        String[] keyArray = keys.split("\\.");
        return get(json, clazz, keyArray);
    }

    public static <T> T get(JSON json, Class<T> clazz, String... keys) {
        if (ArrayUtils.isEmpty(keys)) {
            return null;
        }
        int ilast = keys.length - 1;
        Object rs = json;
        if (ilast > 0) {
            rs = getPreJSON(json, keys, ilast);
        }

        return getBySingleKey(rs, keys[ilast], clazz);
    }

    public static JSONObject getJSONObject(JSONObject json, String... keys) {
        return (JSONObject) get(json, keys);
    }

    public static JSONArray getJSONArray(JSONObject json, String... keys) {
        return (JSONArray) get(json, keys);
    }

    public static <T> List<T> getList(JSONObject json, Class<T> clazz, String... keys) {
        JSONArray array = (JSONArray) get(json, keys);
        return array == null ? null : array.toJavaList(clazz);
    }


    private static Object parseJsonByKey(Object value, String jsonParseKey) {
        if (StringUtils.isBlank(jsonParseKey)) {
            return value;
        }
        if(!(value instanceof JSON)) {
            return null;
        }
        value = getByKeys((JSON) value, jsonParseKey);
        if (value instanceof String) {
            return JSON.parse((String) value);
        } else {
            return value;
        }
    }


    public static <T extends Serializable> String toJSONString(T obj) {
        return JSONObject.toJSONString(obj);
    }

    public static <T extends Serializable> String toJSONStringPretty(T obj) {
        return JSON.toJSONString(obj, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,SerializerFeature.WriteDateUseDateFormat);
    }

    public static <T extends Serializable> byte[] toJSONByte(T obj) {
        return JSONObject.toJSONBytes(obj);
    }
}
