/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service.impl 
 * @author: Administrator   
 * @date: 2019年5月16日 下午7:06:39 
 */
package com.faceRecog.manage.service.impl;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.faceRecog.manage.controller.AttendDetailController;
import com.faceRecog.manage.mapper.AttendCalculateMapper;
import com.faceRecog.manage.mapper.AttendGroupMapper;
import com.faceRecog.manage.mapper.AttendGroupPeriodMapper;
import com.faceRecog.manage.mapper.EmployeeMapper;
import com.faceRecog.manage.mapper.PeriodAttendMapper;
import com.faceRecog.manage.mapper.SignRecordMapper;
import com.faceRecog.manage.model.AttendCalculate;
import com.faceRecog.manage.model.AttendGroup;
import com.faceRecog.manage.model.AttendGroupPeriod;
import com.faceRecog.manage.model.SignRecord;
import com.faceRecog.manage.model.vo.AttendCalculateVO;
import com.faceRecog.manage.service.AttendCalculateService;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/** 
 * @ClassName: AttendCalculateServiceImpl 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月16日 下午7:06:39  
 */
@Service
public class AttendCalculateServiceImpl implements AttendCalculateService{
	 private static Logger logger = LoggerFactory.getLogger(AttendCalculateServiceImpl.class);
	
	
	@Autowired
	private AttendCalculateMapper attendCalculateMapper;
	
	@Autowired
	private EmployeeMapper employeeMapper;
	
	@Autowired
	private SignRecordMapper signRecordMapper;
	
	@Autowired
	private AttendGroupMapper attendGroupMapper;
	
	
	@Autowired
	private AttendGroupPeriodMapper attendGroupPeriodMapper;
	
	@Autowired
	private PeriodAttendMapper periodAttendMapper;
	
	/* (non Javadoc) 
	 * @Title: updateAttendCalculateByEmpId
	 * @Description: TODO
	 * @param attendCalculate
	 * @param yesterday
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.AttendCalculateService#updateAttendCalculateByEmpId(com.faceRecog.manage.model.AttendCalculate, java.lang.String) 
	 */ 
	@Override
	public int updateAttendCalculateByEmpId(AttendCalculate attendCalculate) throws Exception {
		 
		return attendCalculateMapper.updateAttendCalculateByEmpId(attendCalculate);
	}

	/* (non Javadoc) 
	 * @Title: insertAttendCalculate
	 * @Description: TODO
	 * @param attendCalculate
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.AttendCalculateService#insertAttendCalculate(com.faceRecog.manage.model.AttendCalculate) 
	 */ 
	@Override
	public int insertAttendCalculate(AttendCalculate attendCalculate) throws Exception {
		 
		return attendCalculateMapper.insertSelective(attendCalculate);
	}

	/* (non Javadoc) 
	 * @Title: selectPageAttendCalculate
	 * @Description: TODO
	 * @param startDate
	 * @param endDate
	 * @param name
	 * @param empId
	 * @param deptId
	 * @return 
	 * @see com.faceRecog.manage.service.AttendCalculateService#selectPageAttendCalculate(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String) 
	 */ 
	@Override
	public PageInfo<AttendCalculateVO> selectPageAttendCalculate(int pageSize,int pageNo,String startDate, String endDate, String name,
			String empId, String deptId) {
		 PageHelper.startPage(pageNo, pageSize);
		 List<AttendCalculateVO> attendCalculateList=attendCalculateMapper.selectPageAttendCalculate(startDate, endDate, name, empId, deptId);
		return new PageInfo<AttendCalculateVO>(attendCalculateList);
	}

