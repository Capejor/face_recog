/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.quartz 
 * @author: xya
 * @date: 2019年6月24日 下午9:11:29 
 */
package com.faceRecog.manage.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/** 
 * @ClassName: SpringJobFactory 
 * @Description: TODO
 * @author: xya
 * @date: 2019年6月24日 下午9:11:29  
 */
@Component
public class SpringJobFactory extends AdaptableJobFactory{

	  	@Autowired
	    private AutowireCapableBeanFactory capableBeanFactory;

	    @Override
	    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
	        Object jobInstance = super.createJobInstance(bundle);
	        capableBeanFactory.autowireBean(jobInstance);
	        return jobInstance;
	    }
}
