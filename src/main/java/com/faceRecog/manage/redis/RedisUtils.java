package com.faceRecog.manage.redis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.SortParameters.Order;
import org.springframework.data.redis.core.query.SortQueryBuilder;

import com.faceRecog.manage.util.HanyuToPinYin;

/**
 * Redis 工具类
 *
 * @author yclimb
 * @date 2018/4/19
 */
public class RedisUtils {

    /**
     * 主数据系统标识
     */
    public static final String KEY_PREFIX = "mdm";
    /**
     * 分割字符，默认[:]，使用:可用于rdm分组查看
     */
    private static final String KEY_SPLIT_CHAR = ":";

    
    /**
     * redis的key键规则定义
     * @param prefix 项目前缀
     * @param module 模块名称
     * @param func 方法名称
     * @param args 参数..
     * @return key
     */
    public static String keyBuilder(RedisEnum redisEnum, String chineseChr,String id) {
    	//汉子转拼音
    	HanyuToPinYin hanyuToPinYin=new HanyuToPinYin();
    	chineseChr=hanyuToPinYin.getStringPinYin(chineseChr);
        // 项目前缀
    	String prefix=redisEnum.getKeyPrefix();
    	String module=redisEnum.getModule();
        StringBuilder key = new StringBuilder(prefix);
        // KEY_SPLIT_CHAR 为分割字符
        key.append(KEY_SPLIT_CHAR).append(module).append(KEY_SPLIT_CHAR).append(id).append(KEY_SPLIT_CHAR).append(chineseChr);
        
        return key.toString();
    }

    /**
     * 
    * @Title: sortPageList 
    * @Description: redis 分页查询 
    * @param key
    * @param subKey
    * @param by
    * @param isDesc
    * @param isAlpha
    * @param off
    * @param num
    * @return
    * @throws Exception SortQueryBuilder<String>
    * @author xya
    * @date 2019年6月15日上午11:56:54
     */
    public static SortQueryBuilder<String> sortPageList(String key,String subKey,String by,boolean isDesc,boolean isAlpha,int off,int num) throws  Exception{
		SortQueryBuilder<String> builder = SortQueryBuilder.sort(key);
		builder.by(subKey+"*->"+by);
		builder.get("#");
		builder.alphabetical(isAlpha);
		if(isDesc)	
			  builder.order(Order.DESC);
		else
			builder.order(Order.ASC);
		builder.limit(off, num);
		
		return builder;
    }
}