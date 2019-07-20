package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.mapper.AttendCalculateMapper;
import com.faceRecog.manage.mapper.AttendGroupMapper;
import com.faceRecog.manage.mapper.EmpRetroactMapper;
import com.faceRecog.manage.mapper.EmployeeMapper;
import com.faceRecog.manage.mapper.RetroactiveMapper;
import com.faceRecog.manage.mapper.SignRecordMapper;
import com.faceRecog.manage.model.AttendCalculate;
import com.faceRecog.manage.model.AttendGroup;
import com.faceRecog.manage.model.AttendGroupPeriod;
import com.faceRecog.manage.model.EmpRetroact;
import com.faceRecog.manage.model.Retroactive;
import com.faceRecog.manage.model.SignRecord;
import com.faceRecog.manage.model.vo.EmpRetVO;
import com.faceRecog.manage.service.RetroactiveService;
import com.faceRecog.manage.util.CommUtil;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RetroactiveServiceImpl implements RetroactiveService {

    @Autowired
    private RetroactiveMapper retroactiveMapper;

    @Autowired
    private EmpRetroactMapper empRetroactMapper;

    @Autowired
    private SignRecordMapper signRecordMapper;

    @Autowired
    private AttendGroupMapper attendGroupMapper;
    
    @Autowired
    private  AttendCalculateMapper attendCalculateMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRetroactive(Retroactive retroactive, EmpRetroact empRetroact,String attendType,String signType) throws Exception {
        int retroactNum = retroactiveMapper.insertSelective(retroactive);
        int empRetNum = 0;
        if (retroactNum > 0) {
            empRetNum = empRetroactMapper.insertSelective(empRetroact);
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        if(empRetNum>=0){// 补签成功 将补签时间添加到签到表中
        	// 先将补卡时间添加到打卡表中
        	retroactNum=calculaRetroactive(attendType, signType, retroactive.getRetroactTime(), empRetroact.getEmpId());
        }
        return empRetNum;
    }

    @Override
    public List<EmpRetVO> selectAllRetroactive() throws Exception {
        return retroactiveMapper.selectAllRetroactive();
    }

    @Override
    public List<EmpRetVO> selectRetroactiveByDeptId(String deptId) throws Exception {
        return retroactiveMapper.selectRetroactiveByDeptId(deptId);
    }

    @Override
    public List<EmpRetVO> selectByParams(Map<String, Object> map) throws Exception {
        return retroactiveMapper.selectByParams(map);
    }

    @Override
    public int deleteRetroactive(String[] retIds) throws Exception {
        return retroactiveMapper.deleteRetroactive(retIds);
    }

    
    public int calculaRetroactive(String attendType,String signType,String retroactDate,String empId)throws Exception {
    	// 查询当天班的班次信息
    	int affectNum=0;
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		Date currDate=format.parse(retroactDate);// 补卡当前日期时间
		String strCurrDay=format.format(format.parse(retroactDate));// 补卡当前日期字符串时间
		Calendar c = Calendar.getInstance();
		Calendar tomCal = Calendar.getInstance();
		c.setTime(currDate);
		tomCal.setTime(currDate);
		tomCal.add(Calendar.DATE, 1);
        String tomDay=format.format(tomCal.getTime());
        c.add(Calendar.DATE, -2);
        String yesterday=format.format(c.getTime().getTime());
        // 补卡时间转date
        Date retroactTime=fmt.parse(retroactDate);
        
		// 获取员工当天的班制 常规班 两班制 三班制
		List<Map<String, Object>> attendTodayInfoList=attendGroupMapper.selectAttendGroupAttendDanceInfo(empId,strCurrDay);
		// 获取员工昨天的班制 常规班 两班制 三班制
		List<Map<String, Object>> attendYesterdayInfoList=attendGroupMapper.selectAttendGroupAttendDanceInfo(empId,yesterday);
		// 明天的考勤情况
		List<Map<String, Object>> attendTomdayInfoList=attendGroupMapper.selectAttendGroupAttendDanceInfo(empId,tomDay);
		
		// 今天的第一班上班时间
		String tomSignInTime="";
		if(attendTomdayInfoList!=null && attendTomdayInfoList.size()>0 && attendTomdayInfoList.get(0)!=null){
			tomSignInTime=(String)attendTomdayInfoList.get(0).get("workOnTime");
		}else{
			tomSignInTime=strCurrDay+" 23:59:59";
		}
		// 查询员工当前日期的打卡记录
		SignRecord signRecord = signRecordMapper.selectSignRecordByEmpId(empId, retroactDate);
		if(signRecord==null){
			signRecord=new SignRecord();
		}
		// 将补卡时间添加到签到表
		switch (signType) {
		case "signInOne":
			signRecord.setSignInOne(retroactTime);
			break;
		case "signOutOne":
			signRecord.setSignInTwo(retroactTime);
			break;
		case "signInTwo":
			signRecord.setSignInTwo(retroactTime);
			break;
		case "signOutTwo":
			signRecord.setSignInTwo(retroactTime);
			break;
		case "signInThree":
			signRecord.setSignInTwo(retroactTime);
			break;
		case "signOutThree":
			signRecord.setSignInTwo(retroactTime);
			break;
		}
		if(StrKit.isBlank(signRecord.getId())){// 新增
			signRecord.setCreateTime(new Date());
			signRecord.setId(UUIDGenerator.getUUID());
			signRecord.setEmpId(empId);
			signRecord.setSignDate(strCurrDay);
			signRecord.setStatus("1");
			affectNum=signRecordMapper.insertSelective(signRecord);
		}else{// 修改
			affectNum=signRecordMapper.updateByPrimaryKeySelective(signRecord);
		}
		if(affectNum<0){
			return -1;
		}
		//  重新计算今天的考勤
		affectNum=attendTodayCalculate(empId,attendTodayInfoList,signType,tomSignInTime,strCurrDay,signRecord);
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
			String signType,String tomSignInOne,String retroactDate,SignRecord signRecord)throws Exception{
		int affectNum=0;
		
		AttendCalculate attendCalculate = new AttendCalculate();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date currDate =new Date();// 当前时间
		String today=format.format(currDate);
		
		
		// 查询当前打卡员工考勤是否结算过
		AttendCalculate attendCalculateRec = attendCalculateMapper.selectAttendCalculateInfoByEmpId(empId, retroactDate);
		if(attendInfoList!=null && attendInfoList.size()>0 && attendInfoList.get(0)!=null){
			String attendType=(String)attendInfoList.get(0).get("type");
			// 是否还需要计算补卡的时间所属的打卡段
			boolean flag=calculateMilddle(attendInfoList, attendType, signType, tomSignInOne);// 补签时间是否过了上班时间或下班时间
			// 上下班时间计算 最晚上下班时间
			Map<String , Long> workTimeMap=caculatWorkTime(attendInfoList);
			// 重新计算当天打卡情况
			attendCalculate=everySignAfreshCalculate(signRecord, workTimeMap, attendInfoList.size()+"");
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
	
	public AttendCalculate everySignAfreshCalculate(SignRecord signInfoRec ,Map<String, Long> attendInfo,String type){
		AttendCalculate attendCalculate=new AttendCalculate();
		
		Date workOnTime1=StrKit.notNull(attendInfo.get("workOnTime1"))?new Date(attendInfo.get("workOnTime1")):null;//允许最晚上班时间
		Date workOffTime1=StrKit.notNull(attendInfo.get("workOffTime1"))?new Date(attendInfo.get("workOffTime1")):null;//允许最早下班打卡时间
		Date workOnTime2=StrKit.notNull(attendInfo.get("workOnTime2"))?new Date(attendInfo.get("workOnTime2")):null;//允许最晚上班时间
		Date workOffTime2=StrKit.notNull(attendInfo.get("workOffTime2"))?new Date(attendInfo.get("workOffTime2")):null;//允许最早下班打卡时间
		Date workOnTime3=StrKit.notNull(attendInfo.get("workOnTime3"))?new Date(attendInfo.get("workOnTime3")):null;//允许最晚上班时间
		Date workOffTime3=StrKit.notNull(attendInfo.get("workOffTime3"))?new Date(attendInfo.get("workOffTime3")):null;//允许最早下班打卡时间
        Date startOvertime1=StrKit.notNull(attendInfo.get("startOvertime1"))?new Date(attendInfo.get("startOvertime1")):null;//加班开始时间
        Date startOvertime2=StrKit.notNull(attendInfo.get("startOvertime2"))?new Date(attendInfo.get("startOvertime2")):null;//加班开始时间
        Date startOvertime3=StrKit.notNull(attendInfo.get("startOvertime3"))?new Date(attendInfo.get("startOvertime3")):null;//加班开始时间
        
        // 签到记录
    	Date signInTime1 =signInfoRec==null?null:signInfoRec.getSignInOne();
		Date signInTime2 =signInfoRec==null?null:signInfoRec.getSignInTwo();
		Date signInTime3 =signInfoRec==null?null:signInfoRec.getSignInThree();
       
		// 签退记录
		Date signOutTime1= signInfoRec==null?null:signInfoRec.getSignOutOne();
		Date signOutTime2= signInfoRec==null?null:signInfoRec.getSignOutTwo();
		Date signOutTime3= signInfoRec==null?null:signInfoRec.getSignOutThree();
		if(CommonConstant.常规班.getValue().equals(type)){
			if(StrKit.notNull(signInTime1)){
				// 解析签到
				Map<String, Long> diffSignIn=CommUtil.getDateDifference(workOnTime1, signInTime1);
				// 迟到
				if (diffSignIn.get("min") > 0) {
					attendCalculate.setDelayTimes(new BigDecimal(diffSignIn.get("min") ));
					attendCalculate.setDelayNum(1);
				}
			}else{
				attendCalculate.setMissSign(1);
				attendCalculate.setSignInMiss(1);
			}
			if(StrKit.notNull(signOutTime1)){
				    // 解析签退
					Map<String, Long> diffSignOut=CommUtil.getDateDifference(workOffTime1, signOutTime1);
					// 解析加班时长
					Map<String, Long> diffOverTime=CommUtil.getDateDifference(startOvertime1, signOutTime1);
					// 当天的上班记录不为空
					if(StrKit.notNull(signInTime1)){
						// 出勤总时长计算
						Map<String, Long> diffWorkTimeDiff=CommUtil.getDateDifference(signInTime1, signOutTime1);
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
			}else{
				attendCalculate.setMissSign(1);
				attendCalculate.setSignOutMiss(1);
			}
		}else if(CommonConstant.两班制.getValue().equals(type)){
				if(StrKit.notNull(signInTime1)){
					// 解析第一段签到
					Map<String, Long> diffSignIn1=CommUtil.getDateDifference(workOnTime1, signInTime1);// 第一段时差
					// 迟到
					if (diffSignIn1.get("min") > 0) {
						attendCalculate.setDelayTimes(new BigDecimal(diffSignIn1.get("min") ));
						attendCalculate.setDelayNum(1);
					}
				}else{
					attendCalculate.setMissSign(1);
					attendCalculate.setSignInMiss(1);
				}
				if(StrKit.notNull(signInTime2)){
					// 解析第二段签到
					Map<String, Long> diffSignIn2=CommUtil.getDateDifference(workOnTime2, signInTime2);// 第二段时差
					// 迟到
					if (diffSignIn2.get("min") > 0) {
						attendCalculate.setDelayTimes(new BigDecimal(diffSignIn2.get("min")).add(attendCalculate.getDelayTimes()));
						attendCalculate.setDelayNum(attendCalculate.getDelayNum()+1);
					}
				}else{
					attendCalculate.setMissSign(1);
					attendCalculate.setSignInMiss(attendCalculate.getSignInMiss()+1);
				}
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
				}else{
					attendCalculate.setMissSign(1);
					attendCalculate.setSignOutMiss(1);
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
				}else{
					attendCalculate.setMissSign(1);
					attendCalculate.setSignOutMiss(attendCalculate.getSignOutMiss()+1);
				}
		}else if(CommonConstant.三班制.getValue().equals(type)){
				if(StrKit.notNull(signInTime1)){
					// 解析第一段签到
					Map<String, Long> diffSignIn=CommUtil.getDateDifference(workOnTime1, signInTime1);// 第一段时差
					// 迟到
					if (diffSignIn.get("min") > 0) {
						attendCalculate.setDelayTimes(new BigDecimal(diffSignIn.get("min") ));
						attendCalculate.setDelayNum(1);
					}
				}else{
					attendCalculate.setMissSign(1);
					attendCalculate.setSignInMiss(1);
				}
				if(StrKit.notNull(signInTime2)){
					// 解析第二段签到
					Map<String, Long> diffSignIn=CommUtil.getDateDifference(workOnTime2, signInTime2);// 第二段时差
					// 迟到
					if (diffSignIn.get("min") > 0) {
						attendCalculate.setDelayTimes(new BigDecimal(diffSignIn.get("min")).add(attendCalculate.getDelayTimes()));
						attendCalculate.setDelayNum(attendCalculate.getDelayNum()+1);
					}
				}else{
					attendCalculate.setMissSign(1);
					attendCalculate.setSignInMiss(attendCalculate.getSignInMiss()+1);
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
				}else{
					attendCalculate.setMissSign(1);
					attendCalculate.setSignInMiss(attendCalculate.getSignInMiss()+1);
				}
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
				}else{
					attendCalculate.setMissSign(1);
					attendCalculate.setSignOutMiss(1);
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
				}else{
					attendCalculate.setMissSign(1);
					attendCalculate.setSignOutMiss(attendCalculate.getSignOutMiss()+1);
				}
				
				// 解析 第三段签退
				if(StrKit.notNull(signOutTime3)){
					// 解析签退  第三段
					Map<String, Long> diffSignOut=CommUtil.getDateDifference(workOffTime3, signOutTime3);
					// 解析加班时长
					Map<String, Long> diffOverTime=CommUtil.getDateDifference(startOvertime3,signOutTime3);
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
						Map<String, Long> diffWorkTimeDiff=CommUtil.getDateDifference(signInTime3, signOutTime3);
						// 出勤总时长
						if (diffWorkTimeDiff.get("min") > 0) {
							attendCalculate.setWorkTimes(new BigDecimal(diffWorkTimeDiff.get("min")).add(attendCalculate.getWorkTimes()));
						}
					}
				}else{
					attendCalculate.setMissSign(1);
					attendCalculate.setSignOutMiss(attendCalculate.getSignOutMiss()+1);
				}
		}
		return attendCalculate;
	}
	
	
	
	public Map<String, Long> caculatWorkTime(List<Map<String, Object>>attendInfoList)throws Exception{
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Map<String , Long> map=new HashMap<String , Long>();
		
		if(attendInfoList.size()==1){
			/** 计算上下班打卡时间范围**/
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
			
			long lastWorkOnTime=workOnTime.getTime()+Integer.parseInt(allowLateMM)*60*1000;
			long earlyworkOffTime=workOffTime.getTime()-Integer.parseInt(allowEarlyMM)*60*1000;
			long WorkOverTime=workOffTime.getTime()+Integer.parseInt(afterOvertime)*60*1000;
			
	        map.put("workOnTime1", lastWorkOnTime);
	        map.put("workOffTime1", earlyworkOffTime);
	        map.put("workOnTime2", null);
	        map.put("workOffTime2", null);
	        map.put("workOnTime3", null);
	        map.put("workOffTime3", null);
	        map.put("startOvertime1", WorkOverTime);
	        map.put("startOvertime2", null);
	        map.put("startOvertime3", null);
			// 查询下班最晚的打卡点
		}else if(attendInfoList.size()==2){
			// 时间累计
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
			
			
	        long lastWorkOnTime1=workOnTime1.getTime()+Integer.parseInt(allowLateMM)*60*1000;
			long earlyworkOffTime1=workOffTime1.getTime()-Integer.parseInt(allowEarlyMM)*60*1000;
			long WorkOverTime1=workOffTime1.getTime()+Integer.parseInt(afterOvertime)*60*1000;
			
			long lastWorkOnTime2=workOnTime2.getTime()+Integer.parseInt(allowLateMM)*60*1000;
			long earlyworkOffTime2=workOffTime2.getTime()-Integer.parseInt(allowEarlyMM)*60*1000;
			long WorkOverTime2=workOffTime2.getTime()+Integer.parseInt(afterOvertime)*60*1000;
			
	        
	        map.put("workOnTime1", lastWorkOnTime1);
	        map.put("workOffTime1", earlyworkOffTime1);
	        map.put("workOnTime2", lastWorkOnTime2);
	        map.put("workOffTime2", earlyworkOffTime2);
	        map.put("workOnTime3", null);
	        map.put("workOffTime3", null);
	        map.put("startOvertime1", WorkOverTime1);
	        map.put("startOvertime2", WorkOverTime2);
	        map.put("startOvertime3", null);
			
			
		}else if(attendInfoList.size()==3){
			
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
			
			
			long lastWorkOnTime1=workOnTime1.getTime()+Integer.parseInt(allowLateMM)*60*1000;
			long earlyworkOffTime1=workOffTime1.getTime()-Integer.parseInt(allowEarlyMM)*60*1000;
			long WorkOverTime1=workOffTime1.getTime()+Integer.parseInt(afterOvertime)*60*1000;
			
			long lastWorkOnTime2=workOnTime2.getTime()+Integer.parseInt(allowLateMM)*60*1000;
			long earlyworkOffTime2=workOffTime2.getTime()-Integer.parseInt(allowEarlyMM)*60*1000;
			long WorkOverTime2=workOffTime2.getTime()+Integer.parseInt(afterOvertime)*60*1000;
			
			long lastWorkOnTime3=workOnTime3.getTime()+Integer.parseInt(allowLateMM)*60*1000;
			long earlyworkOffTime3=workOffTime3.getTime()-Integer.parseInt(allowEarlyMM)*60*1000;
			long WorkOverTime3=workOffTime3.getTime()+Integer.parseInt(afterOvertime)*60*1000;
	        
	       
	        map.put("workOnTime1", lastWorkOnTime1);
	        map.put("workOffTime1", earlyworkOffTime1);
	        map.put("workOnTime2", lastWorkOnTime2);
	        map.put("workOffTime2", earlyworkOffTime2);
	        map.put("workOnTime3", lastWorkOnTime3);
	        map.put("workOffTime3", earlyworkOffTime3);
	        map.put("startOvertime1", WorkOverTime1);
	        map.put("startOvertime2", WorkOverTime2);
	        map.put("startOvertime3", WorkOverTime3);
		}
		return map;
	}
}
