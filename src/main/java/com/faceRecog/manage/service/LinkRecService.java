/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service 
 * @author: Administrator   
 * @date: 2019年5月7日 下午5:15:01 
 */
package com.faceRecog.manage.service;

import com.faceRecog.manage.model.LinkRec;

/** 
 * @ClassName: linkService 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月7日 下午5:15:01  
 */
public interface LinkRecService {

	
	int insertLinkRec(LinkRec linkRec)throws Exception;
	
	
	int updateLinkRecStateBySn(LinkRec linkRec)throws Exception;
	
	LinkRec selectLinkRecBySn(String sn)throws Exception;
}
