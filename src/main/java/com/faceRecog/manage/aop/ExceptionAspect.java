package com.faceRecog.manage.aop;

import com.faceRecog.manage.annotation.LogAnnotation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.ThrowsAdvice;

import java.lang.reflect.Method;
import java.net.InetAddress;


public class ExceptionAspect implements ThrowsAdvice {
	

	public void afterThrowing(JoinPoint joinPoint, Exception e)
			throws Throwable {
		String methodString = joinPoint.getSignature().getName();
		
		System.out.println("methodString:"+methodString);
		// Method meths[] =
		// joinPoint.getSignature().getDeclaringType().getMethods();
		// Method met=null;
		
		// for(Method meth:meths){
		// if(meth.toString().indexOf(joinPoint.getSignature().getName())>-1){
		// //met=meth;
		// break;
		// }
		// }
		try {
			
			
			MethodSignature ms = (MethodSignature) joinPoint.getSignature();
			Method method = ms.getMethod();
			if (methodString.indexOf("findRegacc") != -1) {// 登录
				// 获取IP地址
				String ipAddress = InetAddress.getLocalHost().getHostAddress();
				System.out.println(ipAddress);
			}
			
			/** 0:异常 1: 登录 2:新增 3:修改 4:删除 */
			
			if (null != method
					&& null != method.getAnnotation(LogAnnotation.class)) {
				//sg.setModular(method.getAnnotation(LogAnnotation.class).desc());//模块名称
				System.out.println("模块名称:"+method.getAnnotation(LogAnnotation.class).desc());
			}
			String mes = e.getMessage();
			int length = e.getStackTrace().length;
			StringBuffer msg = new StringBuffer();
			if (length > 0) {
				msg.append(e + "\n");
				for (int i = 0; i < length; i++) {
					msg.append("\t" + e.getStackTrace()[i] + "\n");
				}
			} else {
				msg.append(mes);
			}
			mes=msg.toString();
			if (mes != null) {
				if (mes.length() > 3500) {
					mes = mes.substring(0, 3500);
				}
			}
			System.out.println("异常内容："+e.getClass().getName());
			
		} catch (Exception ex) {
			//ex.printStackTrace();
		}
	}

}
