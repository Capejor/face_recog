package com.faceRecog.manage.inteceptor;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;




public class SpringWebSocketHandlerInterceptor extends HttpSessionHandshakeInterceptor {
	
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        //System.out.println("Before Handshake");
        
        if (request instanceof ServletServerHttpRequest) {
    		String deviceid=request.getURI().getQuery();
    		//设备端未产生session 将设备编号赋值给当前session 区分连接的设备
        	attributes.put("WEBSOCKET_USERNAME",deviceid);
        	//System.out.println("设备=========="+deviceid);
        	String currDate=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);

    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
    
}