/**   
 * Copyright © 2019 yskj Info. Tech Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.config 
 * @author: Administrator   
 * @date: 2019年4月26日 上午10:56:07 
 */
package com.faceRecog.manage.config;

import javax.annotation.PostConstruct;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.faceRecog.manage.util.StringConverterToDate;

/** 
 * @ClassName: DateConfig 
 * @Description: TODO
 * @author: Administrator
 * @date: 2019年4月26日 上午10:56:07  
 */

@Configuration
public class DateConfig{
	@Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    /**
     * 增加字符串转日期的功能
     */

    @PostConstruct
    public void initEditableAvlidation() {

        ConfigurableWebBindingInitializer initializer = (ConfigurableWebBindingInitializer)handlerAdapter.getWebBindingInitializer();
        if(initializer.getConversionService()!=null) {
            GenericConversionService genericConversionService = (GenericConversionService)initializer.getConversionService();           

            genericConversionService.addConverter(new StringConverterToDate());

        }

    }
    
}