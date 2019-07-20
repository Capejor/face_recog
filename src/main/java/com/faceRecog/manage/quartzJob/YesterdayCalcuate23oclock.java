/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.quartz 
 * @author: Administrator   
 * @date: 2019年5月16日 上午11:29:44 
 */
package com.faceRecog.manage.quartzJob;




import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



 
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
import com.faceRecog.manage.util.ApplicationContextProvider;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;

/** 
 * @ClassName: SchedulerQuartzJob 
 * @Description: 考勤结算定时任务
 * @author: xya
 * @date: 2019年5月16日 上午11:29:44  
 */
@Component
public class YesterdayCalcuate23oclock  implements BaseJob{
  
	private Logger logger = LoggerFactory.getLogger(YesterdayCalcuate23oclock.class);
	 
	 
	private void before(){
		
        System.out.println("23点任务开始执行");
    }

    @Override
    public void execute(JobExecutionContext job) throws JobExecutionException{
        before();
        //String attendGroupId=job.getTrigger().getKey().getGroup();// 从定时任务中获取考勤组id
        int affectNum=0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        String today=format.format(c.getTime());
        int weekTdday= c.get(Calendar.DAY_OF_WEEK)-1;
        if(weekTdday==0){
        	weekTdday=7;
        }
		c.add(Calendar.DATE, -1);// 前一天
        Date start = c.getTime();
        int weekYsday=c.get(Calendar.DAY_OF_WEEK)-1;// 昨天的礼拜
        if(weekYsday==0){
        	weekYsday=7;
        }
        String yesterday= format.format(start);//前一天
        
        
        
		//SignRecordMapper signRecordMapper = ApplicationContextProvider.getBean(SignRecordMapper.class);
		EmployeeMapper employeeMapper = ApplicationContextProvider.getBean(EmployeeMapper.class);
		AttendGroupPeriodMapper attendGroupPeriodMapper = ApplicationContextProvider.getBean(AttendGroupPeriodMapper.class);
		PeriodAttendMapper periodAttendMapper = ApplicationContextProvider.getBean(PeriodAttendMapper.class);
		AttendGroupMapper attendGroupMapper = ApplicationContextProvider.getBean(AttendGroupMapper.class);
		AttendCalculateMapper attendCalculateMapper = ApplicationContextProvider.getBean(AttendCalculateMapper.class);
		try {
			// 根据考情组id查询员工的信息
			List<Map<String, Object>> empList= employeeMapper.selectAllEmpInfo();
			if (empList != null && empList.size() > 0 && empList.get(0) != null) {
				for (Map<String, Object> empMap : empList) {
					String empId = (String) empMap.get("empId");
					// 根据考情组id查询员工的信息
					AttendGroup attendGroup=attendGroupMapper.selectEmpAttendGroupType(empId);
					if(attendGroup!=null){
						if (CommonConstant.固定班.getValue().equals(empMap.get("type"))) {
							// 查询固定班制员工的所属周期 根据员工id 今天的
							AttendGroupPeriod attendGroupPeriod = attendGroupPeriodMapper.selectPeriodByEmpId((String) empMap.get("empId"));
							// 查询周期排班班次 昨天的
							List<Map<String, Object>> periodYestadayAttendList = periodAttendMapper
									.selectPeriodAttendDetailByPrIdAndSortCms(attendGroupPeriod.getPrId(),weekYsday,yesterday);
							// 查询周期排班今天班次
							List<Map<String, Object>> periodTodayAttendList = periodAttendMapper
									.selectPeriodAttendDetailByPrIdAndSortCms(attendGroupPeriod.getPrId(),weekTdday,today);
							if (periodYestadayAttendList != null && periodYestadayAttendList.size() > 0
									&& periodTodayAttendList.get(0) != null) {
								String tdSignInTime="";
								if(periodTodayAttendList!=null && periodTodayAttendList.size()>0 && periodTodayAttendList.get(0)!=null){
									tdSignInTime=yesterday+" "+(String)periodTodayAttendList.get(0).get("workOnTime");
								}else{
									tdSignInTime=yesterday+" 23:59:59";
								}
								// 结算昨天的跨天考勤
								acrossAttendCalcuate(periodYestadayAttendList, empId,tdSignInTime);
							}
						} else if (CommonConstant.排班制.getValue().equals(empMap.get("type"))) {
							// 获取员工昨天的班制 常规班 两班制 三班制
							List<Map<String, Object>> attendYesterdayInfoList = attendGroupMapper.selectAttendGroupAttendDanceInfo(empId, yesterday);
							// 获取员工今天的班次信息
							List<Map<String, Object>> attendTodadayInfoList = attendGroupMapper.selectAttendGroupAttendDanceInfo(empId, today);
							
							if (attendYesterdayInfoList != null && attendYesterdayInfoList.size() > 0
									&& attendYesterdayInfoList.get(0) != null) {
								String tdSignInTime="";
								if(attendTodadayInfoList!=null && attendTodadayInfoList.size()>0 && attendTodadayInfoList.get(0)!=null){
									tdSignInTime=(String)attendTodadayInfoList.get(0).get("workOnTime");
								}else{
									tdSignInTime=yesterday+" 23:59:59";
								}
								// 结算昨天的跨天考勤
								acrossAttendCalcuate(attendYesterdayInfoList, empId,tdSignInTime);
							}
						}
					}else{
						// 查询当前打卡员工考勤是否结算过
						AttendCalculate attendCalculateRec = attendCalculateMapper.selectAttendCalculateInfoByEmpId(empId, today);
						if (attendCalculateRec != null) {
							attendCalculateRec.setIsAttendGroup(0);
							affectNum = attendCalculateMapper.updateByPrimaryKeySelective(attendCalculateRec);
						} else {
							attendCalculateRec=new AttendCalculate();
							attendCalculateRec.setId(UUIDGenerator.getUUID());
							attendCalculateRec.setCreateTime(new Date());
							attendCalculateRec.setStatus("1");
							attendCalculateRec.setEmpId(empId);
							attendCalculateRec.setIsAttendGroup(0);
							affectNum = attendCalculateMapper.insertSelective(attendCalculateRec);
						}
					}
					if(affectNum<0){
						logger.error("************23点定时任务结算昨天一天考勤失败*************");
					}
				}
			}
		} catch (Exception e) {
			logger.error("定时任务结算昨天考勤",e);
			e.printStackTrace();
		}
		after();
	}
    
