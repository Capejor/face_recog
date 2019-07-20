package com.faceRecog.manage.util;

import org.apache.axis.encoding.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MD5Util {
	public static  String hexDigits[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f","+","="};
	public static String password="yskj";//加解密密码
	private static int length=128;
	
	public final static String MD5(String source)throws Exception {
       
        
    	 String result = null;
         try {
             result = source;
             // 获得MD5摘要对象
             MessageDigest messageDigest = MessageDigest.getInstance("MD5");
             // 使用指定的字节数组更新摘要信息
             messageDigest.update(result.getBytes("UTF-8"));
             // messageDigest.digest()获得16位长度
             // result = parseByte2HexStr(messageDigest.digest());
             result = byteArrayToHexString(messageDigest.digest());
             
         } catch (Exception e) {
             e.printStackTrace();
         }
         return  result;
    }
	
	 
	
	
	 /**
     * 
    * @Title: AESEncrypt 
    * @Description: AES加密
    * @param content
    * @return
    * @throws Exception String
    * @author xya
    * @date 2019年1月9日上午9:34:05
     */
    public static String AESEncrypt(String content) throws Exception {  
        byte[] encryptResult = encrypt(content);  
        return Base64.encode(encryptResult);  
    }  
  
    /**
     * 
    * @Title: AESEncrypt 
    * @Description: AES解密
    * @param content
    * @return
    * @throws Exception String
    * @author xya
    * @date 2019年1月9日上午9:34:05
     */
    public static String AESDecrypt(String content) throws Exception {  
  
        byte[] decryptResult = decrypt(Base64.decode(content));  
        return new String(decryptResult,"UTF-8");  
    }  
    /** 
     * 加密 
     *  
     * @param content 
     *            需要加密的内容 
     * @param password 
     *            加密密码 
     * @return 
     * @throws NoSuchAlgorithmException 
     * @throws NoSuchPaddingException 
     * @throws UnsupportedEncodingException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     */  
    private static byte[] encrypt(String content)  
            throws Exception {  
  
        KeyGenerator kgen = KeyGenerator.getInstance("AES");  
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );   
                secureRandom.setSeed(password.getBytes());   
        kgen.init(length, secureRandom);  
        SecretKey secretKey = kgen.generateKey();  
        byte[] enCodeFormat = secretKey.getEncoded();  
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");  
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器  
        byte[] byteContent = content.getBytes("utf-8");  
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化  
        byte[] result = cipher.doFinal(byteContent);  
        return result; // 加密  
  
    }  
  
    /** 
     * 解密 
     *  
     * @param content 
     *            待解密内容 
     * @param password 
     *            解密密钥 
     * @return 
     */  
    private static byte[] decrypt(byte[] content)  
            throws Exception {  
  
        KeyGenerator kgen = KeyGenerator.getInstance("AES");  
                 SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );   
                  secureRandom.setSeed(password.getBytes());   
        kgen.init(length, secureRandom);  
        SecretKey secretKey = kgen.generateKey();  
        byte[] enCodeFormat = secretKey.getEncoded();  
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");  
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器  
        cipher.init(Cipher.DECRYPT_MODE, key);// 初始化  
        byte[] result = cipher.doFinal(content);  
        return result; // 加密  
                  
               
  
    }  
  
//  /**  
//   * 将二进制转换成16进制  
//   *   
//   * @param buf  
//   * @return  
//   */  
//  public static String parseByte2HexStr(byte buf[]) {  
//      StringBuffer sb = new StringBuffer();  
//      for (int i = 0; i < buf.length; i++) {  
//          String hex = Integer.toHexString(buf[i] & 0xFF);  
//          if (hex.length() == 1) {  
//              hex = '0' + hex;  
//          }  
//          sb.append(hex.toUpperCase());  
//      }  
//      return sb.toString();  
//  }  
//  
//  /**  
//   * 将16进制转换为二进制  
//   *   
//   * @param hexStr  
//   * @return  
//   */  
//  public static byte[] parseHexStr2Byte(String hexStr) {  
//      if (hexStr.length() < 1)  
//          return null;  
//      byte[] result = new byte[hexStr.length() / 2];  
//      for (int i = 0; i < hexStr.length() / 2; i++) {  
//          int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);  
//          int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),  
//                  16);  
//          result[i] = (byte) (high * 16 + low);  
//      }  
//      return result;  
//  }  
  
    /** 
     * 加密 
     *  
     * @param content 
     *            需要加密的内容 
     * @param password 
     *            加密密码 
     * @return 
     */  
    public static byte[] encrypt2(String content) {  
        try {  
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");  
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");  
            byte[] byteContent = content.getBytes("utf-8");  
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化  
            byte[] result = cipher.doFinal(byteContent);  
            return result; // 加密  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
  //byte转16进制
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte tem : bytes) {
            stringBuilder.append(byteToHexString(tem));
        }
        return stringBuilder.toString();
    }
    
    //16进制转byte[]
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
	
    public static void main(String[] args){
    	try{
    		 /*String content="qw/s2-13!2-MAFS`-ASDHBAJ";
    		 String aesKey="yskj";
    		 String hh= AESEncrypt(content);
    		byte []bytes=parseHexStr2Byte(hh);
    		、、byte [] pas=AESDecrypt(bytes, aesKey);
    		System.out.println(hh);
    		System.out.println(new String(pas));AESEncrypt*/
    		String currDate=new SimpleDateFormat("yyyyMMdd").format(new Date());
    		System.out.println(AESEncrypt(currDate));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	//System.out.println(MD5.getHexMD5(("-911218274013828CX580我就是我")));898E35A0C44B02CA4AE6E4A9BD39A843
    	
    }
}