	/* (non Javadoc) 
	 * @Title: selectPageAttendCalculateDetail
	 * @Description: TODO
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.AttendCalculateService#selectPageAttendCalculateDetail() 
	 */ 
	@Override
	public PageInfo<Map<String, Object>> selectPageAttendCalculateDetail(List<Map<String, Object>>  dates,int pageNo,int pageSize) throws Exception {
		List<Map<String, Object>> attendDetailList=new ArrayList<Map<String, Object>>();
		Map<String, Object> map=null;
		
		PageHelper.startPage(pageNo, pageSize);
		List<Map<String, Object>> empList=employeeMapper.selectAllEmpInfo();
		if(empList!=null && empList.size()>0 && empList.get(0).containsKey("id")){
			for(Map<String, Object> empMap:empList){
				map=new LinkedHashMap<String, Object>();
				map.put("姓名", empMap.get("name"));
				map.put("部门", empMap.containsKey("deptName")?empMap.get("deptName"):"");
				// 计算今天的考勤缺卡情况
				calculateNewEmp((String)empMap.get("id"));
				for(Map<String, Object> date:dates){
					List<Map<String, Object>> promptList=new ArrayList<Map<String, Object>>();
					AttendCalculateVO attendCalculate=attendCalculateMapper.selectPageAttendCalculateDetail((String)empMap.get("id"),(String)date.get("date"));
					if(attendCalculate!=null){
						
						if(attendCalculate.getIsAttendGroup()<1){// 是否在考勤组中 是否排班
							if(attendCalculate.getIsAttendGroup()==0){
								Map<String, Object> prompt=new HashMap<String, Object>();
								prompt.put("text", "未在考勤组中");
								promptList.add(prompt);
								map.put((String)date.get("day"), promptList);
								continue;
							}else if(attendCalculate.getIsAttendGroup()==-1){
								Map<String, Object> prompt=new HashMap<String, Object>();
								prompt.put("text", "未排班");
								promptList.add(prompt);
								map.put((String)date.get("day"), promptList);
								continue;
							}
							
						}
						if(attendCalculate.getIsAbsenteeism()>0){//旷工
							Map<String, Object> prompt=new HashMap<String, Object>();
							prompt.put("text", "旷工");
							promptList.add(prompt);
							map.put((String)date.get("day"), promptList);
							continue;
						}
						if(attendCalculate.getDelayNum()>0){//迟到
							Map<String, Object> prompt=new HashMap<String, Object>();
							prompt.put("text", "迟到");
							promptList.add(prompt);
						}
						if(attendCalculate.getEarlyNum()>0){
							Map<String, Object> prompt=new HashMap<String, Object>();
							prompt.put("text", "早退");
							promptList.add(prompt);
						}
						if(Integer.valueOf(attendCalculate.getMissSign())>0){//是否缺卡
							if(attendCalculate.getSignInMiss()>0){
								Map<String, Object> prompt=new HashMap<String, Object>();
								prompt.put("text", "上班缺卡");
								promptList.add(prompt);
							}else if(attendCalculate.getSignOutMiss()>0){
								Map<String, Object> prompt=new HashMap<String, Object>();
								prompt.put("text", "下班缺卡");
								promptList.add(prompt);
							}
						}
						
						if(promptList==null || promptList.size()==0){
							Map<String, Object> prompt=new HashMap<String, Object>();
							if(attendCalculate.getIsRest()==1){
								prompt.put("text", "休息");
								promptList.add(prompt);
							}else{
								prompt.put("text", "正常");
								promptList.add(prompt);
							}
						}
					}else{ //考勤数据为空 还未录入考勤资料
						Map<String, Object> prompt=new HashMap<String, Object>();
						prompt.put("text", "*");
						promptList.add(prompt);
					}
					map.put((String)date.get("day"), promptList);
				}
				attendDetailList.add(map);
			}
		}
		return new PageInfo<Map<String, Object>>(attendDetailList);
	}

