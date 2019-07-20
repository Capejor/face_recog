/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.quartzJob 
 * @author: Administrator   
 * @date: 2019年5月16日 下午2:52:22 
 */
package com.faceRecog.manage.quartzJob;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/** 
 * @ClassName: BaseJob 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月16日 下午2:52:22  
 */
public interface  BaseJob extends Job{
	 public void execute(JobExecutionContext context) throws JobExecutionException;
}
