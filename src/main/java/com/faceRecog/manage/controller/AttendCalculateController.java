/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.controller 
 * @author: Administrator   
 * @date: 2019年5月17日 上午10:46:00 
 */
package com.faceRecog.manage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.faceRecog.manage.model.vo.AttendCalculateVO;
import com.faceRecog.manage.service.AttendCalculateService;
import com.faceRecog.manage.util.CommUtil;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.constantUtils.SysOperConstant;
import com.github.pagehelper.PageInfo;

/** 
 * @ClassName: AttendCalculateController 
 * @Description: 考勤明细计算
 * @author: xya
 * @date: 2019年5月17日 上午10:46:00  
 */
@RestController
public class AttendCalculateController {

	
	@Autowired
	private AttendCalculateService attendCalculateService;
	
	/**
	 * 
	* @Title: selectPageAttendCalculate 
	* @Description: 查询考勤结算明细分页
	* @param pageNum
	* @param pageSize
	* @param startDate
	* @param endDate
	* @param name
	* @param empId
	* @param deptId
	* @return Result
	* @author xya
	* @date 2019年5月17日上午11:52:49
	 */
	@RequestMapping("/selectPageAttendCalculate")
	public Result selectPageAttendCalculate(@RequestParam(defaultValue = "1") int pageNum,
			@RequestParam(defaultValue =SysOperConstant.PAGE_SIZE) int pageSize,String startDate
			,String endDate,String name,String empId,String deptId)throws Exception{
		Result result=null;
		
		if(StrKit.isBlank(startDate)){
			return Result.responseError("考勤开始日期不能为空！");
		}else if(StrKit.isBlank(endDate)){
			return Result.responseError("考勤结束日期不能为空！");
		}
		
		PageInfo<AttendCalculateVO> pageInfo=attendCalculateService.selectPageAttendCalculate(pageSize, pageNum, startDate, endDate, name, empId, deptId);
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("pageNum", pageInfo.getPageNum());
			map.put("totalNum", pageInfo.getTotal());
			map.put("pageSize", pageInfo.getPageSize());
			map.put("attendDetail",pageInfo.getList());
			if(pageInfo.getList()!=null && pageInfo.getList().size()>0 && StrKit.notBlank(pageInfo.getList().get(0).getEmpId())){
				result=Result.responseSuccess("查询成功！",map);
			}else{
				result=Result.responseSuccess("查询成功，暂无数据！",map);
			}
			
		return result;
	}
	
	
	/**
	 * 
	* @Title: selectPageAttendCalculateDetail 
	* @Description: 查询考勤明细 
	* @param pageNum
	* @param pageSize
	* @param startDate
	* @param endDate
	* @param name
	* @return
	* @throws Exception Result
	* @author xya
	* @date 2019年5月20日下午7:36:26
	 */
	@RequestMapping("/selectPageAttendCalculateDetail")
	public Result selectPageAttendCalculateDetail(@RequestParam(defaultValue = "1") int pageNum,
			@RequestParam(defaultValue =SysOperConstant.PAGE_SIZE) int pageSize,String startDate
			,String endDate,String name)throws Exception{
		Result result=null;
		if(StrKit.isBlank(startDate)){
			return Result.responseError("考勤开始日期不能为空！");
		}else if(StrKit.isBlank(endDate)){
			return Result.responseError("考勤结束日期不能为空！");
		}
		List<Map<String, Object>> dates=CommUtil.getDays(startDate, endDate);
		PageInfo<Map<String, Object>> pageInfo=attendCalculateService.selectPageAttendCalculateDetail(dates,pageNum,pageSize);
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("pageNum", pageInfo.getPageNum());
		map.put("totalNum", pageInfo.getTotal());
		map.put("pageSize", pageInfo.getPageSize());
		map.put("attendDetail",pageInfo.getList());
		if(pageInfo.getList()!=null && pageInfo.getList().size()>0){
			result=Result.responseSuccess("查询成功！",map);
		}else{
			result=Result.responseSuccess("查询成功，暂无数据！",map);
		}
		return result;
	}
	
	/**
	 * 
	* @Title: selectSignMissAttendDetail 
	* @Description: 查询用户缺卡情况 
	* @param dateStr
	* @param empId
	* @return
	* @throws Exception Result
	* @author xya
	* @date 2019年5月30日下午6:40:15
	 */
	@RequestMapping("/selectSignMissAttendDetail")
	public Result selectSignMissAttendDetail (String yearMonth,String empId)throws Exception{
		Result result=null;
		
		if(StrKit.isBlank(yearMonth)){
			return Result.responseError("日期不能为空！");
		}else if(StrKit.isBlank(empId)){
			return Result.responseError("员工id不能为空！");
		}
		List<Map<String, Object>> missSignList=attendCalculateService.selectSignMissAttendDetail(yearMonth,empId);
		if(missSignList!=null && missSignList.size()>0){
			result=Result.responseSuccess("查询成功！",missSignList);
		}else{
			result=Result.responseSuccess("查询成功，暂无数据！",missSignList);
		}
		return result;
	}
}
