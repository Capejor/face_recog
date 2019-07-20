package com.faceRecog.manage.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.DateConverter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;


public class JSONObjectToBean {
	public static Object JSONObjectToBeanUtil(JSONObject json,
			@SuppressWarnings("rawtypes") Class clazz)
			throws IntrospectionException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		json = transToLowerObject(json);
		//System.out.println(json.toString());
		// 得到类的所有字段属性
		BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
		// 存在一些特殊类型的转换需要向转换器注册转换方法 下面是两个转换器Date和BigDecimal
		// ConvertUtils.register(new DateConvert(), Date.class);
		DateConverter dateConverter = new DateConverter(null);
		dateConverter.setPatterns(new String[]{"yyyy-MM-dd HH:mm:ss","yyyy/MM/dd HH:mm:ss"});
		//DateConverter dateConverter1 = new DateConverter(null);
		//dateConverter1.setPatterns(new String[]{"yyyy-MM-dd","yyyy/MM/dd"});
		ConvertUtils.register(dateConverter, Date.class); 
		//ConvertUtils.register(dateConverter1, Date.class);
		ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
		PropertyDescriptor[] propertyDescriptors = beanInfo
				.getPropertyDescriptors();
		Object obj = clazz.newInstance();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			// 先把属性的名字全小写
			String propertyName = descriptor.getName().toLowerCase();
			// 在大写所有字段去上面方法中去得到map的值
			if (json.containsKey(propertyName)) {
				String value = ConvertUtils.convert(json.get(propertyName));				
				Object[] args = new Object[1];
				args[0] = ConvertUtils.convert(value,descriptor.getPropertyType());
//				if(propertyName.equals("h5ImgAddTime".toLowerCase())){
//					System.out.println(descriptor.getPropertyType());
//				}				
				descriptor.getWriteMethod().invoke(obj, args);
			}else{
				//System.out.println(propertyName);
			}
		}
		return obj;
	}

	/**
	 * json大写转小写
	 * 
	 * @param jSONArray1
	 *            jSONArray1
	 * @return JSONObject
	 */
	public static JSONObject transToLowerObject(JSONObject jSONArray1) {
		JSONObject jSONArray2 = new JSONObject();
		Iterator<?> it = jSONArray1.keys();
		while (it.hasNext()) {
			String key = (String) it.next();
			Object object = jSONArray1.get(key);			
			if (object.getClass().toString().endsWith("JSONObject")) {
				jSONArray2.accumulate(key.toLowerCase(),
						transToLowerObject((JSONObject) object));
			} else if (object.getClass().toString().endsWith("JSONArray")) {
				jSONArray2.accumulate(key.toLowerCase(),
						transToArray(jSONArray1.getJSONArray(key)));
			}else{
				jSONArray2.accumulate(key.toLowerCase(), object);
			}
		}
		return jSONArray2;
	}

	/**
	 * jsonArray转jsonArray
	 * 
	 * @param jSONArray1
	 *            jSONArray1
	 * @return JSONArray
	 */
	public static JSONArray transToArray(JSONArray jSONArray1) {
		JSONArray jSONArray2 = new JSONArray();
		for (int i = 0; i < jSONArray1.size(); i++) {
			Object jArray = jSONArray1.getJSONObject(i);
			if (jArray.getClass().toString().endsWith("JSONObject")) {
				jSONArray2.add(transToLowerObject((JSONObject) jArray));
			} else if (jArray.getClass().toString().endsWith("JSONArray")) {
				jSONArray2.add(transToArray((JSONArray) jArray));
			}
		}
		return jSONArray2;
	}
}
