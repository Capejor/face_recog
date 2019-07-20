/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service.impl 
 * @author: Administrator   
 * @date: 2019年5月11日 下午2:25:01 
 */
package com.faceRecog.manage.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.faceRecog.manage.mapper.AttendCalculateMapper;
import com.faceRecog.manage.mapper.AttendGroupMapper;
import com.faceRecog.manage.mapper.AttendGroupPeriodMapper;
import com.faceRecog.manage.mapper.EmployeeMapper;
import com.faceRecog.manage.mapper.OriginalSignRecordMapper;
import com.faceRecog.manage.mapper.PeriodAttendMapper;
import com.faceRecog.manage.mapper.SignRecordMapper;
import com.faceRecog.manage.model.AttendCalculate;
import com.faceRecog.manage.model.AttendGroup;
import com.faceRecog.manage.model.AttendGroupPeriod;
import com.faceRecog.manage.model.OriginalSignRecord;
import com.faceRecog.manage.model.SignRecord;
import com.faceRecog.manage.service.SignRecordService;
import com.faceRecog.manage.util.CommUtil;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;

/** 
 * @ClassName: SignRecordServiceImpl 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月11日 下午2:25:01  
 */
@Service
public class SignRecordServiceImpl implements SignRecordService{

	@Autowired
	private  SignRecordMapper signRecordMapper;
	
	@Autowired
	private EmployeeMapper employeeMapper;
	
	@Autowired
	private  AttendCalculateMapper attendCalculateMapper;
	
	@Autowired
	private  OriginalSignRecordMapper originalSignRecordMapper;
	
	
	@Autowired
	private  AttendGroupMapper attendGroupMapper;
	

	@Autowired
	private  AttendGroupPeriodMapper attendGroupPeriodMapper;
	
