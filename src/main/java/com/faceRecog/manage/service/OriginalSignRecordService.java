/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service 
 * @author: xya
 * @date: 2019年6月15日 下午2:46:47 
 */
package com.faceRecog.manage.service;

import java.util.List;
import java.util.Map;

/** 
 * @ClassName: OriginalSignRecordService 
 * @Description: TODO
 * @author: xya
 * @date: 2019年6月15日 下午2:46:47  
 */
public interface OriginalSignRecordService {

	
	List<Map<String, Object>>selectPageOrigSignInfo()throws Exception;
}