	/**
	 * 
	* @Title: calculateNewEmmp 
	* @Description: 考勤组员工考勤计算 
	* @param updateEmp
	* @param oldEmp
	* @return boolean
	* @author xya
	* @date 2019年5月23日下午2:05:53
	 */
	public int calculateNewEmp(String empId)throws Exception{
			int affectNum=0;
			int tdWeekday=0;
			int ysWeekday=0;
			int tomWeekday=0;
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date currDate =new Date();// 当前时间
			String today=format.format(currDate);
    		Calendar c = Calendar.getInstance();
    		Calendar tomCal = Calendar.getInstance();
			c.setTime(currDate);
			tomCal.setTime(currDate);
			tdWeekday=c.get(Calendar.DAY_OF_WEEK)-1;// 今天日期星期几
			tomCal.add(Calendar.DATE, 1);
 	        if(tdWeekday==0){// 0为礼拜天
 	        	tdWeekday=7;
 	        }
 	        tomWeekday=tomCal.get(Calendar.DAY_OF_WEEK)-1;// 明天日期星期几
 	        String tomDay=format.format(tomCal.getTime());
	        if(tomWeekday==0){// 0为礼拜天
	        	tomWeekday=7;
	        }
 	        c.add(Calendar.DATE, -1);
 	        String yesterday=format.format(c.getTime().getTime());
 	        ysWeekday=c.get(Calendar.DAY_OF_WEEK)-1;// 昨天日期星期几
	        if(ysWeekday==0){// 0为礼拜天
	        	ysWeekday=7;
	        }
 	        // 查询员工今天的打卡记录
	        SignRecord  signRec = signRecordMapper.selectSignRecordByEmpId(empId,today);
			// 根据考情组id查询员工的信息
			AttendGroup attendGroup=attendGroupMapper.selectEmpAttendGroupType(empId);
			if(attendGroup!=null){
				if(CommonConstant.固定班.getValue().equals(attendGroup.getType())){
						// 查询固定班制员工的所属周期 根据员工id 今天的
		        		AttendGroupPeriod attendGroupPeriod=attendGroupPeriodMapper.selectPeriodByEmpId(empId);
		        		//查询周期排班班次 今天的
		        		List<Map<String, Object>> periodTodayAttendList=periodAttendMapper.selectPeriodAttendDetailByPrIdAndSortCms(attendGroupPeriod.getPrId(),tdWeekday,today);
		        		//查询昨天周期排班班次
		        		List<Map<String, Object>> periodYestadayAttendList=periodAttendMapper.selectPeriodAttendDetailByPrIdAndSortCms(attendGroupPeriod.getPrId(),ysWeekday,yesterday);
		        		// 明天的数据
		        		//查询昨天周期排班班次
		        		List<Map<String, Object>> periodTomdayAttendList=periodAttendMapper.selectPeriodAttendDetailByPrIdAndSortCms(attendGroupPeriod.getPrId(),tomWeekday,tomDay);
		        		// 今天的第一班上班时间
						String tdSignInTime="";
						String tomSignInTime="";
						if(periodTodayAttendList!=null && periodTodayAttendList.size()>0 && periodTodayAttendList.get(0)!=null){
							tdSignInTime=(String)periodTodayAttendList.get(0).get("workOnTime");
						}else{
							tdSignInTime=yesterday+" 23:59:59";
						}
						if(periodTomdayAttendList!=null && periodTomdayAttendList.size()>0 && periodTomdayAttendList.get(0)!=null){
							tomSignInTime=(String)periodTomdayAttendList.get(0).get("workOnTime");
						}else{
							tomSignInTime=today+" 23:59:59";
						}
						// 先结算昨天的跨天考勤
						acrossAttendCalcuate(tdSignInTime,periodYestadayAttendList, empId,attendGroup.getType());
						// 结算今天的考勤
						attendTodayCalculate(empId,periodTodayAttendList,signRec,attendGroup.getType(),tomSignInTime);
				}else if(CommonConstant.排班制.getValue().equals(attendGroup.getType())){
						// 获取员工今天的班制 常规班 两班制 三班制
						List<Map<String, Object>> attendTodayInfoList=attendGroupMapper.selectAttendGroupAttendDanceInfo(empId,today);
						// 获取员工昨天的班制 常规班 两班制 三班制
						List<Map<String, Object>> attendYesterdayInfoList=attendGroupMapper.selectAttendGroupAttendDanceInfo(empId,yesterday);
						// 明天的考勤情况
						List<Map<String, Object>> attendTomdayInfoList=attendGroupMapper.selectAttendGroupAttendDanceInfo(empId,tomDay);
						
						// 今天的第一班上班时间
						String tdSignInTime="";
						String tomSignInTime="";
						if(attendTodayInfoList!=null && attendTodayInfoList.size()>0 && attendTodayInfoList.get(0)!=null){
							tdSignInTime=(String)attendTodayInfoList.get(0).get("workOnTime");
						}else{
							tdSignInTime=yesterday+" 23:59:59";
						}
						if(attendTomdayInfoList!=null && attendTomdayInfoList.size()>0 && attendTomdayInfoList.get(0)!=null){
							tomSignInTime=(String)attendTomdayInfoList.get(0).get("workOnTime");
						}else{
							tomSignInTime=today+" 23:59:59";
						}
						acrossAttendCalcuate(tdSignInTime, attendYesterdayInfoList, empId,attendGroup.getType());
						//  重新计算今天的考勤
						attendTodayCalculate(empId,attendTodayInfoList,signRec,attendGroup.getType(),tomSignInTime);
				}else{
					logger.error("*************************重新计算考勤 考勤组类型错误************************");
					return -1;
				}
			}else{ // 未在考勤组
				// 查询当前打卡员工考勤是否结算过
				AttendCalculate attendCalculateRec = attendCalculateMapper.selectAttendCalculateInfoByEmpId(empId, today);
				if (attendCalculateRec != null) {
					attendCalculateRec.setIsAttendGroup(0);
					affectNum = attendCalculateMapper.updateByPrimaryKeySelective(attendCalculateRec);
				} else {
					attendCalculateRec=new AttendCalculate();
					attendCalculateRec.setId(UUIDGenerator.getUUID());
					attendCalculateRec.setCreateTime(currDate);
					attendCalculateRec.setStatus("1");
					attendCalculateRec.setEmpId(empId);
					attendCalculateRec.setSignDate(today);
					attendCalculateRec.setIsAttendGroup(0);
					affectNum = attendCalculateMapper.insertSelective(attendCalculateRec);
				}
			}
		return affectNum;
	}
	
