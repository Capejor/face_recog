/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service 
 * @author: Administrator   
 * @date: 2019年5月18日 上午11:51:07 
 */
package com.faceRecog.manage.service;

import java.util.List;
import java.util.Map;

import com.faceRecog.manage.model.AttendDetail;
import com.faceRecog.manage.util.Result;

/** 
 * @ClassName: AttendDetailService 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月18日 上午11:51:07  
 */
public interface AttendDetailService {
	
	Result insertBatchAttendDetail(List<AttendDetail> attendDetailList)throws Exception;
	
	
	int updateAttendDetail(List<AttendDetail> attendDetailList)throws Exception;
	
	List<Map<String, Object>>selectAttendDetailByAgId(String agId,String dateStr)throws Exception;

}
