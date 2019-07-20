/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.config 
 * @author: Administrator   
 * @date: 2019年5月16日 上午10:37:31 
 */
package com.faceRecog.manage.config;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.Resource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.faceRecog.manage.quartz.QuartzScheduler;
import com.faceRecog.manage.quartz.SpringJobFactory;
/** 
 * @ClassName: ApplicationStartQuartzJobListener 
 * @Description: TODO
 * @author: xya
 * @date: 2019年5月16日 上午10:37:31  
 */
@Configuration
public class SchedulerConfig{
 
	 @Autowired
	    private SpringJobFactory springJobFactory;

	    @Bean
	    public SchedulerFactoryBean schedulerFactoryBean() {
	        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
	        schedulerFactoryBean.setJobFactory(springJobFactory);
	        return schedulerFactoryBean;
	    }

	    @Bean
	    public Scheduler scheduler() {
	        return schedulerFactoryBean().getScheduler();
	    }
}
