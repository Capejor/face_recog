/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service 
 * @author: Administrator   
 * @date: 2019年5月18日 上午11:51:15 
 */
package com.faceRecog.manage.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import com.faceRecog.manage.mapper.AttendCalculateMapper;
import com.faceRecog.manage.mapper.AttendDetailMapper;
import com.faceRecog.manage.mapper.AttendGroupMapper;
import com.faceRecog.manage.mapper.AttendanceMapper;
import com.faceRecog.manage.mapper.EmpAttendGroupMapper;
import com.faceRecog.manage.mapper.EquipmentMapper;
import com.faceRecog.manage.mapper.InstructionRecMapper;
import com.faceRecog.manage.mapper.OriginalSignRecordMapper;
import com.faceRecog.manage.mapper.SignRecordMapper;
import com.faceRecog.manage.model.AttendCalculate;
import com.faceRecog.manage.model.AttendDetail;
import com.faceRecog.manage.model.Equipment;
import com.faceRecog.manage.model.InstructionRec;
import com.faceRecog.manage.model.SignRecord;
import com.faceRecog.manage.model.User;
import com.faceRecog.manage.model.vo.EmpAttendGroupVO;
import com.faceRecog.manage.redis.RedisEnum;
import com.faceRecog.manage.redis.RedisUtils;
import com.faceRecog.manage.service.AttendDetailService;
import com.faceRecog.manage.service.impl.SignRecordServiceImpl;
import com.faceRecog.manage.util.CommUtil;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;

import net.sf.json.JSONObject;

/** 
 * @ClassName: AttendDetailServiceImpl 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月18日 上午11:51:15  
 */
@Service
public class AttendDetailServiceImpl implements AttendDetailService{

	@Autowired
	private AttendDetailMapper attendDetailMapper;
	
	@Autowired
	private EmpAttendGroupMapper empAttendGroupMapper;
	
	@Autowired
	private SignRecordMapper signRecordMapper;
	
	@Autowired
	private OriginalSignRecordMapper originalSignRecordMapper;
	
	@Autowired
	private AttendGroupMapper attendGroupMapper;
	
	@Autowired
	private AttendCalculateMapper attendCalculateMapper;
	
	@Autowired
    private SpringWebSocketHandler websocket;
    
    @Autowired
    private InstructionRecMapper instructionRecMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;
    
    @Autowired
    private AttendanceMapper attendanceMapper;
    
