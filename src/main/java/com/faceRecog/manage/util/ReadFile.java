package com.faceRecog.manage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ReadFile {

	public static String readContent(String filePath){
	    String charset = "utf-8";
	    File file = new File(filePath);
	    long fileByteLength = file.length();
	    byte[] content = new byte[(int)fileByteLength];
	    FileInputStream fileInputStream = null;
	    try {
	        fileInputStream = new FileInputStream(file);
	        fileInputStream.read(content);
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            fileInputStream.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    String str = null;
		try {
			str = new String(content,charset);
			file.delete();//删除临时文件
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return str;
	}
	
	
	public static void main(String[] args) {
		System.out.println(readContent("C:\\serverAuthorize\\authorize_code.vbs"));
	}
}
