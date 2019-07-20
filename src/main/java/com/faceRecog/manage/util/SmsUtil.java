/**   
 * Copyright © 2019 yskj Info. Tech Ltd. All rights reserved.
 * 
 * @Package: com.etv.manage.utils.sms 
 * @author: Administrator   
 * @date: 2019年4月19日 下午12:02:07 
 */
package com.faceRecog.manage.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;

/** 
 * @ClassName: smsUtil 
 * @Description: TODO
 * @author: Administrator
 * @date: 2019年4月19日 下午12:02:07  
 */
public class SmsUtil {
	private  static int smsCode=0;
	
	/*public synchronized static void addCode(int smsCode){
		smsUtil.sendCode = smsCode;
	}*/
	 /**
     * 获取验证码
     *
     * @param userName
     */
    public static int  getSmsCode(String userName) {
    	
        smsCode = new Random().nextInt(8000) + 1000;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("account", "N7313674");//API账号
        map.put("password", "40aC8zMtU377ab");//API密码
        map.put("msg", "【YS人脸识别系统】验证码10分钟内有效，软件的验证码:" + smsCode);//短信内容
        map.put("phone", userName);//手机号
        map.put("report", "true");   //是否需要状态报告
        map.put("extend", "123");//自定义扩展码
        JSONObject js =(JSONObject) JSONObject.toJSON(map);
        final String xxxx = js.toString();
        final String sendUrl = "http://smssh1.253.com/msg/send/json";
        //Log.e("cdl", "====请求得参数===" + xxxx);
        new Thread() {
            @Override
            public void run() {
                super.run();
                String desc = sendSmsByPost(sendUrl, xxxx);
                JSONObject jsonObj= JSONObject.parseObject(desc);
                System.out.println("==========SMS========="+desc);
                if(!"0".equals(jsonObj.getString("code"))){
                	smsCode=0;
                }
            }
        }.start();
        return smsCode;
    }

    public static String sendSmsByPost(String path, String postContent) {
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.connect();
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(postContent.getBytes("UTF-8"));
            os.flush();
            StringBuilder sb = new StringBuilder();
            int httpRspCode = httpURLConnection.getResponseCode();
            //Log.e("cdl", "=====获取验证==" + httpRspCode);
            if (httpRspCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                return sb.toString();
            } else {
                //Log.e("cdl", "=====获取验证==" + httpRspCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("cdl", "=====获取验证error==" + e.toString());
        }
        return null;
    }

    public static void main(String[] args) {
    	SmsUtil.getSmsCode("17603089778");
	}
}
