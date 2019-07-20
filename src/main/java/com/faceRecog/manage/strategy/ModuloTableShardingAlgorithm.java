/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.strategy 
 * @author: xya
 * @date: 2019年6月18日 下午5:12:26 
 */
package com.faceRecog.manage.strategy;


import com.google.gson.GsonBuilder;

import io.shardingsphere.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * @ClassName: ModuloTableShardingAlgorithm 
 * @Description: TODO
 * @author: xya
 * @date: 2019年6月18日 下午5:12:26  
 */
/**
 * 分表策略的基本实现
 * Created by Kane on 2018/1/22.
 */
public class ModuloTableShardingAlgorithm implements PreciseShardingAlgorithm<Date> {

	private static Logger log=LoggerFactory.getLogger(ModuloTableShardingAlgorithm.class);
	
	
	@Override
    public String doSharding(final Collection<String> availableTargetNames, final PreciseShardingValue<Date> shardingValue) {
        int size = availableTargetNames.size();
        for (String each : availableTargetNames) {
        	//组装多个分片键的值，确定其对应的目标数据表
            SimpleDateFormat fmt= new SimpleDateFormat("yyyy");
            String year="";
			year = fmt.format(shardingValue.getValue());
			//return each+"_"+year;// 生成分配的的数据表表名
			if (each.endsWith(year)) {
				return each;
			}
        }
        throw new UnsupportedOperationException();
    }
	
   /* @Override
    public String doEqualSharding(Collection<String> tableNames, ShardingValue<Date> shardingValue) {
    	log.info("[数据库策略] 当前SQL语句为：=条件语句， 分片建的值为：{}", new GsonBuilder().create().toJson(shardingValue));
        for (String each : tableNames) {
        	//组装多个分片键的值，确定其对应的目标数据表
            SimpleDateFormat fmt= new SimpleDateFormat("yyyy");
            String year="";
			year = fmt.format(shardingValue.getValue());
			//return each+"_"+year;// 生成分配的的数据表表名
			if (each.endsWith(year)) {
				return each;
			}
        }
        throw new IllegalArgumentException();
    }



	 (non Javadoc) 
	 * @Title: doInSharding
	 * @Description: TODO
	 * @param availableTargetNames
	 * @param shardingValue
	 * @return 
	 * @see com.dangdang.ddframe.rdb.sharding.router.strategy.SingleKeyShardingAlgorithm#doInSharding(java.util.Collection, com.dangdang.ddframe.rdb.sharding.api.ShardingValue) 
	  
	@Override
	public Collection<String> doInSharding(Collection<String> availableTargetNames,
			ShardingValue<Date> shardingValue) {
		// TODO Auto-generated method stub
		return null;
	}



	 (non Javadoc) 
	 * @Title: doBetweenSharding
	 * @Description: TODO
	 * @param availableTargetNames
	 * @param shardingValue
	 * @return 
	 * @see com.dangdang.ddframe.rdb.sharding.router.strategy.SingleKeyShardingAlgorithm#doBetweenSharding(java.util.Collection, com.dangdang.ddframe.rdb.sharding.api.ShardingValue) 
	  
	@Override
	public Collection<String> doBetweenSharding(Collection<String> availableTargetNames,
			ShardingValue<Date> shardingValue) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public Collection<String> doInSharding(Collection<String> tableNames, ShardingValue<String> shardingValue) {
        Collection<String> result = new LinkedHashSet<>(tableNames.size());
        for (String each : tableNames) {
        	//组装多个分片键的值，确定其对应的目标数据表
            SimpleDateFormat fmt= new SimpleDateFormat("yyyy");
            String year="";
			try {
				year = fmt.format(fmt.parse(shardingValue.getValue()));
				if (each.endsWith(year)) {
					return each;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
        return result;
    }

    @Override
    public Collection<String> doBetweenSharding(Collection<String> tableNames, ShardingValue<String> shardingValue) {
        Collection<String> result = new LinkedHashSet<>(tableNames.size());
        Range<Long> range = (Range<Long>) shardingValue.getValueRange();
        for (Long i = range.lowerEndpoint(); i <= range.upperEndpoint(); i++) {
            for (String each : tableNames) {
                if (each.endsWith(i % 2 + "")) {
                    result.add(each);
                }
            }
        }
        return result;
    }*/
}
