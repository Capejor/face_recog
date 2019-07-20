package com.faceRecog.manage.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * 日期工具类
 *
 * @Author hzj
 * @Date 2017/7/7 17:48
 * @Description :
 */

public class DateUtils {


    /**
     * java.util.Date 转换为  java.time.LocalDate
     *
     * @param date
     * @return
     */
    public LocalDate dateToLocalDate(Date date){
        if(null == date){
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    /**
     * java.sql.Date 转换为  java.time.LocalDate <br/>
     *
     * java.sql.Date 必须先转为为 java.util.Date ,如果直接转换，则会出现  java.lang.UnsupportedOperationException 异常
     *
     * @param date
     * @return
     */
    public LocalDate dateToLocalDate(java.sql.Date date){
        if(null == date){
            return null;
        }
        Date utilDate = new Date(date.getTime());
        return utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }



}
