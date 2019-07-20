/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service.impl 
 * @author: Administrator   
 * @date: 2019年5月17日 下午1:49:48 
 */
package com.faceRecog.manage.service.impl;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.socket.TextMessage;

import com.faceRecog.manage.handler.SpringWebSocketHandler;
import com.faceRecog.manage.mapper.AttendDetailMapper;
import com.faceRecog.manage.mapper.AttendGroupAttendDanceMapper;
import com.faceRecog.manage.mapper.AttendGroupLeaderMapper;
import com.faceRecog.manage.mapper.AttendGroupMapper;
import com.faceRecog.manage.mapper.AttendGroupPeriodMapper;
import com.faceRecog.manage.mapper.DepartmentMapper;
import com.faceRecog.manage.mapper.EmpAttendGroupMapper;
import com.faceRecog.manage.mapper.EmployeeMapper;
import com.faceRecog.manage.mapper.EquipmentMapper;
import com.faceRecog.manage.mapper.InstructionRecMapper;
import com.faceRecog.manage.mapper.PeriodAttendMapper;
import com.faceRecog.manage.mapper.PeriodMapper;
import com.faceRecog.manage.model.AttendDetail;
import com.faceRecog.manage.model.AttendGroup;
import com.faceRecog.manage.model.AttendGroupAttendDance;
import com.faceRecog.manage.model.AttendGroupLeader;
import com.faceRecog.manage.model.AttendGroupPeriod;
import com.faceRecog.manage.model.Attendance;
import com.faceRecog.manage.model.Department;
import com.faceRecog.manage.model.EmpAttendGroup;
import com.faceRecog.manage.model.Employee;
import com.faceRecog.manage.model.Equipment;
import com.faceRecog.manage.model.InstructionRec;
import com.faceRecog.manage.model.Period;
import com.faceRecog.manage.model.PeriodAttend;
import com.faceRecog.manage.model.User;
import com.faceRecog.manage.service.AttendGroupService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;

import net.sf.json.JSONObject;

/** 
 * @ClassName: AttnedGroupServiceImpl 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月17日 下午1:49:48  
 */
@Service
public class AttendGroupServiceImpl implements AttendGroupService{

	@Autowired
	private AttendGroupMapper attendGroupMapper;
	
	@Autowired
	private DepartmentMapper departmentMapper;
	
	@Autowired
	private EmployeeMapper employeeMapper;
	
	@Autowired
	private AttendGroupPeriodMapper attendGroupPeriodMapper;
	
	@Autowired
	private AttendGroupLeaderMapper attendGroupLeaderMapper;
	
	@Autowired
	private AttendGroupAttendDanceMapper attendGroupAttendDanceMapper;// 考勤组班次
	
	@Autowired
	private EmpAttendGroupMapper empAttendGroupMapper;
	
	@Autowired
	private PeriodMapper periodMapper;

	@Autowired
	private PeriodAttendMapper periodAttendMapper;
	
	@Autowired
	private AttendDetailMapper attendDetailMapper;
	
	@Autowired
    private SpringWebSocketHandler websocket;
    
