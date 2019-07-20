package com.faceRecog.manage.annotation;

import java.lang.annotation.*;

//自定义注解相关设置

@Target({ElementType.METHOD})   
@Retention(RetentionPolicy.RUNTIME)  
@Inherited
@Documented
public @interface LogAnnotation {
	//自定义注解的属性，default是设置默认值
	int operType() default -1;
	String module() default "未知模块";
    String desc() default "无描述信息";  
}
