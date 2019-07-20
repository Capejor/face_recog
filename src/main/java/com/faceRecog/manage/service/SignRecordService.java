/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service 
 * @author: Administrator   
 * @date: 2019年5月11日 下午2:24:48 
 */
package com.faceRecog.manage.service;


 

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.faceRecog.manage.model.SignRecord;
import com.faceRecog.manage.util.Result;

/** 
 * @ClassName: SignRecordService 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月11日 下午2:24:48  
 */
public interface SignRecordService {

	
	Result insertSignRecord(SignRecord signRecord,String type)throws Exception;

	int updateSignRecordById(SignRecord signRecord)throws Exception;
	
	Map<String, Object>selectAttendGroupEmpSignInfoByEmpId(String empId,String yesterDay)throws Exception;
		
	Result insertFreeSign(Date signTime,String signDate,String empId,String signType)throws Exception;
}
