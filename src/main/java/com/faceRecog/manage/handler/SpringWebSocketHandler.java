package com.faceRecog.manage.handler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.faceRecog.manage.model.LinkRec;
import com.faceRecog.manage.service.EquipmentService;
import com.faceRecog.manage.service.InstructionRecService;
import com.faceRecog.manage.service.LinkRecService;
import com.faceRecog.manage.util.ApplicationContextProvider;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


/**
 * 
  * ClassName: SpringWebSocketHandler 
  * @Description: socket消息处理
  * @author xya
  * @date 2018年9月17日
 */
@Component
public class SpringWebSocketHandler extends TextWebSocketHandler {
	private static final Map<String,WebSocketSession> users;//这个会出现性能问题，最好用Map来存储，key用userid
	private Logger logger = LoggerFactory.getLogger(SpringWebSocketHandler.class);
	
	@Autowired
	private LinkRecService linkRecService;
	
	
	@Autowired
	private InstructionRecService instructionRecService;
	

	// 静态初使化当前类
    public static SpringWebSocketHandler springWebSocketHandler;
    //注解@PostConstruct，这样方法就会在Bean初始化之后被Spring容器执行
    @PostConstruct
    public void init() {
    	springWebSocketHandler = this;
    }
	
    
    static {
        users =new ConcurrentHashMap<String,WebSocketSession>();
    }

