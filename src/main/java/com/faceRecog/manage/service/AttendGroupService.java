/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service 
 * @author: Administrator   
 * @date: 2019年5月17日 下午1:49:41 
 */
package com.faceRecog.manage.service;

import java.util.List;
import java.util.Map;

import com.faceRecog.manage.util.Result;

/** 
 * @ClassName: AttnedGroupService 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月17日 下午1:49:41  
 */
public interface AttendGroupService {

	
	
	Map<String, Object>selectDepartEmployeeTree()throws Exception;
	
	
	Result insertOrUpdateAttendGroup(String  scheduleIds,String  periodIds,
			String attendGroupName,String  leaderIds,String   empIds,
			String  deptIds,String agId,String effective,String type)throws Exception;
	
	
	List<Map<String, Object>>selectAllAttendGroup()throws Exception;
	
	
	int deleteAttendGroup(String agId)throws Exception;
	
	
//	/String updateEmpAttendGroup(String agId,List<Map<String, Object>> empIdLs)throws Exception;
	
	
	Map<String, Object>selectAttendGroup(String agId)throws Exception;
	
	
	Map<String, Object>selectAttendGroupAttendDance(String agId)throws Exception;
	
	
	int selectAttendGroupNameExist(String attendGroupName,String agId)throws Exception;
}
