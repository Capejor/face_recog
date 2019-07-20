/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service.impl 
 * @author: Administrator   
 * @date: 2019年5月7日 下午5:15:25 
 */
package com.faceRecog.manage.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.faceRecog.manage.mapper.EquipmentMapper;
import com.faceRecog.manage.mapper.LinkRecMapper;
import com.faceRecog.manage.model.LinkRec;
import com.faceRecog.manage.service.LinkRecService;

/** 
 * @ClassName: LinkServiceImpl 
 * @Description:连接记录
 * @author: xya
 * @date: 2019年5月7日 下午5:15:25  
 */
@Service
public class LinkRecServiceImpl implements LinkRecService{

	@Autowired
	private LinkRecMapper linkRecMapper;
	
	@Autowired
	private EquipmentMapper equipmentMapper;
	
	/* (non Javadoc) 
	 * @Title: insertLinkRec
	 * @Description: TODO
	 * @param linkRec
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.LinkRecService#insertLinkRec(com.faceRecog.manage.model.LinkRec) 
	 */ 
	@Override
	public int insertLinkRec(LinkRec linkRec) throws Exception {
		 
		return linkRecMapper.insertSelective(linkRec);
	}

	/* (non Javadoc) 
	 * @Title: updateLinkRecState
	 * @Description: TODO
	 * @param linkRec
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.LinkRecService#updateLinkRecState(com.faceRecog.manage.model.LinkRec) 
	 */ 
	@Override
	@Transactional
	public int updateLinkRecStateBySn(LinkRec linkRec) throws Exception {
		// 更新设备表设备的在线状态
		int affectNum=equipmentMapper.updateEquipmentLineStateBySn(linkRec.getSn(),linkRec.getStatus());
		if(affectNum<0){
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return -1;
		}
		return linkRecMapper.updateLinkRecStateBySn(linkRec);
	}

	/* (non Javadoc) 
	 * @Title: selectLinkRecBySn
	 * @Description: TODO
	 * @param sn
	 * @return
	 * @throws Exception 
	 * @see com.faceRecog.manage.service.LinkRecService#selectLinkRecBySn(java.lang.String) 
	 */ 
	@Override
	public LinkRec selectLinkRecBySn(String sn) throws Exception {
		 
		return linkRecMapper.selectLinkRecBySn(sn);
	}

}
