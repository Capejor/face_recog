/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.controller 
 * @author: Administrator   
 * @date: 2019年5月18日 上午11:49:58 
 */
package com.faceRecog.manage.controller;


import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.faceRecog.manage.model.AttendDetail;
import com.faceRecog.manage.service.AttendDetailService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;

/** 
 * @ClassName: AttendDetailController 
 * @Description: 考勤明细 人员排班
 * @author: xya
 * @date: 2019年5月18日 上午11:49:58  
 */

@RestController
public class AttendDetailController {
	 private static Logger logger = LoggerFactory.getLogger(AttendDetailController.class);
	
	@Autowired
	private AttendDetailService  attendDetailService;
	
	
	/**
	 * 
	* @Title: insertAttendDetail 
	* @Description:新增修改考勤明细 
	* @return Result
	* @author xya
	* @date 2019年5月18日下午1:49:51
	 */
	@RequestMapping("/insertAttendDetail")
	public Result insertAttendDetail(@RequestBody List<AttendDetail> attendDetailList)throws Exception{
		Result result=null;
		if(attendDetailList==null || attendDetailList.size()<1 || !StrKit.notNull(attendDetailList.get(0))){
			return Result.responseError("考勤明细不能为空！",attendDetailList);
		}
		result=attendDetailService.insertBatchAttendDetail(attendDetailList);
		
		return result;
	}
	
	/**
	 * 
	* @Title: updateAttendDetail 
	* @Description: 修改
	* @param attendDetailList
	* @return Result
	* @author xya
	* @date 2019年5月18日下午3:04:37
	 */
	@RequestMapping("/updateAttendDetail")
	public Result updateAttendDetail(@RequestBody List<AttendDetail> attendDetailList,String startTime)throws Exception{
		Result result=null;
		if(attendDetailList==null || attendDetailList.size()<1 || StrKit.isBlank(attendDetailList.get(0).getEmpId())){
			return Result.responseError("考勤明细不能为空！");
		}else if(StrKit.isBlank(startTime)){
			return Result.responseError("排班起始日期不能为空！");
		}
		int affectNum=attendDetailService.updateAttendDetail(attendDetailList);
		if(affectNum<0){
			return Result.responseError("修改失败！");
		}
		result=Result.responseSuccess("修改成功！");
		return result;
	}
	
	
	/**
	 * 
	* @Title: selectAttendDetail 
	* @Description: 根据考勤组id查询考勤组排班明细 
	* @param agId
	* @return Result
	* @author xya
	* @date 2019年5月22日下午8:00:25
	 */
	@RequestMapping("/selectAttendDetail")
	public Result selectAttendDetail(String agId,String dateStr)throws Exception{
		Result result=null;
		
		if(StrKit.isBlank(agId)){
			return Result.responseError("考勤组id不能为空");
		}else if(StrKit.isBlank(dateStr)){
			return Result.responseError("考勤日期不能为空");
		}
		
		List<Map<String, Object>> attendDetailList=attendDetailService.selectAttendDetailByAgId(agId,dateStr);
		if(attendDetailList!=null && attendDetailList.size()>0 && attendDetailList.get(0)!=null){
			result=Result.responseSuccess("查询成功！",attendDetailList);
		}else{
			result=Result.responseSuccess("查询成功！,暂无数据！",attendDetailList);
		}
		
		return result;
	}
	
}
