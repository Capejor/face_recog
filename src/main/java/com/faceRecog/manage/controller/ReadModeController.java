/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.controller 
 * @author: Administrator   
 * @date: 2019年5月8日 上午11:14:22 
 */
package com.faceRecog.manage.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.faceRecog.manage.model.ReadMode;
import com.faceRecog.manage.service.ReadModeService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;

/** 
 * @ClassName: ReadModeController 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月8日 上午11:14:22  
 */
@RestController
public class ReadModeController {

	
	@Autowired
	private ReadModeService readModeService;
	
	/**
	 * 
	* @Title: insertReadMode 
	* @Description: 新增人员信息识别参数 
	* @param readMode
	* @return readType 1刷脸 2刷卡 3卡+脸 4人证
	* @author xya
	* @date 2019年5月8日上午11:26:35
	 */
	@RequestMapping("/insertOrUpdateReadMode")
	public Result insertOrUpdateReadMode(ReadMode readMode)throws Exception{
		Result result=null;
		int affectNum=0;
		
		if(StrKit.isBlank(readMode.getEqId())){
			return Result.responseError("设备id不能为空!");
		}
		if(StrKit.notBlank(readMode.getReadType())){
			if("1".equals(readMode.getReadType()) && readMode.getPrecisionNum()<0){
				return Result.responseError("识别阀值不能小于0!");
			}else if("2".equals(readMode.getReadType()) && (StrKit.isBlank(readMode.getSerialPort()) ||
					StrKit.isBlank(readMode.getPeripheralType()))){
				return Result.responseError("串口类型或外设参数不能为空!");
			}else if("3".equals(readMode.getReadType()) &&  (StrKit.isBlank(readMode.getSerialPort()) ||
					StrKit.isBlank(readMode.getPeripheralType()) || readMode.getPrecisionNum()<0)){
				return Result.responseError("串口类型或外设参数不能为空或识别阀值不能小于0!");
			}else if("4".equals(readMode.getReadType()) &&  (StrKit.isBlank(readMode.getSerialPort()) ||
					StrKit.isBlank(readMode.getPeripheralType()) || readMode.getPrecisionNum()<0)){
				return Result.responseError("串口类型或外设参数不能为空或识别阀值不能小于0!");
			}
		}else{
			return Result.responseError("读取模式不能为空！");
		}
		if(StrKit.isBlank(readMode.getId())){
			readMode.setId(UUIDGenerator.getUUID());
			readMode.setCreateTime(new Date());
			affectNum=readModeService.insertReadMode(readMode);
		}else{
			affectNum=readModeService.updateReadModeByPrimaryKey(readMode);
		}
		
		if(affectNum<0){
			return Result.responseError("新增失败！");
		}
		result=Result.responseSuccess("新增成功！");
		return result;
	}
	
	/**
	 * 
	* @Title: selectReadModeByEqId 
	* @Description: 根据设备id查询识别模式信息
	* @param eqId
	* @return Result
	* @author xya
	* @date 2019年5月9日上午11:13:09
	 */
	@RequestMapping("/selectReadMode")
	public Result selectReadModeByEqId(String eqId)throws Exception{
		Result result=null;
		
		if(StrKit.isBlank(eqId)){
			return Result.responseError("设备id不能为空！");
		}
		ReadMode readMode=readModeService.selectReadModeByEqId(eqId);
		if(readMode==null){
			return Result.responseSuccess("暂无数据！");
		}
		result=Result.responseSuccess("查询成功！",readMode);
		return result;
	}
}
