package com.faceRecog.manage.util;


import com.faceRecog.manage.model.User;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;


public class PasswordHelper {
	//private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
	private String algorithmName = "md5";
	private int hashIterations = 2;

	public String encryptPassword(String password,String salt) {
		//String salt=randomNumberGenerator.nextBytes().toHex();
		String newPassword = new SimpleHash(algorithmName, password,  ByteSource.Util.bytes(salt), hashIterations).toString();

		return newPassword;
	}
	public static void main(String[] args) {
		PasswordHelper passwordHelper = new PasswordHelper();
		System.out.println(passwordHelper.encryptPassword("admin", "7t630o"));
	}
}
