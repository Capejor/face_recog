package com.faceRecog.manage.util;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author hzj
 * @Date 2017/7/7 11:17
 * @Description :
 */
public class ShardingUtils {
    /**
     * 获取分片键的默认值集合
     *
     * @param shardingKey
     * @return
     */
    public static Set<Object> defaultShardingValues(String shardingKey){

        Set valueSet = new HashSet<Object>();

        if("name".equals(shardingKey)){
            valueSet.add("a");
            valueSet.add("b");
        }else if("time".equals(shardingKey)){
            valueSet.add(LocalDate.of(2017,4,1));
            valueSet.add(LocalDate.of(2017,5,1));
        }else if("exchange".equals(shardingKey)){
            valueSet.add("sh");
            valueSet.add("sz");
        }
        return valueSet;
    }


}
