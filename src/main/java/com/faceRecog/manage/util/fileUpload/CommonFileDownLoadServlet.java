package com.faceRecog.manage.util.fileUpload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;



import org.springframework.web.context.support.SpringBeanAutowiringSupport;


import com.faceRecog.manage.util.CommUtil;
import com.faceRecog.manage.util.UuidUtils;



/**
 * 文件通用文件下载
 * 
 */
public class CommonFileDownLoadServlet extends HttpServlet{

	
	private static final long serialVersionUID = 394623284021687675L;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		 SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());  
	}
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//获取配置文件里的保存路径
		String realPath = CommUtil.getTomcatPath();
		String url ="";
		if("errLog".equals(request.getParameter("url"))){//导出服务器错误日志
			url="/usr/log/us_error.log";
			Path path=Paths.get(url); 
			String contentType=Files.probeContentType(path);
			CommonFileDownLoadServlet.singleDownLoad(url, response, contentType);
		}else{
			url =realPath+ request.getParameter("url");
			Path path=Paths.get(url); 
			String contentType=Files.probeContentType(path);
			CommonFileDownLoadServlet.singleDownLoad(url, response, contentType);
		}
	}
	
	/**
	 * 单个文件下载
	 * @param url
	 * @param response
	 * @param contentType
	 * @throws IOException
	 */
	public static void singleDownLoad(String url,HttpServletResponse response,String contentType)throws IOException{
		//文件路径
				String filePath = "";
				//文件名称
				String filename = "";
				//文件扩展名
				//String EXTNAME = "";
				
				//判断下载的文件路径是否存在
				boolean flg = true;
				//判断传入的参数URL 如果存在URL 优先用URL下载
					if (url != null && !"".equals(url)) {
						File fileURL = new File(url);
						if (fileURL.exists()) {
							filePath = url;
							filename = url.substring(url.lastIndexOf("/") + 1);
							flg = true;
						} else {
							flg = false;
						}
					} 		
					if (flg) {			
						File file = new File(filePath);
						
						if (file.exists()) {
							// 设置request对象的字符编码
								filename = URLEncoder.encode(filename, "UTF-8");
							
							// 根据文件的类型设置response对象的ContentType
							//String contentType = getServletContext().getMimeType(filePath);
			
							if (contentType == null) {
								contentType = "application/octet-stream";
							}
							response.setContentType(contentType);
							 
							// 设置response的头信息
							response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.replaceAll(" ", "").getBytes("utf-8"),"iso8859-1"));
							response.addHeader("Content-Length", "" + file.length());
			
							InputStream is = null;
							OutputStream os = null;
							try {
								is = new BufferedInputStream(new FileInputStream(filePath));
								// 定义response的输出流
								os = new BufferedOutputStream(response.getOutputStream());
								// 定义buffer
								byte[] buffer = new byte[4 * 1024]; // 4k Buffer
								int read = 0;
								// 从文件中读入数据并写到输出字节流中
								while ((read = is.read(buffer)) != -1) {
									// 将输出字节流写到response的输出流中
									os.write(buffer,0,read);
								}
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								// 关闭输出字节流和response输出流
								is.close();
								os.close();
								os.flush();
							}
						} else {
							flg = false;
						}
					}else{
						//如果下载的文件不存在
						response.setContentType("text/html;charset=gb2312");
						PrintWriter out = response.getWriter();
						out.print("<script>");
						out.print("alert(\""+filename +"资源不存在！\")");
						out.print("</script>");
					}
	}
	
	/**
	 * 多文件zip下载
	 * @param response
	 * @param urls
	 * @param request
	 * @throws IOException
	 */
	public static void zipDownLoad(HttpServletResponse response,String []urls,HttpServletRequest request)throws IOException{
		
		 //压缩文件初始设置
        String path="/upload";
        String base_name = CommonUtil.fileNameFormat()+UuidUtils.getUuid();
        String  fileZip = base_name + ".zip"; // 拼接zip文件
        String filePath = path+"/" + fileZip;//之后用来生成zip文件

        //filePathArr为根据前台传过来的信息，通过数据库查询所得出的pdf文件路径集合（具体到后缀），此处省略
        File tempFile=new File(filePath);
        if(!tempFile.getParentFile().exists()){
        	tempFile.getParentFile().mkdirs();
        }
        List<String> list=Arrays.asList(urls);//将数组转换为list集合
        List<String> urlList = new ArrayList<String>(list);
        for(int i=0;i<urlList.size();i++){
        	String downPath=urlList.get(i);
        	if(!new File(downPath).exists()){
        		urlList.remove(downPath);
        	}
        }
        File[]  files = new File[urlList.size()];//
        for(int x=0;x<urlList.size();x++){
        	files[x]=new File(urlList.get(x));
        	if(!files[x].exists() || files[x].isDirectory()){
        		//如果下载的文件不存在
    			response.setContentType("text/html;charset=gb2312");
    			PrintWriter out = response.getWriter();
    			out.print("<script>");
    			out.print("alert(\""+files[x].getName()+"资源不存在！\")");
    			out.print("</script>");
    			return;
        	}
        	
        }
        if(files!=null && files.length>0){
	        // 创建临时压缩文件
	        try {
	            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
	            ZipOutputStream zos = new ZipOutputStream(bos);
	            ZipEntry ze = null;
	            for (int i = 0; i < files.length; i++) {//将所有需要下载的pdf文件都写入临时zip文件
	                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(files[i]));
	                ze = new ZipEntry(files[i].getName());
	                zos.putNextEntry(ze);
	                int s = -1;
	                byte[] buffer = new byte[4096];
	                while ((s = bis.read(buffer)) != -1) {
	                    zos.write(buffer,0,s);
	                }
	                bis.close();
	            }
	            zos.flush();
	            zos.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        //以上，临时压缩文件创建完成
	
	        //进行浏览器下载       
	        //获得浏览器代理信息
	        final String userAgent = request.getHeader("USER-AGENT");
	        //判断浏览器代理并分别设置响应给浏览器的编码格式
	        String finalFileName = null;
	        if(StringUtils.contains(userAgent, "MSIE")||StringUtils.contains(userAgent,"Trident")){//IE浏览器
	            finalFileName = URLEncoder.encode(fileZip,"UTF8");
	            System.out.println("IE浏览器");
	        }else if(StringUtils.contains(userAgent, "Mozilla")){//google,火狐浏览器
	            finalFileName = new String(fileZip.getBytes(), "ISO8859-1");
	        }else{
	            finalFileName = URLEncoder.encode(fileZip,"UTF8");//其他浏览器
	        }
	        response.setContentType("application/x-download");//告知浏览器下载文件，而不是直接打开，浏览器默认为打开
	        response.setHeader("Content-Disposition" ,"attachment;filename=\"" +finalFileName+ "\"");//下载文件的名称
	
	        ServletOutputStream servletOutputStream=response.getOutputStream();
	        DataOutputStream temps = new DataOutputStream(
	                        servletOutputStream);
	
	        DataInputStream in = new DataInputStream(
	                                new FileInputStream(filePath));//浏览器下载文件的路径
	        byte[] b = new byte[2048];
	        File reportZip=new File(filePath);//之后用来删除临时压缩文件
	        try {
	            while ((in.read(b)) != -1) {
	            temps.write(b);
	        }
	            temps.flush();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally{
	            if(temps!=null) temps.close();
	            if(in!=null) in.close();
	            if(reportZip!=null) reportZip.delete();//删除服务器本地产生的临时压缩文件
	            servletOutputStream.close();
	        }
        }else{
        	//如果下载的文件不存在
			response.setContentType("text/html;charset=gb2312");
			PrintWriter out = response.getWriter();
			out.print("<script>");
			out.print("alert(\"资源不存在！\")");
			out.print("</script>");
        }
	}
        
}