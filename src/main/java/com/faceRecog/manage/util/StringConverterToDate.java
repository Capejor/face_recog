/**   
 * Copyright © 2019 yskj Tech Co., Ltd. All rights reserved.
 * 
 * @Package: com.faceRecog.manage.util 
 * @author: Administrator   
 * @date: 2019年4月30日 上午9:12:57 
 */
package com.faceRecog.manage.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;
/** 
 * @ClassName: StringToDateConverte 
 * @Description: TODO
 * @author: xya
 * @date: 2019年4月30日 上午9:12:57  
 */
public class StringConverterToDate implements Converter<String, Date>{
	 	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
	    private static final String shortDateFormat = "yyyy-MM-dd";

	    @Override
	    public Date convert(String value) {

	        if(StringUtils.isEmpty(value)) {
	            return null;
	        }

	        value = value.trim();

	        try {
	            if(value.contains("-")) {
	                SimpleDateFormat formatter;
	                if(value.contains(":")) {
	                    formatter = new SimpleDateFormat(dateFormat);
	                }else {
	                    formatter = new SimpleDateFormat(shortDateFormat);
	                }

	                Date dtDate = formatter.parse(value);
	                return dtDate;              
	            }else if(value.matches("^\\d+$")) {
	                Long lDate = new Long(value);
	                return new Date(lDate);
	            }
	        } catch (Exception e) {
	            throw new RuntimeException(String.format("parser %s to Date fail", value));
	        }
	        throw new RuntimeException(String.format("parser %s to Date fail", value));
	    }
}
