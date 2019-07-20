package com.faceRecog.manage.exception;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;


   
  
public class MyExceptionHandler implements HandlerExceptionResolver{  
	private Logger logger = LoggerFactory.getLogger(MyExceptionHandler.class);
    @Override  
    public ModelAndView resolveException(HttpServletRequest request,  
            HttpServletResponse response, Object handler, Exception ex) {  
        logger.error("Catch Exception: ",ex);//把漏网的异常信息记入日志  
        Map<String, Object> model = new HashMap<>();
        ModelAndView mv= new ModelAndView();
        FastJsonJsonView view=new FastJsonJsonView();
        model.put("ex", ex);
        //System.out.println(ex);
        Map<String,Object> map = new HashMap<>();
        if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
            // 输出JSON
        	if(ex instanceof UnauthorizedException||ex instanceof UnauthenticatedException){   
        		map.put("msg", "未知错误！");
    			map.put("code", 1);
    			view.setAttributesMap(map);
    			mv.setView(view);
    			return mv;
        	}else if(ex instanceof MultipartException){
                map.put("msg", "未知错误！");
                view.setAttributesMap(map);
                mv.setView(view);
                //webUtils.writeJson(map, response);
        	}            
            return mv;
        } else {
			map.put("msg", "未知错误！");
			map.put("code", 1);
			view.setAttributesMap(map);
			mv.setView(view);
			return mv; 
        }
        
    }  
  
}  
