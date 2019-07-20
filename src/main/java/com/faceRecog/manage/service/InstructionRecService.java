/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.service 
 * @author: Administrator   
 * @date: 2019年5月9日 下午3:19:07 
 */
package com.faceRecog.manage.service;


import com.faceRecog.manage.util.Result;

/**
 * @ClassName: InstructionService 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月9日 下午3:19:07  
 */
public interface InstructionRecService {

	Result insertInstructionRec(String instruction , String []sns, String password)throws Exception;
	
	
	int updateInstructionRecStatus(String id)throws Exception;
}