	/* (non Javadoc) 
	 * @Title: insertBatchAttendDetail
	 * @Description: TODO
	 * @param attendDetailList
	 * @return 
	 * @see com.faceRecog.manage.service.AttendDetailService#insertBatchAttendDetail(java.util.List) 
	 */ 
	@Override
	@Transactional
	public Result insertBatchAttendDetail(List<AttendDetail> attendDetailList) throws Exception{
		int affectNum=0;
		Result result=null;
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		attendDetailList.removeAll(Collections.singleton(null));
		//String []dateStr=new String[attendDetailList.size()];
		/*// 删除原考勤明细数据 新增修改同一个接口
		affectNum=attendDetailMapper.deleteAttendDetailByAgId(attendDetailList.get(0).getEmpId());
		if(affectNum<0){
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return Result.responseError("保存失败！");
		}*/
		for(int i=0;i<attendDetailList.size();i++){
			AttendDetail attendDetail=attendDetailList.get(i);
			AttendDetail attendDetailNext=null;
			if(i<attendDetailList.size()-1){
				attendDetailNext=attendDetailList.get(i+1);
			}
			String tomAttendanceId="";
			// 判断下一个日期是不是当前日期的下一天
			if(attendDetailNext!=null){
				long todayMillis=format.parse(attendDetail.getDateStr()).getTime()+24*60*60*1000;
				long tomMillis=format.parse(attendDetailNext.getDateStr()).getTime();
				if(todayMillis==tomMillis){
					tomAttendanceId=attendDetailNext.getAttendId();
				}
			}
			if(StrKit.isBlank(attendDetail.getEmpId())){
				return Result.responseError("员工id不能为空！");
			}else if(StrKit.isBlank(attendDetail.getAttendId())){
				return Result.responseError("班次id不能为空！");
			}else if(StrKit.isBlank(attendDetail.getDateStr())){
				return Result.responseError("考勤日期不能为空！");
			}
			
			Date empAttendDate=format.parse(attendDetail.getDateStr());
			Date currDate=format.parse(format.format(new Date()));// 当前时间
			
			// 查询员工考勤生效时间
			EmpAttendGroupVO empAttendGroup=empAttendGroupMapper.selectEmpAttendGroupByEmpId(attendDetail.getEmpId());
			if(empAttendGroup!=null){
				String dateStr=empAttendGroup.getApplyTime();
				Date applyTime=format.parse(dateStr);//考勤生效时间
				String isCover=attendDetail.getIsCover();//考勤是否覆盖 0不覆盖 1覆盖
				
				// 修改已经排好的班 如果没有就新增
				affectNum=attendDetailMapper.updateAttendDetailByEmpIdAndDateStr(attendDetail.getEmpId(), attendDetail.getDateStr(),attendDetail.getAttendId());
				if(affectNum==0){
					attendDetail.setId(UUIDGenerator.getUUID());
					attendDetail.setCreateTime(new Date());
					attendDetail.setYearMonthStr(fmt.format(fmt.parse(attendDetail.getDateStr())));
					affectNum=attendDetailMapper.insertSelective(attendDetail);
				}
				// 考勤生效时间判断
				if(applyTime.getTime()>currDate.getTime()){ // 明天生效 今天的考勤还是按今天的算
					// 需要判断今天考勤和以前考勤是否覆盖重新计算 默认覆盖
					if(empAttendDate.getTime()<currDate.getTime()){// 大于等于今天的时间的考勤还没开始结算 所以需要过滤掉
						// 判断是否需要覆盖
						if("1".equals(isCover)){
							affectNum=calculateNewEmmp(empAttendGroup.getEmpId(),attendDetail.getDateStr(),tomAttendanceId);
						}
					}
					
				}else{// 今天立即生效
					if(affectNum<0){
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						return Result.responseError("保存失败！");
					}
					if(empAttendDate.getTime()<=currDate.getTime()){// 大于今天的时间的考勤还没开始结算 所以需要过滤掉
						// 判断是否需要覆盖
						if("1".equals(isCover)){
							affectNum=calculateNewEmmp(empAttendGroup.getEmpId(),attendDetail.getDateStr(),tomAttendanceId);
						}
					}
				}
			}else{
				return Result.responseError("员工未在考勤组！");
			}
		}
		if(affectNum<0){
			return Result.responseError("保存失败！");
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
                    return Result.responseError("保存失败！");
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
		result=Result.responseSuccess("保存成功！");
		return result;
	}

	/* (non Javadoc) 
	 * @Title: updateAttendDetail
	 * @Description: TODO
	 * @param attendDetailList
	 * @return 
	 * @see com.faceRecog.manage.service.AttendDetailService#updateAttendDetail(java.util.List) 
	 */ 
	@Override
	@Transactional
	public int updateAttendDetail(List<AttendDetail> attendDetailList) {
		int affectNum=0;
		affectNum=attendDetailMapper.deleteAttendDetailByAgId(attendDetailList.get(0).getEmpId());
		if(affectNum>0){
			attendDetailList.removeAll(Collections.singleton(null));//删除空对象
			if(attendDetailList!=null && attendDetailList.size()>0){
				for(AttendDetail attendDetail:attendDetailList){
					affectNum=attendDetailMapper.insertSelective(attendDetail);
				}
			}else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return -1;
			}
		}
		return affectNum;
	}

