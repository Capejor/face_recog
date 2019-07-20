/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service.impl 
 * @author: xya
 * @date: 2019年6月15日 下午2:46:54 
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.faceRecog.manage.mapper.AttendGroupMapper;
import com.faceRecog.manage.mapper.EmployeeMapper;
import com.faceRecog.manage.mapper.OriginalSignRecordMapper;
import com.faceRecog.manage.model.AttendGroupAttendDance;
import com.faceRecog.manage.model.OriginalSignRecord;
import com.faceRecog.manage.model.serverVO.EmpServer;
import com.faceRecog.manage.redis.RedisCache;
import com.faceRecog.manage.redis.RedisEnum;
import com.faceRecog.manage.redis.RedisUtils;
import com.faceRecog.manage.service.OriginalSignRecordService;
import com.faceRecog.manage.util.CommUtil;
import com.faceRecog.manage.util.constantUtils.CommonConstant;

import net.sf.json.JSONObject;
import redis.clients.jedis.SortingParams;

/** 
 * @ClassName: OriginalSignRecordServiceImpl 
 * @Description: TODO
 * @author: xya
 * @date: 2019年6月15日 下午2:46:54  
 */
@Service
public class OriginalSignRecordServiceImpl implements OriginalSignRecordService{

	@Autowired
	private RedisCache redisCache;
	
	@Autowired
	private EmployeeMapper employeeMapper;
	
	@Autowired
	private OriginalSignRecordMapper originalSignRecordMapper;
	
	@Autowired
	private AttendGroupMapper attendGroupMapper;
	
	/* (non Javadoc) 
	 * @Title: selectPageOrigSignInfo
	 * @Description: TODO
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.OriginalSignRecordService#selectPageOrigSignInfo() 
	 */ 
	@Override
	public List<Map<String, Object>> selectPageOrigSignInfo() throws Exception {
		String empIds[]={};
		Map<String, Object> origSignMap=new HashMap<String, Object>();
		List<Map<String, Object>> origSignList=new ArrayList<Map<String, Object>>();
		
		List<Map<String, Object>>empList=employeeMapper.selectEmpInfoByParam(empIds);
		if(empList!=null && empList.size()>0){
			String startDate="2019-06-01";
			String endDate="2019-06-30";
			//List<Map<String, Object>> dateMap=CommUtil.getDays(startDate, endDate);
			for(Map<String, Object> empIfo:empList){
				// 查询时间范围内所有的原始打卡记录
				List<OriginalSignRecord> origSignRecLst=originalSignRecordMapper.selectPageOrigSignInfo(startDate, endDate,(String)empIfo.get("id"));
				if(origSignRecLst!=null && origSignRecLst.size()>0){
					for(OriginalSignRecord originalSignRecord :origSignRecLst){
						// 获取当前日期的考勤班次信息
						List<Map<String, Object>> attendInfoLst=attendGroupMapper.selectAttendGroupAttendDanceInfo(originalSignRecord.getEmpId(), originalSignRecord.getDateStr());
						if(attendInfoLst!=null && attendInfoLst.size()>0){
							// 计算打卡时间是否正常
							origSignMap=caculteSignTimeState(attendInfoLst,originalSignRecord);
							origSignMap.put("empId", empIfo.get("id"));
							origSignMap.put("empName", empIfo.get("name"));
							origSignMap.put("deptName", empIfo.get("deptName"));
							origSignMap.put("duty", empIfo.get("duty"));
							origSignMap.put("signDate", originalSignRecord.getDateStr());
						}else{// 未排班
							//origSignMap=caculteSignTimeState(attendInfoLst,originalSignRecord);
							origSignMap.put("empId", empIfo.get("id"));
							origSignMap.put("empName", empIfo.get("name"));
							origSignMap.put("deptName", empIfo.get("deptName"));
							origSignMap.put("duty", empIfo.get("duty"));
							origSignMap.put("signDate", originalSignRecord.getDateStr());
							origSignMap.put("signTime", originalSignRecord.getSignTime());
							origSignMap.put("signResult", "未排班打卡");
						}
						origSignList.add(origSignMap);
					}
				}
			}
		}else{
			return null;
		}
		return origSignList;
	}

	
	
