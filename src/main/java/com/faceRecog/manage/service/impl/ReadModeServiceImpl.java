/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service.impl 
 * @author: Administrator   
 * @date: 2019年5月8日 上午10:54:13 
 */
package com.faceRecog.manage.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.faceRecog.manage.mapper.ReadModeMapper;
import com.faceRecog.manage.model.ReadMode;
import com.faceRecog.manage.service.ReadModeService;

/** 
 * @ClassName: ReadModeServiceImpl 
 * @Description: 读取模式
 * @author: xya
 * @date: 2019年5月8日 上午10:54:13  
 */
@Service
public class ReadModeServiceImpl implements ReadModeService{

	
	@Autowired
	private ReadModeMapper readModeMapper;
	
	/* (non Javadoc) 
	 * @Title: insertReadMode
	 * @Description: TODO
	 * @param readMode
	 * @return 
	 * @see com.faceRecog.manage.service.ReadModeService#insertReadMode(com.faceRecog.manage.model.ReadMode) 
	 */ 
	@Override
	public int insertReadMode(ReadMode readMode) throws Exception{
		 
		return readModeMapper.insertSelective(readMode);
	}

	/* (non Javadoc) 
	 * @Title: selectReadModeByPrimaryKey
	 * @Description: TODO
	 * @param id
	 * @return 
	 * @see com.faceRecog.manage.service.ReadModeService#selectReadModeByPrimaryKey(java.lang.String) 
	 */ 
	@Override
	public ReadMode selectReadModeByPrimaryKey(String id) {
		 
		return readModeMapper.selectByPrimaryKey(id);
	}

	/* (non Javadoc) 
	 * @Title: selectReadModeByEqId
	 * @Description: TODO
	 * @param eqId
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.ReadModeService#selectReadModeByEqId(java.lang.String) 
	 */ 
	@Override
	public ReadMode selectReadModeByEqId(String eqId) throws Exception {
	 
		return readModeMapper.selectReadModeByEqId(eqId);
	}

	/* (non Javadoc) 
	 * @Title: updateReadModeByPrimaryKey
	 * @Description: TODO
	 * @param readMode
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.ReadModeService#updateReadModeByPrimaryKey(com.faceRecog.manage.model.ReadMode) 
	 */ 
	@Override
	public int updateReadModeByPrimaryKey(ReadMode readMode) throws Exception {
		 
		return readModeMapper.updateByPrimaryKeySelective(readMode);
	}

}
