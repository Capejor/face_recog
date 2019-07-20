/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.redis 
 * @author: xya
 * @date: 2019年6月13日 下午6:46:48 
 */
package com.faceRecog.manage.redis;

/**
 * Redis 枚举类
 *
 * @author yclimb
 * @date 2018/4/19
 */
public enum RedisEnum {

    /**
     * 数据字典Service - 根据字典类型查询字典数据
     */
	attendance_table("faceRecog", "attendance"),
    origSign_table("faceRecog", "origSign");

    /**
     * 系统标识
     */
    private String keyPrefix;
    /**
     * 模块名称
     */
    private String module;
   

    RedisEnum(String keyPrefix, String module) {
        this.keyPrefix = keyPrefix;
        this.module = module;
    }

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}


     
}