    @Autowired
    private InstructionRecMapper instructionRecMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;
	/* (non Javadoc) 
	 * @Title: selectDepartEmployeeTree
	 * @Description: TODO
	 * @return 
	 * @see com.faceRecog.manage.service.AttendGroupService#selectDepartEmployeeTree() 
	 */ 
	@Override
	public Map<String, Object> selectDepartEmployeeTree() throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String, Object> childMap=new HashMap<String, Object>();
		List<Map<String, Object>> listChild=new ArrayList<Map<String, Object>>();
		//先查询公司所有的部门
		//查询最顶级的部门信息
		Department department=departmentMapper.selectFirstDepart();
		childMap.put("id", department.getId());
		childMap.put("name", department.getDeptName());
		childMap.put("type", "dept");
		setChildTree(childMap);
		listChild.add(childMap);
		map.put("children", listChild);
		return map;
	}

	
	public Map<String, Object> setChildTree(Map<String, Object> map){
		Map<String , Object> childMap=new HashMap<String , Object>();
		List<Map<String, Object>> listChild=new ArrayList<Map<String, Object>>();
		map.put("children", listChild);
		//查询部门下的员工
		List<Employee> empList=employeeMapper.selectEmployeeByDeptId((String)map.get("id"));
		if(empList!=null && empList.size()>0 && StrKit.notBlank(empList.get(0).getId())){
			for(Employee emp:empList){
				childMap= new HashMap<String , Object>();
				childMap.put("id", emp.getId());
				childMap.put("name", emp.getName());
				childMap.put("type", "emp");
				listChild.add(childMap);
			}
		}
		
		//查询下级部门
		List<Department> deptList=departmentMapper.selectAllChildDepartByParentId((String)map.get("id"));
		if(deptList!=null && deptList.size()>0 && StrKit.notBlank(deptList.get(0).getId())){
			for(Department dept:deptList){
				childMap= new HashMap<String , Object>();
				childMap.put("id", dept.getId());
				childMap.put("name", dept.getDeptName());
				childMap.put("type", "dept");
				listChild.add(childMap);
				setChildTree(childMap);
			}
		}
		
		return map;
	}


	/* (non Javadoc) 
	 * @Title: insertAttendGroup
	 * @Description: TODO
	 * @param scheduleIds
	 * @param periodIds
	 * @param attendGroupName
	 * @param leaderIds
	 * @param empIds
	 * @param deptIds
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.AttendGroupService#insertAttendGroup(java.lang.String[], java.lang.String[], java.lang.String, java.lang.String[], java.lang.String[], java.lang.String[]) 
	 */ 
	@Override
	@Transactional
	public Result insertOrUpdateAttendGroup(String scheduleIds, String periodIds, String attendGroupName, String leaderIds,
			String empIds, String deptIds,String agId,String effective,String type) throws Exception {
		Result result=null;
		int affectNum=0;
		int weekday=0;
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM");
		Date currDate =new Date();// 当前传入时间
		String today=format.format(currDate);// 今天年月日
		String yearMonth=fmt.format(currDate);// 今天的年月
		Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        weekday=c.get(Calendar.DAY_OF_WEEK)-1;// 当前日期星期几
        if(weekday==0){// 0为礼拜天
        	weekday=7;
        }
		c.add(Calendar.DATE, 1);
		String tomorrow=format.format(c.getTime());// 明天
		
		String[] periodIdStr=periodIds.split(",");
		String[] scheduleIdStr=scheduleIds.split(",");
		String[] leaderIdStr=leaderIds.split(",");
		String[] empIdStr=empIds.split(",");
		String[]deptIdStr=deptIds.split(",");
		
		//string 数组去除空字符
		List<String> tmp = new ArrayList<String>();
        for (String str:periodIdStr) {
            if (str!=null && str.length()!=0) {
                tmp.add(str);
            }
        }
        List<String> periodIdLs = tmp;
        tmp = new ArrayList<String>();
        for (String str:scheduleIdStr) {
            if (str!=null && str.length()!=0) {
                tmp.add(str);
            }
        }
        List<String> scheduleIdLs = tmp;
        tmp = new ArrayList<String>();
        for (String str:leaderIdStr) {
            if (str!=null && str.length()!=0) {
                tmp.add(str);
            }
        }
        List<String> leaderIdLs = tmp;
        tmp = new ArrayList<String>();
        for (String str:empIdStr) {
            if (str!=null && str.length()!=0) {
                tmp.add(str);
            }
        }
        List<String> empIdLs = tmp;
        tmp = new ArrayList<String>();
        for (String str:deptIdStr) {
            if (str!=null && str.length()!=0) {
                tmp.add(str);
            }
        }
        List<String> deptIdLs = tmp;
        if(StrKit.isBlank(agId)){//考勤组id为空新增
        	AttendGroup attendGroup=new  AttendGroup();
    		attendGroup.setId(UUIDGenerator.getUUID());
    		attendGroup.setCreateTime(new Date());
    		attendGroup.setName(attendGroupName);
    		attendGroup.setStatus("1");
    		attendGroup.setType(type);
    		affectNum=attendGroupMapper.insertSelective(attendGroup);
    		if(affectNum>0){
    			//新增考勤考勤负责人中间
    			if(leaderIdLs.size()>0){
    				for(String leaderId:leaderIdLs){
    					AttendGroupLeader attendGroupLeader=new AttendGroupLeader();
    					attendGroupLeader.setAgId(attendGroup.getId());
    					attendGroupLeader.setEmpId(leaderId);
    					attendGroupLeader.setId(UUIDGenerator.getUUID());
    					affectNum=attendGroupLeaderMapper.insertSelective(attendGroupLeader);
    					if(affectNum<0){
    	    				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    	    				return Result.responseError("新增失败！");
    	    			}
    				}
    			}else{
    				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    				return Result.responseError("请选择考勤组负责人！");
    			}
    			if(affectNum>0){
    				if(scheduleIdLs.size()==0 && periodIdStr.length==0){
    					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    					return Result.responseError("请选择班次或周期！");
    				}
    				// 新增班次考勤组中间数据
    				if(scheduleIdLs.size()>0){
    					for(String scheduleId:scheduleIdLs){
    						AttendGroupAttendDance agd=new AttendGroupAttendDance();
        					agd.setAdId(scheduleId);
        					agd.setAgId(attendGroup.getId());
        					agd.setId(UUIDGenerator.getUUID());
        					affectNum=attendGroupAttendDanceMapper.insertSelective(agd);
    					}
    				}
    				
    				if(affectNum>0){
    					if(deptIdLs.size()==0 && empIdLs.size()==0){
    							TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    							return Result.responseError("请选择员工或者部门！");
    					}
    					//如果部门id不为空，查询出部门下的员工添加到员工数组里 进行批量操作
    					if(deptIdLs.size()>0){
    						List<Employee> empList=employeeMapper.selectBatchEmpByDeptId(deptIdLs);
    						if(empList!=null && empList.size()>0 && StrKit.notBlank(empList.get(0).getId())){
    							for(int i=0;i<empList.size();i++){
    				            	empIdLs.add(empList.get(i).getId());
    							}
    						}
    					}
    					
    					
    	    			/*//重新计算新增后考勤组人员的考勤结算数据
    	    			//........
    					for(Map<String, Object> empMap:empIdLs){
    						EmpAttendGroup empAttendGroup=empAttendGroupMapper.selectEmpAttendGroupByEmpId((String)empMap.get("empId"));
    						if(empAttendGroup!=null){
    							affectNum= calculateNewEmmp(empAttendGroup.getEmpId(),agId);
    						}
    					}*/
    					// 删除原有的员工和考勤组的绑定关系 重新添加
    					affectNum=empAttendGroupMapper.deleteBatchEmpAttendGroupByEmpId(empIdLs);
    	    			if(affectNum<0){
    	    				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    	        			return Result.responseError("修改失败！");
    	    				
    	    			}
    					//新增员工考勤组表
    					if(empIdLs.size()>0){
    						for(String empId:empIdLs){
    							EmpAttendGroup empAttendGroup=new EmpAttendGroup();
    							empAttendGroup.setAgId(attendGroup.getId());
    							empAttendGroup.setApplyTime(today);
    							empAttendGroup.setEmpId(empId);
    							empAttendGroup.setId(UUIDGenerator.getUUID());
    							affectNum=empAttendGroupMapper.insertSelective(empAttendGroup);
    						}
    						
    					}
    					if(CommonConstant.固定班.getValue().equals(type)){ // 固定班制还需添加周期
    						Period period=new Period();
    						period.setId(UUIDGenerator.getUUID());
    				        period.setCreateTime(new Date());
    				        period.setCreateUserId("1");
    				        period.setStatus("1");
    				        period.setDayNum(7);
    				        period.setType(type);// 固定班周期
    				        period.setPeriodName(attendGroupName);
    				        int insertNum = periodMapper.insertSelective(period);
    				        if(insertNum<0){
    				        	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    	    					return Result.responseError("新增失败！");
    				        }
    				        AttendGroupPeriod attendGroupPeriod=new AttendGroupPeriod();
    				        attendGroupPeriod.setAgId(attendGroup.getId());
    				        attendGroupPeriod.setId(UUIDGenerator.getUUID());
    				        attendGroupPeriod.setPrId(period.getId());
    				        affectNum=attendGroupPeriodMapper.insertSelective(attendGroupPeriod);
    				        if(insertNum<0){
    				        	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    	    					return Result.responseError("新增失败！");
    				        }
    				        if(scheduleIdLs!=null && scheduleIdLs.size()>0 && scheduleIdLs.size()==7){
    				        	// 新增周期班次中间表
    				        	for(int i=0;i<scheduleIdLs.size();i++){
    				        		String scheduleId=scheduleIdLs.get(i);
    				        		PeriodAttend periodAttend=new PeriodAttend();
    				        		periodAttend.setId(UUIDGenerator.getUUID());
    				        		periodAttend.setPeriodId(period.getId());
    				        		periodAttend.setSort(i+1);
    				        		periodAttend.setAttendId(scheduleId);
    				        		affectNum=periodAttendMapper.insertSelective(periodAttend);
    				        		if(insertNum<0){
    	    				        	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    	    	    					return Result.responseError("新增失败！");
    	    				        }
    				        	}
    				        }else{
    				        	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    	    					return Result.responseError("参数错误！");
    				        }
    					}else if(CommonConstant.排班制.getValue().equals(type)){
    						if(periodIdStr.length>0){//新增周期考勤组中间数据
    							for(String periodId:periodIdStr){
    								AttendGroupPeriod attendGroupPeriod=new AttendGroupPeriod();
    								attendGroupPeriod.setAgId(attendGroup.getId());
    								attendGroupPeriod.setId(UUIDGenerator.getUUID());
    								attendGroupPeriod.setPrId(periodId);
    								affectNum=attendGroupPeriodMapper.insertSelective(attendGroupPeriod);
    							}
    	    				}
    					}
    				}else{
    					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    					return Result.responseError("新增失败！");
    				}
    			}else{
    				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    				return Result.responseError("新增失败！");
    			}
    		}else{
    			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    			return Result.responseError("新增失败！");
    		}
    		result=Result.responseSuccess("新增成功！",attendGroup.getId());
        }else{// 考勤组id不为空修改
        	AttendGroup attendGroup=new  AttendGroup();
    		attendGroup.setId(agId);
    		attendGroup.setCreateTime(new Date());
    		attendGroup.setName(attendGroupName);
    		attendGroup.setStatus("1");
    		attendGroup.setUpdateTime(new Date());
    		affectNum=attendGroupMapper.updateByPrimaryKeySelective(attendGroup);
    		if(affectNum>0){
    			//删除考勤组班次
    			affectNum=attendGroupAttendDanceMapper.deleteAttendGroupAttendDanceByAgId(agId);
    			if(affectNum<0){
    				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        			return Result.responseError("修改失败！");
    				
    			}
    			// 删除考勤组负责人
    			affectNum=attendGroupLeaderMapper.deleteAttendGroupLeaderByAgId(agId);
    			if(affectNum<0){
    				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        			return Result.responseError("修改失败！");
    				
    			}
    			
    			// 新增考勤考勤负责人中间
    			if(leaderIdLs.size()>0){
    				for(String leaderId:leaderIdLs){
    					AttendGroupLeader attendGroupLeader=new AttendGroupLeader();
    					attendGroupLeader.setAgId(agId);
    					attendGroupLeader.setEmpId(leaderId);
    					attendGroupLeader.setId(UUIDGenerator.getUUID());
    					affectNum=attendGroupLeaderMapper.insertSelective(attendGroupLeader);
    					if(affectNum<0){
    	    				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    	    				return Result.responseError("新增失败！");
    	    			}
    				}
    			}
    			if(affectNum>0){
    				if(scheduleIdLs.size()==0 && periodIdStr.length==0){
    					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    					return Result.responseError("请选择班次或周期！");
    				}
    				// 新增班次考勤组中间数据
    				if(scheduleIdLs.size()>0){
    					for(String scheduleId:scheduleIdLs){
    						AttendGroupAttendDance agd=new AttendGroupAttendDance();
        					agd.setAdId(scheduleId);
        					agd.setAgId(agId);
        					agd.setId(UUIDGenerator.getUUID());
        					affectNum=attendGroupAttendDanceMapper.insertSelective(agd);
    					}
    				}
    				
    				if(affectNum>0){
    					if(deptIdLs.size()==0 && empIdLs.size()==0){
    							TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    							return Result.responseError("请选择员工或者部门！");
    					}
    					// 如果部门id不为空，查询出部门下的员工添加到员工数组里 进行批量操作
    					if(deptIdLs.size()>0){
    						List<Employee> empList=employeeMapper.selectBatchEmpByDeptId(deptIdLs);
    						if(empList!=null && empList.size()>0 && StrKit.notBlank(empList.get(0).getId())){
    							for(int i=0;i<empList.size();i++){
    				            	empIdLs.add(empList.get(i).getId());
    							}
    						}
    					}
    					// 看看是立即生效还是明天生效
    					// 重新计算修改后 考勤组人员的考勤结算数据
    	    			// 查询修改前考勤组的所有员工
    	    			List<Map<String, Object>> oldEmpAgList=empAttendGroupMapper.selectEmpInfoByAgId(agId);
    	    			for(String empId:empIdLs){
    	    				int apearNum=0;
    	    				// 对比现有数据库中该考勤组员工和修改后是否有新加入的员工
    	    				for(Map<String, Object> oldEmpMap:oldEmpAgList){
    	    					if(empId.equals(oldEmpMap.get("empId"))){
    	    						// 计算修改后的员工是否出现在现有的考勤组里 如果出现说明是原有的员工 没有出现说明是新加入考勤组员工
    	    						++apearNum;
    	    					}
    	    				}
    	    				// 出现次数为0为新加入员工 重新计算考勤  
    	    				if(apearNum==0){
    	    					if("tomorrow".equals(effective)){ // 明天生效 保存生效时间
    	    						//员工修改前所属的考勤组类型
    	    						AttendGroup attendGroupRec=attendGroupMapper.selectEmpAttendGroupType(empId);
    	    						if(attendGroupRec!=null){
    	    							if(attendGroupRec.getType().equals(CommonConstant.固定班.getValue())){
        	    							// 明天生效 将今天的考勤明细保存到考勤明细表中
        	    							// 查询固定班制员工的所属周期 根据员工id
        	    			        		AttendGroupPeriod attendGroupPeriod=attendGroupPeriodMapper.selectPeriodByEmpId(empId);
        	    			        		//查询周期排班班次
        	    			        		List<Map<String, Object>> periodAttendList=periodAttendMapper.selectPeriodAttendDetailByPrIdAndSortCms(attendGroupPeriod.getPrId(),weekday,today);
        	    			        		AttendDetail attendDetail=new AttendDetail();
        	    			        		attendDetail.setId(UUIDGenerator.getUUID());
        	    			        		attendDetail.setDateStr(today);
        	    							attendDetail.setCreateTime(new Date());
        	    							attendDetail.setYearMonthStr(yearMonth);
        	    							attendDetail.setAttendId((String)periodAttendList.get(0).get("id"));
        	    							affectNum=attendDetailMapper.insertSelective(attendDetail);
        	    						}
    	    						}
    	    					}
    	    				}
    	    			}
    	    			if(affectNum<0){
    	    				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    	        			return Result.responseError("修改失败！");
    	    				
    	    			}
    	    			// 删除原有的员工和考勤组的绑定关系 重新添加
    					affectNum=empAttendGroupMapper.deleteBatchEmpAttendGroupByEmpId(empIdLs);
    					if(affectNum<0){
    	    				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    	        			return Result.responseError("修改失败！");
    	    				
    	    			}
    					affectNum=empAttendGroupMapper.deleteEmpAttendGroupByAgId(agId);
    					if(affectNum<0){
    	    				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    	        			return Result.responseError("修改失败！");
    	    				
    	    			}
    					
    					if(empIdLs.size()>0){
    						String timeParam="";
    						// 判断考勤生效时间 立即生效还是明天生效
	    					if("today".equals(effective)){ // 立即生效 保存生效时间
	    						timeParam=today;
	    					}else if("tomorrow".equals(effective)){ // 明天生效 保存生效时间
	    						// 明天生效 修改从明天开始的排班明细
	    						timeParam=tomorrow;
	    					}else{
	    						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    						return Result.responseError("参数错误！");
	    					}
	    					for(String empId:empIdLs){
    							EmpAttendGroup empAttendGroup=new EmpAttendGroup();
    							empAttendGroup.setAgId(attendGroup.getId());
    							empAttendGroup.setApplyTime(timeParam);
    							empAttendGroup.setEmpId(empId);
    							empAttendGroup.setId(UUIDGenerator.getUUID());
    							affectNum=empAttendGroupMapper.insertSelective(empAttendGroup);
    						}
    					}
    					
    					// 删除周期班次中间表数据
    					if(CommonConstant.固定班.getValue().equals(type)){ // 固定班制还需添加周期
    						  if(periodIdLs.size()==0){
    							  TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    							  return Result.responseError("周期id不能为空！");
    						  }
    						 affectNum=periodAttendMapper.deletePeriodAttendByPeriodId(periodIdLs.get(0));
    						 if(affectNum<0){
    							 	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    		    					return Result.responseError("修改失败！");
    						 }
							if(scheduleIdLs!=null && scheduleIdLs.size()>0 && scheduleIdLs.size()==7){
								// 新增周期班次中间表
								for(int i=0;i<scheduleIdLs.size();i++){
									String scheduleId=scheduleIdLs.get(i);
									PeriodAttend periodAttend=new PeriodAttend();
									periodAttend.setId(UUIDGenerator.getUUID());
									periodAttend.setPeriodId(periodIdLs.get(0));
									periodAttend.setSort(i+1);
									periodAttend.setAttendId(scheduleId);
									affectNum=periodAttendMapper.insertSelective(periodAttend);
									if(affectNum<0){
								    	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
										return Result.responseError("新增失败！");
								    }
								}
     				        }else{
     				        	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
     	    					return Result.responseError("参数错误！");
     				        }
    					}else{
    						// 删除考勤组周期
    		    			affectNum=attendGroupPeriodMapper.deleteAttendGroupPeriod(agId);
    		    			if(affectNum<0){
    		    				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    		        			return Result.responseError("修改失败！");
    		    				
    		    			}
    		    			// 新增周期考勤组中间数据
    		    			for(String periodId:periodIdLs){
								AttendGroupPeriod attendGroupPeriod=new AttendGroupPeriod();
								attendGroupPeriod.setAgId(attendGroup.getId());
								attendGroupPeriod.setId(UUIDGenerator.getUUID());
								attendGroupPeriod.setPrId(periodId);
								affectNum=attendGroupPeriodMapper.insertSelective(attendGroupPeriod);
							}
    					}
    				}else{
    					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    					return Result.responseError("修改失败！");
    				}
    			}else{
    				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    				return Result.responseError("修改失败！");
    			}
    		}
    		result=Result.responseSuccess("修改成功！",agId);
        }
        
        // 获取当前登入人员的id
        User user=(User) SecurityUtils.getSubject().getPrincipal();
        String usId="";
        if(user!=null){
        	usId=user.getId();
        }else{
        	usId="1";
        }
        List<Equipment> eqList=equipmentMapper.selectAllEquipmentByUsId(usId);
        if(eqList!=null && eqList.size()>0 && eqList.get(0)!=null){
        	for(Equipment equipment: eqList){
        		// 新增指令发送记录 发送socket消息给设备端
                InstructionRec instructionRec = new InstructionRec();
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setCreateTime(new Date());
                instructionRec.setStatus("-1");
                instructionRec.setSn(equipment.getSn());
                instructionRec.setInstruction(CommonConstant.UPDATE_ATTENDANCE.getValue());
                affectNum = instructionRecMapper.insertSelective(instructionRec);
                if (affectNum < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return Result.responseError(result.getMsg().substring(0, 2)+"失败！");
                }
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_ATTENDANCE.getValue()));
                jsonObj.put("id", instructionRec.getId());
                jsonObj.put("sendTime", new Date().getTime());
                //发送指令
                websocket.sendMessageToUser(equipment.getSn(), new TextMessage(jsonObj.toString()));
        	}
        }
		
		return result;
	}


	/* (non Javadoc) 
	 * @Title: selectAttendGroup
	 * @Description: TODO
	 * @return 
	 * @see com.faceRecog.manage.service.AttendGroupService#selectAttendGroup() 
	 */ 
	@Override
	public List<Map<String, Object>> selectAllAttendGroup() throws Exception{
		 
		return attendGroupMapper.selectAllAttendGroup();
	}


	/* (non Javadoc) 
	 * @Title: deleteAttendGroup
	 * @Description: TODO
	 * @param agId
	 * @return 
	 * @see com.faceRecog.manage.service.AttendGroupService#deleteAttendGroup(java.lang.String) 
	 */ 
	@Override
	@Transactional
	public int deleteAttendGroup(String agId) throws Exception{
		int affectNum=0;
		
		affectNum=attendGroupLeaderMapper.deleteAttendGroupLeaderByAgId(agId);
		if(affectNum<0){
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return -1;
		}
		affectNum=empAttendGroupMapper.deleteEmpAttendGroupByAgId(agId);
		if(affectNum<0){
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return -1;
		}
		// 获取当前登入人员的id
        User user=(User) SecurityUtils.getSubject().getPrincipal();
        String usId="";
        if(user!=null){
        	usId=user.getId();
        }else{
        	usId="1";
        }
        List<Equipment> eqList=equipmentMapper.selectAllEquipmentByUsId(usId);
        if(eqList!=null && eqList.size()>0 && eqList.get(0)!=null){
        	for(Equipment equipment: eqList){
        		// 新增指令发送记录 发送socket消息给设备端
                InstructionRec instructionRec = new InstructionRec();
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setCreateTime(new Date());
                instructionRec.setStatus("-1");
                instructionRec.setSn(equipment.getSn());
                instructionRec.setInstruction(CommonConstant.UPDATE_FACE.getValue());
                affectNum = instructionRecMapper.insertSelective(instructionRec);
                if (affectNum < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return -1;
                }
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                jsonObj.put("code", Integer.valueOf(CommonConstant.UPDATE_FACE.getValue()));
                jsonObj.put("id", instructionRec.getId());
                jsonObj.put("sendTime", new Date().getTime());
                //发送指令
                websocket.sendMessageToUser(equipment.getSn(), new TextMessage(jsonObj.toString()));
        	}
        }
		return attendGroupMapper.deleteByPrimaryKey(agId);
	}


	/* (non Javadoc) 
	 * @Title: updateEmpAttendGroup
	 * @Description: TODO
	 * @param agId
	 * @param empId
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.AttendGroupService#updateEmpAttendGroup(java.lang.String, java.lang.String[]) 
	 */ 
	/*@Override
	@Transactional
	public String updateEmpAttendGroup(String agId, List<Map<String, Object>> empIdLs) throws Exception {
		int affectNum=0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date currDate =new Date();// 当前传入时间
		String today=format.format(currDate);// 今天
		
		affectNum=empAttendGroupMapper.deleteEmpAttendGroupByAgId(agId);
		if(affectNum>0){
			affectNum=empAttendGroupMapper.insertBatchEmpAttendGroup(agId, empIdLs,today);
		}else{
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return -1;
		}
		return "";
	}*/


	/* (non Javadoc) 
	 * @Title: selectAttendGroup
	 * @Description: TODO
	 * @param agId
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.AttendGroupService#selectAttendGroup(java.lang.String) 
	 */ 
	@Override
	public Map<String, Object> selectAttendGroup(String agId) throws Exception {
		 Map<String, Object> agMap=new HashMap<String, Object>();
		 AttendGroup attendGroup=attendGroupMapper.selectByPrimaryKey(agId);
		 if(attendGroup!=null){
			 //查询周期
			 AttendGroupPeriod agPeriod=attendGroupPeriodMapper.selectAgPeriodByAgId(agId);
			 if(CommonConstant.固定班.getValue().equals(attendGroup.getType())){
				List<Attendance> attendanceList=periodAttendMapper.selectPeriodAttendByAgId(agId);
				agMap.put("periodAttendLst", attendanceList!=null && attendanceList.size()>0?attendanceList:new ArrayList<>());
			 }else if(CommonConstant.排班制.getValue().equals(attendGroup.getType())){
				//查询班次信息
				 AttendGroupAttendDance agDance=attendGroupAttendDanceMapper.selectAttendDanceByAgId(agId);
				 agMap.put("agDance", agDance==null?"":agDance.getAdId());
			 }
			 //查询考勤组负责人
			 AttendGroupLeader agLader=attendGroupLeaderMapper.selectAgLeaderByAgId(agId);
			 //查询考勤组人员信息
			 EmpAttendGroup empAg=empAttendGroupMapper.selectEmpAgInfoByAgId(agId);
			 
			 agMap.put("agLeader", agLader==null?"":agLader.getEmpId());
			 agMap.put("agPeriod",agPeriod==null?"": agPeriod.getPrId());
			 
			 agMap.put("agEmp", empAg==null?"":empAg.getEmpId());
			 agMap.put("type", attendGroup.getType());
		 }else{
			 return null;
		 }
		 agMap.put("agName", attendGroup.getName());
		 agMap.put("agId", agId);
		return agMap;
	}


	/* (non Javadoc) 
	 * @Title: selectAttendGroupAttendDance
	 * @Description: TODO
	 * @param agId
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.AttendGroupService#selectAttendGroupAttendDance(java.lang.String) 
	 */ 
	@Override
	public Map<String, Object> selectAttendGroupAttendDance(String agId) throws Exception {
		Map<String, Object> map =new HashMap<String, Object>();
		Map<String, Object> commonMap =new HashMap<String, Object>();
		List<Map<String, Object>> scheduleList=new ArrayList<Map<String, Object>>();
		
		List<Period> periodList=periodMapper.selectPeriodInfoByAgId(agId);
		//查询周期排班班次
		if(periodList!=null && periodList.size()>0 && periodList.get(0)!=null){
			for(Period period: periodList){
				Map<String, Object> scheduleMap =new HashMap<String, Object>();
				List<Map<String, Object>> attendGroupPeriodLst=attendGroupPeriodMapper.selectAgPeriodInfoByAgId(period.getId());
				scheduleMap.put("atDance", attendGroupPeriodLst==null || attendGroupPeriodLst.get(0)==null?new ArrayList<>():attendGroupPeriodLst);
				scheduleMap.put("name", period.getPeriodName());
				scheduleList.add(scheduleMap);
			}
		}
		//查询常规班次
		List<Map<String, Object>> agAttendDnaceLst=attendGroupAttendDanceMapper.selectAttendDanceInfoByAgId(agId);
		commonMap.put("atDance", agAttendDnaceLst!=null && agAttendDnaceLst.get(0)!=null?agAttendDnaceLst:new ArrayList<>());
		map.put("schedule", scheduleList);
		map.put("common",commonMap);
		return map;
	}
	
	
	
	/*//**
	 * 
	* @Title: updateAttendDetailStartWithTomorrow 
	* @Description: 修改员工的考勤明细从明天开始 
	* @return int
	* @author xya
	* @date 2019年5月28日下午2:37:26
	 */
	public int updateAttendDetailStartWithTomorrow(String empId){
		
		return 0;
	}
	
	

	/* (non Javadoc) 
	 * @Title: selectAttendGroupNameExist
	 * @Description: TODO
	 * @param attendGroupName
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.AttendGroupService#selectAttendGroupNameExist(java.lang.String) 
	 */ 
	@Override
	public int selectAttendGroupNameExist(String attendGroupName,String agId) throws Exception {
		 
		return attendGroupMapper.selectAttendGroupNameExist(attendGroupName,agId);
	}
}
