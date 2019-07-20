/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service 
 * @author: Administrator   
 * @date: 2019年5月16日 下午7:06:33 
 */
package com.faceRecog.manage.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.faceRecog.manage.model.AttendCalculate;
import com.faceRecog.manage.model.Attendance;
import com.faceRecog.manage.model.vo.AttendCalculateVO;
import com.github.pagehelper.PageInfo;

/** 
 * @ClassName: AttendCalculateService 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月16日 下午7:06:33  
 */
public interface AttendCalculateService {

	
	int updateAttendCalculateByEmpId(AttendCalculate attendCalculate)throws Exception;
	
	int insertAttendCalculate(AttendCalculate attendCalculate)throws Exception;
	
	PageInfo<AttendCalculateVO> selectPageAttendCalculate(int pageSize,int pageNo,String startDate,String endDate,String name,String empId,String deptId)throws Exception;
	
	
	PageInfo<Map<String, Object>> selectPageAttendCalculateDetail(List<Map<String, Object>> dates,int pageNo,int pageSize)throws Exception;
	
	List<Map<String, Object>>selectSignMissAttendDetail(String dateStr,String empId)throws Exception;
}
