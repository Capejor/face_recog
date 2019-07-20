package com.faceRecog.manage.util;

import java.math.BigDecimal;

/**
  * @copyright：
  * @ClassName: UuidUtils
  * @Description: 分布式主键生成器
  * @author zhaoxing
  * @date 2016年7月11日 下午5:12:54
  * @version V1.0  
  *
 */
public class UuidUtils {
	/**
	  * @copyright：
	  * @version V1.0  
	  * @Title: getUuid  
	  * @Description: 获取一个uuuid字符串 
	  * @return String  返回类型
	  * @throws
	 */
	public static String getUuid(){
		return UUIDGenerator.getUUID();
	}
	public static void main(String[] args) {
		BigDecimal bd = new BigDecimal(4.236);
		BigDecimal bd1= new BigDecimal(4.885);
		if(bd.compareTo(bd1)==-1)
			System.out.println();
		System.out.println();
	}
}
