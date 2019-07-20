package com.faceRecog.manage.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.jsoup.safety.Whitelist;
import org.springframework.boot.system.ApplicationHome;

import net.sf.json.JSONObject;

public class CommUtil {
	/**
	 * 是否控制台打印异常信息
	 */
	public static final boolean errorPrint = false;
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	private static final Whitelist user_content_filter = Whitelist.relaxed();
	static int totalFolder;
	static int totalFile;

	static {
		user_content_filter.addTags(new String[] { "embed", "object", "param",
				"span", "div", "font" });
		user_content_filter.addAttributes(":all", new String[] { "style",
				"class", "id", "name" });
		user_content_filter.addAttributes("object", new String[] { "width",
				"height", "classid", "codebase" });
		user_content_filter.addAttributes("param", new String[] { "name",
				"value" });
		user_content_filter.addAttributes("embed",
				new String[] { "src", "quality", "width", "height",
						"allowFullScreen", "allowScriptAccess", "flashvars",
						"name", "type", "pluginspage" });

		totalFolder = 0;
		totalFile = 0;
	}

	public static String first2low(String str) {
		String s = "";
		s = str.substring(0, 1).toLowerCase() + str.substring(1);
		return s;
	}

	public static String first2upper(String str) {
		String s = "";
		s = str.substring(0, 1).toUpperCase() + str.substring(1);
		return s;
	}

	public static List<String> str2list(String s) throws IOException {
		List<String> list = new ArrayList<String>();
		if ((s != null) && (!s.equals(""))) {
			StringReader fr = new StringReader(s);
			BufferedReader br = new BufferedReader(fr);
			String aline = "";
			while ((aline = br.readLine()) != null) {
				list.add(aline);
			}
		}
		return list;
	}		

	public static Date formatDate(String s) {
		Date d = null;
		try {
			d = dateFormat.parse(s);
		} catch (Exception localException) {
		}
		return d;
	}

	public static Date formatDate(String s, String format) {
		Date d = null;
		try {
			SimpleDateFormat dFormat = new SimpleDateFormat(format);
			d = dFormat.parse(s);
		} catch (Exception localException) {
		}
		return d;
	}

	public static String formatTime(String format, Object v) {
		if (v == null)
			return null;
		if (v.equals(""))
			return "";
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(v);
	}

	public static String formatLongDate(Object v) {
		if ((v == null) || (v.equals("")))
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(v);
	}

	public static String formatShortDate(Object v) {
		if (v == null)
			return null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(v);
	}

	public static String decode(String s) {
		String ret = s;
		try {
			ret = URLDecoder.decode(s.trim(), "UTF-8");
		} catch (Exception localException) {
		}
		return ret;
	}

	public static String encode(String s) {
		String ret = s;
		try {
			ret = URLEncoder.encode(s.trim(), "UTF-8");
		} catch (Exception localException) {
		}
		return ret;
	}

	public static String convert(String str, String coding) {
		String newStr = "";
		if (str != null)
			try {
				newStr = new String(str.getBytes("ISO-8859-1"), coding);
			} catch (Exception e) {
				return newStr;
			}
		return newStr;
	}

	public static boolean isNotNull(Object obj) {
		return (obj != null) && (!obj.toString().equals(""));
	}

	public static boolean deleteFolder(String path) {
		boolean flag = false;
		File file = new File(path);

		if (!file.exists()) {
			return flag;
		}

		if (file.isFile()) {
			return deleteFile(path);
		}
		return deleteDirectory(path);
	}

