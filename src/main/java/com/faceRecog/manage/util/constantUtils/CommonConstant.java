package com.faceRecog.manage.util.constantUtils;

/**
 * 
  * ClassName: CommonConstant 
  * @Description: 通用枚举常量类
  * @author xya
  * @date 2018年9月21日
 */
public enum CommonConstant {

	
	
	
	/**
	 * 指令发送类型  区分指令发送来源 如：windows 安卓
	 */
	TYPE("1000"),//指令发送类型  区分指令发送来源 如：windows 安卓
	/**
	 * 唤醒设备建立socket连接
	 */
	WAKE_TYPE("2000"),//唤醒设备建立socket连接
	/*===================================socket指令相关 start=======================================================*/	
	/**
	 * 设备重启
	 */
	RESTART("5001"),//设备重启
	/**
	 * 恢复出厂设置
	 */
	RESTORE_FACTORY("5002"),//恢复出厂设置
	/**
	 * 远程开门
	 */
	REMOTE_DOOR("5003"),//远程开门
	/**
	 * 新增人脸
	 */
	UPDATE_FACE("5004"),// 新增人脸
	
	/**
	 * 更新访客
	 */
	UPDATE_VISITOR("5005"),// 更新访客
	
	/**
	 * 更新门禁
	 */
	UPDATE_PASS_AUTH("5006"),// 更新门禁
	
	/**
	 * 更新设备信息
	 */
	UPDATE_EQUIPMENT("5007"),// 更新设备信息
	
	/**
	 * 新增员工
	 */
	UPDATE_ATTENDANCE("5008"),// 更新打卡排班信息
	
	/**
	 * 检查设备更新
	 */
	CHECK_UPGRADE("5009"),// 检查设备更新
	
	/**
	 * 更新员工
	 */
	UPDATE_EMPLOYEE("5010"),// 更新员工
	 
	 /**
	  * 设备端心跳消息
	  */
	 HEARTBEAT_MSG("6002"),
	 
	 /**
	  * 普通socket消息
	  */
	 COMMON_MSG("6001"),
	 
	 /**
	  *指令响应
	  */
	 RECEIVE_RES_MSG("6003"),
	
/*===================================阿里云相关 start=======================================================*/
	/**
	 * 阿里云服务器地址
	 */
	 ALIYUN_SERVER_URL("http://47.107.50.81:8080/etv/webservice/"),//阿里云服务器地址
	
	/**
	 * 端口号
	 */
	 HOST("8080"),//端口号
	
	
	
/*=====================================班次相关 start=====================================================*/	
	 休息 ("0"),
	 
	 常规班("1"),
	 
	 两班制("2"),
	 
	 三班制("3"),
	
	签到("signIn"),
	
	签退("signOut"),
	
	固定班("1"),
	
	排班制("2"),
	
	次日("1"),
	
	当天("0");
/*========================================~(@^_^@) 完了==================================================*/	
	private final  String enumConst;
	
	CommonConstant(String enumConst){
		this.enumConst=enumConst;
	}
	
	
	public String getValue(){
		return enumConst;
	}

	
	
}