	/**
	 * 
	* @Title: attendTypeCalculate 
	* @Description: 今天的考勤结算 
	* @param attendType
	* @param empId
	* @param attendInfoList
	* @param signRecord
	* @param attendYesterdayInfoList
	* @return
	* @throws Exception int
	* @author xya
	* @date 2019年5月30日下午3:18:09
	 */
	public  int attendTodayCalculate(String empId,List<Map<String, Object>>attendInfoList,
			SignRecord signRecord,String agType,String tomSignInOne)throws Exception{
		int affectNum=0;
		
		AttendCalculate attendCalculate = new AttendCalculate();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date currDate =new Date();// 当前时间
		String today=format.format(currDate);
		// 查询当前打卡员工考勤是否结算过
		AttendCalculate attendCalculateRec = attendCalculateMapper.selectAttendCalculateInfoByEmpId(empId, today);
		if(attendInfoList!=null && attendInfoList.size()>0 && attendInfoList.get(0)!=null){
			String attendType=(String)attendInfoList.get(0).get("type");
			
			if(CommonConstant.常规班.getValue().equals(attendType)){
				
				// 开始计算缺卡
				List<String> signNullTimeList=new ArrayList<String>();
				if(signRecord!=null){
					// 判断哪个班段为空
					signNullTimeList=signTimeNullJudge(signRecord, attendType);
				}else{
					signNullTimeList.add("signInOne");
					signNullTimeList.add("signOutOne");
				}
				if(signNullTimeList!=null && signNullTimeList.size()>0){
					int mark=0;// 旷工标记
					int signInMiss=0;
					int signOutMiss=0;
					for(String signNull:signNullTimeList){
						boolean flag=calculateMilddle(attendInfoList, attendType, signNull, tomSignInOne);
						if(flag){// 缺卡
							mark++;
							if(signNull.contains(CommonConstant.签到.getValue())){
								++signInMiss;
							}else{
								++signOutMiss;
							}
						}
					}
					if(mark==2){
						attendCalculate.setIsAbsenteeism(1);// 旷工
					}else{
						attendCalculate.setMissSign(1);
						attendCalculate.setSignInMiss(signInMiss);
						attendCalculate.setSignOutMiss(signOutMiss);
					}
				}
			}else if(CommonConstant.两班制.getValue().equals(attendType)){
				
				
				List<String> signNullTimeList=new ArrayList<String>();
				if(signRecord!=null){
					// 判断哪个班段为空
					signNullTimeList=signTimeNullJudge(signRecord, attendType);
				}else{
					signNullTimeList.add("signInOne");
					signNullTimeList.add("signOutOne");
					signNullTimeList.add("signInTwo");
					signNullTimeList.add("signOutTwo");
				}
				if(signNullTimeList!=null && signNullTimeList.size()>0){
					int mark=0;// 旷工标记
					int signInMiss=0;
					int signOutMiss=0;
					for(String signNull:signNullTimeList){
						boolean flag=calculateMilddle(attendInfoList, attendType, signNull, tomSignInOne);
						if(flag){// 缺卡
							mark++;
							if(signNull.contains(CommonConstant.签到.getValue())){
								++signInMiss;
							}else{
								++signOutMiss;
							}
						}
					}
					if(mark==4){
						attendCalculate.setIsAbsenteeism(1);// 旷工
					}else{
						attendCalculate.setMissSign(1);
						attendCalculate.setSignInMiss(signInMiss);
						attendCalculate.setSignOutMiss(signOutMiss);
					}
				}
			}else if(CommonConstant.三班制.getValue().equals(attendType)){
				
				// 开始计算缺卡
				List<String> signNullTimeList=new ArrayList<String>();
				if(signRecord!=null){
					// 判断哪个班段为空
					signNullTimeList=signTimeNullJudge(signRecord, attendType);
				}else{
					signNullTimeList.add("signInOne");
					signNullTimeList.add("signInOutOne");
					signNullTimeList.add("signInTwo");
					signNullTimeList.add("signOutTwo");
					signNullTimeList.add("signInThree");
					signNullTimeList.add("signOutThree");
				}
				if(signNullTimeList!=null && signNullTimeList.size()>0){
					int mark=0;// 旷工标记
					int signInMiss=0;
					int signOutMiss=0;
					for(String signNull:signNullTimeList){
						boolean flag=calculateMilddle(attendInfoList, attendType, signNull, tomSignInOne);
						if(flag){// 缺卡
							mark++;
							if(signNull.contains(CommonConstant.签到.getValue())){
								++signInMiss;
							}else{
								++signOutMiss;
							}
						}
					}
					if(mark==6){
						attendCalculate.setIsAbsenteeism(1);// 旷工
					}else{
						attendCalculate.setMissSign(1);
						attendCalculate.setSignInMiss(signInMiss);
						attendCalculate.setSignOutMiss(signOutMiss);
					}
				}
				
			}
		}else{// 未排班
			attendCalculate.setIsAttendGroup(-1);
		}
		
		if(attendCalculate!=null){
			if(attendCalculateRec!=null){
				attendCalculate.setIsAttendGroup(1);
				attendCalculate.setId(attendCalculateRec.getId());
				attendCalculate.setSignDate(today);
				affectNum=attendCalculateMapper.updateByPrimaryKeySelective(attendCalculate);
			}else{
				attendCalculate.setIsAttendGroup(1);
				attendCalculate.setId(UUIDGenerator.getUUID());
				attendCalculate.setCreateTime(currDate);
				attendCalculate.setStatus("1");
				attendCalculate.setEmpId(empId);
				attendCalculate.setSignDate(today);
				affectNum=attendCalculateMapper.insertSelective(attendCalculate);
			}
		}
		return affectNum;
	}
	
