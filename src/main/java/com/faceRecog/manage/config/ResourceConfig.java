/**   
 * Copyright © 2019 yskj Info. Tech Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.config 
 * @author: Administrator   
 * @date: 2019年4月26日 上午10:56:07 
 */
package com.faceRecog.manage.config;




import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.faceRecog.manage.util.CommUtil;


/** 
 * @ClassName: ResourceConfig 
 * @Description: TODO
 * @author: Administrator
 * @date: 2019年4月26日 上午10:56:07  
 */

@Configuration
public class ResourceConfig extends WebMvcConfigurerAdapter{
	
    
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
        //和页面有关的静态目录都放在项目的static目录下
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        //上传的图片在D盘下的OTA目录下，访问路径如：http://localhost:8081/OTA/d3cf0281-bb7f-40e0-ab77-406db95ccf2c.jpg
        //其中OTA表示访问的前缀。"file:D:/OTA/"是文件真实的存储路径
        registry.addResourceHandler("/**").addResourceLocations("file:"+CommUtil.getJarPath());//+CommUtil.getJarPath()
    }
}