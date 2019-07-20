/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.strategy 
 * @author: xya
 * @date: 2019年6月18日 下午5:16:33 
 */
package com.faceRecog.manage.strategy;


import com.faceRecog.manage.util.fileUpload.CommonUtil;

import io.shardingsphere.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分库策略的简单实现
 * Created by Kane on 2018/1/22.
 */
public   class ModuloDatabaseShardingAlgorithm implements PreciseShardingAlgorithm<String> {
	private static Logger log=LoggerFactory.getLogger(ModuloDatabaseShardingAlgorithm.class);
	
	InputStream ins = this.getClass().getClassLoader().getResourceAsStream("application.properties");
    Map<String, String> map = CommonUtil.readSafePro(ins);
    private String databaseName=map.get("spring.datasource.name");
	
    
    @Override
    public String doSharding(final Collection<String> availableTargetNames, final PreciseShardingValue<String> shardingValue) {
        int size = availableTargetNames.size();
        for (String each : availableTargetNames) {
            if (each.endsWith(databaseName)) {
            	return each;
            }
        }
        throw new UnsupportedOperationException();
    }
	/*@Override
    public String doEqualSharding(Collection<String> databaseNames, ShardingValue<String> shardingValue) {
		log.info("[数据库策略] 当前SQL语句为：=条件语句， 分片建的值为：{}", new GsonBuilder().create().toJson(shardingValue));
		//目前不分库所以直接写死
		for (String each : databaseNames) {
            if (each.endsWith(databaseName)) {
            	return each;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Collection<String> doInSharding(Collection<String> databaseNames, ShardingValue<String> shardingValue) {
    	Collection<String> result = new LinkedHashSet<>(databaseNames.size());
        for (String each : databaseNames) {
            if (each.endsWith(databaseName)) {
            	 result.add(each);
            }
        }
        return result;
    }

    @Override
    public Collection<String> doBetweenSharding(Collection<String> databaseNames, ShardingValue<String> shardingValue) {
    	 log.error("[数据库策略] 该分片键不支持使用between...and 语法进行查询");
         return null;
    }*/
}