	/**
	 * 
	* @Title: acrossAttendCalcuate 
	* @Description: 昨天的跨天考勤计算 
	* @param signInOneTime 今天的第一班上班时间点
	* @param attendYesterdayInfoList 昨天的考勤排班信息
	* @param agType 考勤组班制类型 固定班 还是排班
	* @return int
	* @author xya
	* @date 2019年5月30日上午11:26:51
	 */
	public int acrossAttendCalcuate(String signInOneTime, List<Map<String, Object>> attendYesterdayInfoList,
			String empId,String agType) throws Exception {
		int affectNum = 0;

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date currDate = new Date();// 当前时间
		Calendar c = Calendar.getInstance();
		c.setTime(currDate);
		c.add(Calendar.DATE, -1);
		String yesterday = format.format(c.getTime().getTime());
		AttendCalculate attendCalculate = new AttendCalculate();
		
		// 查询当前打卡员工考勤昨天是否结算过
		AttendCalculate attendCalculateRec = attendCalculateMapper.selectAttendCalculateInfoByEmpId(empId, yesterday);
		
		if(attendYesterdayInfoList!=null && attendYesterdayInfoList.size()>0 && attendYesterdayInfoList.get(0)!=null){
				// 班次类型
				String attendType = attendYesterdayInfoList.get(0).get("type").toString();
				// 查询员工昨天的打卡记录
				SignRecord signRecord = signRecordMapper.selectSignRecordByEmpId(empId, yesterday);
				
				if(CommonConstant.常规班.getValue().equals(attendType)){
					// 开始计算缺卡
					List<String> signNullTimeList=new ArrayList<String>();
					if(signRecord!=null){
						// 判断哪个班段为空
						signNullTimeList=signTimeNullJudge(signRecord, attendType);
					}else{
						signNullTimeList.add("signInOne");
						signNullTimeList.add("signOutOne");
					}
					if(signNullTimeList!=null && signNullTimeList.size()>0){
						int mark=0;// 旷工标记
						int signInMiss=0;
						int signOutMiss=0;
						for(String signNull:signNullTimeList){
							boolean flag=calculateMilddle(attendYesterdayInfoList, attendType, signNull, signInOneTime);
							if(flag){// 缺卡
								mark++;
								if(signNull.contains(CommonConstant.签到.getValue())){
									++signInMiss;
								}else{
									++signOutMiss;
								}
							}
						}
						if(mark==2){
							attendCalculate.setIsAbsenteeism(1);// 旷工
						}else{
							attendCalculate.setMissSign(1);
							attendCalculate.setSignInMiss(signInMiss);
							attendCalculate.setSignOutMiss(signOutMiss);
						}
					}
				}else if(CommonConstant.两班制.getValue().equals(attendType)){
					List<String> signNullTimeList=new ArrayList<String>();
					if(signRecord!=null){
						// 判断哪个班段为空
						signNullTimeList=signTimeNullJudge(signRecord, attendType);
					}else{
						signNullTimeList.add("signInOne");
						signNullTimeList.add("signOutOne");
						signNullTimeList.add("signInTwo");
						signNullTimeList.add("signOutTwo");
					}
					if(signNullTimeList!=null && signNullTimeList.size()>0){
						int mark=0;// 旷工标记
						int signInMiss=0;
						int signOutMiss=0;
						for(String signNull:signNullTimeList){
							boolean flag=calculateMilddle(attendYesterdayInfoList, attendType, signNull, signInOneTime);
							if(flag){// 缺卡
								mark++;
								if(signNull.contains(CommonConstant.签到.getValue())){
									++signInMiss;
								}else{
									++signOutMiss;
								}
							}
						}
						if(mark==4){
							attendCalculate.setIsAbsenteeism(1);// 旷工
						}else{
							attendCalculate.setMissSign(1);
							attendCalculate.setSignInMiss(signInMiss);
							attendCalculate.setSignOutMiss(signOutMiss);
						}
					}
					
				}else if(CommonConstant.三班制.getValue().equals(attendType)){
					// 开始计算缺卡
					List<String> signNullTimeList=new ArrayList<String>();
					if(signRecord!=null){
						// 判断哪个班段为空
						signNullTimeList=signTimeNullJudge(signRecord, attendType);
					}else{
						signNullTimeList.add("signInOne");
						signNullTimeList.add("signOutOne");
						signNullTimeList.add("signInTwo");
						signNullTimeList.add("signOutTwo");
						signNullTimeList.add("signInThree");
						signNullTimeList.add("signOutThree");
					}
					if(signNullTimeList!=null && signNullTimeList.size()>0){
						int mark=0;// 旷工标记
						int signInMiss=0;
						int signOutMiss=0;
						for(String signNull:signNullTimeList){
							boolean flag=calculateMilddle(attendYesterdayInfoList, attendType, signNull, signInOneTime);
							if(flag){// 缺卡
								mark++;
								if(signNull.contains(CommonConstant.签到.getValue())){
									++signInMiss;
								}else{
									++signOutMiss;
								}
							}
						}
						if(mark==6){
							attendCalculate.setIsAbsenteeism(1);// 旷工
						}else{
							attendCalculate.setMissSign(1);
							attendCalculate.setSignInMiss(signInMiss);
							attendCalculate.setSignOutMiss(signOutMiss);
						}
					}
				}
			}else{// 未排班
				attendCalculate.setIsAttendGroup(-1);
			}
		if (attendCalculate != null) {
			if (attendCalculateRec != null) {
				attendCalculate.setId(attendCalculateRec.getId());
				attendCalculate.setIsAttendGroup(1);
				affectNum = attendCalculateMapper.updateByPrimaryKeySelective(attendCalculate);
			} else {
				attendCalculate.setId(UUIDGenerator.getUUID());
				attendCalculate.setIsAttendGroup(1);
				attendCalculate.setCreateTime(currDate);
				attendCalculate.setStatus("1");
				attendCalculate.setEmpId(empId);
				attendCalculate.setSignDate(yesterday);
				affectNum = attendCalculateMapper.insertSelective(attendCalculate);
			}
		}
		return affectNum;
	}

