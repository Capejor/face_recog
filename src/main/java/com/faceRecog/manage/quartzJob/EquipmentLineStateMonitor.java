/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.quartz 
 * @author: Administrator   
 * @date: 2019年5月16日 上午11:29:44 
 */
package com.faceRecog.manage.quartzJob;


import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
 
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.faceRecog.manage.handler.SpringWebSocketHandler;
import com.faceRecog.manage.model.LinkRec;
import com.faceRecog.manage.service.EquipmentService;
import com.faceRecog.manage.util.ApplicationContextProvider;

/** 
 * @ClassName: SchedulerQuartzJob 
 * @Description: 考勤结算定时任务
 * @author: xya
 * @date: 2019年5月16日 上午11:29:44  
 */
@Component
@Resource
public class EquipmentLineStateMonitor  implements BaseJob{
  
	private Logger logger = LoggerFactory.getLogger(EquipmentLineStateMonitor.class);
	 
	 
	 
	private void before(){
		
        System.out.println("每1分钟任务开始执行");
    }

    @Override
    public void execute(JobExecutionContext job) throws JobExecutionException{
        before();
        try {
        	EquipmentService equipmentService = ApplicationContextProvider.getBean(EquipmentService.class);
        	SpringWebSocketHandler websocket=new SpringWebSocketHandler();
			//System.out.println("监测连接时间=====");
			List<Map<String, Object>> linkRecList=equipmentService.selectAllOnLineEquipmentInfo();
			if(linkRecList!=null && linkRecList.size()>0 && linkRecList.get(0)!=null){
				for(Map<String, Object> linkRecMap:linkRecList){
					Date linkTime=(Date)linkRecMap.get("newLinkTime");// 最新的一次连接时间
					Date currTime=new Date();
					long diff=(currTime.getTime()-linkTime.getTime())/1000;
					//System.out.println("设备:"+linkRecMap.get("clNo")+"连接时间:"+diff);
					//如果设备的连接时间相比现在时间大于一分30秒更新设备状态为离线状态
					if(diff>30){
						LinkRec linkRec=new LinkRec();
						linkRec.setSn((String)linkRecMap.get("sn"));
						websocket.closeScoket(linkRec);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("定时器监测设备的socket连接状态 设备表连接状态更新失败",e);
		}
		after();
	}
    
    private void after(){
        System.out.println("任务结束");
    }
    
   
}
