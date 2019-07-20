/**
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 *
 * @Package: com.faceRecog.manage.controller
 * @author: Administrator
 * @date: 2019年5月17日 下午1:45:02
 */
package com.faceRecog.manage.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.faceRecog.manage.service.AttendGroupService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;

/**
 * @ClassName: AttendGroupController
 * @Description: 考勤组
 * @author: xya
 * @date: 2019年5月17日 下午1:45:02
 */

@RestController
public class AttendGroupController {


	
	@Autowired
	private AttendGroupService attendGroupService;
	/**
	 * 
	* @Title: selectDepartEmployeeTree 
	* @Description: 查询部门员工树结构 
	* @return Result
	* @author xya
	* @date 2019年5月17日下午1:48:06
	 */
	@RequestMapping("/selectDepartEmployeeTree")
	public Result selectDepartEmployeeTree()throws Exception{
		Result result=null;
		Map<String, Object> deptMap=attendGroupService.selectDepartEmployeeTree();
		if(deptMap!=null){
			result=Result.responseSuccess("查询成功！",deptMap);
		}else{
			result=Result.responseSuccess("查询成功，暂无数据！",deptMap);
		}
		return result;
		
	}
	
	
	/**
	 * 
	* @Title: insertAttendGroup 
	* @Description: 新增考勤组 
	* @return
	* @throws Exception Result
	* @author xya
	* @date 2019年5月17日下午3:10:42
	 */
	@RequestMapping("/insertOrUpdateAttendGroup")
	public Result insertOrUpdateAttendGroup(String scheduleIds,String periodIds,
			String attendGroupName,String leaderIds,String empIds,String deptIds,
			String agId,String addEmps,String effective,String type)throws Exception{
		Result result=null;
		
		if(StrKit.isBlank(attendGroupName)){
			return Result.responseError("考勤组名称不能为空！");
		}else if(StrKit.isBlank(leaderIds)){
			return Result.responseError("负责人不能为空！");
		}else if(StrKit.isBlank(scheduleIds) && StrKit.isBlank(periodIds)){
			return Result.responseError("请选择周期或班次！");
		}else if(StrKit.isBlank(empIds) && StrKit.isBlank(deptIds)){
			return Result.responseError("请选择部门或员工！");
		}else if(StrKit.isBlank(type)){
			return Result.responseError("班制类型不能为空！");
		}
		// 判断考情组名称是否被占用
		int count=attendGroupService.selectAttendGroupNameExist(attendGroupName,agId);
		if(count>0){
			return Result.responseError("考勤组名称被占用！");
		}else{
			result=attendGroupService.insertOrUpdateAttendGroup(scheduleIds,periodIds,attendGroupName,
					leaderIds,empIds,deptIds,agId,effective,type);
		}
		return result;
		
	}
	
	
	/**
	 * 
	* @Title: selectAttendGroup 
	* @Description:  查询所有的考勤组 
	* @return
	* @throws Exception Result
	* @author xya
	* @date 2019年5月20日上午9:17:25
	 */
	@RequestMapping("/selectAllAttendGroup")
	public Result selectAllAttendGroup()throws Exception{
		Result result=null;
		
		List<Map<String, Object>> attendGroupList=attendGroupService.selectAllAttendGroup();
		if(attendGroupList!=null && attendGroupList.size()>0 && attendGroupList.get(0).containsKey("id")){
			result=Result.responseSuccess("查询成功！",attendGroupList);
		}else{
			result=Result.responseSuccess("查询成功，赞无数数据！",attendGroupList);
		}
		return result;
		
	}
	
	
	/**
	 * 
	* @Title: deleteAttendGroup 
	* @Description: 删除考勤组 
	* @return
	* @throws Exception Result
	* @author xya
	* @date 2019年5月20日下午7:59:44
	 */
	@RequestMapping("/deleteAttendGroup")
	public Result deleteAttendGroup(String agId)throws Exception{
		Result result=null;
		
		if(StrKit.isBlank(agId)){
			return Result.responseError("考勤组id不能为空！");
		}
		int affectNum=attendGroupService.deleteAttendGroup(agId);
		if(affectNum>0){
			result=Result.responseSuccess("删除成功！");
		}else{
			result=Result.responseError("删除失败！");
		}
		return result;
		
	}
	
	/**
	 * 
	* @Title: updateEmpAttendGroup 
	* @Description: 修改考勤组人员 
	* @param agId
	* @return
	* @throws Exception Result
	* @author xya
	* @date 2019年5月20日下午8:48:58
	 */
	/*@RequestMapping("/updateEmpAttendGroup")
	public Result updateEmpAttendGroup(String agId,String empIds)throws Exception{
		Result result=null;
		String[] empId=empIds.split(",");
		if(StrKit.isBlank(agId)){
			return Result.responseError("考勤组id不能为空！");
		}else if(StrKit.isBlank(empIds)){
			return Result.responseError("人员id不能为空！");
		}
		List<Map<String, Object>> tmp = new ArrayList<Map<String, Object>>();
        for (String str:empId) {
            if (str!=null && str.length()!=0) {
            	Map<String, Object> map=new HashMap<String, Object>();
            	map.put("empId", str);
            	map.put("id", UUIDGenerator.getUUID());
                tmp.add(map);
            }
        }
        List<Map<String, Object>> empIdLs = tmp;
        if (empId.length == 0) {
            return Result.responseError("人员id不能为空！");
        }
        int affectNum = attendGroupService.updateEmpAttendGroup(agId, empIdLs);
        if (affectNum > 0) {
            result = Result.responseSuccess("删除成功！");
        } else {
            result = Result.responseError("删除失败！");
        }
        return result;

    }
*/
    /**
     * @return
     * @throws Exception Result
     * @Title: selectAttendGroup
     * @Description: 回显考勤组信息
     * @author xya
     * @date 2019年5月21日上午10:21:50
     */
    @RequestMapping("/selectAttendGroup")
    public Result selectAttendGroup(String agId) throws Exception {
        Result result = null;
        if (StrKit.isBlank(agId)) {
            return Result.responseError("考勤组id不能为空！");
        }
        Map<String, Object> attendGroupInfo = attendGroupService.selectAttendGroup(agId);
        if (attendGroupInfo != null) {
            result = Result.responseSuccess("查询成功！", attendGroupInfo);
        } else {
            result = Result.responseSuccess("查询成功，暂无考勤组数据！", attendGroupInfo);
        }
        return result;
    }

    /**
     * @param agId
     * @return
     * @throws Exception Result
     * @Title: selectAttendGroupAttendDance
     * @Description: 查询考勤组班次和周期班次信息
     * @author xya
     * @date 2019年5月22日上午11:08:11
     */
    @RequestMapping("/selectAttendGroupAttendDance")
    public Result selectAttendGroupAttendDance(String agId) throws Exception {
        Result result = null;
        if (StrKit.isBlank(agId)) {
            return Result.responseError("考勤组id不能为空！");
        }
        Map<String, Object> agAttendDance = attendGroupService.selectAttendGroupAttendDance(agId);
        if (agAttendDance != null) {
            result = Result.responseSuccess("查询成功！", agAttendDance);
        } else {
            result = Result.responseSuccess("查询成功，暂无考勤组数据！", agAttendDance);
        }
        return result;
    }
}
