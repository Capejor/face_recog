/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.config 
 * @author: xya
 * @date: 2019年6月24日 下午9:14:18 
 */
package com.faceRecog.manage.config;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import com.faceRecog.manage.quartz.QuartzScheduler;

/** 
 * @ClassName: ApplicationStartQuartzJobListener 
 * @Description: TODO
 * @author: xya
 * @date: 2019年6月24日 下午9:14:18  
 */
@Configuration
public class ApplicationStartQuartzJobListener implements ApplicationListener<ContextRefreshedEvent>{
	private static Logger log = LoggerFactory.getLogger(ApplicationStartQuartzJobListener.class);
	
	@Autowired
    private QuartzScheduler quartzScheduler;

    /**
     * 初始启动quartz
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            //成分分析
            //quartzService.createScheduleJob("IngredientAnalysisTask", "*/5 * * * * ?", IngredientAnalysisTask.class);
            //投票统计
        	quartzScheduler.startJob();
        	log.info("任务已经启动...");
        } catch (SchedulerException e) {
        	log.error(e.getMessage());
        }
    }
}
