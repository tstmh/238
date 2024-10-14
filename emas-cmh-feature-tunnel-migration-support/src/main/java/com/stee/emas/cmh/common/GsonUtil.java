package com.stee.emas.cmh.common;

import com.google.gson.Gson;

/**
 * @author Wang Yu
 * crated at 2022/5/30
 */
public class GsonUtil {
    private final static Gson gsonInstance = new Gson();

    private GsonUtil() {}

    public static Gson gsonInstance() {
        return gsonInstance;
    }

    public static String toJson(Object obj) {
        return gsonInstance.toJson(obj);
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        return gsonInstance.fromJson(json, clazz);
    }
}
