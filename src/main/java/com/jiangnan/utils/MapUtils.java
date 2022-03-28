package com.jiangnan.utils;

import java.lang.reflect.Type;
import java.util.Map;

/**
 *
 * @author chenliang
 * @email wschenliang@aliyun.com
 */
public class MapUtils {


    public static boolean isNotEmpty(Map<String, String> properties) {
        return properties != null && properties.keySet().size() >= 1;
    }

    public static boolean isEmpty(Map<Class<?>, Map<Type, Type>> typeMap) {
        return typeMap == null || typeMap.keySet().size() == 0;
    }
}