	public Map<String, Object> caculteSignTimeState(List<Map<String, Object>> attendInfoLst,
			OriginalSignRecord origSignRecord)throws Exception{
		Map<String, Object> map=new HashMap<String,Object>();
		
		String attendType=(String)attendInfoLst.get(0).get("type");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar tomCad =new GregorianCalendar();
		Calendar yesCad =new GregorianCalendar();
		tomCad.setTime(format.parse(origSignRecord.getDateStr()));
		yesCad.setTime(format.parse(origSignRecord.getDateStr()));
		tomCad.add(Calendar.DATE, 1);
		yesCad.add(Calendar.DATE, -1);
		String tom=format.format(tomCad.getTime());
		String yes=format.format(yesCad.getTime());
		List<Map<String, Object>> attendTomInfoList=attendGroupMapper.selectAttendGroupAttendDanceInfo(origSignRecord.getEmpId(),tom);
		List<Map<String, Object>> attendYesterInfoList=attendGroupMapper.selectAttendGroupAttendDanceInfo(origSignRecord.getEmpId(),yes);
		String tomSignInOneTime="";
		if(attendTomInfoList!=null && attendTomInfoList.size()>0){
			tomSignInOneTime=(String)attendTomInfoList.get(0).get("workOnTime");
		}else{
			String outAcross=(String)attendInfoLst.get(attendInfoLst.size()-1).get("outAcross");// 当前打卡所属班次是否跨班
			if(CommonConstant.次日.getValue().equals(outAcross)){
				tomSignInOneTime=tomCad+" 23:59";
			}else{
				tomSignInOneTime=origSignRecord.getDateStr()+" 23:59";
			}
		}
		String yesSignOutEndTime="";
		if(attendYesterInfoList!=null && attendYesterInfoList.size()>0){
			String outAcross=(String)attendYesterInfoList.get(attendYesterInfoList.size()-1).get("outAcross");// 当前打卡所属班次是否跨班
			if(CommonConstant.次日.getValue().equals(outAcross)){
				Calendar signEndOutTime =new GregorianCalendar();
				signEndOutTime.setTime(fmt.parse((String)attendYesterInfoList.get(attendYesterInfoList.size()-1).get("workOffTime")));
				signEndOutTime.add(Calendar.DATE, 1);
				yesSignOutEndTime=fmt.format(signEndOutTime.getTime());
			}
		}else{
			yesSignOutEndTime=yes+" 23:59";
		}
		map=calculateMilddle(attendInfoLst, attendType, tomSignInOneTime, yesSignOutEndTime,origSignRecord.getSignTime());
		return map;
	}
	
	
	public Map<String, Object> calculateMilddle(List<Map<String, Object>> attendInfoList,String attendType
			,String nextDaySignInOne,String beforDaySignOutEnd,Date signDate)throws ParseException{
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		// 历史打卡时间时间戳
		long signTime=signDate.getTime();
		// 前一天最后下班的时间戳
		long beforDaySignTime=df.parse(beforDaySignOutEnd).getTime();
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
		
		Map<String, Object> map=new HashMap<String, Object>();
		if(CommonConstant.常规班.getValue().equals(attendType)){
			Map<String, Object> attendDanceMap1=attendInfoList.get(0);
			
			long workOnTime1=df.parse((String)attendDanceMap1.get("workOnTime")).getTime();//
			long workOffTime1 = df.parse((String) attendDanceMap1.get("workOffTime")).getTime();// 下班时间
			long workOnTime2 =df.parse(nextDaySignInOne).getTime();// 下一天的上班时间  时间戳
			
			
			// 加班时间节点（下班后多久开始算加班）
			String afterOvertime = attendInfoList.get(0).get("afterOvertime").toString();
			// 允许的最晚的签到时间
			String allowLateMM = attendInfoList.get(0).get("allowLate").toString();
			// 允许的最早的签退时间
			String allowEarlyMM = attendInfoList.get(0).get("allowLate").toString();
			
			long lastWorkOnTime=workOnTime1+Integer.parseInt(allowLateMM)*60*1000;
			long earlyworkOffTime=workOffTime1-Integer.parseInt(allowEarlyMM)*60*1000;
			long WorkOverTime=workOffTime1+Integer.parseInt(afterOvertime)*60*1000;
			
			
			// 获取两个中间节点
			long signInOneTime=(workOffTime1-workOnTime1)/2;
			long signOutOneTime=(workOnTime2-workOffTime1)/2;
			long signInOne=signInOneTime+workOnTime1;//1559349000000+16200000
			long signOutOne=signOutOneTime+workOffTime1;//      16200000
			// 判断签到时间属于哪个班段
			if(beforDaySignTime<signTime && signTime<signInOne){
				if(signTime>lastWorkOnTime){
					map.put("signTime", attendDanceMap1.get("workOnTime"));
					map.put("signResult", "迟到");
				}else{
					map.put("signTime", attendDanceMap1.get("workOnTime"));
					map.put("signResult", "正常");
				}
			}else if(signInOne<=signTime && signTime<signOutOne){
				if(signTime<earlyworkOffTime){
					map.put("signTime", attendDanceMap1.get("workOffTime"));
					map.put("signResult", "早退");
				}else{
					map.put("signTime", attendDanceMap1.get("workOffTime"));
					map.put("signResult", "正常");
				}
			}
		}else if(CommonConstant.两班制.getValue().equals(attendType)){
			Map<String, Object> attendDanceMap1=attendInfoList.get(0);
			Map<String, Object> attendDanceMap2=attendInfoList.get(1);
			
			
			long workOnTime1=df.parse((String)attendDanceMap1.get("workOnTime")).getTime();//
			long workOffTime1 = df.parse((String) attendDanceMap1.get("workOffTime")).getTime();// 下班时间
			long workOnTime2=df.parse((String)attendDanceMap2.get("workOnTime")).getTime();//
			long workOffTime2 = df.parse((String) attendDanceMap2.get("workOffTime")).getTime();// 下班时间
			long workOnTime3 =df.parse(nextDaySignInOne).getTime();// 下一天的上班时间  时间戳
			
			
			// 加班时间节点（下班后多久开始算加班）
			String afterOvertime=attendInfoList.get(0).get("afterOvertime").toString();
			// 允许的最晚的签到时间
			String allowLateMM=attendInfoList.get(0).get("allowLate").toString();
			// 允许的最早的签退时间
			String allowEarlyMM=attendInfoList.get(0).get("allowLate").toString();
			
			
	        long lastWorkOnTime1=workOnTime1+Integer.parseInt(allowLateMM)*60*1000;
			long earlyworkOffTime1=workOffTime1-Integer.parseInt(allowEarlyMM)*60*1000;
			long WorkOverTime1=workOffTime1+Integer.parseInt(afterOvertime)*60*1000;
			
			long lastWorkOnTime2=workOnTime2+Integer.parseInt(allowLateMM)*60*1000;
			long earlyworkOffTime2=workOffTime2-Integer.parseInt(allowEarlyMM)*60*1000;
			long WorkOverTime2=workOffTime2+Integer.parseInt(afterOvertime)*60*1000;
			
			// 获取两个中间节点
			long signInOneTime=(workOffTime1-workOnTime1)/2;
			long signOutOneTime=(workOnTime2-workOffTime1)/2;
			long signInTwoTime=(workOffTime2-workOnTime2)/2;
			long signOutTwoTime=(workOnTime3-workOffTime2)/2;
			
			long signInOne= signInOneTime+workOnTime1;
			long signOutOne= signOutOneTime+workOffTime1;
			long signInTwo= signInTwoTime+workOnTime2;
			long signOutTwo=signOutTwoTime+workOffTime2;
			// 判断签到时间所属的班段范围
			if(beforDaySignTime<signTime && signTime<signInOne){
				if(signTime>lastWorkOnTime1){
					map.put("signTime", attendDanceMap1.get("workOnTime"));
					map.put("signResult", "迟到");
				}else{
					map.put("signTime", attendDanceMap1.get("workOnTime"));
					map.put("signResult", "正常");
				}
			}else if(signInOne<=signTime && signTime<signOutOne){
				if(signTime<earlyworkOffTime1){
					map.put("signTime", attendDanceMap1.get("workOffTime"));
					map.put("signResult", "早退");
				}else{
					map.put("signTime", attendDanceMap1.get("workOffTime"));
					map.put("signResult", "正常");
				}
			}else if(signOutOne<=signTime && signTime<signInTwo){
				if(signTime>lastWorkOnTime2){
					map.put("signTime", attendDanceMap2.get("workOnTime"));
					map.put("signResult", "迟到");
				}else{
					map.put("signTime", attendDanceMap2.get("workOnTime"));
					map.put("signResult", "正常");
				}
			}else if(signInTwo<=signTime && signTime<signOutTwo){
				if(signTime<earlyworkOffTime2){
					map.put("signTime", attendDanceMap2.get("workOffTime"));
					map.put("signResult", "早退");
				}else{
					map.put("signTime", attendDanceMap2.get("workOffTime"));
					map.put("signResult", "正常");
				}
			}
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
			
			// 加班时间节点（下班后多久开始算加班）
			String afterOvertime=attendInfoList.get(0).get("afterOvertime").toString();
			// 允许的最晚的签到时间
			String allowLateMM=attendInfoList.get(0).get("allowLate").toString();
			// 允许的最早的签退时间
			String allowEarlyMM=attendInfoList.get(0).get("allowLate").toString();
			
			
			long lastWorkOnTime1=workOnTime1+Integer.parseInt(allowLateMM)*60*1000;
			long earlyworkOffTime1=workOffTime1-Integer.parseInt(allowEarlyMM)*60*1000;
			long WorkOverTime1=workOffTime1+Integer.parseInt(afterOvertime)*60*1000;
			
			long lastWorkOnTime2=workOnTime2+Integer.parseInt(allowLateMM)*60*1000;
			long earlyworkOffTime2=workOffTime2-Integer.parseInt(allowEarlyMM)*60*1000;
			long WorkOverTime2=workOffTime2+Integer.parseInt(afterOvertime)*60*1000;
			
			long lastWorkOnTime3=workOnTime3+Integer.parseInt(allowLateMM)*60*1000;
			long earlyworkOffTime3=workOffTime3-Integer.parseInt(allowEarlyMM)*60*1000;
			long WorkOverTime3=workOffTime3+Integer.parseInt(afterOvertime)*60*1000;
			
			
			// 获取两个中间节点
			long signInOneTime=(workOffTime1-workOnTime1)/2;
			long signOutOneTime=(workOnTime2-workOffTime1)/2;
			long signInTwoTime=(workOffTime2-workOnTime2)/2;
			long signOutTwoTime=(workOnTime3-workOffTime2)/2;
			long signInThreeTime=(workOffTime3-workOnTime3)/2;
			long signOutThreeTime=(workOnTime4-workOffTime3)/2;
			
			long signInOne= signInOneTime+workOnTime1;
			long signOutOne= signOutOneTime+workOffTime1;
			long signInTwo =signInTwoTime+workOnTime2;
			long signOutTwo= signOutTwoTime+workOffTime2;
			long signInThree =signInThreeTime+workOnTime3;
			long signOutThree= signOutThreeTime+workOffTime3;
			
			
			// 判断签到时间所属的班段范围
			if(beforDaySignTime<signTime && signTime<signInOne){
				if(signTime>lastWorkOnTime1){
					map.put("signTime", attendDanceMap1.get("workOnTime"));
					map.put("signResult", "迟到");
				}else{
					map.put("signTime", attendDanceMap1.get("workOnTime"));
					map.put("signResult", "正常");
				}
			}else if(signInOne<=signTime && signTime<signOutOne){
				if(signTime<earlyworkOffTime1){
					map.put("signTime", attendDanceMap1.get("workOffTime"));
					map.put("signResult", "早退");
				}else{
					map.put("signTime", attendDanceMap1.get("workOffTime"));
					map.put("signResult", "正常");
				}
			}else if(signOutOne<=signTime && signTime<signInTwo){
				if(signTime>lastWorkOnTime2){
					map.put("signTime", attendDanceMap2.get("workOnTime"));
					map.put("signResult", "迟到");
				}else{
					map.put("signTime", attendDanceMap2.get("workOnTime"));
					map.put("signResult", "正常");
				}
			}else if(signInTwo<=signTime && signTime<signOutTwo){
				if(signTime<earlyworkOffTime2){
					map.put("signTime", attendDanceMap2.get("workOffTime"));
					map.put("signResult", "早退");
				}else{
					map.put("signTime", attendDanceMap2.get("workOffTime"));
					map.put("signResult", "正常");
				}
			}else if(signOutTwo<=signTime && signTime<signInThree){
				if(signTime>lastWorkOnTime3){
					map.put("signTime", attendDanceMap3.get("workOnTime"));
					map.put("signResult", "迟到");
				}else{
					map.put("signTime", attendDanceMap3.get("workOnTime"));
					map.put("signResult", "正常");
				}
			}else if(signInThree<=signTime && signTime<signOutThree){
				if(signTime<earlyworkOffTime3){
					map.put("signTime", attendDanceMap3.get("workOffTime"));
					map.put("signResult", "早退");
				}else{
					map.put("signTime", attendDanceMap3.get("workOffTime"));
					map.put("signResult", "正常");
				}
			}
		}
		
		return map;
	}
}