	@Autowired
	private  PeriodAttendMapper periodAttendMapper;
	
	
	/* (non Javadoc) 
	 * @Title: insertSignRecord
	 * @Description: TODO
	 * @param clock
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.SignRecordService#insertSignRecord(com.faceRecog.manage.model.Clock) 
	 */ 
	@Override
	@Transactional
	public Result insertSignRecord(SignRecord signRecord,String type) throws Exception {
		Result result=null;
		int affectNum=0;
		int insertResult=0;
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		// 查询员工所属考勤组的类型
        AttendGroup attendGroup =attendGroupMapper.selectEmpAttendGroupType(signRecord.getEmpId());
		//判断员工当天有没有打卡
        SignRecord  signRec = signRecordMapper.selectSignRecordByEmpId(signRecord.getEmpId(),signRecord.getSignDate());
        if (signRec==null) {
        	signRecord.setId(UUIDGenerator.getUUID());
        	signRecord.setCreateTime(new Date());
        	signRecord.setEmpId(signRecord.getEmpId());
        	signRecord.setStatus("1");
            insertResult = signRecordMapper.insertSelective(signRecord);
            if (insertResult > 0) {
                result = Result.responseSuccess("打卡成功");
            } else {
                result = Result.responseError("打卡失败");
            }
        }else {
        	signRecord.setId(signRec.getId());
        	// 上班打卡不为空 判断是否已打上班卡 上班打卡只记录最早的一次
        	if(CommonConstant.签到.getValue().equals(type)){
        		if(!StrKit.notNull(signRec.getSignInOne()) && StrKit.notNull(signRecord.getSignInOne())){
        			affectNum= signRecordMapper.updateByPrimaryKeySelective(signRecord);
            	}else if(!StrKit.notNull(signRec.getSignInTwo()) && StrKit.notNull(signRecord.getSignInTwo())){
            		affectNum= signRecordMapper.updateByPrimaryKeySelective(signRecord);
            	}else if(!StrKit.notNull(signRec.getSignInThree()) && StrKit.notNull(signRecord.getSignInThree())){
            		affectNum= signRecordMapper.updateByPrimaryKeySelective(signRecord);
            	}
        	}else{
        		affectNum= signRecordMapper.updateByPrimaryKeySelective(signRecord);
        	}
            if (affectNum < 0) {
            	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            	return  Result.responseError("打卡失败");
            }
        }
        // 记录员工员工原始打卡记录
        insertResult=insertOriginalSignRecord(signRecord,type);
        if(insertResult<0){
        	 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        	 return Result.responseError("打卡失败！");
        }
       
        if(attendGroup!=null){
        	if(attendGroup.getType().equals(CommonConstant.固定班.getValue())){
        		int weekday=0;
        		
        		Calendar c = Calendar.getInstance();
    	        c.setTime(fmt.parse(signRecord.getSignDate()));
    	        weekday=c.get(Calendar.DAY_OF_WEEK)-1;// 当前日期星期几
    	        if(weekday==0){// 0为礼拜天
    	        	weekday=7;
    	        }
        		// 查询固定班制员工的所属周期 根据员工id
        		AttendGroupPeriod attendGroupPeriod=attendGroupPeriodMapper.selectPeriodByEmpId(signRecord.getEmpId());
        		//查询当天周期排班班次
        		List<Map<String, Object>> periodAttendList=periodAttendMapper.selectPeriodAttendDetailByPrIdAndSortCms(attendGroupPeriod.getPrId(),weekday,signRecord.getSignDate());
        		if(periodAttendList!=null && periodAttendList.size()>0 && periodAttendList.get(0)!=null){
        			for(Map<String, Object> attendMap:periodAttendList){
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
        		}
        		String attendType=periodAttendList.get(0).get("type").toString();
        		if (CommonConstant.常规班.getValue().equals(attendType)) {
    					affectNum = firstSignOut(signRecord, periodAttendList, signRec,type);
    			} else if (CommonConstant.两班制.getValue().equals(attendType)) {
    				if (StrKit.notNull(signRecord.getSignOutOne())) {// 第一段下班打卡
    					affectNum = firstSignOut(signRecord, periodAttendList, signRec,type);
    				}
    				if (StrKit.notNull(signRecord.getSignOutTwo())) { // 第二段下班打卡
    					affectNum = secondSignOut(signRecord, periodAttendList, signRec,type);
    				}
    			} else if (CommonConstant.三班制.getValue().equals(attendType)) {
    				if (StrKit.notNull(signRecord.getSignOutOne())) {// 第一段下班打卡
    					affectNum = firstSignOut(signRecord, periodAttendList, signRec,type);
    				}
    				if (StrKit.notNull(signRecord.getSignOutTwo())) { // 第二段下班打卡
    					affectNum = secondSignOut(signRecord, periodAttendList, signRec,type);
    				}
    				if (StrKit.notNull(signRecord.getSignOutThree())) {// 第三段打开
    					affectNum = thirdSignOut(signRecord, periodAttendList, signRec,type);
    				}
    			}
        	}else if(attendGroup.getType().equals(CommonConstant.排班制.getValue())){
        		//根据员工id查询当天的考勤信息(班次信息)
        		List<Map<String, Object>> attendInfoList=employeeMapper.selectAttendInfo(signRecord.getEmpId(),signRecord.getSignDate());
        		if (attendInfoList != null && attendInfoList.size() > 0) {
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
        			
        			// 班次类型
            		String attendType=attendInfoList.get(0).get("type").toString();
        			if (CommonConstant.常规班.getValue().equals(attendType)) {
        					affectNum = firstSignOut(signRecord, attendInfoList, signRec,type);
        			} else if (CommonConstant.两班制.getValue().equals(attendType)) {
        				if (StrKit.notNull(signRecord.getSignOutOne())) {// 第一段下班打卡
        					affectNum = firstSignOut(signRecord, attendInfoList, signRec,type);
        				}
        				if (StrKit.notNull(signRecord.getSignOutTwo())) { // 第二段下班打卡
        					affectNum = secondSignOut(signRecord, attendInfoList, signRec,type);
        				}
        			} else if (CommonConstant.三班制.getValue().equals(attendType)) {
        				if (StrKit.notNull(signRecord.getSignOutOne())) {// 第一段下班打卡
        					affectNum = firstSignOut(signRecord, attendInfoList, signRec,type);
        				}
        				if (StrKit.notNull(signRecord.getSignOutTwo())) { // 第二段下班打卡
        					affectNum = secondSignOut(signRecord, attendInfoList, signRec,type);
        				}
        				if (StrKit.notNull(signRecord.getSignOutThree())) {// 第三段打开
        					affectNum = thirdSignOut(signRecord, attendInfoList, signRec,type);
        				}
        			}
        		}
        	}
        }else{// 员工未在考勤组
        	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        	return Result.responseError("员工未在考勤组，打卡失败！");
        }
        if(affectNum<0){
        	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        	return Result.responseError("打卡失败！");
        }
        result=Result.responseSuccess("打卡成功！");
		return result;
	}

	

	/* (non Javadoc) 
	 * @Title: updateSignRecordById
	 * @Description: TODO
	 * @param signRecord
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.SignRecordService#updateSignRecordById(com.faceRecog.manage.model.SignRecord) 
	 */ 
	@Override
	public int updateSignRecordById(SignRecord signRecord) throws Exception {
		 
		return signRecordMapper.updateByPrimaryKeySelective(signRecord);
	}

	
	// 第一段班考勤计算
	public int firstSignOut(SignRecord signOutRecord, List<Map<String, Object>> attendInfoList, SignRecord signInfo,String type)
			throws Exception {
		int affectNum = 0;
		
		// 时间累计
		Calendar gcFir = new GregorianCalendar();
		Calendar gcSec = new GregorianCalendar();
		Calendar gcThir = new GregorianCalendar();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// 每打一次下班卡结算一次考勤

		// 查询当前员工当前日期是否结算考勤
		AttendCalculate attendCalculateRec = attendCalculateMapper
				.selectAttendCalculateInfoByEmpId(signOutRecord.getEmpId(), signOutRecord.getSignDate());
		
		// 上班时间
		Date workOnTime = fmt.parse(attendInfoList.get(0).get("workOnTime").toString());
		// 下班时间
		Date workOffTime = fmt.parse(attendInfoList.get(0).get("workOffTime").toString());
	
		
		// 加班时间节点（下班后多久开始算加班）
		String afterOvertime = attendInfoList.get(0).get("afterOvertime").toString();
		// 允许的最晚的签到时间
		String allowLateMM = attendInfoList.get(0).get("allowLate").toString();
		// 允许的最早的签退时间
		String allowEarlyMM = attendInfoList.get(0).get("allowLate").toString();

		gcFir.setTime(workOnTime);
		gcFir.add(GregorianCalendar.MINUTE, Integer.parseInt(allowLateMM));
		gcSec.setTime(workOffTime);
		gcSec.add(GregorianCalendar.MINUTE, -Integer.parseInt(allowEarlyMM));
		gcThir.setTime(workOffTime);
		gcThir.add(GregorianCalendar.MINUTE, Integer.parseInt(afterOvertime));
		workOnTime = gcFir.getTime();// 允许最晚上班时间
		workOffTime = gcSec.getTime();// 允许最早下班打卡时间
		Date startOvertime = gcThir.getTime();// 加班开始时间
		Map<String , Date> map=new HashMap<String , Date>();
        map.put("workOnTime1", workOnTime);
        map.put("workOffTime1", workOffTime);
        map.put("workOnTime2", null);
        map.put("workOffTime2", null);
        map.put("workOnTime3", null);
        map.put("workOffTime3", null);
        map.put("startOvertime1", startOvertime);
        map.put("startOvertime2", null);
        map.put("startOvertime3", null);
        if(attendCalculateRec!=null){
        	attendCalculateRec.setDelayNum(0);
            attendCalculateRec.setDelayTimes(new BigDecimal(0));
            attendCalculateRec.setEarlyNum(0);
            attendCalculateRec.setEarlyTimes(new BigDecimal(0));
            attendCalculateRec.setOvertimeNum(0);
            attendCalculateRec.setOvertimeTimes(new BigDecimal(0));
            attendCalculateRec.setWorkTimes(new BigDecimal(0));
            attendCalculateRec.setMissSign(0);
            attendCalculateRec.setSignInMiss(0);
            attendCalculateRec.setSignOutMiss(0);
        }else{
        	attendCalculateRec=new AttendCalculate();
        }
        
        attendCalculateRec=everySignAfreshCalculate(attendCalculateRec, signInfo, signOutRecord,map,CommonConstant.常规班.getValue(),type);
        if (attendCalculateRec != null) {
			affectNum = attendCalculateMapper.updateByPrimaryKeySelective(attendCalculateRec);
		} else {
			attendCalculateRec=new AttendCalculate();
			attendCalculateRec.setId(UUIDGenerator.getUUID());
			attendCalculateRec.setCreateTime(new Date());
			attendCalculateRec.setStatus("1");
			attendCalculateRec.setSignDate(signOutRecord.getSignDate());
			attendCalculateRec.setEmpId(signOutRecord.getEmpId());
			affectNum = attendCalculateMapper.insertSelective(attendCalculateRec);
		}
		return affectNum;
	}
			
			
	// 第二段班考勤计算
	public int secondSignOut(SignRecord signOutRecord, List<Map<String, Object>> attendInfoList, SignRecord signInfo,String type)
			throws Exception {
		int affectNum = 0;

		// 时间累计
		Calendar gcFirOn =new GregorianCalendar();
		Calendar gcFirOff =new GregorianCalendar();
		Calendar gcFirOver =new GregorianCalendar();
		Calendar gcSecOff =new GregorianCalendar();
		Calendar gcSecOn =new GregorianCalendar();
		Calendar gcSecOver =new GregorianCalendar();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		// 查询当前打卡员工考勤是否结算过
        AttendCalculate attendCalculateRec=attendCalculateMapper.selectAttendCalculateInfoByEmpId(signOutRecord.getEmpId(), signOutRecord.getSignDate());
        
		
		// 上班时间
		Date workOnTime1=fmt.parse(attendInfoList.get(0).get("workOnTime").toString());
		// 下班时间
		Date workOffTime1=fmt.parse(attendInfoList.get(0).get("workOffTime").toString());
		// 上班时间
		Date workOnTime2=fmt.parse(attendInfoList.get(1).get("workOnTime").toString());
		// 下班时间
		Date workOffTime2=fmt.parse(attendInfoList.get(1).get("workOffTime").toString());
		// 加班时间节点（下班后多久开始算加班）
		String afterOvertime=attendInfoList.get(0).get("afterOvertime").toString();
		// 允许的最晚的签到时间
		String allowLateMM=attendInfoList.get(0).get("allowLate").toString();
		// 允许的最早的签退时间
		String allowEarlyMM=attendInfoList.get(0).get("allowLate").toString();
		
		
		gcFirOn.setTime(workOnTime1);
		gcFirOn.add(GregorianCalendar.MINUTE,Integer.parseInt(allowLateMM));
		gcFirOff.setTime(workOffTime1);
		gcFirOff.add(GregorianCalendar.MINUTE,-Integer.parseInt(allowEarlyMM));
		gcFirOver.setTime(workOffTime1);
		gcFirOver.add(GregorianCalendar.MINUTE,Integer.parseInt(afterOvertime));
		workOnTime1=gcFirOver.getTime();//允许最晚上班时间
		workOffTime1=gcFirOver.getTime();//允许最早下班打卡时间
        Date startOvertime1=gcFirOver.getTime();//加班开始时间
        
        gcSecOn.setTime(workOnTime1);
        gcSecOn.add(GregorianCalendar.MINUTE,Integer.parseInt(allowLateMM));
        gcSecOff.setTime(workOffTime1);
        gcSecOff.add(GregorianCalendar.MINUTE,-Integer.parseInt(allowEarlyMM));
		gcSecOver.setTime(workOffTime1);
		gcSecOver.add(GregorianCalendar.MINUTE,Integer.parseInt(afterOvertime));
		workOnTime1=gcSecOver.getTime();//允许最晚上班时间
		workOffTime1=gcSecOver.getTime();//允许最早下班打卡时间
        Date startOvertime2=gcSecOver.getTime();//加班开始时间
        
        
        Map<String , Date> map=new HashMap<String , Date>();
        map.put("workOnTime1", workOnTime1);
        map.put("workOffTime1", workOffTime1);
        map.put("workOnTime2", workOnTime2);
        map.put("workOffTime2", workOffTime2);
        map.put("workOnTime3", null);
        map.put("workOffTime3", null);
        map.put("startOvertime1", startOvertime1);
        map.put("startOvertime2", startOvertime2);
        map.put("startOvertime3", null);
        
        if(attendCalculateRec!=null){
        	attendCalculateRec.setDelayNum(0);
            attendCalculateRec.setDelayTimes(new BigDecimal(0));
            attendCalculateRec.setEarlyNum(0);
            attendCalculateRec.setEarlyTimes(new BigDecimal(0));
            attendCalculateRec.setOvertimeNum(0);
            attendCalculateRec.setOvertimeTimes(new BigDecimal(0));
            attendCalculateRec.setWorkTimes(new BigDecimal(0));
        }else{
        	attendCalculateRec=new AttendCalculate();
        }
        attendCalculateRec=everySignAfreshCalculate(attendCalculateRec, signInfo, signOutRecord,map,CommonConstant.两班制.getValue(),type);
        if (attendCalculateRec != null) {
			affectNum = attendCalculateMapper.updateByPrimaryKeySelective(attendCalculateRec);
		} else {
			attendCalculateRec=new AttendCalculate();
			attendCalculateRec.setId(UUIDGenerator.getUUID());
			attendCalculateRec.setCreateTime(new Date());
			attendCalculateRec.setStatus("1");
			attendCalculateRec.setSignDate(signOutRecord.getSignDate());
			attendCalculateRec.setEmpId(signOutRecord.getEmpId());
			affectNum = attendCalculateMapper.insertSelective(attendCalculateRec);
		}
		return affectNum;
	}
		
	//第三段班考勤计算
	public  int thirdSignOut(SignRecord signOutRecord,
			List<Map<String, Object>> attendInfoList,SignRecord signInfo,String type)throws Exception{
		int affectNum=0;
		//时间累计
		Calendar gcFirOn =new GregorianCalendar();
		Calendar gcFirOff =new GregorianCalendar();
		Calendar gcFirOver =new GregorianCalendar();
		Calendar gcSecOff =new GregorianCalendar();
		Calendar gcSecOn =new GregorianCalendar();
		Calendar gcSecOver =new GregorianCalendar();
		Calendar gcThirOn =new GregorianCalendar();
		Calendar gcThirOff =new GregorianCalendar();
		Calendar gcThirOver =new GregorianCalendar();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		// 查询当前打卡员工考勤是否结算过
        AttendCalculate attendCalculateRec=attendCalculateMapper.selectAttendCalculateInfoByEmpId(signOutRecord.getEmpId(), signOutRecord.getSignDate());
        
		// 上班时间
		Date workOnTime3=fmt.parse(attendInfoList.get(2).get("workOnTime").toString());
		// 下班时间
		Date workOffTime3=fmt.parse(attendInfoList.get(2).get("workOffTime").toString());
		// 上班时间
		Date workOnTime1=fmt.parse(attendInfoList.get(0).get("workOnTime").toString());
		// 下班时间
		Date workOffTime1=fmt.parse(attendInfoList.get(0).get("workOffTime").toString());
		// 上班时间
		Date workOnTime2=fmt.parse(attendInfoList.get(1).get("workOnTime").toString());
		// 下班时间
		Date workOffTime2=fmt.parse(attendInfoList.get(1).get("workOffTime").toString());
		// 加班时间节点（下班后多久开始算加班）
		String afterOvertime=attendInfoList.get(0).get("afterOvertime").toString();
		// 允许的最晚的签到时间
		String allowLateMM=attendInfoList.get(0).get("allowLate").toString();
		// 允许的最早的签退时间
		String allowEarlyMM=attendInfoList.get(0).get("allowLate").toString();
		
		
		gcFirOn.setTime(workOnTime1);
		gcFirOn.add(GregorianCalendar.MINUTE,Integer.parseInt(allowLateMM));
		gcFirOff.setTime(workOffTime1);
		gcFirOff.add(GregorianCalendar.MINUTE,-Integer.parseInt(allowEarlyMM));
		gcFirOver.setTime(workOffTime1);
		gcFirOver.add(GregorianCalendar.MINUTE,Integer.parseInt(afterOvertime));
		workOnTime1=gcFirOver.getTime();//允许最晚上班时间
		workOffTime1=gcFirOver.getTime();//允许最早下班打卡时间
        Date startOvertime1=gcFirOver.getTime();//加班开始时间
        
        gcSecOn.setTime(workOnTime1);
        gcSecOn.add(GregorianCalendar.MINUTE,Integer.parseInt(allowLateMM));
        gcSecOff.setTime(workOffTime1);
        gcSecOff.add(GregorianCalendar.MINUTE,-Integer.parseInt(allowEarlyMM));
		gcSecOver.setTime(workOffTime1);
		gcSecOver.add(GregorianCalendar.MINUTE,Integer.parseInt(afterOvertime));
		workOnTime1=gcSecOver.getTime();//允许最晚上班时间
		workOffTime1=gcSecOver.getTime();//允许最早下班打卡时间
        Date startOvertime2=gcSecOver.getTime();//加班开始时间
        
        gcThirOn.setTime(workOnTime1);
        gcThirOn.add(GregorianCalendar.MINUTE,Integer.parseInt(allowLateMM));
		gcThirOff.setTime(workOffTime1);
		gcThirOff.add(GregorianCalendar.MINUTE,-Integer.parseInt(allowEarlyMM));
		gcThirOver.setTime(workOffTime1);
		gcThirOver.add(GregorianCalendar.MINUTE,Integer.parseInt(afterOvertime));
		workOnTime1=gcThirOver.getTime();//允许最晚上班时间
		workOffTime1=gcThirOver.getTime();//允许最早下班打卡时间
        Date startOvertime3=gcThirOver.getTime();//加班开始时间
        
        Map<String , Date> map=new HashMap<String , Date>();
        map.put("workOnTime1", workOnTime1);
        map.put("workOffTime1", workOffTime1);
        map.put("workOnTime2", workOnTime2);
        map.put("workOffTime2", workOffTime2);
        map.put("workOnTime3", workOnTime3);
        map.put("workOffTime3", workOffTime3);
        map.put("startOvertime1", startOvertime1);
        map.put("startOvertime2", startOvertime2);
        map.put("startOvertime3", startOvertime3);
        if (attendCalculateRec != null) {
	        attendCalculateRec.setDelayNum(0);
	        attendCalculateRec.setDelayTimes(new BigDecimal(0));
	        attendCalculateRec.setEarlyNum(0);
	        attendCalculateRec.setEarlyTimes(new BigDecimal(0));
	        attendCalculateRec.setOvertimeNum(0);
	        attendCalculateRec.setOvertimeTimes(new BigDecimal(0));
	        attendCalculateRec.setWorkTimes(new BigDecimal(0));
        }else{
        	attendCalculateRec=new AttendCalculate();
        }
        attendCalculateRec=everySignAfreshCalculate(attendCalculateRec, signInfo, signOutRecord,map,CommonConstant.三班制.getValue(),type);
        if (attendCalculateRec != null) {
			affectNum = attendCalculateMapper.updateByPrimaryKeySelective(attendCalculateRec);
		} else {
			attendCalculateRec=new AttendCalculate();
			attendCalculateRec.setId(UUIDGenerator.getUUID());
			attendCalculateRec.setCreateTime(new Date());
			attendCalculateRec.setStatus("1");
			attendCalculateRec.setSignDate(signOutRecord.getSignDate());
			attendCalculateRec.setEmpId(signOutRecord.getEmpId());
			affectNum = attendCalculateMapper.insertSelective(attendCalculateRec);
		}
        return affectNum;
	}

		/* (non Javadoc) 
		 * @Title: selectAttendGroupEmpSignInfoByAgId
		 * @Description: TODO
		 * @param agId
		 * @return
		 * @throws Exception 
		 * @see com.faceRecog.manage.service.SignRecordService#selectAttendGroupEmpSignInfoByAgId(java.lang.String) 
		 */ 
		@Override
		public Map<String, Object> selectAttendGroupEmpSignInfoByEmpId(String empId,String yesterDay) throws Exception {
			 
			return signRecordMapper.selectAttendGroupEmpSignInfoByEmpId(empId,yesterDay);
		}
	
	
	
		/* (non Javadoc) 
		 * @Title: insertFreeSign
		 * @Description: TODO
		 * @param signTime
		 * @param signDate
		 * @param empId
		 * @return
		 * @throws Exception 
		 * @see com.faceRecog.manage.service.SignRecordService#insertFreeSign(java.util.Date, java.lang.String, java.lang.String) 
		 */ 
		@Override
		public Result insertFreeSign(Date signTime, String signDate, String empId,String signType) throws Exception {
			Result result=null;
			int affectNum=0;
			int insertResult=0;
			
			// 保存员工原始打卡记录
			OriginalSignRecord  originalSignRecord=new OriginalSignRecord();
			originalSignRecord.setId(UUIDGenerator.getUUID());
			originalSignRecord.setCreateTime(new Date());
			originalSignRecord.setEmpId(empId);
			originalSignRecord.setSignTime(signTime);
			insertResult=originalSignRecordMapper.insertSelective(originalSignRecord);
			
			if(insertResult<0){
				return Result.responseError("打卡失败");
			}
			//判断员工当天有没有打卡
	        SignRecord  signRec = signRecordMapper.selectSignRecordByEmpId(empId,signDate);
	        SignRecord signRecord =new SignRecord();
	        if (signRec==null) {
	        	signRecord.setId(UUIDGenerator.getUUID());
	        	signRecord.setCreateTime(new Date());
	        	signRecord.setEmpId(empId);
	        	signRecord.setStatus("1");
	        	signRecord.setSignInOne(signTime);
	        	signRecord.setSignDate(signDate);
	        	insertResult= signRecordMapper.insertSelective(signRecord);
	            if (insertResult < 0) {
	            	 result = Result.responseError("打卡失败");
	            }
	        }else {
	        	signRecord.setId(signRec.getId());
	        	signRecord.setSignOutOne(signTime);
	            insertResult = signRecordMapper.updateByPrimaryKeySelective(signRecord);
	            if (insertResult < 0) {
	            	result = Result.responseError("打卡失败");
	            }
	        }
	        
	        // 查询当前传入日期的人员是否结算考勤 在signDate日期
	        AttendCalculate attendCalculateRec=attendCalculateMapper.selectAttendCalculateInfoByEmpId(empId, signDate);
	        if(attendCalculateRec!=null){
	    	 	if("inGroup".equals(signType)){
		        	attendCalculateRec.setIsAttendGroup(-1);//是否在考勤组中 在考勤组但是未排班
		        }else if("notInGroup".equals(signType)){
		        	attendCalculateRec.setIsAttendGroup(0);//是否在考勤组中 不在
		        }
	        	affectNum=attendCalculateMapper.updateByPrimaryKeySelective(attendCalculateRec);
	        }else{
	        	attendCalculateRec=new AttendCalculate();
	        	if("inGroup".equals(signType)){
		        	attendCalculateRec.setIsAttendGroup(-1);//是否在考勤组中 在考勤组但是未排班
		        }else if("notInGroup".equals(signType)){
			        attendCalculateRec.setIsAttendGroup(0);//是否在考勤组中
		        }
	        	attendCalculateRec.setId(UUIDGenerator.getUUID());
	        	attendCalculateRec.setCreateTime(new Date());
	        	attendCalculateRec.setStatus("1");
	        	attendCalculateRec.setSignDate(signDate);
	        	attendCalculateRec.setEmpId(empId);
	        	affectNum=attendCalculateMapper.insertSelective(attendCalculateRec);
	        }
	        if(affectNum<0){
	        	return Result.responseError("打卡失败");
	        }
	        result = Result.responseSuccess("打卡成功");
			return result;
		}
	
		
		/**
		 * 
		* @Title: insertOriginalSignRecord 
		* @Description: 新增员工原始打卡记录 
		* @param signRecord
		* @return int
		* @author xya
		* @date 2019年5月23日下午3:11:02
		 */
		public int insertOriginalSignRecord(SignRecord signRecord,String type){
			 OriginalSignRecord  originalSignRecord=new OriginalSignRecord();
			 originalSignRecord.setId(UUIDGenerator.getUUID());
			 originalSignRecord.setCreateTime(new Date());
			 originalSignRecord.setEmpId(signRecord.getEmpId());
			 originalSignRecord.setDateStr(signRecord.getSignDate());
			 if(CommonConstant.签退.getValue().equals(type)){// 签退卡
				 if(StrKit.notNull(signRecord.getSignOutOne())){
					 originalSignRecord.setSignTime(signRecord.getSignOutOne());
				 }else if(StrKit.notNull(signRecord.getSignOutTwo())){
					 originalSignRecord.setSignTime(signRecord.getSignOutTwo());
				 }else if(StrKit.notNull(signRecord.getSignOutThree())){
					 originalSignRecord.setSignTime(signRecord.getSignOutThree());
				 }
			 }else if(CommonConstant.签到.getValue().equals(type)){
				 if(StrKit.notNull(signRecord.getSignInOne())){
					 originalSignRecord.setSignTime(signRecord.getSignInOne());
				 }else if(StrKit.notNull(signRecord.getSignInTwo())){
					 originalSignRecord.setSignTime(signRecord.getSignInTwo());
				 }else if(StrKit.notNull(signRecord.getSignInThree())){
					 originalSignRecord.setSignTime(signRecord.getSignInThree());
				 }
			 }
			return originalSignRecordMapper.insertSelective(originalSignRecord);
		}
		
		/**
		 * 	
		* @Title: everySignAfreshCalculate 
		* @Description: 每次打卡重新更新考勤数据 
		* @param attendCalculate
		* @param signInfoRec
		* @param currSignInfo
		* @param attendInfo
		* @param type
		* @param signType
		* @return AttendCalculate
		* @author xya
		* @date 2019年6月12日上午9:56:51
		 */
		public AttendCalculate everySignAfreshCalculate(AttendCalculate attendCalculate,
				SignRecord signInfoRec,SignRecord currSignInfo,Map<String, Date> attendInfo,String type,String signType){
			
			Date workOnTime1=attendInfo.get("workOnTime1");//允许最晚上班时间
			Date workOffTime1=attendInfo.get("workOffTime1");//允许最早下班打卡时间
			Date workOnTime2=attendInfo.get("workOnTime2");//允许最晚上班时间
			Date workOffTime2=attendInfo.get("workOffTime2");//允许最早下班打卡时间
			Date workOnTime3=attendInfo.get("workOnTime3");//允许最晚上班时间
			Date workOffTime3=attendInfo.get("workOffTime3");//允许最早下班打卡时间
	        Date startOvertime1=attendInfo.get("startOvertime1");//加班开始时间
	        Date startOvertime2=attendInfo.get("startOvertime2");//加班开始时间
	        Date startOvertime3=attendInfo.get("startOvertime3");//加班开始时间
	        
	        // 签到记录
        	Date signInTime1 =signInfoRec==null?currSignInfo.getSignInOne():signInfoRec.getSignInOne();
 			Date signInTime2 =signInfoRec==null?currSignInfo.getSignInTwo():signInfoRec.getSignInTwo();
 			Date signInTime3 =signInfoRec==null?currSignInfo.getSignInThree():signInfoRec.getSignInThree();
	       
			// 签退记录
			Date signOutTime1= signInfoRec==null?currSignInfo.getSignOutOne():signInfoRec.getSignOutOne();
			Date signOutTime2= signInfoRec==null?currSignInfo.getSignOutTwo():signInfoRec.getSignOutTwo();
			Date signOutTime3= signInfoRec==null?currSignInfo.getSignOutThree():signInfoRec.getSignOutThree();
			Date currSignOutTime1= currSignInfo.getSignOutOne();
			Date currSignOutTime2= currSignInfo.getSignOutTwo();
			Date currSignOutTime3= currSignInfo.getSignOutThree();
			if(CommonConstant.常规班.getValue().equals(type)){
		        
		        if(signType.equals(CommonConstant.签到.getValue())){
					if(StrKit.notNull(signInTime1)){
						// 解析签到
						Map<String, Long> diffSignIn=CommUtil.getDateDifference(workOnTime1, signInTime1);
						// 迟到
						if (diffSignIn.get("min") > 0) {
							attendCalculate.setDelayTimes(new BigDecimal(diffSignIn.get("min")));
							attendCalculate.setDelayNum(1);
						}
					}
		        	
				}else if(signType.equals(CommonConstant.签退.getValue())){
					 if(StrKit.notNull(currSignOutTime1)){
						    // 解析签退
							Map<String, Long> diffSignOut=CommUtil.getDateDifference(workOffTime1, currSignOutTime1);
							// 解析加班时长
							Map<String, Long> diffOverTime=CommUtil.getDateDifference(startOvertime1, currSignOutTime1);
							// 当天的上班记录不为空
							if(StrKit.notNull(signInTime1)){
								// 出勤总时长计算
								Map<String, Long> diffWorkTimeDiff=CommUtil.getDateDifference(signInTime1, currSignOutTime1);
								// 出勤总时长
								if (diffWorkTimeDiff.get("min") > 0) {
									attendCalculate.setWorkTimes(new BigDecimal(diffWorkTimeDiff.get("min")));
								}
							}
							// 早退
							if (diffSignOut.get("min") > 0) {
								attendCalculate.setEarlyTimes(new BigDecimal(diffSignOut.get("min")));
								attendCalculate.setEarlyNum(1);
							}
							// 加班时长计算
							if (diffOverTime.get("min") > 0) {
								attendCalculate.setOvertimeTimes(new BigDecimal(diffOverTime.get("min")));
								attendCalculate.setOvertimeNum(1);
							}
					 }
					
				}
			}else if(CommonConstant.两班制.getValue().equals(type)){
		        if(signType.equals(CommonConstant.签到.getValue())){
					if(StrKit.notNull(signInTime1)){
						// 解析第一段签到
						Map<String, Long> diffSignIn1=CommUtil.getDateDifference(workOnTime1, signInTime1);// 第一段时差
						// 迟到
						if (diffSignIn1.get("min") > 0) {
							attendCalculate.setDelayTimes(new BigDecimal(diffSignIn1.get("min") ));
							attendCalculate.setDelayNum(1);
						}
					}
					if(StrKit.notNull(signInTime2)){
						// 解析第二段签到
						Map<String, Long> diffSignIn2=CommUtil.getDateDifference(workOnTime2, signInTime2);// 第二段时差
						// 迟到
						if (diffSignIn2.get("min") > 0) {
							attendCalculate.setDelayTimes(new BigDecimal(diffSignIn2.get("min")).add(attendCalculate.getDelayTimes()));
							attendCalculate.setDelayNum(attendCalculate.getDelayNum()+1);
						}
					}
					
				}else if(signType.equals(CommonConstant.签退.getValue())){
					// 第一段
					if(StrKit.notNull(signOutTime1)){
						// 解析签退第一段
						Map<String, Long> diffSignOut=CommUtil.getDateDifference(workOffTime1, signOutTime1);
						// 解析加班时长
						Map<String, Long> diffOverTime=CommUtil.getDateDifference(startOvertime1,signOutTime1);
						// 早退
						if (diffSignOut.get("min") > 0) {
							attendCalculate.setEarlyTimes(new BigDecimal(diffSignOut.get("min")));
							attendCalculate.setEarlyNum(1);
						}
						
						// 加班时长计算
						if (diffOverTime.get("min") > 0) {
							attendCalculate.setOvertimeTimes(new BigDecimal(diffOverTime.get("min")));
							attendCalculate.setOvertimeNum(1);
						}
						
						// 出勤时长计算 当天的上班记录不为空
						if(StrKit.notNull(signInTime1)){
							// 出勤总时长计算
							Map<String, Long> diffWorkTimeDiff=CommUtil.getDateDifference(signInTime1, currSignOutTime1);
							// 出勤总时长
							if (diffWorkTimeDiff.get("min") > 0) {
								attendCalculate.setWorkTimes(new BigDecimal(diffWorkTimeDiff.get("min")));
							}
						}
					}
					
					// 解析 第二段签退
					if(StrKit.notNull(signOutTime2)){
						// 解析签退  第二段
						Map<String, Long> diffSignOut=CommUtil.getDateDifference(workOffTime2, signOutTime2);
						// 解析加班时长
						Map<String, Long> diffOverTime=CommUtil.getDateDifference(startOvertime2,signOutTime2);
						// 早退
						if (diffSignOut.get("min") > 0) {
							attendCalculate.setEarlyTimes(new BigDecimal(diffSignOut.get("min")).add(attendCalculate.getEarlyTimes()));
							attendCalculate.setEarlyNum(attendCalculate.getEarlyNum()+1);
						}
						
						// 加班时长计算
						if (diffOverTime.get("min") > 0) {
							attendCalculate.setOvertimeTimes(new BigDecimal(diffOverTime.get("min")).add(attendCalculate.getOvertimeTimes()));
							attendCalculate.setOvertimeNum(attendCalculate.getOvertimeNum()+1);
						}
						// 出勤时长计算 当天的上班记录不为空
						if(StrKit.notNull(signInTime2)){
							// 出勤总时长计算
							Map<String, Long> diffWorkTimeDiff=CommUtil.getDateDifference(signInTime2, currSignOutTime2);
							// 出勤总时长
							if (diffWorkTimeDiff.get("min") > 0) {
								attendCalculate.setWorkTimes(new BigDecimal(diffWorkTimeDiff.get("min")).add(attendCalculate.getWorkTimes()));
							}
						}
					}
					
				}
			}else if(CommonConstant.三班制.getValue().equals(type)){
				if(signType.equals(CommonConstant.签到.getValue())){
					if(StrKit.notNull(signInTime1)){
						// 解析第一段签到
						Map<String, Long> diffSignIn=CommUtil.getDateDifference(workOnTime1, signInTime1);// 第一段时差
						// 迟到
						if (diffSignIn.get("min") > 0) {
							attendCalculate.setDelayTimes(new BigDecimal(diffSignIn.get("min") ));
							attendCalculate.setDelayNum(1);
						}
					}
					if(StrKit.notNull(signInTime2)){
						// 解析第二段签到
						Map<String, Long> diffSignIn=CommUtil.getDateDifference(workOnTime2, signInTime2);// 第二段时差
						// 迟到
						if (diffSignIn.get("min") > 0) {
							attendCalculate.setDelayTimes(new BigDecimal(diffSignIn.get("min")).add(attendCalculate.getDelayTimes()));
							attendCalculate.setDelayNum(attendCalculate.getDelayNum()+1);
						}
					}
					//第三段 打卡签到
					if(StrKit.notNull(signInTime3)){
						// 解析第三段签到
						Map<String, Long> diffSignIn=CommUtil.getDateDifference(workOnTime3, signInTime3);// 第三段时差
						// 迟到
						if (diffSignIn.get("min") > 0) {
							attendCalculate.setDelayTimes(new BigDecimal(diffSignIn.get("min")).add(attendCalculate.getDelayTimes()));
							attendCalculate.setDelayNum(attendCalculate.getDelayNum()+1);
						}
					}
					
				}else if(signType.equals(CommonConstant.签退.getValue())){
					// 第一段
					if(StrKit.notNull(signOutTime1)){
						// 解析签退第一段
						Map<String, Long> diffSignOut=CommUtil.getDateDifference(workOffTime1, signOutTime1);
						// 解析加班时长
						Map<String, Long> diffOverTime=CommUtil.getDateDifference(startOvertime1,signOutTime1);
						// 早退
						if (diffSignOut.get("min") > 0) {
							attendCalculate.setEarlyTimes(new BigDecimal(diffSignOut.get("min")));
							attendCalculate.setEarlyNum(1);
						}
						
						// 加班时长计算
						if (diffOverTime.get("min") > 0) {
							attendCalculate.setOvertimeTimes(new BigDecimal(diffOverTime.get("min")));
							attendCalculate.setOvertimeNum(1);
						}
						
						// 出勤时长计算 当天的上班记录不为空
						if(StrKit.notNull(signInTime1)){
							// 出勤总时长计算
							Map<String, Long> diffWorkTimeDiff=CommUtil.getDateDifference(signInTime1, signOutTime1);
							// 出勤总时长
							if (diffWorkTimeDiff.get("min") > 0) {
								attendCalculate.setWorkTimes(new BigDecimal(diffWorkTimeDiff.get("min")));
							}
						}
					}
					
					// 解析 第二段签退
					if(StrKit.notNull(signOutTime2)){
						// 解析签退  第二段
						Map<String, Long> diffSignOut=CommUtil.getDateDifference(workOffTime2, signOutTime2);
						// 解析加班时长
						Map<String, Long> diffOverTime=CommUtil.getDateDifference(startOvertime2,signOutTime2);
						// 早退
						if (diffSignOut.get("min") > 0) {
							attendCalculate.setEarlyTimes(new BigDecimal(diffSignOut.get("min")).add(attendCalculate.getEarlyTimes()));
							attendCalculate.setEarlyNum(attendCalculate.getEarlyNum()+1);
						}
						
						// 加班时长计算
						if (diffOverTime.get("min") > 0) {
							attendCalculate.setOvertimeTimes(new BigDecimal(diffOverTime.get("min")).add(attendCalculate.getOvertimeTimes()));
							attendCalculate.setOvertimeNum(attendCalculate.getOvertimeNum()+1);
						}
						// 出勤时长计算 当天的上班记录不为空
						if(StrKit.notNull(signInTime2)){
							// 出勤总时长计算
							Map<String, Long> diffWorkTimeDiff=CommUtil.getDateDifference(signInTime2, signOutTime2);
							// 出勤总时长
							if (diffWorkTimeDiff.get("min") > 0) {
								attendCalculate.setWorkTimes(new BigDecimal(diffWorkTimeDiff.get("min")).add(attendCalculate.getWorkTimes()));
							}
						}
					}
					
					// 解析 第三段签退
					if(StrKit.notNull(currSignOutTime3)){
						// 解析签退  第三段
						Map<String, Long> diffSignOut=CommUtil.getDateDifference(workOffTime3, currSignOutTime3);
						// 解析加班时长
						Map<String, Long> diffOverTime=CommUtil.getDateDifference(startOvertime3,currSignOutTime3);
						// 早退
						if (diffSignOut.get("min") > 0) {
							attendCalculate.setEarlyTimes(new BigDecimal(diffSignOut.get("min")).add(attendCalculate.getEarlyTimes()));
							attendCalculate.setEarlyNum(attendCalculate.getEarlyNum()+1);
						}
						
						// 加班时长计算
						if (diffOverTime.get("min") > 0) {
							attendCalculate.setOvertimeTimes(new BigDecimal(diffOverTime.get("min")).add(attendCalculate.getOvertimeTimes()));
							attendCalculate.setOvertimeNum(attendCalculate.getOvertimeNum()+1);
						}
						// 出勤时长计算 当天的上班记录不为空
						if(StrKit.notNull(signInTime3)){
							// 出勤总时长计算
							Map<String, Long> diffWorkTimeDiff=CommUtil.getDateDifference(signInTime3, currSignOutTime3);
							// 出勤总时长
							if (diffWorkTimeDiff.get("min") > 0) {
								attendCalculate.setWorkTimes(new BigDecimal(diffWorkTimeDiff.get("min")).add(attendCalculate.getWorkTimes()));
							}
						}
					}
					
				}
			}
			return attendCalculate;
		}
		
		
		public List<String> signTimeNullJudge(SignRecord signRec,String attendType){
			 List<String> signRecList =new ArrayList<String>();
			if(CommonConstant.常规班.getValue().equals(attendType)){
				if(!StrKit.notNull(signRec.getSignInOne())){
					signRecList.add("signInOne");
				}
			}else if(CommonConstant.两班制.getValue().equals(attendType)){
				if(!StrKit.notNull(signRec.getSignInOne())){
					signRecList.add("signInOne");
				}
				if(!StrKit.notNull(signRec.getSignInTwo())){
					signRecList.add("signInTwo");
				}
			}else if(CommonConstant.三班制.getValue().equals(attendType)){
				if(!StrKit.notNull(signRec.getSignInOne())){
					signRecList.add("signInOne");
				}
				if(!StrKit.notNull(signRec.getSignInTwo())){
					signRecList.add("signInTwo");
				}
				if(!StrKit.notNull(signRec.getSignInThree())){
					signRecList.add("signInThree");
				}
			}
			return signRecList;
		}
		
		
		public static void main(String[] args) {
			Map<String , Object> map=new HashMap<String , Object>();
	        map.put("workOnTime1", 12);
	        map.put("workOffTime1", 12);
	        map.put("workOnTime2", null);
	        map.put("workOffTime2", null);
	        map.put("workOnTime3", null);
	        map.put("workOffTime3", null);
	        map.put("startOvertime1", 23);
	        map.put("startOvertime2", null);
	        map.put("startOvertime3", null);
	        Map<String , Long> map1=new HashMap<String , Long>();
	        map1.put("min", 18l);
	        BigDecimal bg=new BigDecimal((Integer)map.get("startOvertime1"));
	        System.out.println(new BigDecimal(map1.get("min")));
	        
	        String img="http://47.107.50.81:8081//upload/temp/images//1140797137009770496.jpg,http://47.107.50.81:8081///upload/temp/images//1140801116842229760.jpg,http://47.107.50.81:8081///upload/temp/images//1140801250166571008.jpg,http://47.107.50.81:8081///upload/temp/images//1140801250191736832.jpg,http://47.107.50.81:8081///upload/temp/images//1140801250212708352.jpg,http://47.107.50.81:8081///upload/temp/images//1140801250242068480.jpg,http://47.107.50.81:8081///upload/temp/images//1140801250254651392.jpg,http://47.107.50.81:8081///upload/temp/images//1140801415539589120.jpg,http://47.107.50.81:8081///upload/temp/images//1140801415598309376.jpg";
	        try {
				Blob b = new SerialBlob(img.getBytes("UTF-8"));
				System.out.println(b);
			} catch (UnsupportedEncodingException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//String 转 blob  
		}
}