	public static boolean deleteFile(String path) {
		boolean flag = false;
		File file = new File(path);

		if ((file.isFile()) && (file.exists())) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	public static boolean deleteDirectory(String path) {
		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}
		File dirFile = new File(path);

		if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
			return false;
		}
		boolean flag = true;

		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag) {
			return false;
		}

		return dirFile.delete();
	}

	public static char randomChar() {
		char[] chars = { 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f',
				'F', 'g', 'G', 'h', 'H', 'i', 'I', 'j', 'J', 'k', 'K', 'l',
				'L', 'm', 'M', 'n', 'N', 'o', 'O', 'p', 'P', 'q', 'Q', 'r',
				'R', 's', 'S', 't', 'T', 'u', 'U', 'v', 'V', 'w', 'W', 'x',
				'X', 'y', 'Y', 'z', 'Z' };
		int index = (int) (Math.random() * 52.0D) - 1;
		if (index < 0) {
			index = 0;
		}
		return chars[index];
	}

	public static String[] splitByChar(String s, String c) {
		String[] list = s.split(c);
		return list;
	}

	public static Object requestByParam(HttpServletRequest request, String param) {
		if (!request.getParameter(param).equals("")) {
			return request.getParameter(param);
		}
		return null;
	}

	public static String substring(String s, int maxLength) {
		if (!StringUtilsCus.hasLength(s))
			return s;
		if (s.length() <= maxLength) {
			return s;
		}
		return s.substring(0, maxLength) + "...";
	}

	public static String substringfrom(String s, String from) {
		if (s.indexOf(from) < 0)
			return "";
		return s.substring(s.indexOf(from) + from.length());
	}

	public static int null2Int(Object s) {
		int v = 0;
		if (s != null)
			try {
				v = Integer.parseInt(s.toString());
			} catch (Exception localException) {
			}
		return v;
	}

	public static float null2Float(Object s) {
		float v = 0.0F;
		if (s != null)
			try {
				v = Float.parseFloat(s.toString());
			} catch (Exception localException) {
			}
		return v;
	}

	public static double null2Double(Object s) {
		double v = 0.0D;
		if (s != null)
			try {
				v = Double.parseDouble(null2String(s));
			} catch (Exception localException) {
			}
		return v;
	}

	public static boolean null2Boolean(Object s) {
		boolean v = false;
		if (s != null)
			try {
				v = Boolean.parseBoolean(s.toString());
			} catch (Exception localException) {
			}
		return v;
	}

	public static String null2String(Object s) {
		return s == null ? "" : s.toString().trim();
	}

	public static Long null2Long(Object s) {
		Long v = Long.valueOf(-1L);
		if (s != null)
			try {
				v = Long.valueOf(Long.parseLong(s.toString()));
			} catch (Exception localException) {
			}
		return v;
	}

	public static String getTimeInfo(long time) {
		int hour = (int) time / 3600000;
		long balance = time - hour * 1000 * 60 * 60;
		int minute = (int) balance / 60000;
		balance -= minute * 1000 * 60;
		int seconds = (int) balance / 1000;
		String ret = "";
		if (hour > 0)
			ret = ret + hour + "小时";
		if (minute > 0)
			ret = ret + minute + "分";
		else if ((minute <= 0) && (seconds > 0))
			ret = ret + "零";
		if (seconds > 0)
			ret = ret + seconds + "秒";
		return ret;
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if ((ip == null) || (ip.length() == 0)
				|| ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0)
				|| ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0)
				|| ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static int indexOf(String s, String sub) {
		return s.trim().indexOf(sub.trim());
	}

	public static final String randomString(int length) {
		char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				.toCharArray();
		if (length < 1) {
			return "";
		}
		Random randGen = new Random();
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	public static final String randomInt(int length) {
		if (length < 1) {
			return null;
		}
		Random randGen = new Random();
		char[] numbersAndLetters = "0123456789".toCharArray();

		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(10)];
		}
		return new String(randBuffer);
	}

	public static long getDateDistance(String time1, String time2) {
		long quot = 0L;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000L / 60L / 60L / 24L;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return quot;
	}

	public static double div(Object a, Object b) {
		double ret = 0.0D;
		if ((!null2String(a).equals("")) && (!null2String(b).equals(""))) {
			BigDecimal e = new BigDecimal(null2String(a));
			BigDecimal f = new BigDecimal(null2String(b));
			if (null2Double(f) > 0.0D)
				ret = e.divide(f, 3, 1).doubleValue();
		}
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(ret)).doubleValue();
	}

	public static double subtract(Object a, Object b) {
		double ret = 0.0D;
		BigDecimal e = new BigDecimal(null2Double(a));
		BigDecimal f = new BigDecimal(null2Double(b));
		ret = e.subtract(f).doubleValue();
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(ret)).doubleValue();
	}

	public static double add(Object a, Object b) {
		double ret = 0.0D;
		BigDecimal e = new BigDecimal(null2Double(a));
		BigDecimal f = new BigDecimal(null2Double(b));
		ret = e.add(f).doubleValue();
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(ret)).doubleValue();
	}

	public static double mul(Object a, Object b) {
		BigDecimal e = new BigDecimal(null2Double(a));
		BigDecimal f = new BigDecimal(null2Double(b));
		double ret = e.multiply(f).doubleValue();
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(ret)).doubleValue();
	}

	public static double formatMoney(Object money) {
		DecimalFormat df = new DecimalFormat("0.00");
		return Double.valueOf(df.format(money)).doubleValue();
	}

	public static int M2byte(float m) {
		float a = m * 1024.0F * 1024.0F;
		return (int) a;
	}

	public static boolean convertIntToBoolean(int intValue) {
		return intValue != 0;
	}

	public static String getURL(HttpServletRequest request) {

		String contextPath = request.getContextPath().equals("/") ? ""
				: request.getContextPath();
		String url = "http://" + request.getServerName();

		if (null2Int(Integer.valueOf(request.getServerPort())) != 80)
			url = url + ":"
					+ null2Int(Integer.valueOf(request.getServerPort()))
					+ contextPath;
		else {
			url = url + contextPath;
		}
		return url;
	}


	public static int parseDate(String type, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (type.equals("y")) {
			return cal.get(1);
		}
		if (type.equals("M")) {
			return cal.get(2) + 1;
		}
		if (type.equals("d")) {
			return cal.get(5);
		}
		if (type.equals("H")) {
			return cal.get(11);
		}
		if (type.equals("m")) {
			return cal.get(12);
		}
		if (type.equals("s")) {
			return cal.get(13);
		}
		return 0;
	}

	public static int[] readImgWH(String imgurl) {
		boolean b = false;
		try {
			URL url = new URL(imgurl);

			BufferedInputStream bis = new BufferedInputStream(url.openStream());

			byte[] bytes = new byte[100];

			OutputStream bos = new FileOutputStream(new File(
					"C:\\thetempimg.gif"));
			int len;
			while ((len = bis.read(bytes)) > 0) {
				bos.write(bytes, 0, len);
			}
			bis.close();
			bos.flush();
			bos.close();

			b = true;
		} catch (Exception e) {
			b = false;
		}
		int[] a = new int[2];
		if (b) {
			File file = new File("C:\\thetempimg.gif");
			BufferedImage bi = null;
			boolean imgwrong = false;
			try {
				bi = ImageIO.read(file);
				try {
					//int i = bi.getType();
					imgwrong = true;
				} catch (Exception e) {
					imgwrong = false;
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			if (imgwrong) {
				a[0] = bi.getWidth();
				a[1] = bi.getHeight();
			} else {
				a = null;
			}

			file.delete();
		} else {
			a = null;
		}
		return a;
	}

	public static boolean fileExist(String path) {
		File file = new File(path);
		return file.exists();
	}

	public static int splitLength(String s, String c) {
		int v = 0;
		if (!s.trim().equals("")) {
			v = s.split(c).length;
		}
		return v;
	}

	public static double fileSize(File folder) {
		totalFolder += 1;

		long foldersize = 0L;
		File[] filelist = folder.listFiles();
		for (int i = 0; i < filelist.length; i++) {
			if (filelist[i].isDirectory()) {
				foldersize = (long) (foldersize + fileSize(filelist[i]));
			} else {
				totalFile += 1;
				foldersize += filelist[i].length();
			}
		}
		return div(Long.valueOf(foldersize), Integer.valueOf(1024));
	}

	public static int fileCount(File file) {
		if (file == null) {
			return 0;
		}
		if (!file.isDirectory()) {
			return 1;
		}
		int fileCount = 0;
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isFile()) {
				fileCount++;
			} else if (f.isDirectory()) {
				fileCount++;
				fileCount += fileCount(file);
			}
		}
		return fileCount;
	}

	public static String get_all_url(HttpServletRequest request) {
		String query_url = request.getRequestURI();
		if ((request.getQueryString() != null)
				&& (!request.getQueryString().equals(""))) {
			query_url = query_url + "?" + request.getQueryString();
		}
		return query_url;
	}

	public static Color getColor(String color) {
		if (color.charAt(0) == '#') {
			color = color.substring(1);
		}
		if (color.length() != 6)
			return null;
		try {
			int r = Integer.parseInt(color.substring(0, 2), 16);
			int g = Integer.parseInt(color.substring(2, 4), 16);
			int b = Integer.parseInt(color.substring(4), 16);
			return new Color(r, g, b);
		} catch (NumberFormatException nfe) {
		}
		return null;
	}

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

		return (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
				|| (ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
				|| (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
				|| (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION)
				|| (ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
				|| (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS);
	}

	public static boolean isMessyCode(String strName) {
		Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
		Matcher m = p.matcher(strName);
		String after = m.replaceAll("");
		String temp = after.replaceAll("\\p{P}", "");
		char[] ch = temp.trim().toCharArray();
		float chLength = ch.length;
		float count = 0.0F;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (Character.isLetterOrDigit(c))
				continue;
			if (!isChinese(c)) {
				count += 1.0F;
				System.out.print(c);
			}
		}

		float result = count / chLength;

		return result > 0.4D;
	}

	public static String trimSpaces(String IP) {
		while (IP.startsWith(" ")) {
			IP = IP.substring(1, IP.length()).trim();
		}
		while (IP.endsWith(" ")) {
			IP = IP.substring(0, IP.length() - 1).trim();
		}
		return IP;
	}

	public static boolean isIp(String IP) {
		boolean b = false;
		IP = trimSpaces(IP);
		if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
			String[] s = IP.split("\\.");
			if ((Integer.parseInt(s[0]) < 255)
					&& (Integer.parseInt(s[1]) < 255)
					&& (Integer.parseInt(s[2]) < 255)
					&& (Integer.parseInt(s[3]) < 255))
				b = true;
		}
		return b;
	}

	public static String generic_domain(HttpServletRequest request) {
		String system_domain = "localhost";
		String serverName = request.getServerName();
		if (isIp(serverName))
			system_domain = serverName;
		else {
			system_domain = serverName.substring(serverName.indexOf(".") + 1);
		}
		return system_domain;
	}
	
	public static boolean isToday(Date date) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		if (fmt.format(date).toString()
				.equals(fmt.format(new Date()).toString())) {// 格式化为相同格式
			return true;
		} else {
			return false;
		}

	}
	/** 
	 * 读取流 
	 *  
	 * @param inStream 
	 * @return 字节数组 
	 * @throws Exception 
	 */  
	public static byte[] readStream(InputStream inStream) throws Exception {  
	    ByteArrayOutputStream outSteam = new ByteArrayOutputStream();  
	    byte[] buffer = new byte[1024];  
	    int len = -1;  
	    while ((len = inStream.read(buffer)) != -1) {  
	        outSteam.write(buffer, 0, len);  
	    }  
	    outSteam.close();  
	    inStream.close();  
	    return outSteam.toByteArray();  
	}
	public static String readMailTem(String content){
		if(content==null||"".equals(content)){
			return null;
		}
		InputStream input = CommUtil.class.getClassLoader().getResourceAsStream("mailTem.ftl");
		try {
			String result=new String(readStream(input),"utf-8").replace("{{mailTemplateContent}}", content);			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 获取指定位数的随机数
	 * @param length
	 * @return
	 */
	public static String getRandom(int length) {
        String randomNum = "";
        try {
            randomNum = String.format("%0" + length + "d", Math.abs(new Random().nextLong()));
            randomNum = randomNum.substring(0, length);
        } catch (Exception e) {
            e.printStackTrace();           
        }
        return randomNum;
	}
	/**
	 * 获取请求字符串
	 * @param request
	 * @param charset
	 * @return
	 */
	public static String parseRequst(HttpServletRequest request, String charset) {
		String body = "";
		try {
			ServletInputStream inputStream = request.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream, charset));
			while (true) {
				String info = br.readLine();
				if (info == null) {
					break;
				}
				if (body == null || "".equals(body)) {
					body = info;
				} else {
					body += info;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return body;
	}
	
	public static String getServerUrl(HttpServletRequest request) {
        String url = "";
        try {
        	InetAddress address = InetAddress.getLocalHost();
			//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
            String hostAddress = address.getHostAddress();//192.168.0.121      
			String scheme = request.getScheme();//http
			int serverPort = request.getServerPort();//8080
			url = scheme+"://"+hostAddress+":"+serverPort+"/";
        } catch (UnknownHostException e) {
            e.printStackTrace();           
        }
        return url;
	}
	
	public static String getSerip() {
		String ip = "";
    	String chinaz = "http://ip.chinaz.com/getip.aspx";
    	
    	StringBuilder inputLine = new StringBuilder();
    	String read = "";
    	URL url = null;
    	HttpURLConnection urlConnection = null;
    	BufferedReader in = null;
    	try {
    		url = new URL(chinaz);
    		urlConnection = (HttpURLConnection) url.openConnection();
    	    in = new BufferedReader( new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
    		while((read=in.readLine())!=null){
    			System.out.println("we");
    			inputLine.append(read+"\r\n");
    		}
    	} catch (MalformedURLException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}finally{
    		if(in!=null){
    			try {
    				in.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	Pattern p = Pattern.compile("\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>");
    	Matcher m = p.matcher(inputLine.toString());
    	if(m.find()){
    		String ipstr = m.group(1);
    		ip = ipstr;
    	}
        return ip;
	}
	
	public static String getServerUrlInfo1(HttpServletRequest request) {
        String url = "";
        try {
        	InetAddress address = InetAddress.getLocalHost();
			//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
            String hostAddress =address.getHostAddress();
			String scheme = request.getScheme();//http
			int serverPort = request.getServerPort();//8080
			//String contextPath = request.getContextPath();//项目名
			url = scheme+"://"+hostAddress+":"+serverPort+"/";
        } catch (UnknownHostException e) {
            e.printStackTrace();           
        }
        return url;
	}
	
	
	
	public static String getTomcatPath(){
		
		return System.getProperty("catalina.home")+"/webapps/";
		
	}
	
	/**
	 * 
	* @Title: getJarPath 
	* @Description: 获取jar包部署路劲 
	* @return String
	* @author xya
	* @date 2019年5月28日上午11:08:32
	 */
	public static String getJarPath(){
		ApplicationHome home=new ApplicationHome(CommUtil.class);
		File jarFile=home.getSource();
		String path="";
		if(jarFile!=null){
			path=jarFile.getParentFile().getPath();
		}else{
			path="d:\\";
		}
		path=path.substring(0, path.indexOf(":")+1)+"/";
		return path;
	}
	
	public static String getServerUrl1(HttpServletRequest request)throws Exception{
		   String localip=request.getRemoteAddr();
		    String scheme = request.getScheme();//http
		    int serverPort = request.getServerPort();//8080
			localip= scheme+"://"+localip+":"+serverPort+"/";
			return localip;
    }
	
	public static String checkJsonObjValueIsNull(JSONObject jsonObj){
		Iterator<String> it = jsonObj.keys(); 
		while(it.hasNext()){
		// 获得key
			String key = it.next(); 
			String value = jsonObj.getString(key); 
			if(StrKit.isBlank(value)){
				return key;
			}
		}
		return null;
	}
	 /**
     * 获取两个日期之间的所有日期
     * 
     * @param startTime
     *            开始日期
     * @param endTime
     *            结束日期
     * @return
     */
    public static List<Map<String, Object>> getDays(String startTime, String endTime) {

        // 返回的日期集合
        List<Map<String, Object>> days = new ArrayList<Map<String, Object>>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);
            
            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
            	Map<String, Object> map=new HashMap<String, Object>();
            	map.put("date", dateFormat.format(tempStart.getTime()));
            	map.put("day", tempStart.get(Calendar.DATE)+"");
            	days.add(map);
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }
	
    public  static Map<String, Object> getTimeDifference(String startTime,String endTime) {
    	Map<String, Object> map =new HashMap<String, Object>();
		//格式日期格式，在此我用的是"2018-01-24 19:49:50"这种格式
		//可以更改为自己使用的格式，例如：yyyy/MM/dd HH:mm:ss 。。。
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(startTime);
			two = df.parse(endTime);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 );
			sec = (diff / 1000 - day * 24 * 60 * 60);
			map.put("day", day);
			map.put("hour", hour);
			map.put("min", Integer.valueOf(String.valueOf(min)));
			map.put("s", sec);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		   return map;
	}
    
    public  static Map<String, Long> getDateDifference(Date startTime,Date endTime) {
    	Map<String, Long> map =new HashMap<String, Long>();
		//格式日期格式，在此我用的是"2018-01-24 19:49:50"这种格式
		//可以更改为自己使用的格式，例如：yyyy/MM/dd HH:mm:ss 。。。
		   try{
			   long l=endTime.getTime()-startTime.getTime();       //获取时间差
			   long day=l/(24*60*60*1000);
			   long hour=(l/(60*60*1000)-day*24);
			   long min=((l/(60*1000))-day*24*60);
			   long s=(l/1000-day*24*60*60);
			   map.put("day", day);
			   map.put("hour", hour);
			   map.put("min", min);
			   map.put("s", s);
			   //System.out.println(""+day+"天"+hour+"小时"+min+"分"+s+"秒");
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return map;
	}
    
    public static  boolean getTimeByCalendar(String dateStr){
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	Calendar cal = Calendar.getInstance();
    	try {
			cal.setTime(df.parse(dateStr));
	    	int year = cal.get(Calendar.YEAR);//获取年份
	    	int month=cal.get(Calendar.MONTH);//获取月份
	    	int day=cal.get(Calendar.DATE);//获取日
	    	int hour=cal.get(Calendar.HOUR);//小时
	    	int minute=cal.get(Calendar.MINUTE);//分
	    	int second=cal.get(Calendar.SECOND);//秒
	    	int WeekOfYear = cal.get(Calendar.DAY_OF_WEEK);//一周的第几天

	    	System.out.println("现在的时间是：公元"+year+"年"+month+"月"+day+"日 "+hour+"时"+minute+"分"+second+"秒 星期"+WeekOfYear);
    	} catch (ParseException e) {
			e.printStackTrace();
		}
    return true;
	}
    
	public static void main(String[] args) {
		System.out.println(getTimeByCalendar("2019-06-05 08:05"));
		
	}
}
