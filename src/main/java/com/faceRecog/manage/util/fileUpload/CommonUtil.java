package com.faceRecog.manage.util.fileUpload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 通用工具方法
 * @author zhangyuxiang
 *
 */
public class CommonUtil {
	/**
	 * method:getNumFromString(从字符传中抽取数字)
	 * return:String
	 */
	public static String getNumFromString(String str) {
		String numStr = "0";
		if(!"".equals(str) && null != str) {
			String regEx="[^0-9]";   
			Pattern p = Pattern.compile(regEx);   
			Matcher m = p.matcher(str);   
			numStr =  m.replaceAll("").trim();
		}
		return numStr;
	}
	
	/**
	 * method:pictuerSizeFormat(日期格式的转换 EEE MMM dd HH:mm:ss Z yyyy → yyyy-MM-dd HH:mm:ss)
	 * return:String
	 */
	public static String dateFormat(String time) {
		String date = time;
		SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
		try {
			//把字符串传换成Date类型
			Date dateTime = sdf1.parse(time);
			//日期格式的转换
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = sdf.format(dateTime);
		} catch (ParseException e) {
			date = time;
		}

		return date;
	}
	
	/**
	 * method:FileSizeFormat(将一个单位为BYTE的LONG整数转化为以MB,KB为单位的STRING)
	 * return:filesize
	 */
	public static String fileSizeFormat(long size) {
		String filesize = "";
		//精确计算的结果
		BigDecimal decimal = new BigDecimal(size);
		BigDecimal megabyte = new BigDecimal(1024*1024);
		//指定计算答案的精度
		float resultMB = decimal.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
		
		if(resultMB >1) {
			filesize = resultMB + " MB";
		} else {
			BigDecimal megabyte_1 = new BigDecimal(1024);
			//指定计算答案的精度
			float resultKB = decimal.divide(megabyte_1, 2, BigDecimal.ROUND_UP).floatValue();
			filesize = resultKB + " KB";
		}
		
		return filesize;
	}

	/**
	 * 字符为null的时候，转为""
	 */
	public static String nullFormtSpace(String str) {
		String result = str;
		if (null == result) {
			result = "";
		} else {
			result = result.trim();
		}
		return result;
	}
	
	/**
	 * 日期取值 格式（yyyy-MM-dd）
	 * @return
	 */
	public static String dateFormat_yyyy_MM_dd(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * 日期取值 格式（yyyy-MM-dd HH:mm:ss）
	 * @return
	 */
	public static String dateFormat_yyyy_MM_dd_HH_mm_ss(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * 百分比转换
	 * @param finshSize 已完成的大小
	 * @param totalSize 总大小
	 * @return
	 */
	public static String parcentFormat(double finshSize,double totalSize){
		String persentStr = "";
		if(totalSize == finshSize){
			persentStr = "100%";
		} else if (totalSize > finshSize){
			double persent = finshSize/totalSize;
			DecimalFormat df = new DecimalFormat("0.0");
			persentStr = df.format(persent*100) + "%";
		} else {
			persentStr = "0%";
		}
		return persentStr;
	}
	
	/**
	 * 读取配置文件
	 * @Map
	 */
	public static Map<String, String> readSafePro(InputStream in){
		Map<String, String> map = new HashMap<String, String>();
		Properties prop = new Properties();
		try {
			//读取属性文件properties
			InputStream inStream = new BufferedInputStream(in);
			prop.load(inStream);
			Iterator<String> it = prop.stringPropertyNames().iterator();
			
			while (it.hasNext()) {
				String key = it.next();
				String value = prop.getProperty(key);
				map.put(key, value);
			}
			
		} catch (Exception e) {
		}
		return map;
	}
	
	/**
	 * 根据时间随机生成文件名
	 * @return
	 */
	public synchronized static String fileNameFormat(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmSSS");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * 日期比较
	 * @param start_data 开始时间
	 * @param end_data   结束时间
	 * @return boolean
	 */
	public static boolean dataCompare(String data1,String data2){
		boolean flg = false;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(data1);
            Date dt2 = df.parse(data2);
            if (dt1.getTime() > dt2.getTime()) {
            	flg = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return flg;
	}
	
	
	
	/**
	 * 删除文件下的所有文件及目录
	 * @param filePath
	 * @return
	 */
	public static void clearDir(File file){
		if (file.isDirectory()) {
            for (File f : file.listFiles()) {  
                clearDir(f);  
                f.delete();  
            }  
        }  
        file.delete();
	}
	
	/**
	 * 计算两个日期相差天数
	 * @param startDate
	 * @param endState
	 * @return
	 */
	public static long calDays(String startDate,String endState){
		long calDays = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			long to = df.parse(startDate).getTime();
			long from = df.parse(endState).getTime();
			calDays = (from-to)/(1000 * 60 * 60 * 24);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return calDays;
	}
	
	/**
	 * 日期加天数
	 * @param date
	 * @param days
	 * @return
	 */
	public static String addDaysDate(String date,long days){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar fromCal=Calendar.getInstance();
		try {
			Date nowTime = df.parse(date);
			fromCal.setTime(nowTime);
			fromCal.add(Calendar.DATE,(int)days);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return df.format(fromCal.getTime());
	}
	
	
	
	
}
