/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.controller 
 * @author: xya
 * @date: 2019年6月15日 下午2:43:56 
 */
package com.faceRecog.manage.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.faceRecog.manage.service.OriginalSignRecordService;
import com.faceRecog.manage.util.Result;

/** 
 * @ClassName: OriginalSignRecordController 
 * @Description: 原始打卡
 * @author: xya
 * @date: 2019年6月15日 下午2:43:56  
 */
@RestController
public class OriginalSignRecordController {

	@Autowired
	private OriginalSignRecordService originalSignRecordService;
	
	/**
	 * 
	* @Title: selectPageOrigSignInfo 
	* @Description: 查询原始打卡记录 
	* @return
	* @throws Exception Result
	* @author xya
	* @date 2019年6月21日下午2:46:30
	 */
	@RequestMapping("selectPageOrigSignInfo")
	public Result selectPageOrigSignInfo()throws Exception{
		Result result=null;
		List<Map<String, Object>> origSignLst=originalSignRecordService.selectPageOrigSignInfo();
		if(origSignLst!=null && origSignLst.size()>0){
			result=Result.responseSuccess("查询成功！",origSignLst);
		}else{
			result=Result.responseSuccess("暂无数据！",new ArrayList<>());
		}
		return result;
	}
}