    private void after(){
        System.out.println("任务结束");
    }
    
    /**
	 * 
	* @Title: acrossAttendCalcuate 
	* @Description: 昨天的跨天考勤计算 
	* @param signInOneTime 今天的第一班上班时间点
	* @param attendYesterdayInfoList 昨天的考勤排班信息
	* @return int
	* @author xya
	* @date 2019年5月30日上午11:26:51
	 */
	public int acrossAttendCalcuate(List<Map<String, Object>> attendYesterdayInfoList,
			String empId,String tdSignInTime) throws Exception {
		

		SignRecordMapper signRecordMapper = ApplicationContextProvider.getBean(SignRecordMapper.class);
		AttendCalculateMapper attendCalculateMapper = ApplicationContextProvider.getBean(AttendCalculateMapper.class);
		AttendCalculate attendCalculate = new AttendCalculate();
		int affectNum = 0;

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date currDate = new Date();// 当前时间
		Calendar c = Calendar.getInstance();
		c.setTime(currDate);
		c.add(Calendar.DATE, -1);
		String yesterday = format.format(c.getTime().getTime());
		
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
							boolean flag=calculateMilddle(attendYesterdayInfoList, attendType, signNull, tdSignInTime);
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
							boolean flag=calculateMilddle(attendYesterdayInfoList, attendType, signNull, tdSignInTime);
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
							boolean flag=calculateMilddle(attendYesterdayInfoList, attendType, signNull, tdSignInTime);
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
				affectNum = attendCalculateMapper.updateByPrimaryKeySelective(attendCalculate);
			} else {
				attendCalculate.setId(UUIDGenerator.getUUID());
				attendCalculate.setCreateTime(currDate);
				attendCalculate.setStatus("1");
				attendCalculate.setEmpId(empId);
				attendCalculate.setSignDate(yesterday);
				affectNum = attendCalculateMapper.insertSelective(attendCalculate);
			}
		}
		return affectNum;
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
