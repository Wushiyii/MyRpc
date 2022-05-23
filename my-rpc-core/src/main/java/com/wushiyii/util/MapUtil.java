package com.wushiyii.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Map;

public class MapUtil {

    public static Map<String, String> objectToMap(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<Map<String, String>>(){});
    }

    public static <T> T  mapToObject(Map<String, String> map, Class<T> clazz) {
        return JSON.parseObject(JSON.toJSONString(map), clazz);
    }

}
