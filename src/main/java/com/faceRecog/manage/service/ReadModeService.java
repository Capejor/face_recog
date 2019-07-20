/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service 
 * @author: Administrator   
 * @date: 2019年5月8日 上午10:54:05 
 */
package com.faceRecog.manage.service;


import com.faceRecog.manage.model.ReadMode;

/** 
 * @ClassName: ReadModeService 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月8日 上午10:54:05  
 */
public interface ReadModeService {

	
	
	int insertReadMode(ReadMode readMode)throws Exception;
	
	ReadMode selectReadModeByPrimaryKey(String id)throws Exception;
	
	ReadMode selectReadModeByEqId(String eqId)throws Exception;
	
	int updateReadModeByPrimaryKey(ReadMode readMode)throws Exception;
}
