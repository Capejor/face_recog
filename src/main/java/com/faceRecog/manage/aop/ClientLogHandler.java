package com.faceRecog.manage.aop;

import com.faceRecog.manage.util.CommUtil;
import net.sf.json.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;


public class ClientLogHandler {
	private Logger logger = LoggerFactory.getLogger(ClientLogHandler.class);
	
	public Object around(ProceedingJoinPoint pjd){
		if(pjd!=null){
			 RequestAttributes ra = RequestContextHolder.getRequestAttributes();
		        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
		        HttpServletRequest request = sra.getRequest();
		        String url = request.getRequestURL().toString();
		        String method = request.getMethod();
		        String uri = request.getRequestURI();
		        String queryString = request.getQueryString();
		        logger.info("请求开始, 各个参数, url: {}, method: {}, uri: {}, params: {}"+ url+ method+uri+queryString);
		}
		
		
        Object result = null;  
        String methodName = pjd.getSignature().getName(); 
//        logger.info("@Around：被织入的目标对象为：" + pjd.getTarget());
        Object args=new Object();
		try {
			args = adminOptionContent(pjd.getArgs());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
//		System.out.println(args);
//        System.out.println("@After：参数为：" + Arrays.toString(pjd.getArgs()));
        //执行目标方法  
        try {  
            //前置通知  
//        	String temp=getRequestStr(pjd);
//        	System.out.println("-------------"+temp);
        	logger.info("Arround:The method "+methodName +" begins with "+ args);              
            result = pjd.proceed();  
            //后置通知  
            logger.info("Arround:The method "+ methodName+" ends with the Result "+ result);  
        } catch (Throwable e) {  
            e.printStackTrace();  
            //异常通知  
            logger.error("Arround:The method "+ methodName+"occurs exception:",e);  
            //throw new RuntimeException(e);  
            //不抛出异常的话，异常就被上面抓住，执行下去，返回result，result值为null，转换为int  
        }  
        //返回通知  
//        logger.info("Arround:The method "+ methodName+" ends with the Result "+ result);  
          
        return result;  
    }  
	/** 
     * 使用Java反射来获取被拦截方法(insert、update)的参数值， 
     * 将参数值拼接为操作内容 
     */  
    public String adminOptionContent(Object[] args) throws Exception{ 
        if (args == null) {  
            return null;  
        } 
        StringBuffer rs = new StringBuffer();  
        
        // 遍历参数对象  
        for (Object info : args) {
            //获取对象类型
        	if(info==null){
        		continue;
        	}
        	if(info instanceof String ||info instanceof Integer){
        		rs.append(info);
        	}else{
        		rs.append(info);
        	}
        	
        	rs.append(",");
        }  
          
        return rs.toString();  
    }  
    
    private String getRequestStr(ProceedingJoinPoint pjp){  
        String requestStr = "";  
        if(pjp.getArgs() != null && pjp.getArgs().length > 0){  
            for (int i = 0; i < pjp.getArgs().length; i++) {  
                if(pjp.getArgs()[i] instanceof LinkedHashMap){  
                    Map<String,Object> map = (Map<String,Object>)pjp.getArgs()[i];
                    requestStr += "Map参数值为：" + JSONObject.fromObject(map) + "|";  
                }else if(pjp.getArgs()[i] instanceof HttpServletRequest){  
                    HttpServletRequest req = ((HttpServletRequest)pjp.getArgs()[i]);                     
                    Enumeration<?> enu = req.getParameterNames();    
                    requestStr += "HttpServletRequest参数值为：" + JSONObject.fromObject(enu) + "|"+ CommUtil.parseRequst(req,"utf-8");
                }else if(pjp.getArgs()[i] instanceof String){  
                    requestStr += "HttpServletRequestString参数值为：" + pjp.getArgs()[i].toString() + "|";  
                }  
            }  
        }  
        return requestStr;  
    }  
  
}