	/* (non Javadoc) 
	 * @Title: selectAttendDetailByAgId
	 * @Description: TODO
	 * @param agId
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.AttendDetailService#selectAttendDetailByAgId(java.lang.String) 
	 */ 
	@Override
	public List<Map<String, Object>> selectAttendDetailByAgId(String agId,String dateStr) throws Exception {
		
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> emList=empAttendGroupMapper.selectEmpInfoByAgId(agId);
		if(emList!=null && emList.size()>0 && emList.get(0)!=null){
			for(Map<String, Object> empMap:emList){
				Map<String, Object> map=new HashMap<String, Object>();
				// 查询员工信息
				EmpAttendGroupVO empAttendGroup=empAttendGroupMapper.selectEmpAttendGroupByEmpId((String)empMap.get("empId"));
				if(empAttendGroup!=null){
					//查询员工月排班明细
					List<Map<String, Object>> attendDeatilList=attendDetailMapper.selectAttendDetailByEmpId(empAttendGroup.getEmpId(),dateStr);
					map.put("empName", empAttendGroup.getName());
					map.put("entryTime", empAttendGroup.getEntryTime());
					map.put("empId", empAttendGroup.getEmpId());
					map.put("applyTime", empAttendGroup.getApplyTime());
					map.put("schedules", attendDeatilList);
					list.add(map);
				}else{
					continue;
				}
			}
		}else{
			return new ArrayList<Map<String, Object>>();
		}
		return list;
	}
	