	/* (non Javadoc) 
	 * @Title: selectSignMissAttendDetail
	 * @Description: TODO
	 * @param dateStr
	 * @param empId
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.AttendCalculateService#selectSignMissAttendDetail(java.lang.String, java.lang.String) 
	 */ 
	@Override
	public List<Map<String, Object>> selectSignMissAttendDetail(String dateStr, String empId) throws Exception {
		 
		return attendCalculateMapper.selectSignMissAttendDetail(dateStr,empId);
	}	
	
	
	
	public boolean calculateMilddle(List<Map<String, Object>> attendInfoList,String attendType
			,String middleKey,String nextDaySignInOne)throws ParseException{
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// 当前时间时间戳
		long currTime=new Date().getTime();
		
		// 重新计算时间
		for(Map<String, Object> attendMap:attendInfoList){
			String inAcross=(String)attendMap.get("inAcross");
			String outAcross=(String)attendMap.get("outAcross");
			String workOnTime=(String)attendMap.get("workOnTime");
			String workOffTime=(String)attendMap.get("workOffTime");
			
			Calendar cal = Calendar.getInstance();
			if(CommonConstant.次日.getValue().equals(inAcross)){
				cal.setTime(df.parse(workOnTime));
				cal.add(Calendar.DATE, 1);
				attendMap.put("workOnTime", df.format(cal.getTime()));
			}
			if(CommonConstant.次日.getValue().equals(outAcross)){
				cal.setTime(df.parse(workOffTime));
				cal.add(Calendar.DATE, 1);
				attendMap.put("workOffTime", df.format(cal.getTime()));
			}
			
		}
		
		Map<String, Long> signMildTime=new HashMap<String, Long>();
		if(CommonConstant.常规班.getValue().equals(attendType)){
			Map<String, Object> attendDanceMap1=attendInfoList.get(0);
			
			long workOnTime1=df.parse((String)attendDanceMap1.get("workOnTime")).getTime();//
			long workOffTime1 = df.parse((String) attendDanceMap1.get("workOffTime")).getTime();// 下班时间
			long workOnTime2 =df.parse(nextDaySignInOne).getTime();// 下一天的上班时间  时间戳
			
			// 获取两个中间节点
			long signInOneTime=(workOffTime1-workOnTime1)/2;
			long signOutOneTime=(workOnTime2-workOffTime1)/2;
			signMildTime.put("signInOne", signInOneTime+workOnTime1);
			signMildTime.put("signOutOne", signOutOneTime+workOffTime1);
		}else if(CommonConstant.两班制.getValue().equals(attendType)){
			Map<String, Object> attendDanceMap1=attendInfoList.get(0);
			Map<String, Object> attendDanceMap2=attendInfoList.get(1);
			
			long workOnTime1=df.parse((String)attendDanceMap1.get("workOnTime")).getTime();//
			long workOffTime1 = df.parse((String) attendDanceMap1.get("workOffTime")).getTime();// 下班时间
			long workOnTime2=df.parse((String)attendDanceMap2.get("workOnTime")).getTime();//
			long workOffTime2 = df.parse((String) attendDanceMap2.get("workOffTime")).getTime();// 下班时间
			long workOnTime3 =df.parse(nextDaySignInOne).getTime();// 下一天的上班时间  时间戳
			
			// 获取两个中间节点
			long signInOneTime=(workOffTime1-workOnTime1)/2;
			long signOutOneTime=(workOnTime2-workOffTime1)/2;
			long signInTwoTime=(workOffTime2-workOnTime2)/2;
			long signOutTwoTime=(workOnTime3-workOffTime2)/2;
			signMildTime.put("signInOne", signInOneTime+workOnTime1);
			signMildTime.put("signOutOne", signOutOneTime+workOffTime1);
			signMildTime.put("signInTwo", signInTwoTime+workOnTime2);
			signMildTime.put("signOutTwo", signOutTwoTime+workOffTime2);
		}else if(CommonConstant.三班制.getValue().equals(attendType)){
			Map<String, Object> attendDanceMap1=attendInfoList.get(0);
			Map<String, Object> attendDanceMap2=attendInfoList.get(1);
			Map<String, Object> attendDanceMap3=attendInfoList.get(2);
			
			long workOnTime1=df.parse((String)attendDanceMap1.get("workOnTime")).getTime();//
			long workOffTime1 = df.parse((String) attendDanceMap1.get("workOffTime")).getTime();// 下班时间
			long workOnTime2=df.parse((String)attendDanceMap2.get("workOnTime")).getTime();//
			long workOffTime2 = df.parse((String) attendDanceMap2.get("workOffTime")).getTime();// 下班时间
			long workOnTime3=df.parse((String)attendDanceMap3.get("workOnTime")).getTime();//
			long workOffTime3 = df.parse((String) attendDanceMap3.get("workOffTime")).getTime();// 下班时间
			long workOnTime4 =df.parse(nextDaySignInOne).getTime();// 下一天的上班时间  时间戳
			
			// 获取两个中间节点
			long signInOneTime=(workOffTime1-workOnTime1)/2;
			long signOutOneTime=(workOnTime2-workOffTime1)/2;
			long signInTwoTime=(workOffTime2-workOnTime2)/2;
			long signOutTwoTime=(workOnTime3-workOffTime2)/2;
			long signInThreeTime=(workOffTime3-workOnTime3)/2;
			long signOutThreeTime=(workOnTime4-workOffTime3)/2;
			signMildTime.put("signInOne", signInOneTime+workOnTime1);
			signMildTime.put("signOutOne", signOutOneTime+workOffTime1);
			signMildTime.put("signInTwo", signInTwoTime+workOnTime2);
			signMildTime.put("signOutTwo", signOutTwoTime+workOffTime2);
			signMildTime.put("signInThree", signInThreeTime+workOnTime3);
			signMildTime.put("signOutThree", signOutThreeTime+workOffTime3);
		}
		// 获取当前缺卡时间段 中间点
		long middleTime= signMildTime.get(middleKey);
		if(currTime>middleTime){
			//当前时间大于打卡节点 视为缺卡
			return true;
		}else{
			return false;
		}
	}
	
