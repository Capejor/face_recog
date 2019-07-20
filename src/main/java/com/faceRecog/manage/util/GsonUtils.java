package com.faceRecog.manage.util;

import com.google.gson.GsonBuilder;

/**
 * @Author hzj
 * @Date 2017/7/6 15:19
 * @Description :
 */
public class GsonUtils {

    /**
     *  对象转json字符串
     *
     * @param obj
     * @return
     */
    public static String objToJson(Object obj){

        if(null == obj) {
            return null;
        }

        return new GsonBuilder().create().toJson(obj);
    }


    /**
     * json字符串 转为 对象
     *
     * @param jsonstr
     * @param clzz
     * @param <T>
     * @return
     */
    public  static <T> T jsonToObj(String jsonstr, T clzz){


        return null;
    }


}