	/**
	 * 
	* @Title: calculateNewEmmp 
	* @Description: 考勤组员工考勤重新计算 
	* @param empId
	* @param agId
	* @return
	* @throws Exception int
	* @author xya
	* @date 2019年5月24日下午5:13:09
	 */
	public int calculateNewEmmp(String empId,String dateStr,String tomAttendanceId)throws Exception{
		int affectNum=0;
		AttendCalculate attendCalculateRec= new AttendCalculate();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat formatss = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
		Calendar tomCad =new GregorianCalendar();
		Calendar yesCad =new GregorianCalendar();
		Date currDate =new Date();// 当前传入时间
		String today=format.format(currDate);
		String paramDateStr=format.format(format.parse(dateStr));
		tomCad.setTime(format.parse(paramDateStr));
		yesCad.setTime(format.parse(paramDateStr));
		tomCad.add(Calendar.DATE, 1);
		yesCad.add(Calendar.DATE, -1);
		String tom=format.format(tomCad.getTime());
		String yes=format.format(yesCad.getTime());
			// 重新计算当前日期的考勤
			/*// 查询员工当前日期的打卡记录
	        SignRecord  signRec = signRecordMapper.selectSignRecordByEmpId(empId,paramDateStr);*/
			// 获取员工当前日期的班制 常规班 两班制 三班制
			List<Map<String, Object>> attendInfoList=attendGroupMapper.selectAttendGroupAttendDanceInfo(empId,paramDateStr);
			// 获取当前员工当前日期后一天的日期班次信息
			Map<String, Object> attendInfoMap=null;
			if(StrKit.isBlank(tomAttendanceId)){
				List<Map<String, Object>> attendInfoNextDayList=attendGroupMapper.selectAttendGroupAttendDanceInfo(empId,tom);
				if(attendInfoNextDayList!=null && attendInfoNextDayList.size()>0 && attendInfoNextDayList.get(0)!=null){
					attendInfoMap=attendInfoNextDayList.get(0);
				}
			}else{
				attendInfoMap=attendanceMapper.selectAttendInfoByAtId(tomAttendanceId);
				attendInfoMap.put("workOnTime", tom+" "+attendInfoMap.get("workOnTime"));
			}
			String nextDayWorkOnTime="";// 另一天的第一班上班时间
			if(attendInfoMap!=null){
				 nextDayWorkOnTime=(String) attendInfoMap.get("workOnTime");
			}else{
				 nextDayWorkOnTime=paramDateStr+" 00:00:00";
			}
			// 获取当前员工前一天的考勤班次信息
			List<Map<String, Object>> attendInfoYesDayList=attendGroupMapper.selectAttendGroupAttendDanceInfo(empId,yes);
			String beforDayWorkOffTime="";// 前一天的最后一班下班班时间
			if(attendInfoYesDayList!=null && attendInfoYesDayList.size()>0 && attendInfoYesDayList.get(0)!=null){
				beforDayWorkOffTime=(String)attendInfoYesDayList.get(0).get("workOnTime");
			}else{
				beforDayWorkOffTime=paramDateStr+" 23:59:59";
			}
			// 判断员工班制是否跨天
			if(attendInfoList!=null && attendInfoList.size()>0 && attendInfoList.get(0)!=null){
				Calendar c = Calendar.getInstance();
	    		for(Map<String, Object> attendInfo:attendInfoList){
	    			String inAcorss=(String)attendInfo.get("inAcorss");
	    			String outAcross=(String)attendInfo.get("outAcross");
	    			String workOnTime=(String)attendInfo.get("workOnTime");
	    			String workOffTime=(String)attendInfo.get("workOffTime");
	    			if(CommonConstant.次日.getValue().equals(inAcorss)){// 上班跨天
	    				c.setTime(fmt.parse(workOnTime));
	    				c.add(Calendar.DATE, 1);
	    				workOnTime=fmt.format(c.getTime());
	    				attendInfo.put("workOnTime", workOnTime);
	    			}
	    			if(CommonConstant.次日.getValue().equals(outAcross)){// 下班跨天
	    				c.setTime(fmt.parse(workOffTime));
	    				c.add(Calendar.DATE, 1);
	    				workOffTime=fmt.format(c.getTime());
	    				attendInfo.put("workOffTime", workOffTime);
	    			}
	    		}
	    	}
			/*// 查询当前打卡员工考勤是否结算过
			AttendCalculate attendCalculateRec = attendCalculateMapper.selectAttendCalculateInfoByEmpId(empId, paramDateStr);*/
			// 班次类型
			String attendType=attendInfoList.get(0).get("type").toString();
			if(attendInfoList!=null && attendInfoList.size()>0){
	    		if(CommonConstant.常规班.getValue().equals(attendType)){
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
	    					
	    			Map<String , Long> map=new HashMap<String , Long>();
	    	        map.put("workOnTime1", lastWorkOnTime);
	    	        map.put("workOffTime1", earlyworkOffTime);
	    	        map.put("workOnTime2", null);
	    	        map.put("workOffTime2", null);
	    	        map.put("workOnTime3", null);
	    	        map.put("workOffTime3", null);
	    	        map.put("startOvertime1", WorkOverTime);
	    	        map.put("startOvertime2", null);
	    	        map.put("startOvertime3", null);
	    	        
	    			Map<String, Object> midleMap=calculateMilddle(attendInfoList, attendType, nextDayWorkOnTime,empId);
	    			Map<String, Object> signTimeMap=new HashMap<String, Object>();
	    			signTimeMap.put("empId",empId);
	    			signTimeMap.put("signInStOneTime", formatss.format(new Date(fmt.parse(beforDayWorkOffTime).getTime())));
	    			signTimeMap.put("signInEndOneTime", midleMap.get("signInOne"));
	    			signTimeMap.put("signOutStOneTime",  midleMap.get("signInOne"));
	    			signTimeMap.put("signOutEndOneTime",  midleMap.get("signOutOne"));
	    			// 查询最早的上班打卡点 去原始打卡数据中查
	    			Map<String, Object> signResultMap=originalSignRecordMapper.selectOriginalSignRecordByDateAndEmpId(signTimeMap);
	    			SignRecord signRecord=new SignRecord();
	    			signRecord.setSignInOne((Date)signResultMap.get("signOneInTime"));
	    			signRecord.setSignOutOne((Date)signResultMap.get("signOneOutTime"));
	    			signRecord.setEmpId(empId);
	    			if(!today.equals(paramDateStr)){
	    				attendCalculateRec=everySignAfreshCalculate(signRecord, map, attendType);
	    			}
	    			// 查询下班最晚的打卡点
				}else if(CommonConstant.两班制.getValue().equals(attendType)){
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
	    			
			        Map<String , Long> map=new HashMap<String , Long>();
			        map.put("workOnTime1", lastWorkOnTime1);
			        map.put("workOffTime1", earlyworkOffTime1);
			        map.put("workOnTime2", lastWorkOnTime2);
			        map.put("workOffTime2", earlyworkOffTime2);
			        map.put("workOnTime3", null);
			        map.put("workOffTime3", null);
			        map.put("startOvertime1", WorkOverTime1);
			        map.put("startOvertime2", WorkOverTime2);
			        map.put("startOvertime3", null);
					
					Map<String, Object> midleMap=calculateMilddle(attendInfoList, attendType, nextDayWorkOnTime,empId);
					Map<String, Object> signTimeMap=new HashMap<String, Object>();
	    			signTimeMap.put("empId",empId);
	    			signTimeMap.put("signInStOneTime", formatss.format(new Date(fmt.parse(beforDayWorkOffTime).getTime())));
	    			signTimeMap.put("signInEndOneTime", midleMap.get("signInOne"));
	    			signTimeMap.put("signOutStOneTime",  midleMap.get("signInOne"));
	    			signTimeMap.put("signOutEndOneTime",  midleMap.get("signOutOne"));
	    			signTimeMap.put("signInStTwoTime", midleMap.get("signOutOne"));
	    			signTimeMap.put("signInEndTwoTime", midleMap.get("signInTwo"));
	    			signTimeMap.put("signOutStTwoTime", midleMap.get("signInTwo"));
	    			signTimeMap.put("signOutEndTwoTime", midleMap.get("signOutTwo"));
	    			// 查询最早的上班打卡点 去原始打卡数据中查
	    			Map<String, Object> signResultMap=originalSignRecordMapper.selectOriginalSignRecordByDateAndEmpId(signTimeMap);
	    			SignRecord signRecord=new SignRecord();
	    			signRecord.setSignInOne((Date)signResultMap.get("signOneInTime"));
	    			signRecord.setSignOutOne((Date)signResultMap.get("signOneOutTime"));
	    			signRecord.setSignInTwo((Date)signResultMap.get("signTwoInTime"));
	    			signRecord.setSignOutTwo((Date)signResultMap.get("signTwoOutTime"));
	    			signRecord.setEmpId(empId);
	    			
	    			if(!today.equals(paramDateStr)){
	    				attendCalculateRec=everySignAfreshCalculate(signRecord, map, attendType);
	    			}
	    			// 查询下班最晚的打卡点
				}else if(CommonConstant.三班制.getValue().equals(attendType)){
					
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
			        
			        Map<String , Long> map=new HashMap<String , Long>();
			        map.put("workOnTime1", lastWorkOnTime1);
			        map.put("workOffTime1", earlyworkOffTime1);
			        map.put("workOnTime2", lastWorkOnTime2);
			        map.put("workOffTime2", earlyworkOffTime2);
			        map.put("workOnTime3", lastWorkOnTime3);
			        map.put("workOffTime3", earlyworkOffTime3);
			        map.put("startOvertime1", WorkOverTime1);
			        map.put("startOvertime2", WorkOverTime2);
			        map.put("startOvertime3", WorkOverTime3);
					
					
					Map<String, Object> midleMap=calculateMilddle(attendInfoList, attendType, nextDayWorkOnTime,empId);
					Map<String, Object> signTimeMap=new HashMap<String, Object>();
	    			signTimeMap.put("empId", empId);
	    			signTimeMap.put("signInStOneTime", formatss.format(new Date(fmt.parse(beforDayWorkOffTime).getTime())));
	    			signTimeMap.put("signInEndOneTime", midleMap.get("signInOne"));
	    			signTimeMap.put("signInEndOneTime", midleMap.get("signInOne"));
	    			signTimeMap.put("signOutStOneTime",  midleMap.get("signInOne"));
	    			signTimeMap.put("signOutEndOneTime",  midleMap.get("signOutOne"));
	    			signTimeMap.put("signInStTwoTime", midleMap.get("signOutOne"));
	    			signTimeMap.put("signInEndTwoTime", midleMap.get("signInTwo"));
	    			signTimeMap.put("signOutStTwoTime", midleMap.get("signInTwo"));
	    			signTimeMap.put("signOutEndTwoTime", midleMap.get("signOutTwo"));
	    			signTimeMap.put("signInStThreeTime", midleMap.get("signOutTwo"));
	    			signTimeMap.put("signInEndThreeTime", midleMap.get("signInThree"));
	    			signTimeMap.put("signOutStThreeTime", midleMap.get("signInThree"));
	    			signTimeMap.put("signOutEndThreeTime", midleMap.get("signOutThree"));
	    			// 查询最早的上班打卡点 去原始打卡数据中查
	    			Map<String, Object> signResultMap=originalSignRecordMapper.selectOriginalSignRecordByDateAndEmpId(signTimeMap);
	    			SignRecord signRecord=new SignRecord();
	    			signRecord.setSignInOne((Date)signResultMap.get("signOneInTime"));
	    			signRecord.setSignOutOne((Date)signResultMap.get("signOneOutTime"));
	    			signRecord.setSignInTwo((Date)signResultMap.get("signTwoInTime"));
	    			signRecord.setSignOutTwo((Date)signResultMap.get("signTwoOutTime"));
	    			signRecord.setSignInThree((Date)signResultMap.get("signThreeInTime"));
	    			signRecord.setSignOutTwo((Date)signResultMap.get("signThreeOutTime"));
	    			signRecord.setEmpId(empId);
	    			if(!today.equals(paramDateStr)){
	    				attendCalculateRec=everySignAfreshCalculate(signRecord, map, attendType);
	    			}
				}
	    		
	    		// 删除原考勤结算数据
	    		attendCalculateMapper.deleteAttendCalcuateByEmpIdAnDate(empId,paramDateStr);
	    		attendCalculateRec.setId(UUIDGenerator.getUUID());
	    		attendCalculateRec.setCreateTime(currDate);
	    		attendCalculateRec.setStatus("1");
	    		attendCalculateRec.setEmpId(empId);
				attendCalculateRec.setSignDate(paramDateStr);
				affectNum=attendCalculateMapper.insertSelective(attendCalculateRec);
			}
		return affectNum;
	}
	
	
	public Map<String, Object> calculateMilddle(List<Map<String, Object>> attendInfoList,String attendType
			,String nextDaySignInOne,String empId)throws ParseException{
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
		// 当前时间时间戳
		long currTime=new Date().getTime();
		
		/*// 重新计算时间
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
			
		}*/
		
		Map<String, Object> signMildTime=new HashMap<String, Object>();
		if(CommonConstant.常规班.getValue().equals(attendType)){
			Map<String, Object> attendDanceMap1=attendInfoList.get(0);
			
			long workOnTime1=df.parse((String)attendDanceMap1.get("workOnTime")).getTime();//
			long workOffTime1 = df.parse((String) attendDanceMap1.get("workOffTime")).getTime();// 下班时间
			long workOnTime2 =df.parse(nextDaySignInOne).getTime();// 下一天的上班时间  时间戳
			
			// 获取两个中间节点
			long signInOneTime=(workOffTime1-workOnTime1)/2;
			long signOutOneTime=(workOnTime2-workOffTime1)/2;
			signMildTime.put("signInOne", format.format(new Date(signInOneTime+workOnTime1)));//1559349000000+16200000
			signMildTime.put("signOutOne", format.format(new Date(signOutOneTime+workOffTime1)));//      16200000
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
			signMildTime.put("signInOne", format.format(new Date(signInOneTime+workOnTime1)));
			signMildTime.put("signOutOne", format.format(new Date(signOutOneTime+workOffTime1)));
			signMildTime.put("signInTwo", format.format(new Date(signInTwoTime+workOnTime2)));
			signMildTime.put("signOutTwo", format.format(new Date(signOutTwoTime+workOffTime2)));
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
			signMildTime.put("signInOne", format.format(new Date(signInOneTime+workOnTime1)));
			signMildTime.put("signOutOne", format.format(new Date(signOutOneTime+workOffTime1)));
			signMildTime.put("signInTwo", format.format(new Date(signInTwoTime+workOnTime2)));
			signMildTime.put("signOutTwo", format.format(new Date(signOutTwoTime+workOffTime2)));
			signMildTime.put("signInThree", format.format(new Date(signInThreeTime+workOnTime3)));
			signMildTime.put("signOutThree",format.format(new Date(signOutThreeTime+workOffTime3)));
		}
		
		signMildTime.put("empId", empId);
		return signMildTime;
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
	
	
	public static void main(String[] args) throws ParseException {
			String str="2019-06-30 08:05";
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:59");
			SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			
			
			System.out.println(dff.format(new Date(dff.parse(str).getTime()+3*60*1000)));
			long time=1559365200000l;
			System.out.println(df.format(new Date(time)));
			System.out.println(RedisUtils.keyBuilder(RedisEnum.attendance_table,"肖运安","ewewre234f"));
	}

}