	public List<String> signTimeNullJudge(SignRecord signRec,String attendType){
		 List<String> signRecList =new ArrayList<String>();
		if(CommonConstant.常规班.getValue().equals(attendType)){
			if(!StrKit.notNull(signRec.getSignInOne())){
				signRecList.add("signInOne");
			}
			if(!StrKit.notNull(signRec.getSignOutOne())){
				signRecList.add("signOutOne");
			}
		}else if(CommonConstant.两班制.getValue().equals(attendType)){
			if(!StrKit.notNull(signRec.getSignInOne())){
				signRecList.add("signInOne");
			}
			if(!StrKit.notNull(signRec.getSignOutOne())){
				signRecList.add("signOutOne");
			}
			if(!StrKit.notNull(signRec.getSignInTwo())){
				signRecList.add("signInTwo");
			}
			if(!StrKit.notNull(signRec.getSignOutTwo())){
				signRecList.add("signOutTwo");
			}
		}else if(CommonConstant.三班制.getValue().equals(attendType)){
			if(!StrKit.notNull(signRec.getSignInOne())){
				signRecList.add("signInOne");
			}
			if(!StrKit.notNull(signRec.getSignOutOne())){
				signRecList.add("signOutOne");
			}
			if(!StrKit.notNull(signRec.getSignInTwo())){
				signRecList.add("signInTwo");
			}
			if(!StrKit.notNull(signRec.getSignOutTwo())){
				signRecList.add("signOutTwo");
			}
			if(!StrKit.notNull(signRec.getSignInThree())){
				signRecList.add("signInThree");
			}
			if(!StrKit.notNull(signRec.getSignOutThree())){
				signRecList.add("signOutThree");
			}
		}
		return signRecList;
	}
}