    public SpringWebSocketHandler() {
    	
    }

 
    /**
     * 连接成功时候，会触发页面上onopen方法
     */
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if(!users.containsKey(session.getAttributes().get("WEBSOCKET_USERNAME"))){
        	users.put((String) session.getAttributes().get("WEBSOCKET_USERNAME"), session);
        }
        //String currDate=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        System.out.println("connect to the websocket success......当前数量:"+users.size());
        //这块会实现自己业务，比如，当用户登录后，会把离线消息推送给用户
        String sn= String.valueOf(session.getAttributes().get("WEBSOCKET_USERNAME"));
        LinkRec linkRecord=springWebSocketHandler.linkRecService.selectLinkRecBySn(sn);
        if(linkRecord!=null){
        	linkRecord.setStatus("1");
        	linkRecord.setNewLinkTime(new Date());
        	linkRecord.setSn(sn);
        	int affectNum=springWebSocketHandler.linkRecService.updateLinkRecStateBySn(linkRecord);
        	if(affectNum<0){
            	logger.error("修改设备连接记录出错");
            }
        }else{
        	linkRecord=new LinkRec();
        	linkRecord.setCreateTime(new Date());
        	linkRecord.setNewLinkTime(new Date());
        	linkRecord.setStatus("1");
        	linkRecord.setSn(sn);
        	linkRecord.setId(UUIDGenerator.getUUID());
            int affectNum=springWebSocketHandler.linkRecService.insertLinkRec(linkRecord);
            if(affectNum<0){
            	logger.error("新增设备连接记录出错");
            }
        }
        TextMessage returnMessage = new TextMessage("我们已经建立通信！");
        session.sendMessage(returnMessage);
        //System.out.println("返回给用户消息"+returnMessage);
    }

    /**
     * 关闭连接时触发
     */
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    	//System.out.println("websocket connection closed......");
    	
        String sn= (String) session.getAttributes().get("WEBSOCKET_USERNAME");//设备编号

        //String currDate=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        System.out.println("设备"+sn+"已退出！");
        Set<Entry<String, WebSocketSession>> set=users.entrySet();
		 Iterator<Entry<String, WebSocketSession>> iterator=set.iterator();
	        while(iterator.hasNext()){
	            Entry<String, WebSocketSession> entry=iterator.next();
//	          String name=entry.getKey();
	            String name=entry.getKey();
	            if(name.contains(sn)){
	                //特别注意：不能使用map.remove(name)  否则会报同样的错误
	            	iterator.remove();
	            }
	        }
	        LinkRec linkRec=new LinkRec();
	        linkRec.setSn(sn);
	        linkRec.setDiscTime(new Date());
	        linkRec.setStatus("0");
	        int affectNum=springWebSocketHandler.linkRecService.updateLinkRecStateBySn(linkRec);
	        if(affectNum<0){
	        	logger.error("设备关闭修改连接记录出错");
	        }
    }

    /**
     * 客户端发送消息接收 触发
     */
    @Override    
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        message.getPayload();
        System.out.println("message:"+message.getPayload().toString());
        Map<String, Object> map= new HashMap<String, Object>();
        map=session.getAttributes();
        TextMessage returnMessage =null;
		//System.out.println("发送用户当前用户sessionid"+String.valueOf(map.get("WEBSOCKET_USERNAME")));
		JSONObject jsonRecObj=new JSONObject();
		jsonRecObj=JSONObject.fromObject(message.getPayload().toString());
		// 判断是设备端是否是心跳机制 还是设备连接socket
		if(jsonRecObj.get("code").equals(CommonConstant.HEARTBEAT_MSG.getValue())){// 心跳
			JSONObject jsonObj=new JSONObject();
			jsonObj.put("type", 2000);//心跳类型值
			jsonObj.put("code", 4002);
			String sn=String.valueOf(map.get("WEBSOCKET_USERNAME"));
			LinkRec linkRecord=new LinkRec();
			linkRecord.setStatus("1");
			linkRecord.setSn(sn);
        	linkRecord.setNewLinkTime(new Date());
			int affectNum = linkRecService.updateLinkRecStateBySn(linkRecord);
			if(affectNum<0){
	        	logger.error("设备发送心跳修改连接记录出错");
	        }
			returnMessage=new TextMessage(jsonObj.toString());
			String eqNo=String.valueOf(map.get("WEBSOCKET_USERNAME"));
			sendMessageToUser(eqNo,returnMessage);
		}else if(jsonRecObj.get("code").equals(CommonConstant.COMMON_MSG.getValue())){// 设备连接初始
			returnMessage=new TextMessage("收到来至设备"+String.valueOf(map.get("WEBSOCKET_USERNAME"))+"的消息");
			sendMessageToUser(String.valueOf(map.get("WEBSOCKET_USERNAME")),returnMessage);
	    }else if(jsonRecObj.get("code").equals(CommonConstant.RECEIVE_RES_MSG.getValue())){// 指令响应消息 修改指令发送的状态
	    	int affectNum=instructionRecService.updateInstructionRecStatus(String.valueOf(jsonRecObj.getString("msg")));
	    	if(affectNum<0){
	    		logger.error("修改指令发送状态失败");
	    	}
	    	System.out.println(Integer.valueOf(String.valueOf(jsonRecObj.getString("msg"))));
	    }
    }

    /**
     * 连接异常
     */
    public void handleTransportError(WebSocketSession session, Throwable exception)throws Exception{
         if(session.isOpen()){
			session.close();
		 }
		 String clNo= (String) session.getAttributes().get("WEBSOCKET_USERNAME");//设备编号
         //System.out.println(clNo+":异常websocket connection closed......");
         Set<Entry<String, WebSocketSession>> set=users.entrySet();
		 Iterator<Entry<String, WebSocketSession>> iterator=set.iterator();
	        while(iterator.hasNext()){
	            Entry<String, WebSocketSession> entry=iterator.next();
//	          String name=entry.getKey();
	            String name=entry.getKey();
	            if(name.contains(clNo)){
	                //特别注意：不能使用map.remove(name)  否则会报同样的错误
	            	iterator.remove();
	            }
	        }
    }

    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给某个用户发送消息
     *
     * @param userName
     * @param message
     */
    public  void sendMessageToUser(String userName, TextMessage message) throws IOException{
        for (Entry<String, WebSocketSession> item : users.entrySet()) {
            if (item.getValue().getAttributes().get("WEBSOCKET_USERNAME").equals(userName)) {
                    if (item.getValue().isOpen()) {
                    	item.getValue().sendMessage(message);
                    }
                break;
            }
        }
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToUsers(TextMessage message) {
    	for (Entry<String, WebSocketSession> item : users.entrySet()) {
            try {
                if (item.getValue().isOpen()) {
                	item.getValue().sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }   
    
    /**
     * 
     * @Title: closeScoket
     * @Description: 关闭socket
     * @param: @param clNo   
     * @return: void   
     * @throws
     */
	public void closeScoket(LinkRec linkRec) {
		linkRecService = ApplicationContextProvider.getBean(LinkRecService.class);
		
		int affectNum = 0;
		int count = 0;
		String sn=linkRec.getSn();
		linkRec.setStatus("0");
		linkRec.setDiscTime(new Date());
		// System.out.println("===手动关闭连接 断电或断网===");
		if (users.size() > 0) {
			Set<Entry<String, WebSocketSession>> set = users.entrySet();
			Iterator<Entry<String, WebSocketSession>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Entry<String, WebSocketSession> entry = iterator.next();
				// String name=entry.getKey();
				String name = entry.getKey();
				WebSocketSession session = entry.getValue();
				if (name.contains(sn)) {
					// 特别注意：不能使用map.remove(name) 否则会报同样的错误
					try {
						session.close();
						count++;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
			if (count < 1) {
				try {
					linkRec.setStatus("0");
					affectNum = linkRecService.updateLinkRecStateBySn(linkRec);
					if (affectNum < 0) {
						logger.error("定时器监测设备的socket连接状态 连接记录表连接状态更新失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				linkRec.setStatus("0");
				affectNum = linkRecService.updateLinkRecStateBySn(linkRec);
				if (affectNum < 0) {
					logger.error("定时器监测设备的socket连接状态 连接记录表连接状态更新失败");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
      
}
